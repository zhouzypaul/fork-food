package edu.brown.cs.fork.database.queries;

import com.google.gson.Gson;
import edu.brown.cs.fork.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing methods to query from the user database.
 */
public class QueryUsers {
  private Database db = new Database();
  private Connection conn;
  private static final Gson GSON = new Gson();

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
   * Queries all users.
   * @return a list of hashmaps representing users
   * @throws SQLException SQLException
   */
  public List<Map<String, String>> queryAllUsers() throws SQLException {
    String sql = "SELECT * FROM user;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    return getUsers(prep);
  }

  /**
   * Queries a user info by its userId.
   * @param id id of user
   * @return a list of hashmaps representing users
   * @throws SQLException SQLException
   */
  public List<Map<String, String>> queryUserByID(String id) throws SQLException {
    String sql = "SELECT * FROM user WHERE user.userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, id);
    return getUsers(prep);
  }

  /**
   * Queries all userIds.
   * @return a list of string representing all user ids
   * @throws SQLException SQLException
   */
  public List<String> queryAllUserIds() throws SQLException {
    String sql = "SELECT user.userId FROM user;";
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

  public boolean upsertUserInfo(String userId, List<String> colsToUpdate, List<String> info) {
    if (colsToUpdate.size() != info.size()) {
      return false;
    }
    // build update string
    // example: pref_high_review='1000', pref_num_reviews='2000'
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < info.size(); i++) {
      if (i == info.size() - 1) {
        sb.append(colsToUpdate.get(i)).append("='").append(info.get(i)).append("'");
      } else {
        sb.append(colsToUpdate.get(i)).append("='").append(info.get(i)).append("', ");
      }
    }
    String setStr = sb.toString();

    StringBuilder qStr = new StringBuilder();
    qStr.append("UPDATE training SET ").append(setStr)
      .append(" WHERE userId = ? AND business_id = '';");
    String sql = qStr.toString();

    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, userId);
      int affectedRow = prep.executeUpdate();
      if (affectedRow == 0) {
        return insertUserPref(userId, colsToUpdate, info);
      } else {
        return true;
      }
    } catch (SQLException e) {
      System.out.println("ERROR: Can't update user info with userId: " + userId);
      return false;
    }
  }

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

  // this will be for naive bayes, we should return a Person here
  // for now putting in List<Map<String, String>> so things won't break
  public List<Map<String, String>> getUserInfo(String userId) throws SQLException {
    String sql = "SELECT * FROM training WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, userId);
    ResultSet rs = prep.executeQuery();
    List<Map<String, String>> results = new ArrayList<>();
    while (rs.next()) {
      Map<String, String> rest = new HashMap<>();
      String businessId = rs.getString(2);
      String foodType = rs.getString(3);
      String star = rs.getString(4);
      String priceRange = rs.getString(5);
      String numReview = rs.getString(6);
      String distance = rs.getString(7);
      String label = rs.getString(8);
      String timestamp = rs.getString(9);
      rest.put("businessId", businessId);
      // more put statements here
      results.add(rest);
    }
    prep.close();
    rs.close();
    return results;
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
