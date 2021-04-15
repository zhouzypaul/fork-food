package edu.brown.cs.fork;

import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.database.queries.QueryUsers;
import edu.brown.cs.fork.exceptions.NoTestDataException;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.recommendation.NaiveBayesClassifier;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.restaurants.Restaurant;
import edu.brown.cs.fork.users.Group;
import edu.brown.cs.fork.users.Person;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A singleton class containing REPL and databases.
 */
public class Hub {
  private static final QueryUsers USER_DB = new QueryUsers();
  private static final QueryRestaurants REST_DB = new QueryRestaurants();

  private static final int NUM_RECOMMEND = 10;
  private static final int POSITIVE_CLASS = 1;
  private static final double DEFAULT_RADIUS = 10;
  private static final double GOTTEN_WAY_PRESERVE_RATIO = 0.7;

  // Food categories
  private static final List<String> CATEGORIES = Arrays.asList(
      "Burgers", "Chinese", "Pizza", "Italian", "Sushi Bars", "Indian", "Vietnamese", "Steakhouses",
      "Breakfast & Brunch", "Desserts", "Coffee & Tea", "Greek", "Middle Eastern", "Vegan",
      "Mexican", "Thai", "American", "Salad", "Barbeque", "Seafood");


  /**
   * Constructor.
   */
  public Hub() { }

  /**
   * Returns the users database query client.
   * @return the users database query client
   */
  public static QueryUsers getUserDB() {
    return USER_DB;
  }

  /**
   * Returns the restaurants database query client.
   * @return the restaurants database query client
   */
  public static QueryRestaurants getRestDB() {
    return REST_DB;
  }

  /**
   * Getter for CATEGORIES.
   * @return all categories the recommendation algorithm is interested in
   */
  public static List<String> getCategories() {
    return CATEGORIES;
  }

  /**
   * Return a list of categories which is the union of allRestCategories and CATEGORIES.
   * @param allRestCategories a restaurant's list of food categories
   * @return union of allRestCategories and CATEGORIES
   */
  public static List<String> getOverlap(List<String> allRestCategories) {
    List<String> overlap = new ArrayList<>();
    for (String str : allRestCategories) {
      if (CATEGORIES.contains(str)) {
        overlap.add(str);
      }
    }
    return overlap;
  }

  /**
   * Checks if anyCategory is in our list of categories of interest.
   * @param anyCategory a category
   * @return whether anyCategory is in CATEGORIES
   */
  public static boolean isInCategories(String anyCategory) {
    return CATEGORIES.contains(anyCategory);
  }

  /**
   * recommend a list of restaurants for a group of users.
   * @param userIds an array of user IDs, one for each user in a group
   * @param hostCoordinate the coordinate of the host, in the form of (latitude, longitude)
   * @return a list of recommended restaurants.
   * @throws OutOfRangeException when the Restaurant constructor takes in illegal args, or the
   *                            coordinates passed in is illegal.
   * @throws SQLException when SQL errors
   * @throws NoTestDataException when there are no testing data available
   * @throws NoUserException when no user ids are passed in
   */
  public static List<Restaurant> recommendRestaurants(Set<String> userIds, double[] hostCoordinate)
          throws OutOfRangeException, SQLException, NoTestDataException, NoUserException {
    if (userIds.size() <= 0) {
      throw new NoUserException("no users passed in");
    }
    if (hostCoordinate.length != 2) {
      throw new OutOfRangeException("coordinate has to be an array of length 2");
    }
    // getting the list of users
    List<Person> userList = new LinkedList<>();
    for (String id : userIds) {
      Person p = USER_DB.trainRowsToPerson(id);
      userList.add(p);
    }
    Group group = new Group(userList);
    List<LabeledRestaurant> groupPreference = group.getCollectivePreference();
    // get radius by averaging
    double radius = 0;
    for (String id : userIds) {
      String r = USER_DB.getUserPref(id).get("distance").get(0);
      radius += Double.parseDouble(r);
    }
    radius = radius / userIds.size();
    // getting training and testing restaurants
    List<Restaurant> restaurantsWithinRadius =
            REST_DB.getTestingRests(radius, hostCoordinate[0], hostCoordinate[1]);
    while (restaurantsWithinRadius.size() < 3 * NUM_RECOMMEND) {
      radius += 5;
      System.out.println("expanding search radius for restaurants");
      restaurantsWithinRadius =
              REST_DB.getTestingRests(radius, hostCoordinate[0], hostCoordinate[1]);
    }
    // init recommendation algorithm
    NaiveBayesClassifier<Restaurant, LabeledRestaurant> recAlgo =
            new NaiveBayesClassifier<>(groupPreference, restaurantsWithinRadius);
    return recAlgo.recommend(NUM_RECOMMEND, POSITIVE_CLASS);
  }

  /**
   * given the swiping results of users from the frontend, rank the restaurants by how the user
   * voted on them. The votes of the user are weighted, where each voter has a vote between 0 to 1
   * on each restaurant (0 means the user's vote doesn't matter at all, and 1 means the user's vote
   * matters very much). The weight of the vote is determined by the gottenWay field of the user.
   * This method also updates the gottenWay field of the user in the database after the ranking
   * is finished.
   *
   * @param userIds - a set of userIds
   * @param votes - a maps from (restaurantId --> (userId --> whether like)), representing the
   *              swipe results each user had on each restaurant. An integer 1 corresponds to
   *              "like", and 0 corresponds to "not like"
   *
   * @return the top ranking restaurant id
   */
  public static String rankRestaurants(Set<String> userIds,
                                           Map<String, Map<String, Integer>> votes)
          throws NoUserException, SQLException {
    // handle invalid inputs
    if (userIds.size() <= 0 || votes.size() <= 0) {
      throw new NoUserException("no users/votes passed in to the ranking algorithm");
    }
    // get the user's gottenWay from DB
    Map<String, Float> userGottenWays = new HashMap<>();
    for (String id : userIds) {
      userGottenWays.put(id, USER_DB.getUserGottenWay(id));
    }
    // rank the restaurants
    Map<String, Float> votesCount = new HashMap<>();
    for (String restaurantId : votes.keySet()) {
      float currentVote = 0;
      Map<String, Integer> swipeResults = votes.get(restaurantId);
      for (String userId : swipeResults.keySet()) {
        currentVote = currentVote + swipeResults.get(userId) * userGottenWays.get(userId);
      }
      votesCount.put(restaurantId, currentVote);
    }
    // get the highest value in votesCount
    Map.Entry<String, Float> maxEntry = null;
    for (Map.Entry<String, Float> entry : votesCount.entrySet()) {
      if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
        maxEntry = entry;
      }
    }
    assert maxEntry != null;
    String highestRankingRestaurant = maxEntry.getKey();
    // update user's gottenWay in DB
    for (String userId : userIds) {
      float newGottenWay = (float) (userGottenWays.get(userId) * GOTTEN_WAY_PRESERVE_RATIO
              + (1 - votes.get(highestRankingRestaurant).get(userId))
              * (1 - GOTTEN_WAY_PRESERVE_RATIO));
      USER_DB.updateUserGottenWay(userId, newGottenWay);
    }
    // return top restaurant
    return highestRankingRestaurant;
  }

  /**
   * Sets up Hub and runs the REPL.
   */
  public void run() {
    REST_DB.initRestaurants("data/restaurants.sqlite3");
    USER_DB.initUsers("data/users.sqlite3");
  }
}
