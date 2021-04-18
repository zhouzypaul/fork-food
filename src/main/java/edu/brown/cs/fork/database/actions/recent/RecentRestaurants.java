package edu.brown.cs.fork.database.actions.recent;

import edu.brown.cs.fork.exceptions.NoRestaurantException;
import edu.brown.cs.fork.exceptions.NoUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Actions on recent restaurants.
 */
public class RecentRestaurants {
  private static final int RECENTSIZE = 10;
  private final Connection conn;

  /**
   * Establishes database connection.
   * @param conn connection
   */
  public RecentRestaurants(Connection conn) {
    this.conn = conn;
  }

  /**
   * Get a user's most recent top restaurants.
   * @param userId user id
   * @return a list of strings representing business ids
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public List<String> getMostRecentRests(String userId)
    throws SQLException, NoUserException {
    List<String> results = new ArrayList<>();
    String sql = "SELECT recentRests FROM login WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, userId);
    ResultSet rs = prep.executeQuery();
    int count = 0;
    while (rs.next()) {
      count += 1;
      String rests = rs.getString(1);

      String pattern = "[^,\\s][^,]*[^,\\s]*";
      Pattern r = Pattern.compile(pattern);
      Matcher m = r.matcher(rests);
      while (m.find()) {
        String rest = Collections.singletonList(m.group()).get(0);
        results.add(rest);
      }
    }
    if (count == 0) {
      throw new NoUserException("User: " + userId + " doesn\'t exist.");
    }
    prep.close();
    rs.close();
    return results;
  }

  /**
   * Updates a user's most recent top restaurants.
   * @param userId user id
   * @param restId top restaurant's id
   * @return a boolean representing whether the update action is successful
   */
  public boolean updateMostRecentRests(String userId, String restId) {
    try {
      List<String> recentRests = getMostRecentRests(userId);
      if (recentRests.size() < RECENTSIZE) {
        recentRests.add(restId);
      } else if (recentRests.size() == RECENTSIZE) {
        recentRests.remove(0);
        recentRests.add(restId);
      } else {
        System.out.println("ERROR: Too many recent restaurants.");
        return false;
      }

      return setRecentRests(userId, recentRests);
    } catch (SQLException | NoUserException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  /**
   * Deletes a specific recent restaurant id.
   * @param userId user id
   * @param restId rest id to delete
   * @return the restaurant id, -1 if unsuccessful
   */
  public int deleteRecentRest(String userId, String restId)
    throws NoRestaurantException {
    try {
      List<String> recentRests = getMostRecentRests(userId);
      if (!recentRests.contains(restId)) {
        throw new NoRestaurantException("Restaurant with id: " + restId
          + " is not one of " + userId + "'s most recent restaurants.");
      }
      int restIdx = recentRests.indexOf(restId);
      recentRests.remove(restId);
      boolean success = setRecentRests(userId, recentRests);
      if (success) {
        return restIdx;
      } else {
        return -1;
      }
    } catch (SQLException | NoUserException e) {
      System.out.println("ERROR: " + e.getMessage());
      return -1;
    }
  }

  /**
   * Sets the recentRests field in the login table.
   * @param userId user id
   * @param restIds ids to parse into a string for db
   * @return whether the update is successful
   */
  public boolean setRecentRests(String userId, List<String> restIds) {
    StringBuilder allRecentRests = new StringBuilder();
    for (int i = 0; i < restIds.size(); i++) {
      if (i == restIds.size() - 1) {
        allRecentRests.append(restIds.get(i));
      } else {
        allRecentRests.append(restIds.get(i)).append(", ");
      }
    }

    String sql = "UPDATE login SET recentRests = ? WHERE userId = ?;";
    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, allRecentRests.toString());
      prep.setString(2, userId);
      int affectedRows = prep.executeUpdate();
      return affectedRows == 1;
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }
}
