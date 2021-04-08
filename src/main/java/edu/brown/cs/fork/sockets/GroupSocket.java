package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class GroupSocket {
  private static final Gson GSON = new Gson();
  // TODO: use hashmap of session queues, can we even do this, i.e. send a message to the server on conncet?
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
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
    JsonObject messageObj = GSON.fromJson(message, JsonObject.class);

    JsonObject json = new JsonObject();
    json.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

    JsonObject payload = new JsonObject();
    payload.addProperty("senderId", nextId++);
    payload.addProperty("senderMessage", "hi");
    json.add("payload", payload);

    // send message to session
    String update = GSON.toJson(json);
    // TODO use some logic here to get from hashmap based on group id
    for (Session sesh : sessions) {
      sesh.getRemote().sendString(message); // sending to each session in queue
    }
  }
}
