package edu.brown.cs.fork.database.actions;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.database.DistanceCalculator;
import edu.brown.cs.fork.exceptions.NoRestaurantException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Backend methods for user preference, including survey and swiping.
 */
public class UserPref {
  private final Connection conn;
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int NINE = 9;
  private static final int TEN = 10;
  private static final double RAD = 3958.8;

  /**
   * Establishes database connection.
   * @param conn connection
   */
  public UserPref(Connection conn) {
    this.conn = conn;
  }

  /**
   * A string that would be used to insert a user into training table.
   * @return a string to be set with PreparedStatement
   */
  public String insertStr() {
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO training (userId, business_id, foodType, star, ");
    sb.append("priceRange, numReviews, distance, label, timestamp, name) VALUES ");
    sb.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    return sb.toString();
  }

  /**
   * Insert a blank row into training table where every field is empty.
   * @param id id of user
   * @return a boolean indicating if insertion is successful
   */
  public boolean insertBlankRow(String id) {
    String sql = insertStr();
    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, id);
      prep.setString(2, "");
      prep.setString(3, "");
      prep.setString(4, "");
      prep.setString(5, "");
      prep.setString(6, "");
      prep.setString(SEVEN, "");
      prep.setString(EIGHT, "");
      prep.setString(NINE, "");
      prep.setString(TEN, "");
      int affectedRows = prep.executeUpdate();
      return (affectedRows == 1);
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  /**
   * Get user preferences from the training table where business_id is empty.
   * @param id id of user
   * @return a map representing user preferences from preference survey
   * @throws SQLException SQLException
   */
  public Map<String, List<String>> getUserPref(String id) throws SQLException {
    Map<String, List<String>> results = new HashMap<>();
    String sql = "SELECT * FROM training WHERE userId = ? AND business_id = '';";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, id);
    ResultSet rs = prep.executeQuery();

    // all columns in the training table
    List<String> cols = Arrays.asList("userId", "business_id", "foodType", "star",
        "priceRange", "numReviews", "distance", "label", "timestamp");
    // first make an empty Arraylist for every column
    for (String col : cols) {
      List<String> empty = new ArrayList<>();
      results.put(col, empty);
    }

    while (rs.next()) {
      String userId = rs.getString(1);
      String businessId = rs.getString(2);
      String foodType = rs.getString(4);
      String star = rs.getString(5);
      String priceRange = rs.getString(6);
      String numReviews = rs.getString(SEVEN);
      String distance = rs.getString(EIGHT);
      String label = rs.getString(NINE);
      String timestamp = rs.getString(TEN);
      // make a list out of the queried column info
      List<String> vals = Arrays.asList(userId, businessId, foodType, star,
        priceRange, numReviews, distance, label, timestamp);
      // for each <column_name, info> pair, add info in the ArrayList
      // associated with the column name
      for (int i = 0; i < cols.size(); i++) {
        List<String> thisColList = results.get(cols.get(i));
        thisColList.add(vals.get(i));
        results.put(cols.get(i), thisColList);
      }
    }
    prep.close();
    rs.close();
    return results;
  }

  /**
   * Insert rows representing user preferences from preferences survey.
   * Note that business_id must be "".
   * @param colsToSet columns to insert
   * @param info information for each column
   * @return a boolean indicating whether insertion is successful
   */
  public boolean insertUserPref(List<String> colsToSet, List<String> info) {
    if (colsToSet.size() != info.size()) {
      return false;
    }

    // build insert string
    // example: (userId, distance, label)
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (int i = 0; i < info.size(); i++) {
      if (i == info.size() - 1) {
        sb.append(colsToSet.get(i));
      } else {
        sb.append(colsToSet.get(i)).append(", ");
      }
    }
    sb.append(")");
    String insertStr = sb.toString();

    // build value string
    // example: (7, 3.0, 1)
    StringBuilder val = new StringBuilder();
    val.append("(");
    for (int i = 0; i < info.size(); i++) {
      if (i == info.size() - 1) {
        val.append("'").append(info.get(i)).append("'");
      } else {
        val.append("'").append(info.get(i)).append("', ");
      }
    }
    val.append(")");
    String valueStr = val.toString();

    StringBuilder qStr = new StringBuilder();
    qStr.append("INSERT INTO training ").append(insertStr).append(" VALUES ").append(valueStr);
    String sql = qStr.toString();

    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: Can't insert info into table <training>");
      return false;
    }
  }

  /**
   * Delete all user preferences from preferences survey.
   * @param id id of user
   * @return a boolean indicating whether deletion is successful
   */
  public boolean deleteUserPref(String id) {
    String sql = "DELETE FROM training WHERE userId = ? AND business_id = '';";
    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, id);
      prep.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  /**
   * Insert user swiping preferences.
   * @param userId user id
   * @param userLat latitude of user
   * @param userLon longitude of user
   * @param restIDs a list of recommended restaurant ids
   * @param likeOrDislike a list indicating if user likes a restaurant
   * @return a boolean indicating whether insertion is successful
   */
  public boolean insertUserSwipePref(String userId, Double userLat, Double userLon,
                                     List<String> restIDs, List<String> likeOrDislike) {
    String sql = insertStr();
    if (restIDs.size() != likeOrDislike.size()) {
      return false;
    }

    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      for (int i = 0; i < restIDs.size(); i++) {
        Map<String, String> rest = Hub.getRestDB().queryRestByID(restIDs.get(i));
        prep.setString(1, userId);
        prep.setString(2, rest.get("business_id"));
        prep.setString(4, rest.get("numStars"));
        prep.setString(5, rest.get("priceRange"));
        prep.setString(6, rest.get("numReviews"));

        // calculate distance from user to restaurant
        Double restLat = Double.parseDouble(rest.get("latitude"));
        Double restLon = Double.parseDouble(rest.get("longitude"));
        DistanceCalculator calc = new DistanceCalculator();
        List<Double> userCoor = Arrays.asList(userLat, userLon);
        List<Double> restCoor = Arrays.asList(restLat, restLon);
        String dist = String.valueOf(calc.getHaversineDistance(userCoor, restCoor, RAD));

        prep.setString(SEVEN, dist);
        prep.setString(EIGHT, likeOrDislike.get(i));
        prep.setString(NINE, "");
        prep.setString(TEN, rest.get("name"));

        // parse long categories string into individual categories of interest
        String categories = rest.get("categories");
        String pattern = "[^,\\s][^,]*[^,\\s]*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(categories);
        while (m.find()) {
          // get an individual category
          String restCategory = Collections.singletonList(m.group()).get(0);
          if (Hub.isInCategories(restCategory)) {
            // see if this is a category that a user can select in survey
            prep.setString(3, restCategory);
            prep.executeUpdate();
          }
        }
      }
      return true;
    } catch (SQLException | NullPointerException | IndexOutOfBoundsException
      | NoRestaurantException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }
}
