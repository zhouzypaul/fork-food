package edu.brown.cs.fork;

import edu.brown.cs.fork.actions.ActionLoadDB;
import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.database.queries.QueryUsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A singleton class containing REPL and databases.
 */
public class Hub {
  private final Repl repl = new Repl();
  private static final QueryUsers USER_DB = new QueryUsers();
  private static final QueryRestaurants REST_DB = new QueryRestaurants();
  private final ActionLoadDB loadDB = new ActionLoadDB();

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
