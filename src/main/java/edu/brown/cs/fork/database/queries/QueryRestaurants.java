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
  private static final int TEN = 10;
  private static final int ONEONE = 11;
  private static final int ONETWO = 11;
  private static final double SIXNINE = 69.0;
  private static final double ONEONEONE = 111.0;

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
  private String selectRestaurants() {
    return new StringBuilder().append("SELECT res.name, res.stars, res.numReviews, ")
      .append("res.categories, res.priceRange, res.'attributes.RestaurantsTakeOut', ")
      .append("res.'attributes.RestaurantsDelivery', res.'attributes.RestaurantsGoodForGroups', ")
      .append("res.'attributes.Alcohol', res.'attributes.GoodForKids', res.state, res.city ")
      .append("FROM restaurants as res ").toString();
  }

  /**
   * Executes prepGetRestaurantByID with given id.
   * @param id id of restaurant of interest
   * @return restaurant with given id in a list
   * @throws SQLException SQLException
   */
  public List<Map<String, String>> queryRestByID(String id) throws SQLException {
    String sql = selectRestaurants() + "WHERE res.business_id = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, id);
    return getRestaurantsWithPrep(prep);
  }

  /**
   * Converts miles to latitude, longitude degrees.
   * @param miles miles
   * @return latitude, longitude degrees
   */
  private Double milesToEarth(Double miles) {
    return miles / SIXNINE;
  }

  /**
   * Converts miles to latitude, longitude degrees.
   * @param km kilometers
   * @return latitude, longitude degrees
   */
  private Double kmToEarth(Double km) {
    return km / ONEONEONE;
  }

  /**
   * Prepares statement to query restaurants in a bounding box defined by half and coors.
   * @param half half of the side length of a square bounding box
   * @param lat latitude of the center of the square bounding box
   * @param lon longitude of the center of the square bounding box
   * @return a PreparedStatement
   */
  public List<Map<String, String>> queryRestByRad(Double half, Double lat, Double lon)
      throws SQLException {
    double topLeftLat = lat + milesToEarth(half);
    double topLeftLon = lon - milesToEarth(half);
    double botRightLat = lat - milesToEarth(half);
    double botRightLon = lon + milesToEarth(half);
    String bbox1 = "WHERE res.latitude <= ? AND res.latitude >= ? AND ";
    String bbox2 = "res.longitude >= ? AND res.longitude <= ?";
    String sql = new StringBuilder().append(selectRestaurants())
          .append(bbox1).append(bbox2).toString();
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, String.valueOf(topLeftLat));
    prep.setString(2, String.valueOf(botRightLat));
    prep.setString(3, String.valueOf(topLeftLon));
    prep.setString(4, String.valueOf(botRightLon));
    return getRestaurantsWithPrep(prep);
  }

  /**
   * Queries restaurants based on the given PreparedStatement.
   * @param prep a PreparedStatement that filters all restaurants
   * @return all restaurants according to input prep
   * @throws SQLException SQLException
   */
  private List<Map<String, String>> getRestaurantsWithPrep(PreparedStatement prep)
      throws SQLException {
    List<Map<String, String>> results = new ArrayList<>();
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      Map<String, String> rest = new HashMap<>();
      String name = rs.getString(1);
      String numStars = rs.getString(2);
      String numReviews = rs.getString(3);
      String categories = rs.getString(4);
      String priceRange = rs.getString(5);
      String takeout = rs.getString(6);
      String delivery = rs.getString(SEVEN);
      String goodForGroups = rs.getString(EIGHT);
      String alcohol = rs.getString(NINE);
      String goodForKids = rs.getString(TEN);
      String state = rs.getString(ONEONE);
      String city = rs.getString(ONETWO);
      rest.put("name", name);
      rest.put("numStars", numStars);
      rest.put("numReviews", numReviews);
      rest.put("categories", categories);
      rest.put("priceRange", priceRange);
      rest.put("takeout", takeout);
      rest.put("delivery", delivery);
      rest.put("goodForGroups", goodForGroups);
      rest.put("alcohol", alcohol);
      rest.put("goodForKids", goodForKids);
      rest.put("state", state);
      rest.put("city", city);
      results.add(rest);
    }
    prep.close();
    rs.close();
    return results;
  }
}
