package edu.brown.cs.fork.database.actions.recent;

import edu.brown.cs.fork.exceptions.NoRestaurantException;
import edu.brown.cs.fork.exceptions.NoUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    return Recent.getStrings(userId, results, sql, this.conn);
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
      if (!Recent.updateMostRecent(restId, recentRests, RECENTSIZE)) {
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
   * @throws NoRestaurantException NoRestaurantException
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
    StringBuilder allRecentRests = Recent.setRecent(restIds);

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
