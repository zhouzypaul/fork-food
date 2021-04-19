package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;

import com.google.gson.JsonObject;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.eclipse.jetty.websocket.api.CloseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Socket for handling hosting, joining a room and swiping.
 */
@WebSocket
public class GroupSocket {
  private static final Gson GSON = new Gson();

  private static final Hashtable<Integer, Queue<Session>> ROOMS = new Hashtable<>();
  private static final Hashtable<Integer, double[]> ROOM_COORS = new Hashtable<>();
  private static final Hashtable<Integer, HashSet<String>> USER_ROOMS = new Hashtable<>();
  private static final Hashtable<Integer, HashSet<String>> USERS_COPY = new Hashtable<>();
  private static final Hashtable<Session, Integer> SESSION_ROOMS = new Hashtable<>();
  private static final Hashtable<Session, String> SESSION_USER = new Hashtable<>();
  private static final Hashtable<Integer, Hashtable<String, Hashtable<String, Integer>>>
          USER_DECISIONS = new Hashtable<>();
  private static final HashSet<Integer> STARTED_ROOMS = new HashSet<>();
  private static int nextId = 0;

  // <userId, [<restId, decision>, ...]>
  private static final Map<String, Map<String, List<String>>> SWIPING_PREF = new HashMap<>();

  /**
   * Message type of a session.
   */
  private enum MESSAGETYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  /**
   * Connect to socket.
   * @param session socket session
   * @throws IOException IOException
   */
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // build CONNECT message
    JsonObject json = new JsonObject();
    json.addProperty("type", MESSAGETYPE.CONNECT.ordinal());

    JsonObject payload = new JsonObject();
    payload.addProperty("id", nextId++);

    json.add("payload", payload);

    String message = GSON.toJson(json);

    session.getRemote().sendString(message);
  }

  /**
   * Close a socket.
   * @param session socket session
   * @param statusCode status code of session
   * @param reason reason of session
   */
  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    if (SESSION_ROOMS.get(session) == null) {
      return;
    }

    int roomId = SESSION_ROOMS.get(session);
    String user = SESSION_USER.get(session);

    ROOMS.get(roomId).remove(session);
    USER_ROOMS.get(roomId).remove(user);

    // prep update message
    JsonObject updateMessage = new JsonObject();
    updateMessage.addProperty("type", MESSAGETYPE.UPDATE.ordinal());

    JsonObject payload = new JsonObject();

    if (!STARTED_ROOMS.contains(roomId)) { // if room not started resend the userlist
      // same code as update_users

      JsonObject users = new JsonObject();
      users.add("users", GSON.toJsonTree(USER_ROOMS.get(roomId)));

      payload.addProperty("type", "update_user");
      payload.add("senderMessage", users);

    } else {
      // same code as done
      if (USER_ROOMS.get(roomId).isEmpty()) {
        try {
          getResult(roomId, payload);

        } catch (SQLException | NoUserException e) {
          System.out.println("ERROR " + e);
          return;
        }
      } else {
        return;
      }
    }
    updateMessage.add("payload", payload);

    // send message to everyone in room
    String update = GSON.toJson(updateMessage);
    try {
      for (Session sesh : ROOMS.get(roomId)) {
        sesh.getRemote().sendString(update); // sending to each session in room
      }
    } catch (IOException e) {
      System.out.println("ERROR: " + e);
    }

    USERS_COPY.get(roomId).remove(user);
    if (USER_ROOMS.get(roomId).isEmpty()) {
      cleanup(roomId);
    }

    // cleanup
    SESSION_ROOMS.remove(session);
    SESSION_USER.remove(session);
  }

  /**
   * Socket session message.
   * @param session socket session
   * @param message message
   * @throws IOException IOException
   */
  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    // convert message to JsonObject
    try {
      JSONObject messageObj = new JSONObject(message);
      String type = messageObj.getJSONObject("message").getString("type");
      int roomId = messageObj.getJSONObject("message").getInt("roomId");

      // prepare update message
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("type", MESSAGETYPE.UPDATE.ordinal());

      JsonObject payload = new JsonObject();
      payload.addProperty("senderId", messageObj.getInt("id"));

      // boolean to keep track of if room can be cleared
      boolean doneWithRoom = false;

      switch (type) {
        case "update_user":
          // add current session to correct room
          if (ROOMS.get(roomId) == null) {
            ROOMS.put(roomId, new ConcurrentLinkedQueue<>());
            USER_ROOMS.put(roomId, new HashSet<>());
            USERS_COPY.put(roomId, new HashSet<>());
          }
          String user = messageObj.getJSONObject("message").getString("username");
          USER_ROOMS.get(roomId).add(user);
          USERS_COPY.get(roomId).add(user);
          ROOMS.get(roomId).add(session);
          SESSION_ROOMS.put(session, roomId);
          SESSION_USER.put(session, user);

          JsonObject users = new JsonObject();
          users.add("users", GSON.toJsonTree(USER_ROOMS.get(roomId)));

          payload.addProperty("type", "update_user");
          payload.add("senderMessage", users);
          break;

        case "start":
          double lat = messageObj.getJSONObject("message").getDouble("lat");
          double lon = messageObj.getJSONObject("message").getDouble("lon");
          ROOM_COORS.put(roomId, new double[]{lat, lon});

          Set<String> usernames = USER_ROOMS.get(roomId);
          STARTED_ROOMS.add(roomId);

          List<Restaurant> recommendedRestaurants = new ArrayList<>();

          try {
            recommendedRestaurants = Hub.recommendRestaurants(usernames, new double[]{lat, lon});
          } catch (Exception e) {
            System.out.println("ERROR: " + e);
          }

          Hashtable<String, Hashtable<String, Integer>> restaurantVotes = new Hashtable<>();
          for (Restaurant restaurant : recommendedRestaurants) {
            Hashtable<String, Integer> userDecision = new Hashtable<>();
            for (String username : usernames) {
              userDecision.put(username, 0);
            }
            restaurantVotes.put(restaurant.getId(), userDecision);
          }

          USER_DECISIONS.put(roomId, restaurantVotes);

          JsonObject restaurants = new JsonObject();
          restaurants.add("restaurants", GSON.toJsonTree(recommendedRestaurants));

          payload.addProperty("type", "start");
          payload.add("senderMessage", restaurants);

          break;

        case "swipe":
          String usern = messageObj.getJSONObject("message").getString("username");
          String resId = messageObj.getJSONObject("message").getString("resId");
          int decision = messageObj.getJSONObject("message").getInt("like");
          // add to the USER_DECISIONS table
          USER_DECISIONS.get(roomId).get(resId).replace(usern, decision);
          // add to the SWIPING_PREF map
          addToSwipePref(usern, resId, String.valueOf(decision));
          return;

        case "done":
          String username = messageObj.getJSONObject("message").getString("username");
          double[] doneCoor = ROOM_COORS.get(roomId);

          boolean success = addToDatabase(username, doneCoor);
          if (!success) {
            System.out.println("ERROR: Can't add user swiping preferences.");
            return;
          }

          USER_ROOMS.get(roomId).remove(username);
          if (USER_ROOMS.get(roomId).isEmpty()) {
            try {
              getResult(roomId, payload);

              doneWithRoom = true;
            } catch (SQLException | NoUserException e) {
              System.out.println("ERROR " + e);
              return;
            }
          } else {
            return;
          }
          break;

        default:
          System.out.println("Unrecognized message type");
      }
      updateMessage.add("payload", payload);

      // send message to everyone in room
      String update = GSON.toJson(updateMessage);

      for (Session sesh : ROOMS.get(roomId)) {
        sesh.getRemote().sendString(update); // sending to each session in room
      }

      if (doneWithRoom) {
        cleanup(roomId);
      }

    } catch (JSONException e) {
      System.out.println("ERROR: invalid json" + e);
    } catch (CloseException e) {
      System.out.println("Socket time out");
    }
  }

  /**
   * Add to SWIPE_PREF.
   * @param user username
   * @param resId restaurant id
   * @param decision whether user likes the restaurant
   */
  public void addToSwipePref(String user, String resId, String decision) {
    if (!SWIPING_PREF.containsKey(user)) {
      Map<String, List<String>> prefs = new HashMap<>();
      prefs.put("business_id", new ArrayList<>());
      prefs.put("decisions", new ArrayList<>());
      SWIPING_PREF.put(user, prefs);
    }
    List<String> restIds = SWIPING_PREF.get(user).get("business_id");
    List<String> decisions = SWIPING_PREF.get(user).get("decisions");
    restIds.add(resId);
    decisions.add(decision);
  }

  /**
   * Add all swipe decisions to database.
   * @param username user
   * @param coor host coordinate
   * @return whether the action is successful
   */
  public boolean addToDatabase(String username, double[] coor) {
    List<String> prefRestaurants = SWIPING_PREF.get(username).get("business_id");
    List<String> prefDecisions = SWIPING_PREF.get(username).get("decisions");
    return Hub.getUserDB().insertUserSwipePref(username, coor[0],
        coor[1], prefRestaurants, prefDecisions);
  }

  /**
   * Throws error.
   * @param error error
   */
  @OnWebSocketError
  public void throwError(Throwable error) {
    error.printStackTrace();
  }

  /**
   * Checks if a room code is valid, meaning that it exists and has not started yet.
   * @param code to verify
   * @return true if valid, false if not
   */
  public static boolean valid(int code) {
    return USER_ROOMS.containsKey(code) && !STARTED_ROOMS.contains(code);
  }

  /**
   * Removes entry with id as key from all data structures.
   * @param id of entry to remove
   */
  private static void cleanup(Integer id) {
    HashSet<String> users = USER_ROOMS.get(id);
    USER_ROOMS.remove(id);
    USERS_COPY.remove(id);
    ROOMS.remove(id);
    STARTED_ROOMS.remove(id);
    USER_DECISIONS.remove(id);
    ROOM_COORS.remove(id);
    for (String user : users) {
      SWIPING_PREF.remove(user);
    }
  }

  /**
   * Gets restaurant result.
   * @param roomId of current room
   * @param payload to fill
   * @throws NoUserException on error
   * @throws SQLException on error
   */
  private static void getResult(int roomId, JsonObject payload)
      throws NoUserException, SQLException {
    JsonObject result = new JsonObject();
    String commonRes = Hub.rankRestaurants(USERS_COPY.get(roomId),
        USER_DECISIONS.get(roomId));

    Map<String, String> rest = new HashMap<>();
    try {
      rest = Hub.getRestDB().queryRestByID(commonRes);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }

    result.add("result", GSON.toJsonTree(rest));

    payload.addProperty("type", "done");
    payload.add("senderMessage", result);
  }
}
