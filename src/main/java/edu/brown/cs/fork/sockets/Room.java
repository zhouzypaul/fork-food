package edu.brown.cs.fork.sockets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Room {
  private final Hashtable<String, USER_STATUS> users = new Hashtable<>();
  private final Hashtable<Session, String> sessionToUser = new Hashtable<>();
  private final Hashtable<String, Hashtable<String, Integer>> decisions = new Hashtable<>();
  private HashSet<Restaurant> restaurants;
  private double[] coordinate;
  private boolean started = false;

  private static final Gson GSON = new Gson();

  private enum USER_STATUS {
    WAITING_ROOM,
    SWIPING,
    DONE
  }

  private enum MESSAGE_TYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  //TODO: how to keep track of which users are done

  /**
   * Add a user to the room with the associated socket Session.
   * @param session
   * @param username
   */
  public void addUserSession(Session session, String username, int senderId) {
    // add session and username to sessiontouser
    users.put(username, USER_STATUS.WAITING_ROOM);
    sessionToUser.put(session, username);

    // send message to all rooms
    JsonObject updateMessage = new JsonObject();
    updateMessage.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

    JsonObject payload = new JsonObject();
    payload.addProperty("senderId", senderId);

    JsonObject users = new JsonObject();
    users.add("users", GSON.toJsonTree(users()));

    payload.addProperty("type", "update_user");
    payload.add("senderMessage", users);

    updateMessage.add("payload", payload);

    // send message to everyone in room
    String update = GSON.toJson(updateMessage);

    try {
      for (Session s : sessions()) {
        s.getRemote().sendString(update); // sending to each session in room
      }
    } catch (IOException e) {
      System.out.println("ERROR: " + e);
    }
  }

  /**
   * Remove a user from room.
   * @param session
   * @return true if room is ready to be cleaned up and false otherwise
   */
  public boolean removeUserSession(Session session) {
    // get associated username
    if (sessionToUser.get(session) == null) {
      return false;
    }

    String username = sessionToUser.get(session);

    users.remove(username);
    sessionToUser.remove(session);

    // prep update message
    JsonObject updateMessage = new JsonObject();
    updateMessage.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

    JsonObject payload = new JsonObject();

    if (!started) { // if room not started resend the userlist
      // same code as update_users

      JsonObject users = new JsonObject();
      users.add("users", GSON.toJsonTree(users()));

      payload.addProperty("type", "update_user");
      payload.add("senderMessage", users);

    } else {
      // same code as done
      if (allDone()) {
        try {
          updateMessage.add("payload", getResult());
        } catch (SQLException | NoUserException e) {
          System.out.println("ERROR " + e);
          return false;
        }
      } else {
        return false;
      }
    }

    // send message to everyone in room
    String update = GSON.toJson(updateMessage);
    try {
      for (Session s : sessions()) {
        s.getRemote().sendString(update); // sending to each session in room
      }
    } catch (IOException e) {
      System.out.println("ERROR: " + e);
    }

    return users().isEmpty();
  }

  /**
   * Starts the restaurant swiping process.
   */
  public void startSwiping() {
    // generate and store list of recommended restaurants
    // send start swiping message to all sockets
  }

  /**
   * Records a user swipe for a restaurant.
   * @param session
   * @param resId
   * @param like
   */
  public void swipe(Session session, String resId, int like) {

  }

  /**
   * Marks a user as done with swiping. If all users are done, send a decision.
   * @param username
   */
  public void done(String username) {
    // mark user as done
    // check if all users done, then generate and send decision
  }

  private Set<Session> sessions() {
    return sessionToUser.keySet();
  }

  private Set<String> users() {
    return users.keySet();
  }

  private boolean allDone() {
    for (USER_STATUS status : users.values()) {
      if (status != USER_STATUS.DONE) {
        return false;
      }
    }
    return true;
  }

  /**
   * Gets restaurant result.
   * @throws NoUserException on error
   * @throws SQLException on error
   */
  private JsonObject getResult() throws NoUserException, SQLException {
    JsonObject result = new JsonObject();
    String commonRes = Hub.rankRestaurants(users(), decisions);

    Map<String, String> rest = new HashMap<>();
    try {
      rest = Hub.getRestDB().queryRestByID(commonRes);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
    result.add("result", GSON.toJsonTree(rest));

    JsonObject payload = new JsonObject();
    payload.addProperty("type", "done");
    payload.add("senderMessage", result);

    return payload;
  }
}
