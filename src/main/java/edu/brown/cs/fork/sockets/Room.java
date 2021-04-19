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

/**
 * Class representing Rooms.
 */
public class Room {
  // room data
  private double[] coordinate; // of host
  private boolean started = false; // has swiping started

  // users and their statuses
  private final Hashtable<String, USER_STATUS> users = new Hashtable<>();
  private final Hashtable<Session, String> sessionToUser = new Hashtable<>();

  private enum USER_STATUS {
    WAITING_ROOM,
    SWIPING,
    DONE
  }

  // keep track of user swipes
  private final Hashtable<String, Hashtable<String, Integer>> decisions = new Hashtable<>();
  private final Map<String, Map<String, List<String>>> swipes = new HashMap<>();

  // for socket utility
  private static final Gson GSON = new Gson();

  private enum MESSAGE_TYPE {
    CONNECT,
    UPDATE,
    SEND
  }

  /**
   * Add a user to the room with the associated socket Session.
   *
   * @param session  - the session to add to the room
   * @param username - the username of the user to add
   */
  public void addUserSession(Session session, String username) {
    // add session and username
    users.put(username, USER_STATUS.WAITING_ROOM);
    sessionToUser.put(session, username);

    // send update message
    sendMessage("update_user", "users", users());
  }

  /**
   * Remove a user from room.
   *
   * @param session - the session to remove from the room
   * @return true if room is ready to be cleaned up and false otherwise
   */
  public boolean removeUserSession(Session session) {
    // get associated username
    if (!sessionToUser.containsKey(session)) {
      return false;
    }
    String username = sessionToUser.get(session);

    // remove user
    users.remove(username);
    sessionToUser.remove(session);

    // has the room started swiping yet?
    if (!started) { // still in waiting room
      sendMessage("update_users", "users", users());
    } else { // swiping has started
      // remove the users swipes
      for (String resId : decisions.keySet()) {
        decisions.get(resId).remove(username);
      }

      // check if we're ready to send a decision
      if (allDone()) {
        try {
          sendMessage("done", "result", getResult());
        } catch (SQLException | NoUserException e) {
          System.out.println("ERROR " + e);
          return false;
        }
      }
    }

    // return if the room is empty
    return users().isEmpty();
  }

  /**
   * Starts the swiping process by querying a list of recommended restaurants.
   *
   * @param coords - coordinates of where to start searching from
   */
  public void startSwiping(double[] coords) {
    this.started = true;
    this.coordinate = coords;

    // generate and store list of recommended restaurants
    List<Restaurant> recommendedRestaurants = new ArrayList<>();

    try {
      recommendedRestaurants = Hub.recommendRestaurants(users(), coords);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }

    // initialize a hashtable to store user swipes
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
   *
   * @param session current session
   * @param resId restaurant id
   * @param like 1 for like 0 for not like
   */
  public void swipe(Session session, String resId, int like) {
    String username = sessionToUser.get(session);
    decisions.get(resId).replace(username, like);

    // add to swipe preferences for user
    addToSwipePref(username, resId, Integer.toString(like));
  }

  /**
   * Marks a user as done with swiping. If all users are done, send a decision.
   *
   * @param username - user that is done
   */
  public void done(String username) {
    // mark user as done
    users.replace(username, USER_STATUS.DONE);

    // send user swipe preferences to database
    if (!addToDatabase(username, coordinate)) {
      System.out.println("ERROR: Can't add user swiping preferences.");
      return;
    }

    // check if all users done, then generate and send decision
    if (allDone()) {
      try {
        sendMessage("done", "result", getResult());
      } catch (SQLException | NoUserException e) {
        System.out.println("ERROR " + e);
      }
    }
  }

  /**
   * Returns if the room has started the swiping process.
   *
   * @return true if the room has started and false otherwise
   */
  public boolean started() {
    return started;
  }

  /**
   * Gets all sessions.
   *
   * @return all sessions in the room
   */
  private Set<Session> sessions() {
    return sessionToUser.keySet();
  }

  /**
   * Gets all usernames.
   *
   * @return all usernames in the room
   */
  private Set<String> users() {
    return users.keySet();
  }

  /**
   * Checks if all users are done swiping.
   *
   * @return true if all users are done, and false otherwise
   */
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
   *
   * @throws NoUserException on error
   * @throws SQLException    on error
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

  /**
   * Helper to put together a socket update message.
   *
   * @param type  - the type of message "update_user", "start", "swipe", or "done"
   * @param label - the label of the payload
   * @param load  - the load itself to send
   */
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

  /**
   * Add to preferences to swipes.
   *
   * @param user     username
   * @param resId    restaurant id
   * @param decision whether user likes the restaurant
   */
  private void addToSwipePref(String user, String resId, String decision) {
    if (!swipes.containsKey(user)) {
      Map<String, List<String>> prefs = new HashMap<>();
      prefs.put("business_id", new ArrayList<>());
      prefs.put("decisions", new ArrayList<>());
      swipes.put(user, prefs);
    }
    List<String> restIds = swipes.get(user).get("business_id");
    List<String> decisions = swipes.get(user).get("decisions");
    restIds.add(resId);
    decisions.add(decision);
  }

  /**
   * Add all swipe decisions to database.
   *
   * @param username user
   * @param coor     host coordinate
   * @return whether the action is successful
   */
  private boolean addToDatabase(String username, double[] coor) {
    List<String> prefRestaurants = swipes.get(username).get("business_id");
    List<String> prefDecisions = swipes.get(username).get("decisions");
    return Hub.getUserDB().insertUserSwipePref(username, coor[0],
        coor[1], prefRestaurants, prefDecisions);
  }
}
