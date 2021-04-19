package edu.brown.cs.fork.database.actions.recent;

import edu.brown.cs.fork.exceptions.NoRestaurantException;
import edu.brown.cs.fork.exceptions.NoUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Actions on recent restaurants' timestamps.
 */
public class RecentTimestamps {
  private static final int RECENTSIZE = 10;
  private final Connection conn;

  /**
   * Establishes database connection.
   * @param conn connection to database
   */
  public RecentTimestamps(Connection conn) {
    this.conn = conn;
  }

  /**
   * Gets the most recent timestamps for recent restaurants.
   * @param userId user id
   * @return a list of strings representing timestamps in MM/DD/YYYY format
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public List<String> getMostRecentTimes(String userId)
    throws SQLException, NoUserException {
    List<String> results = new ArrayList<>();
    String sql = "SELECT recentTimes FROM login WHERE userId = ?;";
    return Recent.getStrings(userId, results, sql, this.conn);
  }

  /**
   * Updates a user's most recent top restaurant's timestamp.
   * @param userId user id
   * @param timestamp time restaurant associated with a restaurant
   * @return a boolean representing whether the update action is successful
   */
  public boolean updateMostRecentTimes(String userId, String timestamp) {
    try {
      List<String> recentTimes = getMostRecentTimes(userId);
      if (!Recent.updateMostRecent(timestamp, recentTimes, RECENTSIZE)) {
        return false;
      }

      return setRecentTimestamps(userId, recentTimes);
    } catch (SQLException | NoUserException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  /**
   * Deletes a specific recent restaurant's timestamp.
   * @param userId user id
   * @param idx index of restaurant timestamp to delete
   * @return whether the update is successful
   * @throws NoRestaurantException NoRestaurantException
   */
  public boolean deleteRecentTime(String userId, int idx)
    throws NoRestaurantException {
    try {
      List<String> recentTimes = getMostRecentTimes(userId);
      if (idx >= recentTimes.size()) {
        throw new NoRestaurantException("Timestamp at: " + idx
          + " is not one of " + userId + "'s most recent timestamps.");
      }
      recentTimes.remove(idx);
      return setRecentTimestamps(userId, recentTimes);
    } catch (SQLException | NoUserException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  /**
   * Sets the recentTimes field in the login table.
   * @param userId user id
   * @param timestamps timestamps to parse into a string for db
   * @return whether the update is successful
   */
  public boolean setRecentTimestamps(String userId, List<String> timestamps) {
    StringBuilder allRecentTimes = Recent.setRecent(timestamps);

    String sql = "UPDATE login SET recentTimes = ? WHERE userId = ?;";
    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, allRecentTimes.toString());
      prep.setString(2, userId);
      int affectedRows = prep.executeUpdate();
      return affectedRows == 1;
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }
}
