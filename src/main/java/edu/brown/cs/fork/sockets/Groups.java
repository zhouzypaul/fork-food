package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Socket for handling hosting, joining a room and swiping.
 */
@WebSocket
public class Groups {

  private static final Gson GSON = new Gson();
  private static final Hashtable<Integer, Room> ROOMS = new Hashtable<>();
  private static final Hashtable<Session, Integer> SESSION_ROOM = new Hashtable<>();

  /**
   * Message type of a session.
   */
  private enum MESSAGETYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  private static int nextId = 0;

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
   * Closes a socket.
   * @param session socket session
   * @param statusCode status code of session
   * @param reason reason of session
   */
  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    int roomId = SESSION_ROOM.get(session); // get room ID

    // cleanup
    if (ROOMS.get(roomId).removeUserSession(session)) {
      ROOMS.remove(roomId);
      SESSION_ROOM.remove(session);
    }
  }

  /**
   * Socket session message.
   * @param session socket session
   * @param message message
   * @throws IOException IOException
   */
  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    try {
      // extract info from incoming message
      JSONObject messageObj = new JSONObject(message);
      int roomId = messageObj.getJSONObject("message").getInt("roomId");
      String username = messageObj.getJSONObject("message").getString("username");

      // create a new room
      if (ROOMS.get(roomId) == null) {
        ROOMS.put(roomId, new Room());
      }
      Room room = ROOMS.get(roomId);

      String type = messageObj.getJSONObject("message").getString("type");
      switch (type) { // check what type of message
        case "update_user": // updates waiting room with new users
          boolean isHost = messageObj.getJSONObject("message").getBoolean("host");
          room.addUserSession(session, username, isHost);
          SESSION_ROOM.put(session, roomId);
          break;

        case "start": // starts swiping process
          double lat = messageObj.getJSONObject("message").getDouble("lat");
          double lon = messageObj.getJSONObject("message").getDouble("lon");
          room.startSwiping(new double[]{lat, lon});
          break;

        case "swipe": // registers a user swipe
          String resId = messageObj.getJSONObject("message").getString("resId");
          int like = messageObj.getJSONObject("message").getInt("like");
          room.swipe(session, resId, like);
          break;

        case "done": // marks a user as done swiping
          room.done(username);
          break;

        default:
          System.out.println("Unrecognized message type");
          break;
      }
    } catch (JSONException e) {
      System.out.println("ERROR " + e);
    }

  }

  /**
   * Throws an error.
   * @param error error
   */
  @OnWebSocketError
  public void throwError(Throwable error) {
    if (error.getMessage() != null) {
      System.out.println(error.getMessage());
    }
  }

  /**
   * Checks if a code is available to create a new room.
   *
   * @param roomId - the room code to check
   * @return whether the room exists
   */
  public static boolean exists(int roomId) {
    return ROOMS.containsKey(roomId);
  }

  /**
   * Checks if the room with given id has started.
   * @param roomId room code to check
   * @return whether the room has started
   */
  public static boolean started(int roomId) {
    return exists(roomId) && ROOMS.get(roomId).started();
  }
}
