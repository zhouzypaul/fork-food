package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;

import com.google.gson.JsonObject;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.restaurants.Restaurant;
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

@WebSocket
public class GroupSocket {
  private static final Gson GSON = new Gson();

  private static final Hashtable<Integer, Queue<Session>> ROOMS = new Hashtable<>();
  private static final Hashtable<Integer, HashSet<String>> USER_ROOMS = new Hashtable<>();
  private static final Hashtable<Integer, HashSet<String>> USERS_COPY = new Hashtable<>();
  private static final Hashtable<Integer, Hashtable<String, Hashtable<String, Integer>>> USER_DECISIONS = new Hashtable<>();
  private static final HashSet<Integer> STARTED_ROOMS = new HashSet<>();
  private static int nextId = 0;

  private enum MESSAGE_TYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // build CONNECT message
    JsonObject json = new JsonObject();
    json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());

    JsonObject payload = new JsonObject();
    payload.addProperty("id", nextId++);

    json.add("payload", payload);
    // make sure to send a unique id!
    // Hint: can use ordinal to get the number position of an enum, MESSAGE_TYPE.CONNECT.ordinal());

    String message = GSON.toJson(json);

    session.getRemote().sendString(message);

  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    System.out.println("Socket closed");
    // TODO: need to handle if user just dips
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    // convert message to JsonObject
    System.out.println(message);
    try {
      JSONObject messageObj = new JSONObject(message);
      String type = messageObj.getJSONObject("message").getString("type");
      int roomId = messageObj.getJSONObject("message").getInt("roomId");

      // prepare update message
      JsonObject updateMessage = new JsonObject();
      updateMessage.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

      JsonObject payload = new JsonObject();
      payload.addProperty("senderId", messageObj.getInt("id"));

      System.out.println(type);

      //boolean to keep track of if room can be cleared
      boolean doneWithRoom = false;

      switch (type) {
        case "update_user":
          // add current session to correct room
          if (ROOMS.get(roomId) == null) {
            ROOMS.put(roomId, new ConcurrentLinkedQueue<>());
            USER_ROOMS.put(roomId, new HashSet<>());
            USERS_COPY.put(roomId, new HashSet<>());
          }
          USER_ROOMS.get(roomId).add(messageObj.getJSONObject("message").getString("username"));
          USERS_COPY.get(roomId).add(messageObj.getJSONObject("message").getString("username"));
          ROOMS.get(roomId).add(session);

          JsonObject users = new JsonObject();
          users.add("users", GSON.toJsonTree(USER_ROOMS.get(roomId)));

          payload.addProperty("type", "update_user");
          payload.add("senderMessage", users);
          break;

        case "start":
          double lat = messageObj.getJSONObject("message").getDouble("lat");
          double lon = messageObj.getJSONObject("message").getDouble("lon");

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
            for (String user : usernames) {
              userDecision.put(user, 0);
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
          String user = messageObj.getJSONObject("message").getString("username");
          String resId = messageObj.getJSONObject("message").getString("resId");
          // add to the USER_DECISIONS table
          USER_DECISIONS.get(roomId).get(resId).replace(user, messageObj.getJSONObject("message").getInt("like"));
          return;

        case "done":
          String username = messageObj.getJSONObject("message").getString("username");
          USER_ROOMS.get(roomId).remove(username);
          if (USER_ROOMS.get(roomId).isEmpty()) {
            // return a decision
            JsonObject result = new JsonObject();

            try {
              String commonRes = Hub.rankRestaurants(USERS_COPY.get(roomId), USER_DECISIONS.get(roomId));
              USER_DECISIONS.remove(roomId);

              System.out.println("decision " + commonRes);
              Map<String, String> rest = new HashMap<>();
              try {
                rest = Hub.getRestDB().queryRestByID(commonRes);
              } catch (Exception e) {
                System.out.println("ERROR: " + e);
              }

              result.add("result", GSON.toJsonTree(rest));

              payload.addProperty("type", "done");
              payload.add("senderMessage", result);

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
        USER_ROOMS.remove(roomId);
        USERS_COPY.remove(roomId);
        ROOMS.remove(roomId);
      }

    } catch (JSONException e) {
      System.out.println("ERROR: invalid json" + e);
    }
  }

  @OnWebSocketError
  public void throwError(Throwable error) {
    error.printStackTrace();
  }

  public static boolean valid(int code) {
    return USER_ROOMS.containsKey(code) && !STARTED_ROOMS.contains(code);
  }

}
