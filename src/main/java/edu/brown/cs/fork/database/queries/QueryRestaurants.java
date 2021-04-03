package edu.brown.cs.fork.database.queries;

import com.google.gson.Gson;
import edu.brown.cs.fork.database.Database;
import edu.brown.cs.fork.exceptions.SQLErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing methods to query from the restaurants database.
 */
public class QueryRestaurants {
  private final Database db = new Database();
  private Connection conn;
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public QueryRestaurants() { }

  /**
   * Initializes the database connection.
   * @param dbPath path to database
   */
  public void initRestaurants(String dbPath) {
    try {
      db.initDatabase(dbPath);
      this.conn = db.getConn();
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Getter for whether the database is connected.
   * @return if the restaurant database is connected
   */
  public boolean isConnected() {
    return db.isConnected();
  }

  public List<Map<String, String>> getAllRestaurants() throws SQLException {
    List<Map<String, String>> results = new ArrayList<>();
    String sql = "SELECT res.name, res.state FROM restaurants as res;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      String resName = rs.getString(1);
      String state = rs.getString(2);
      Map<String, String> hMap = new HashMap<>();
      hMap.put("name", resName);
      hMap.put("state", state);
      results.add(hMap);
    }
    prep.close();
    rs.close();
    return results;
  }

  // function that parses a categories string into a list of strings

  /**
   * Queries a restaurant by its ID.
   * @param id id of restaurant
   * @return a Map representing the restaurant
   * @throws SQLException SQLException
   * @throws SQLErrorException SQLErrorException
   */
  public Map<String, String> getRestaurantByID(String id) throws SQLException, SQLErrorException {
    Map<String, String> rest = new HashMap<>();
    String sql = "SELECT " +
      "res.name, res.stars, res.numReviews, res.categories, res.'attributes.RestaurantsTakeOut', " +
      "res.'attributes.RestaurantsDelivery', res.'attributes.RestaurantsGoodForGroups', " +
      "res.'attributes.Alcohol', res.'attributes.GoodForKids' " +
      "FROM restaurants as res WHERE res.business_id = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, id);
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      String name = rs.getString(1);
      String numStars = rs.getString(2);
      String numReviews = rs.getString(3);
      String categories = rs.getString(4);
      String takeout = rs.getString(5);
      String delivery = rs.getString(6);
      String goodForGroups = rs.getString(7);
      String alcohol = rs.getString(8);
      String goodForKids = rs.getString(9);
      rest.put("name", name);
      rest.put("numStars", numStars);
      rest.put("numReviews", numReviews);
      rest.put("categories", categories);
      rest.put("takeout", takeout);
      rest.put("delivery", delivery);
      rest.put("goodForGroups", goodForGroups);
      rest.put("alcohol", alcohol);
      rest.put("goodForKids", goodForKids);
    }
    prep.close();
    rs.close();
    return rest;
  }
}
