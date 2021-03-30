package edu.brown.cs.fork;

import edu.brown.cs.fork.actions.ActionLoadDB;
import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.database.queries.QueryUsers;

public class Hub {
  private final Repl repl = new Repl();
  private static final QueryUsers userDB = new QueryUsers();
  private static final QueryRestaurants restDB = new QueryRestaurants();

  private final ActionLoadDB loadDB = new ActionLoadDB();

  public Hub() { }

  public static QueryUsers getUserDB() {
    return userDB;
  }

  public static QueryRestaurants getRestDB() {
    return restDB;
  }

  public void run() {
    setupHub();
    this.repl.run();
  }

  public void setupHub() {
    this.repl.registerAction(loadDB);
  }
}
