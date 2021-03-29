package edu.brown.cs.fork;

import edu.brown.cs.fork.actions.ActionLoadDB;
import edu.brown.cs.fork.database.Database;
import edu.brown.cs.fork.database.queries.Restaurants;
import edu.brown.cs.fork.database.queries.Users;

public class Hub {
  private final Repl repl = new Repl();
  private static final Users userDB = new Users();
  private static final Restaurants restDB = new Restaurants();

  private ActionLoadDB loadDB;

  public Hub() { }

  public static Users getUserDB() {
    return userDB;
  }

  public static Restaurants getRestDB() {
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
