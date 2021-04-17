package edu.brown.cs.fork.database.queries;

import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.database.Database;
import edu.brown.cs.fork.database.DistanceCalculator;
import edu.brown.cs.fork.exceptions.NoRestaurantException;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.exceptions.SQLErrorException;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.restaurants.Restaurant;
import edu.brown.cs.fork.users.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing methods to query from the user database.
 */
public class QueryUsers {
  private Database db = new Database();
  private Connection conn;
  private static final Gson GSON = new Gson();
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int NINE = 9;
  private static final int TEN = 10;
  private static final double RAD = 3958.8;
  private static final int RECENTSIZE = 10;

  /**
   * Constructor.
   */
  public QueryUsers() {  }

  /**
   * Initializes the database connection.
   * @param dbPath path to database
   */
  public void initUsers(String dbPath) {
    try {
      db.initDatabase(dbPath);
      this.conn = db.getConn();
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Returns whether this database is connected.
   * @return if the user database is connected
   */
  public boolean isConnected() {
    return db.isConnected();
  }

  /**
   * Close connection to users db.
   */
  public void close() {
    try {
      this.db.close();
    } catch (SQLErrorException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Queries all userIds.
   * @return a list of string representing all user ids
   * @throws SQLException SQLException
   */
  public Set<String> queryAllUserIds() throws SQLException {
    String sql = "SELECT login.userId FROM login;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    Set<String> results = new HashSet<>();
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      String userId = rs.getString(1);
      results.add(userId);
    }
    prep.close();
    rs.close();
    return results;
  }

  /**
   * Get user's gottenWay parameter.
   * @param userId user id
   * @return user's gottenWay parameter
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public double getUserGottenWay(String userId)
      throws SQLException, NoUserException {
    String sql = "SELECT gottenWay FROM login WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, userId);
    double gottenWay = -1.0;
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      gottenWay = rs.getDouble(1);
    }
    prep.close();
    rs.close();
    if (gottenWay == -1) {
      throw new NoUserException("User " + userId + " not found.");
    }
    return gottenWay;
  }

  /**
   * Updates user's gottenWay parameter.
   * @param userId user id
   * @param gottenWay user's gottenWay parameter
   * @return whether the update is successful
   * @throws SQLException SQLException
   */
  public boolean updateUserGottenWay(String userId, double gottenWay) throws SQLException {
    String sql = "UPDATE login SET gottenWay = ? WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setDouble(1, gottenWay);
    prep.setString(2, userId);
    int affectedRows = prep.executeUpdate();
    return (affectedRows == 1);
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

  /**
   * Queries all preferences of a user and make them into a list of
   * LabeledRestaurant objects for naive bayes training.
   * @param userId user id
   * @return a list of LabeledRestaurants
   * @throws SQLException SQLException
   * @throws NumberFormatException NumberFormatException
   * @throws OutOfRangeException OutOfRangeException
   */
  public List<LabeledRestaurant> trainRowsToLabeledRests(String userId)
      throws SQLException, NumberFormatException, OutOfRangeException {
    List<LabeledRestaurant> results = new ArrayList<>();
    String sql = "SELECT * FROM training WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, userId);
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      String businessId = rs.getString(2);
      String name = rs.getString(3);
      String foodType = rs.getString(4);

      // default star is 2.5 if database field is empty
      String starString = rs.getString(5);
      double star = Hub.DEFAULT_STAR;
      if (!starString.isEmpty()) {
        star = Double.parseDouble(starString);
      }

      // default priceRange is 1 if database field is empty
      String priceRangeString = rs.getString(6);
      int priceRange = Hub.DEFAULT_PRICE_RANGE;
      if (!priceRangeString.isEmpty()) {
        priceRange = Integer.parseInt(priceRangeString);
      }

      // default num reviews is 0 if the database field is empty
      String numReviewsString = rs.getString(SEVEN);
      int numReviews = Hub.DEFAULT_NUM_REVIEWS;
      if (!numReviewsString.isEmpty()) {
        numReviews = Integer.parseInt(numReviewsString);
      }

      // default distance is 10 if the database field is empty
      String distanceString = rs.getString(EIGHT);
      double distance = Hub.DEFAULT_DISTANCE;
      if (!distanceString.isEmpty()) {
        distance = Double.parseDouble(distanceString);
      }

      // default label is 1 if the database field is empty.
      String labelString = rs.getString(NINE);
      int label = Hub.DEFAULT_LABEL;
      if (!labelString.isEmpty()) {
        label = Integer.parseInt(labelString);
      }

      Restaurant rest =
          new Restaurant(businessId, name, foodType, star, numReviews, distance, priceRange);

      LabeledRestaurant labeledRest = new LabeledRestaurant(rest, label);
      results.add(labeledRest);
    }
    prep.close();
    rs.close();
    return results;
  }

  /**
   * Queries all preferences of a user and make rows into a Person object.
   * @param userId user id
   * @return a Person object
   * @throws OutOfRangeException OutOfRangeException
   * @throws SQLException SQLException
   */
  public Person trainRowsToPerson(String userId) throws OutOfRangeException, SQLException {
    List<LabeledRestaurant> rests = trainRowsToLabeledRests(userId);
    return new Person(userId, rests, 1.0);
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
   * Updates a user's most recent top restaurants.
   * @param userId user id
   * @param restId top restaurant's id
   * @return a boolean representing whether the update action is successful
   */
  public boolean updateMostRecentRests(String userId, String restId) {
    try {
      List<String> recentRests = getMostRecentRests(userId);
      if (!recentRests.contains(restId)) {
        if (recentRests.size() < RECENTSIZE) {
          recentRests.add(restId);
        } else if (recentRests.size() == RECENTSIZE) {
          recentRests.remove(0);
          recentRests.add(restId);
        } else {
          System.out.println("ERROR: Too many recent restaurants.");
          return false;
        }
      }

      return setRecentRests(userId, recentRests);
    } catch (SQLException | NoUserException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
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

  /**
   * Puts user login information in the database.
   * @param userId user id
   * @param pwd password (already encrypted with jwt)
   * @return true if registered successfully, false if otherwise
   */
  public boolean registerUser(String userId, String pwd) {
    String sql = "INSERT INTO login VALUES (?, ?, 1, ?, ?);";
    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, userId);
      prep.setString(2, pwd);
      prep.setString(3, "");
      prep.setString(4, "");
      prep.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: Could not add a new user to the database");
      return false;
    }
  }

  /**
   * Get user password.
   * @param userId user id
   * @return password of user with userId
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public String getPwd(String userId) throws SQLException, NoUserException {
    String result = "";
    String sql = "SELECT password FROM login WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, userId);
    ResultSet rs = prep.executeQuery();
    int count = 0;
    while (rs.next()) {
      count += 1;
      result = rs.getString(1);
    }
    if (count == 0) {
      throw new NoUserException("User: " + userId + " doesn\'t exist.");
    }
    prep.close();
    rs.close();
    return result;
  }

  /**
   * Changes user's password.
   * @param userId id of user to update
   * @param newPwd new password
   * @return a boolean indicating whether the update is successful
   * @throws NoUserException if the user can't be found in the database
   */
  public boolean changePwd(String userId, String newPwd)
      throws NoUserException {
    String sql = "UPDATE login SET password = ? WHERE userId = ?;";
    PreparedStatement prep = null;
    try {
      prep = this.conn.prepareStatement(sql);
      prep.setString(1, newPwd);
      prep.setString(2, userId);
      int affectedRows = prep.executeUpdate();
      if (affectedRows == 0) {
        throw new NoUserException("User: " + userId + " doesn\'t exist.");
      }
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  /**
   * Deletes user with userID from database.
   * @param userID id of user to delete
   * @return true if deletion is successful, false if otherwise
   */
  public boolean deleteUser(String userID) {
    String sql = "DELETE FROM login WHERE userId = ?;";
    String sql2 = "DELETE FROM training WHERE userId = ?;";
    try {
      PreparedStatement prep = conn.prepareStatement(sql);
      PreparedStatement prep2 = conn.prepareStatement(sql2);
      prep.setString(1, userID);
      prep2.setString(1, userID);
      prep.executeUpdate();
      prep2.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: Could not delete user " + userID + " from the database");
      return false;
    }
  }
}
