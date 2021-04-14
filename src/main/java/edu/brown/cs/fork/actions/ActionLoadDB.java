package edu.brown.cs.fork.actions;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.database.queries.QueryUsers;

import java.nio.file.Files;
import java.nio.file.Path;

public class ActionLoadDB implements TriggerAction {
  private final QueryRestaurants restDB;
  private final QueryUsers userDB;

  public ActionLoadDB() {
    this.restDB = Hub.getRestDB();
    this.userDB = Hub.getUserDB();
  }

  @Override
  public String command() {
    return "load";
  }

  @Override
  public void run(String[] args) {
    if (args.length != 3) {
      System.out.println("ERROR: Wrong number of arguments");
//      return "ERROR: Wrong number of arguments";
      return;
    }

    String whichDB = args[1];
    String path = args[2];

    if (!Files.exists(Path.of(path))) {
      System.out.println("ERROR: Database doesn't exist");
      return;
//      return "ERROR: Database doesn't exist";
    }

    if (whichDB.equals("rest")) {
      this.restDB.initRestaurants(path);
      System.out.println("Loaded Restaurants Database");
//      return "Loaded Restaurants Database";
    } else if (whichDB.equals("user")) {
      this.userDB.initUsers(path);
      System.out.println("Loaded Users Database");
//      return "Loaded Users Database";
    } else {
      System.out.println("ERROR: The second argument must be either rest or user");
//      return "ERROR: The second argument must be either rest or user";
    }
  }
}
