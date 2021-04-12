package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class GroupSocket {
  private static final Gson GSON = new Gson();
  // TODO: use hashmap of session queues, can we even do this, i.e. send a message to the server on conncet?
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
  private static final HashMap<Integer, Queue<Session>> rooms = new HashMap<>();
  private static final HashMap<Integer, HashSet<String>> userRooms = new HashMap<>();
  private static int nextId = 0;

  private static enum MESSAGE_TYPE {
    CONNECT,
    UPDATE,
    SEND,

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
      int roomId = messageObj.getJSONObject("message").getJSONObject("roomId").getInt("current");

      // add current session to correct room
      if (rooms.get(roomId) == null) {
        rooms.put(roomId, new ConcurrentLinkedQueue<>());
        userRooms.put(roomId, new HashSet<>());
      }
      userRooms.get(roomId).add(messageObj.getJSONObject("message").getString("username"));
      rooms.get(roomId).add(session);

      // create update message
      JsonObject json = new JsonObject();
      json.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

      JsonObject payload = new JsonObject();
      payload.addProperty("senderId", messageObj.getInt("id"));

      JsonObject users = new JsonObject();
      users.add("users", GSON.toJsonTree(userRooms.get(roomId)));

      payload.add("senderMessage", users);
      json.add("payload", payload);

      // send usernames of everyone in room
      String update = json.toString();

      for (Session s : rooms.get(roomId)) {
        s.getRemote().sendString(update); // sending to each session in room
      }
    } catch (JSONException e) {
      System.out.println("ERROR: invalid json" + e);
    }
  }

  @OnWebSocketError
  public void throwError(Throwable error) {
    error.printStackTrace();
  }

  public static boolean roomExists(int code) {
    return userRooms.containsKey(code);
  }
}
