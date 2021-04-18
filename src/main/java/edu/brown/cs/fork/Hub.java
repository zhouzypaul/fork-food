package edu.brown.cs.fork;

import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.database.queries.QueryUsers;
import edu.brown.cs.fork.exceptions.*;
import edu.brown.cs.fork.recommendation.NaiveBayesClassifier;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.restaurants.Restaurant;
import edu.brown.cs.fork.users.Group;
import edu.brown.cs.fork.users.Person;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
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
  private static final double GOTTEN_WAY_PRESERVE_RATIO = 0.7;
  private static final int MAX_NUM_RADIUS_EXPANSION = 5;

  private static final double DEFAULT_RADIUS = 10;
  public static final int DEFAULT_PRICE_RANGE = 1;
  public static final double DEFAULT_STAR = 2.5;
  public static final double DEFAULT_DISTANCE = 10;
  public static final int DEFAULT_NUM_REVIEWS = 0;
  public static final int DEFAULT_LABEL = 1;

  private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy");

  // Food categories
  private static final List<String> CATEGORIES = Arrays.asList(
      "Burgers", "Chinese", "Pizza", "Italian", "Sushi Bars", "Indian", "Vietnamese", "Steakhouses",
      "Breakfast & Brunch", "Desserts", "Coffee & Tea", "Greek", "Middle Eastern", "Vegan",
      "Mexican", "Thai", "American", "Salad", "Barbeque", "Seafood");

  private static final List<String> FRONTEND_CATEGORIES = Arrays.asList(
      "burgers", "chinese", "pizza", "italian", "sushi", "indian", "vietnamese", "steakhouses",
      "breakfast", "desserts", "coffee & tea", "greek", "middle eastern", "vegan",
      "mexican", "thai", "american", "salad", "barbeque", "seafood");

  // Price Ranges
  private static final List<String> PRICE_RANGES = Arrays.asList("1", "2", "3");

  private static final List<String> FRONTEND_PRICE_RANGES = Arrays.asList("$", "$$", "$$$");

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
    try {
      for (String id : userIds) {
        String r = USER_DB.getUserPref(id).get("distance").get(0);
        radius += Double.parseDouble(r);
      }
      radius = radius / userIds.size();
    } catch (NumberFormatException e) {
      radius = DEFAULT_RADIUS;
    }
    // getting training and testing restaurants
    List<Restaurant> restaurantsWithinRadius =
            REST_DB.getTestingRests(radius, hostCoordinate[0], hostCoordinate[1]);
    int expansionCounter = 0;
    while (restaurantsWithinRadius.size() < 3 * NUM_RECOMMEND) {
      expansionCounter++;
      radius += 5;
      System.out.println("expanding search radius for restaurants");
      restaurantsWithinRadius =
              REST_DB.getTestingRests(radius, hostCoordinate[0], hostCoordinate[1]);
      if (expansionCounter > MAX_NUM_RADIUS_EXPANSION) {
        break;
      }
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
   * @param votes - a maps from (restaurantId -- (userId -- whether like)), representing the
   *              swipe results each user had on each restaurant. An integer 1 corresponds to
   *              "like", and 0 corresponds to "not like"
   *
   * @return the top ranking restaurant id
   * @throws NoUserException when the userId set is empty
   * @throws SQLException when an SQL query goes wrong
   */
  public static String rankRestaurants(Set<String> userIds,
                                           Hashtable<String, Hashtable<String, Integer>> votes)
          throws NoUserException, SQLException {
    // handle invalid inputs
    if (userIds.size() <= 0 || votes.size() <= 0) {
      throw new NoUserException("no users/votes passed in to the ranking algorithm");
    }
    // get the user's gottenWay from DB
    Map<String, Double> userGottenWays = new HashMap<>();
    for (String id : userIds) {
      userGottenWays.put(id, USER_DB.getUserGottenWay(id));
    }
    // rank the restaurants
    Map<String, Double> votesCount = new HashMap<>();
    for (String restaurantId : votes.keySet()) {
      double currentVote = 0;
      Map<String, Integer> swipeResults = votes.get(restaurantId);
      for (String userId : swipeResults.keySet()) {
        currentVote = currentVote + swipeResults.get(userId) * userGottenWays.get(userId);
      }
      votesCount.put(restaurantId, currentVote);
    }
    // get the highest value in votesCount
    Map.Entry<String, Double> maxEntry = null;
    for (Map.Entry<String, Double> entry : votesCount.entrySet()) {
      if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
        maxEntry = entry;
      }
    }
    assert maxEntry != null;
    String highestRankingRestaurant = maxEntry.getKey();
    // update user's gottenWay in DB
    for (String userId : userIds) {
      double newGottenWay = userGottenWays.get(userId) * GOTTEN_WAY_PRESERVE_RATIO
              + (1 - votes.get(highestRankingRestaurant).get(userId))
              * (1 - GOTTEN_WAY_PRESERVE_RATIO);
      USER_DB.updateUserGottenWay(userId, newGottenWay);
      // add most recent restaurants to users login table
      USER_DB.updateMostRecentRests(userId, highestRankingRestaurant);
      // add the corresponding timestamp to users login table
      Date date = new Date();
      USER_DB.updateMostRecentTimes(userId, SDF.format(date));
    }
    // return top restaurant
    return highestRankingRestaurant;
  }

  /**
   * Maps a frontend category string to backend category string.
   * @param category frontend category
   * @return matching backend category
   * @throws CategoryNotFoundException if category can't be found
   */
  public static String frontendCategoryToBackend(String category)
      throws CategoryNotFoundException {
    if (!FRONTEND_CATEGORIES.contains(category)) {
      throw new CategoryNotFoundException("Category " + category + " can't be found.");
    }
    int idx = FRONTEND_CATEGORIES.indexOf(category);
    return CATEGORIES.get(idx);
  }

  /**
   * Maps a backend category string to frontend category string.
   * @param category backend category
   * @return matching frontend category
   * @throws CategoryNotFoundException if category can't be found
   */
  public static String backendCategoryToFrontend(String category)
      throws CategoryNotFoundException {
    if (!CATEGORIES.contains(category)) {
      throw new CategoryNotFoundException("Category " + category + " can't be found.");
    }
    int idx = CATEGORIES.indexOf(category);
    return FRONTEND_CATEGORIES.get(idx);
  }

  /**
   * Matches frontend price range with backend price range.
   * @param frontendPriceRange frontend price range
   * @return backend price range
   */
  public static String frontendPriceRangeToBackend(String frontendPriceRange)
      throws PriceRangeNotFoundException {
    if (!FRONTEND_PRICE_RANGES.contains(frontendPriceRange)) {
      throw new PriceRangeNotFoundException("Price range " + frontendPriceRange
          + " can't be found.");
    }
    int idx = FRONTEND_PRICE_RANGES.indexOf(frontendPriceRange);
    return PRICE_RANGES.get(idx);
  }

  /**
   * Matches backend priceRange to frontend.
   * @param backendPR backend price range
   * @return frontend price range
   */
  public static String backendPriceRangeToFrontend(String backendPR)
      throws PriceRangeNotFoundException {
    if (!PRICE_RANGES.contains(backendPR)) {
      throw new PriceRangeNotFoundException("Price range " + backendPR
        + " can't be found.");
    }
    int idx = PRICE_RANGES.indexOf(backendPR);
    return FRONTEND_PRICE_RANGES.get(idx);
  }

  /**
   * Sets up Hub and runs the REPL.
   */
  public void run() {
    REST_DB.initRestaurants("data/restaurants.sqlite3");
    USER_DB.initUsers("data/users.sqlite3");
  }
}
