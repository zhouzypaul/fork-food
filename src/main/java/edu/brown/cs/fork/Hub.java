package edu.brown.cs.fork;

import edu.brown.cs.fork.actions.ActionLoadDB;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import java.util.ArrayList;

/**
 * A singleton class containing REPL and databases.
 */
public class Hub {
  private final Repl repl = new Repl();
  private static final QueryUsers USER_DB = new QueryUsers();
  private static final QueryRestaurants REST_DB = new QueryRestaurants();
  private final ActionLoadDB loadDB = new ActionLoadDB();

  private static final int NUM_RECOMMEND = 10;
  private static final int POSITIVE_CLASS = 1;
  private static final double DEFAULT_RADIUS = 10;

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
    // init recommendation algorithm
    NaiveBayesClassifier<Restaurant, LabeledRestaurant> recAlgo =
            new NaiveBayesClassifier<>(groupPreference, restaurantsWithinRadius);
    return recAlgo.recommend(NUM_RECOMMEND, POSITIVE_CLASS);
  }

  /**
   * Sets up Hub and runs the REPL.
   */
  public void run() {
    setupHub();
    this.repl.run();
  }

  /**
   * Initializes database query clients and registers actions.
   */
  public void setupHub() {
    REST_DB.initRestaurants("data/restaurants.sqlite3");
    USER_DB.initUsers("data/users.sqlite3");
    this.repl.registerAction(loadDB);
  }
}
