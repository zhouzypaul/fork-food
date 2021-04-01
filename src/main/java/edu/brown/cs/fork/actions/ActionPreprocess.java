package edu.brown.cs.fork.actions;

import edu.brown.cs.fork.database.Database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Preprocesses the business and review database.
 * DEPRECATED
 */
public class ActionPreprocess implements TriggerAction {
  private final Database restDB = new Database();
  private Connection restConn;

  public ActionPreprocess() { }

  @Override
  public String command() {
    return "prep";
  }

  public List<String> getRestIDs() throws SQLException {
    List<String> results = new ArrayList<>();
    String sql = "SELECT DISTINCT res.business_id FROM yelp_academic_dataset_business as res;";
    PreparedStatement prep = this.restConn.prepareStatement(sql);
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      String id = rs.getString(1);
      results.add(id);
    }
    prep.close();
    rs.close();
    return results;
  }

  public List<Map<String, Double>> getReviews(String restID) throws SQLException, NumberFormatException  {
    List<Map<String, Double>> results = new ArrayList<>();
    String sql = "SELECT reviews.stars, reviews.useful FROM reviews WHERE reviews.business_id = ?;";
    PreparedStatement prep = this.restConn.prepareStatement(sql);
    prep.setString(1, String.valueOf(restID));
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      double stars = Double.parseDouble(rs.getString(1));
      double useful = Double.parseDouble(rs.getString(2));
      Map<String, Double> hMap = new HashMap<>();
      hMap.put("stars", stars);
      hMap.put("useful", useful);
      results.add(hMap);
    }
    prep.close();
    rs.close();
    return results;
  }

  public Double getAvgStars(List<Map<String, Double>> reviews) {
    if (reviews.size() == 0)  {
      return null;
    }

    double sum = 0;
    for (Map<String, Double> hMap : reviews) {
      sum += hMap.get("stars");
    }

    double avg = sum / reviews.size();

    return Math.round(avg * 1000.0) / 1000.0;
  }

  public void addReviewToRest(String id, Double avg_stars, Double num_reviews) throws SQLException {
    String sql1 = "UPDATE yelp_academic_dataset_business SET 'review.avgStars' = ?, 'review.numReviews' = ? ";
    String sql2 = "WHERE yelp_academic_dataset_business.business_id = ?;";
    PreparedStatement prep = this.restConn.prepareStatement(sql1 + sql2);
    prep.setString(1, String.valueOf(avg_stars));
    prep.setString(2, String.valueOf(num_reviews));
    prep.setString(3, String.valueOf(id));
    Integer affectedRows = prep.executeUpdate();
  }

  public void addNumReviewToRest(String id, Integer avg_stars) throws SQLException {
    String sql1 = "UPDATE yelp_academic_dataset_business SET 'numReviews' = ? ";
    String sql2 = "WHERE yelp_academic_dataset_business.business_id = ?;";
    PreparedStatement prep = this.restConn.prepareStatement(sql1 + sql2);
    prep.setString(1, String.valueOf(avg_stars));
    prep.setString(2, String.valueOf(id));
    Integer affectedRows = prep.executeUpdate();
  }

  @Override
  public void run(String[] args) {
    if (args.length != 2) {
      System.out.println("ERROR: Wrong number of arguments");
    }

    String restDBPath = args[1];

    if (!Files.exists(Path.of(restDBPath))) {
      System.out.println("ERROR: Restaurant database doesn't exist");
      return;
    }

    try {
      this.restDB.initDatabase(restDBPath);
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println("Can't initialize database, " + e.getMessage());
      return;
    }

    this.restConn = this.restDB.getConn();

    List<String> allRestIDs = new ArrayList<>();
    try {
      allRestIDs = getRestIDs();
    } catch (SQLException e) {
      System.out.println("Can't get restaurant IDs, " + e.getMessage());
      return;
    }

    int count = 0;
    for (String id : allRestIDs) {
      count += 1;
      try {
        // get all review data (num_stars and useful (how useful a review is voted by other users))
        // get complete info in case we need to change
        List<Map<String, Double>> reviews = getReviews(id);
//        double avgStars = getAvgStars(reviews);
//        double numReviews = reviews.size();
//        addReviewToRest(id, avgStars, numReviews);
        addNumReviewToRest(id, reviews.size());
        System.out.println(count);
      } catch (SQLException e) {
        System.out.println("Can't get / add reviews, " + e.getMessage());
        return;
      }
    }
  }
}
