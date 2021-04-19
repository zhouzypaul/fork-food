package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.eclipse.jetty.websocket.api.CloseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class Groups {

  private static final Gson GSON = new Gson();
  private static final Hashtable<Integer, Room> ROOMS = new Hashtable<>();
  private static final Hashtable<Session, Integer> SESSION_ROOM = new Hashtable<>();

  private enum MESSAGE_TYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  private static int nextId = 0;

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // build CONNECT message
    JsonObject json = new JsonObject();
    json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());

    JsonObject payload = new JsonObject();
    payload.addProperty("id", nextId++);

    json.add("payload", payload);

    String message = GSON.toJson(json);

    session.getRemote().sendString(message);
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    int roomId = SESSION_ROOM.get(session); // get room ID

    // cleanup
    if (ROOMS.get(roomId).removeUserSession(session)) {
      ROOMS.remove(roomId);
      SESSION_ROOM.remove(session);
    }
  }

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
          room.addUserSession(session, username);
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

  @OnWebSocketError
  public void throwError(Throwable error) {
    error.printStackTrace();
  }

  /**
   * Checks if a code is available to create a new room.
   *
   * @param roomId - the room code to check
   * @return is the room valid
   */
  public static boolean valid(int roomId) {
    if (ROOMS.containsKey(roomId)) {
      return !ROOMS.get(roomId).started();
    }
    return false;
  }
}
