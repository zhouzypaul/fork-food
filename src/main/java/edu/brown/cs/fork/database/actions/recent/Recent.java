package edu.brown.cs.fork.database.actions.recent;

import edu.brown.cs.fork.exceptions.NoUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for RecentRestaurants and RecentTimestamps.
 */
public final class Recent {

  /**
   * Constructor.
   */
  private Recent() {  }

  /**
   * Returns a list of results.
   * @param userId user id
   * @param results results to modify
   * @param sql sql string
   * @param conn connection to database
   * @return list of string representing recent results
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public static List<String> getStrings(String userId, List<String> results, String sql,
      Connection conn) throws SQLException, NoUserException {
    PreparedStatement prep = conn.prepareStatement(sql);
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
      throw new NoUserException("User: " + userId + " doesn't exist.");
    }
    prep.close();
    rs.close();
    return results;
  }

  /**
   * Update most recent data.
   * @param datum datum to add
   * @param recentData recent data
   * @param recentSize upper limit of recent data
   * @return whether the update action is successful
   */
  static boolean updateMostRecent(String datum, List<String> recentData, int recentSize) {
    if (recentData.size() < recentSize) {
      recentData.add(datum);
    } else if (recentData.size() == recentSize) {
      recentData.remove(0);
      recentData.add(datum);
    } else {
      System.out.println("ERROR: Too many recent restaurants.");
      return false;
    }
    return true;
  }

  /**
   * Sets most recent data.
   * @param recentData recent data to modify
   * @return a StringBuilder for classes to perform sql query with
   */
  static StringBuilder setRecent(List<String> recentData) {
    StringBuilder allRecentRests = new StringBuilder();
    for (int i = 0; i < recentData.size(); i++) {
      if (i == recentData.size() - 1) {
        allRecentRests.append(recentData.get(i));
      } else {
        allRecentRests.append(recentData.get(i)).append(", ");
      }
    }
    return allRecentRests;
  }
}
