package edu.brown.cs.fork.database.queries;

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
 * Class containing methods to query from the restaurants database.
 */
public class QueryRestaurants {
  private final Database db = new Database();
  private Connection conn;
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int NINE = 9;

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

  /**
   * SQl query that selects all restaurants (selective attributes).
   * @return sql query string that would return all restaurants
   */
  public String selectRestaurants() {
    return new StringBuilder().append("SELECT res.name, res.stars, res.numReviews, ")
      .append("res.categories, res.'attributes.RestaurantsTakeOut', ")
      .append("res.'attributes.RestaurantsDelivery', res.'attributes.RestaurantsGoodForGroups', ")
      .append("res.'attributes.Alcohol', res.'attributes.GoodForKids' ")
      .append("FROM restaurants as res ").toString();
  }

  /**
   * Get restaurant with given id.
   * @param id id of restaurant of interest
   * @return a PreparedStatement ready to be executed
   * @throws SQLException SQLException
   */
  private PreparedStatement prepGetRestaurantByID(String id) throws SQLException {
    String sql = selectRestaurants() + "WHERE res.business_id = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, id);
    return prep;
  }

  /**
   * Executes prepGetRestaurantByID with given id.
   * @param id id of restaurant of interest
   * @return restaurant with given id
   * @throws SQLException SQLException
   */
  public Map<String, String> queryRestaurantsByID(String id) throws SQLException {
    PreparedStatement prep = prepGetRestaurantByID(id);
    return getRestaurantsWithPrep(prep).get(0);
  }

  /**
   * Queries restaurants based on the given PreparedStatement.
   * @param prep a PreparedStatement that filters all restaurants
   * @return all restaurants according to input prep
   * @throws SQLException SQLException
   */
  public List<Map<String, String>> getRestaurantsWithPrep(PreparedStatement prep)
      throws SQLException {
    List<Map<String, String>> results = new ArrayList<>();
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      Map<String, String> rest = new HashMap<>();
      String name = rs.getString(1);
      String numStars = rs.getString(2);
      String numReviews = rs.getString(3);
      String categories = rs.getString(4);
      String takeout = rs.getString(5);
      String delivery = rs.getString(6);
      String goodForGroups = rs.getString(SEVEN);
      String alcohol = rs.getString(EIGHT);
      String goodForKids = rs.getString(NINE);
      rest.put("name", name);
      rest.put("numStars", numStars);
      rest.put("numReviews", numReviews);
      rest.put("categories", categories);
      rest.put("takeout", takeout);
      rest.put("delivery", delivery);
      rest.put("goodForGroups", goodForGroups);
      rest.put("alcohol", alcohol);
      rest.put("goodForKids", goodForKids);
      results.add(rest);
    }
    prep.close();
    rs.close();
    return results;
  }
}
