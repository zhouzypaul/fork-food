package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GroupSocket {
  private static final Gson GSON = new Gson();
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
    JsonObject payload = new JsonObject();
    payload.addProperty("id", nextId++);
    json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    json.add("payload", payload);
    // make sure to send a unique id!
    // Hint: can use ordinal to get the number position of an enum, MESSAGE_TYPE.CONNECT.ordinal());

    // TODO send message to session
    String message = GSON.toJson(json);
    session.getRemote().sendString(message);
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // TODO remove session from sessions
    sessions.remove(session);
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    // TODO convert message to JsonObject
    GSON.fromJson(message, JsonObject.class);

    JsonObject json = new JsonObject();
    JsonObject payload = new JsonObject();
    payload.addProperty("id", nextId++);
    json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    json.add("payload", payload);
    // make sure to send a unique id!
    // Hint: can use ordinal to get the number position of an enum, MESSAGE_TYPE.CONNECT.ordinal());

    // TODO send message to session
    String message = GSON.toJson(json);
    session.getRemote().sendString(message);

    // TODO build UPDATE message

    // TODO send UPDATE message to each connected client.
  }
}
