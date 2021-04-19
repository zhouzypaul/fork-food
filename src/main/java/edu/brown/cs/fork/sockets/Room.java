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
  // private HashSet<Restaurant> restaurants;
  // private double[] coordinate;
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

  /**
   * Add a user to the room with the associated socket Session.
   * @param session
   * @param username
   */
  public void addUserSession(Session session, String username, int senderId) {
    // add session and username to sessiontouser
    users.put(username, USER_STATUS.WAITING_ROOM);
    sessionToUser.put(session, username);

    sendMessage("update_user", "users", users());
  }

  /**
   * Remove a user from room.
   * @param session
   * @return true if room is ready to be cleaned up and false otherwise
   */
  public boolean removeUserSession(Session session) {
    // get associated username
    if (!sessionToUser.containsKey(session)) {
      return false;
    }

    String username = sessionToUser.get(session);

    users.remove(username);
    sessionToUser.remove(session);

    if (!started) { // if room not started resend the userlist
      sendMessage("update_users", "users", users());
    } else {
      for (String resId : decisions.keySet()) {
        decisions.get(resId).remove(username);
      }

      if (allDone()) {
        try {
          sendMessage("done", "result", getResult());
        } catch (SQLException | NoUserException e) {
          System.out.println("ERROR " + e);
          return false;
        }
      } else {
        return false;
      }
    }

    return users().isEmpty();
  }

  /**
   * Starts the restaurant swiping process.
   */
  public void startSwiping(double[] coords) {
    this.started = true;

    // generate and store list of recommended restaurants
    List<Restaurant> recommendedRestaurants = new ArrayList<>();

    try {
      recommendedRestaurants = Hub.recommendRestaurants(users(), coords);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }

    for (Restaurant restaurant : recommendedRestaurants) {
      Hashtable<String, Integer> userDecision = new Hashtable<>();
      for (String username : users()) {
        userDecision.put(username, 0);
      }
      decisions.put(restaurant.getId(), userDecision);
    }

    // send start swiping message to all sockets
    sendMessage("start", "restaurants", recommendedRestaurants);
  }

  /**
   * Records a user swipe for a restaurant.
   * @param session
   * @param resId
   * @param like
   */
  public void swipe(Session session, String resId, int like) {
    String username = sessionToUser.get(session);
    decisions.get(resId).replace(username, like);
  }

  /**
   * Marks a user as done with swiping. If all users are done, send a decision.
   * @param username
   */
  public void done(String username) {
    // mark user as done
    users.replace(username, USER_STATUS.DONE);
    // check if all users done, then generate and send decision
    if (allDone()) {
      try {
        sendMessage("done", "result", getResult());
      } catch (SQLException | NoUserException e) {
        System.out.println("ERROR " + e);
      }
    }
  }

  public boolean started() {
    return started;
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
  private Map<String, String> getResult() throws NoUserException, SQLException {
    String resultId = Hub.rankRestaurants(users(), decisions);

    Map<String, String> resultRestaurant = new HashMap<>();
    try {
      resultRestaurant = Hub.getRestDB().queryRestByID(resultId);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
    return resultRestaurant;
  }

  private void sendMessage(String type, String label, Object load) {
    JsonObject updateMessage = new JsonObject();
    updateMessage.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

    JsonObject payload = new JsonObject();

    JsonObject senderMessage = new JsonObject();
    senderMessage.add(label, GSON.toJsonTree(load));

    payload.addProperty("type", type);
    payload.add("senderMessage", senderMessage);

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
}
