package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class GroupSocket {
  private static final Gson GSON = new Gson();
  // TODO: use hashmap of session queues, can we even do this, i.e. send a message to the server on conncet?
  private static final Hashtable<Integer, Queue<Session>> rooms = new Hashtable<>();
  private static final Hashtable<Integer, HashSet<String>> userRooms = new Hashtable<>();
//  private static final Hashtable<Integer, Hashtable<String, Set<String>>> userRestaurants = new Hashtable<>();
  private static final Hashtable<Integer, List<String>> userDecisions = new Hashtable<>();
  private static int nextId = 0;

  private static enum MESSAGE_TYPE {
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
    System.out.println("Socket closed" + session);
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
      JsonObject update_message = new JsonObject();
      update_message.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

      JsonObject payload = new JsonObject();
      payload.addProperty("senderId", messageObj.getInt("id"));

      System.out.println(type);

      //boolean to keep track of if room can be cleared
      boolean doneWithRoom = false;

      switch (type) {
        case "update_user":
          // add current session to correct room
          if (rooms.get(roomId) == null) {
            rooms.put(roomId, new ConcurrentLinkedQueue<>());
            userRooms.put(roomId, new HashSet<>());
          }
          userRooms.get(roomId).add(messageObj.getJSONObject("message").getString("username"));
          rooms.get(roomId).add(session);

          JsonObject users = new JsonObject();
          users.add("users", GSON.toJsonTree(userRooms.get(roomId)));

          payload.addProperty("type", "update_user");
          payload.add("senderMessage", users);
          break;

        case "start":
          double lat = messageObj.getJSONObject("message").getDouble("lat");
          double lon = messageObj.getJSONObject("message").getDouble("lon");

          Set<String> usernames = userRooms.get(roomId);


          userDecisions.put(roomId, new LinkedList<>());

          List<Restaurant> recommendedRestaurants = new ArrayList<>();

          try {
            recommendedRestaurants = Hub.recommendRestaurants(usernames, new double[]{lat, lon});
          } catch (Exception e) {
            System.out.println("ERROR: " + e);
          }

          JsonObject restaurants = new JsonObject();
          restaurants.add("restaurants", GSON.toJsonTree(recommendedRestaurants));

          payload.addProperty("type", "start");
          payload.add("senderMessage", restaurants);

          break;

        case "swipe":
          String user = messageObj.getJSONObject("message").getString("username");
          String resId = messageObj.getJSONObject("message").getString("resId");
          // add to the userDecisions table
          if (messageObj.getJSONObject("message").getInt("like") == 1) {
            userDecisions.get(roomId).add(resId);
          }
          return;

        case "done":
          String username = messageObj.getJSONObject("message").getString("username");
          userRooms.get(roomId).remove(username);
          if (userRooms.get(roomId).isEmpty()) {
            // return a decision
            JsonObject result = new JsonObject();

            String commonRes = mostCommon(userDecisions.get(roomId));
            userDecisions.remove(roomId);

            System.out.println("decision " + commonRes);
            Map<String, String> rest = new HashMap<>();
            try {
               rest = Hub.getRestDB().queryRestByID(commonRes).get(0);
            } catch (Exception e) {
              System.out.println("ERROR: " + e);
            }

            result.add("result", GSON.toJsonTree(rest));

            payload.addProperty("type", "done");
            payload.add("senderMessage", result);

            doneWithRoom = true;
          } else {
            return;
          }
          break;

        default:
          System.out.println("Unrecognized message type");
      }
      update_message.add("payload", payload);

      // send usernames of everyone in room
      String update = GSON.toJson(update_message);

      for (Session sesh : rooms.get(roomId)) {
        sesh.getRemote().sendString(update); // sending to each session in room
      }

      if (doneWithRoom) {
        userRooms.remove(roomId);
        rooms.remove(roomId);
      }

    } catch (JSONException e) {
      System.out.println("ERROR: invalid json" + e);
    }
  }

  @OnWebSocketError
  public void throwError(Throwable error) {
    error.printStackTrace();
  }


  public static <T> T mostCommon(List<T> list) {
    Map<T, Integer> map = new HashMap<>();

    for (T t : list) {
      Integer val = map.get(t);
      map.put(t, val == null ? 1 : val + 1);
    }

    Map.Entry<T, Integer> max = null;

    for (Map.Entry<T, Integer> e : map.entrySet()) {
      if (max == null || e.getValue() > max.getValue())
        max = e;
    }

    return max.getKey();
  }


  public static boolean roomExists(int code) {
    return userRooms.containsKey(code);
  }

}
