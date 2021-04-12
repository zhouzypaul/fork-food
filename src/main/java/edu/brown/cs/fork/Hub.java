package edu.brown.cs.fork;

import edu.brown.cs.fork.actions.ActionLoadDB;
import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.database.queries.QueryUsers;
import edu.brown.cs.fork.exceptions.NoTestDataException;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.recommendation.NaiveBayesClassifier;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.restaurants.Restaurant;
import edu.brown.cs.fork.users.Group;
import edu.brown.cs.fork.users.Person;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
   * recommend a list of restaurants for a group of users.
   * @param userIds an array of user IDs, one for each user in a group
   * @param hostCoordinate the coordinate of the host, in the form of (latitude, longitude)
   * @return a list of recommended restaurants.
   */
  public static List<Restaurant> recommendRestaurants(String[] userIds, double[] hostCoordinate)
          throws OutOfRangeException, SQLException, NoTestDataException {
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
    for (LabeledRestaurant lr : groupPreference) {
      Restaurant r = lr.getData();
      radius = radius + r.getDistance();
    }
    radius = radius / groupPreference.size();
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
