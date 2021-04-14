package edu.brown.cs.fork.database.queries;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.database.Database;
import edu.brown.cs.fork.database.DistanceCalculator;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.exceptions.SQLErrorException;
import edu.brown.cs.fork.restaurants.Restaurant;

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
 * Class containing methods to query from the restaurants database.
 */
public class QueryRestaurants {
  private final Database db = new Database();
  private Connection conn;
  private static final double RAD = 3958.8;
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int NINE = 9;
  private static final int TEN = 10;
  private static final int ONEONE = 11;
  private static final int ONETWO = 12;
  private static final int ONETHREE = 13;
  private static final int ONEFOUR = 14;
  private static final int ONEFIVE = 15;
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

  /**
   * Close connection to restaurants db.
   */
  public void close() {
    try {
      this.db.close();
    } catch (SQLErrorException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Queries all restaurants in the database.
   * @return a list of hashmaps representing restaurants.
   * @throws SQLException SQLException
   */
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
      .append("res.'attributes.Alcohol', res.'attributes.GoodForKids', ")
      .append("res.state, res.city, res.business_id, res.latitude, res.longitude ")
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

  public Restaurant getRestObjByID(String id, Double lat, Double lon)
      throws SQLException, OutOfRangeException {
    List<Map<String, String>> restsInBBox = queryRestByID(id);
    List<Restaurant> results = new ArrayList<>();
    for (Map<String, String> rest : restsInBBox) {
      String businessId = rest.get("business_id");
      String name = rest.get("name");
      double star = Double.parseDouble(rest.get("numStars"));
      int numReviews = Integer.parseInt(rest.get("numReviews"));

      // default priceRange is 1 if database field is empty
      String priceRange = rest.get("priceRange");
      int intPriceRange = 1;
      if (!priceRange.isEmpty()) {
        intPriceRange = Integer.parseInt(priceRange);
      }

      // calculate the distance between the restaurant and current user location
      double restLat = Double.parseDouble(rest.get("latitude"));
      double restLon = Double.parseDouble(rest.get("longitude"));
      List<Double> restCoor = Arrays.asList(restLat, restLon);
      List<Double> userCoor = Arrays.asList(lat, lon);
      DistanceCalculator calc = new DistanceCalculator();
      double dist = calc.getHaversineDistance(userCoor, restCoor, RAD);

      // parse long categories string into individual categories of interest
      String categories = rest.get("categories");
      String pattern = "[^,\\s][^,]*[^,\\s]*";
      Pattern r = Pattern.compile(pattern);
      Matcher m = r.matcher(categories);
      while (m.find()) {
        // get an individual category
        String restCategory = Collections.singletonList(m.group()).get(0);
        // see if this is a category that a user can select in survey
        if (Hub.isInCategories(restCategory)) {
          results.add(
              new Restaurant(businessId, name, restCategory,
                star, numReviews, dist, intPriceRange));
        }
      }
    }
    return results.get(0);
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
   * Query restaurants within specified bounding box and make them into Restaurant objects.
   * @param half half of the side length of the bounding box
   * @param lat latitude of bounding box center
   * @param lon longitude of bounding box center
   * @return a list of Restaurant objects
   * @throws SQLException SQLException
   * @throws NumberFormatException NumberFormatException
   * @throws OutOfRangeException OutOfRangeException
   */
  public List<Restaurant> getTestingRests(Double half, Double lat, Double lon)
      throws SQLException, NumberFormatException, OutOfRangeException {
    List<Map<String, String>> restsInBBox = queryRestByRad(half, lat, lon);
    List<Restaurant> results = new ArrayList<>();
    for (Map<String, String> rest : restsInBBox) {
      String businessId = rest.get("business_id");
      String name = rest.get("name");
      double star = Double.parseDouble(rest.get("numStars"));
      int numReviews = Integer.parseInt(rest.get("numReviews"));

      // default priceRange is 1 if database field is empty
      String priceRange = rest.get("priceRange");
      int intPriceRange = 1;
      if (!priceRange.isEmpty()) {
        intPriceRange = Integer.parseInt(priceRange);
        // enforce price range to be only of 1, 2, or 3
        if (intPriceRange >= 4) {
          intPriceRange = 3;
        }
      }

      // calculate the distance between the restaurant and current user location
      double restLat = Double.parseDouble(rest.get("latitude"));
      double restLon = Double.parseDouble(rest.get("longitude"));
      List<Double> restCoor = Arrays.asList(restLat, restLon);
      List<Double> userCoor = Arrays.asList(lat, lon);
      DistanceCalculator calc = new DistanceCalculator();
      double dist = calc.getHaversineDistance(userCoor, restCoor, RAD);

      // parse long categories string into individual categories of interest
      String categories = rest.get("categories");
      String pattern = "[^,\\s][^,]*[^,\\s]*";
      Pattern r = Pattern.compile(pattern);
      Matcher m = r.matcher(categories);
      while (m.find()) {
        // get an individual category
        String restCategory = Collections.singletonList(m.group()).get(0);
        // see if this is a category that a user can select in survey
        if (Hub.isInCategories(restCategory)) {
//          System.out.println(star);
//          System.out.println(numReviews);
//          System.out.println(dist);
//          System.out.println(intPriceRange);
//          System.out.println();
          results.add(
              new Restaurant(businessId, name, restCategory,
                star, numReviews, dist, intPriceRange));
        }
      }
    }
    return results;
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
      String businessId = rs.getString(ONETHREE);
      String latitude = rs.getString(ONEFOUR);
      String longitude = rs.getString(ONEFIVE);
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
      rest.put("business_id", businessId);
      rest.put("latitude", latitude);
      rest.put("longitude", longitude);
      results.add(rest);
    }
    prep.close();
    rs.close();
    return results;
  }
}
