package edu.brown.cs.fork.database.actions;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.restaurants.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Methods to support naive bayes classification.
 */
public class Training {
  private final Connection conn;
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;
  private static final int NINE = 9;

  /**
   * Establishes database connection.
   * @param conn connection
   */
  public Training(Connection conn) {
    this.conn = conn;
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

      Restaurant rest = new Restaurant(businessId, name, foodType,
          star, numReviews, distance, priceRange);

      LabeledRestaurant labeledRest = new LabeledRestaurant(rest, label);
      results.add(labeledRest);
    }
    prep.close();
    rs.close();
    return results;
  }
}
