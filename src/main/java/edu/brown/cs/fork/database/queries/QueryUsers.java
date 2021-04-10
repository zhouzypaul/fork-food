package edu.brown.cs.fork.database.queries;

import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.database.Database;
import edu.brown.cs.fork.database.DistanceCalculator;
import edu.brown.cs.fork.database.ForkUtils;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
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
import java.util.List;
import java.util.Map;
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
   * Queries all userIds.
   * @return a list of string representing all user ids
   * @throws SQLException SQLException
   */
  public List<String> queryAllUserIds() throws SQLException {
    String sql = "SELECT login.userId FROM login;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    List<String> results = new ArrayList<>();
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
   * Queries users info based on the prepared statement.
   * @param prep statement to execute
   * @return a list of hashmaps representing users
   * @throws SQLException SQLException
   */
  private List<Map<String, String>> getUsers(PreparedStatement prep)
      throws SQLException {
    List<Map<String, String>> results = new ArrayList<>();
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      Map<String, String> person = new HashMap<>();
      String userId = rs.getString(1);
      String radius = rs.getString(2);
      String prefHighReview = rs.getString(3);
      String prefNumReviews = rs.getString(4);
      String categories = rs.getString(5);
      person.put("userId", userId);
      person.put("radius", radius);
      person.put("prefHighReview", prefHighReview);
      person.put("prefNumReviews", prefNumReviews);
      person.put("categories", categories);
      results.add(person);
    }
    prep.close();
    rs.close();
    return results;
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
   * @param userId user id
   * @param colsToSet columns to insert
   * @param info information for each column
   * @return a boolean indicating whether insertion is successful
   */
  public boolean insertUserPref(String userId, List<String> colsToSet, List<String> info) {
    if (colsToSet.size() != info.size()) {
      return false;
    }

    // build insert string
    // example: (userId, distance, label)
    StringBuilder sb = new StringBuilder();
    sb.append("(userId, ");
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
    val.append("('").append(userId).append("', ");
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
        Map<String, String> rest = Hub.getRestDB().queryRestByID(restIDs.get(i)).get(0);
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
        ForkUtils utils = new ForkUtils();
        while (m.find()) {
          // get an individual category
          String restCategory = Collections.singletonList(m.group()).get(0);
          if (utils.isInCategories(restCategory)) {
            // see if this is a category that a user can select in survey
            prep.setString(3, restCategory);
            prep.executeUpdate();
          }
        }
      }
      return true;
    } catch (SQLException | NullPointerException | IndexOutOfBoundsException e) {
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
      double star = Double.parseDouble(rs.getString(5));
      String priceRange = rs.getString(6);

      // default priceRange is 1 if database field is empty
      int intPriceRange = 1;
      if (!priceRange.isEmpty()) {
        intPriceRange = Integer.parseInt(priceRange);
      }

      int numReviews = Integer.parseInt(rs.getString(SEVEN));
      double distance = Double.parseDouble(rs.getString(EIGHT));
      int label = Integer.parseInt(rs.getString(NINE));

      Restaurant rest =
          new Restaurant(businessId, name, foodType, star, numReviews, distance, intPriceRange);

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
   * Puts user login information in the database.
   * @param userId user id
   * @param pwd password (already encrypted with jwt)
   * @return true if registered successfully, false if otherwise
   */
  public boolean registerUser(String userId, String pwd) {
    String sql = "INSERT INTO login VALUES (?, ?);";
    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, userId);
      prep.setString(2, pwd);
      prep.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: Could not add a new user to the database");
      return false;
    }
  }

  // do we need an update action for login table? -- feature: changing password

  /**
   * Deletes user with userID from database.
   * @param userID id of user to delete
   * @return true if deletion is successful, false if otherwise
   */
  public boolean deleteUser(String userID) {
    String sql = "DELETE FROM login WHERE userId = ?;";
    try {
      PreparedStatement prep = conn.prepareStatement(sql);
      prep.setString(1, userID);
      prep.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: Could not delete user " + userID + " from the database");
      return false;
    }
  }
}
