package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class GroupSocket {
  private static final Gson GSON = new Gson();
  // TODO: use hashmap of session queues, can we even do this, i.e. send a message to the server on conncet?
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
  private static final Hashtable<Integer, Queue<Session>> rooms = new Hashtable<>();
  private static final Hashtable<Integer, HashSet<String>> userRooms = new Hashtable<>();
  private static int nextId = 0;

  private static enum MESSAGE_TYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // add session to the sessions queue
    sessions.add(session);

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
    sessions.remove(session);
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    // convert message to JsonObject
    System.out.println(message);
    try {
      JSONObject messageObj = new JSONObject(message);
      String type = messageObj.getJSONObject("message").getString("type");
      int roomId = messageObj.getJSONObject("message").getJSONObject("roomId").getInt("current");

      // prepare update message
      JsonObject update_message = new JsonObject();
      update_message.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

      JsonObject payload = new JsonObject();
      payload.addProperty("senderId", messageObj.getInt("id"));

      System.out.println(type);

      switch (type) {
        case "update_user":
          // add current session to correct room
          if (rooms.get(roomId) == null) {
            rooms.put(roomId, new ConcurrentLinkedQueue<>());
            userRooms.put(roomId, new HashSet<>());
          }
          userRooms.get(roomId).add(messageObj.getJSONObject("message").getString("username"));
          rooms.get(roomId).add(session);

          JSONObject users = new JSONObject();
          users.put("users", userRooms.get(roomId));

          payload.addProperty("type", "update_user");
          payload.addProperty("senderMessage", users.toString());
          break;

        case "start":
          // TODO: some paul shit right here
          JSONObject restaurants = new JSONObject();
          restaurants.put("restaurants", userRooms.get(roomId));

          payload.addProperty("type", "start");
          payload.addProperty("senderMessage", restaurants.toString());
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
    } catch (JSONException e) {
      System.out.println("ERROR: invalid json" + e);
    }
  }

  @OnWebSocketError
  public void throwError(Throwable error) {
    error.printStackTrace();
  }

}
