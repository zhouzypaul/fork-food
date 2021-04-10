package edu.brown.cs.fork;

import edu.brown.cs.fork.actions.ActionLoadDB;
import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.database.queries.QueryUsers;

/**
 * A singleton class containing REPL and databases.
 */
public class Hub {
  private final Repl repl = new Repl();
  private static final QueryUsers USER_DB = new QueryUsers();
  private static final QueryRestaurants REST_DB = new QueryRestaurants();

  private final ActionLoadDB loadDB = new ActionLoadDB();

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
