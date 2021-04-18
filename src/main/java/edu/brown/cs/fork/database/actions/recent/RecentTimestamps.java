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
 * Actions on recent restaurants' timestamps.
 */
public class RecentTimestamps {
  private static final int RECENTSIZE = 10;
  private final Connection conn;

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
   * Updates a user's most recent top restaurant's timestamp.
   * @param userId user id
   * @param timestamp time restaurant associated with a restaurant
   * @return a boolean representing whether the update action is successful
   */
  public boolean updateMostRecentTimes(String userId, String timestamp) {
    try {
      List<String> recentTimes = getMostRecentTimes(userId);
      if (recentTimes.size() < RECENTSIZE) {
        recentTimes.add(timestamp);
      } else if (recentTimes.size() == RECENTSIZE) {
        recentTimes.remove(0);
        recentTimes.add(timestamp);
      } else {
        System.out.println("ERROR: Too many recent restaurants.");
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
    StringBuilder allRecentTimes = new StringBuilder();
    for (int i = 0; i < timestamps.size(); i++) {
      if (i == timestamps.size() - 1) {
        allRecentTimes.append(timestamps.get(i));
      } else {
        allRecentTimes.append(timestamps.get(i)).append(", ");
      }
    }

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
