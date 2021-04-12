package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.*;

/**
 * Class to query all user ids in the login table of users database.
 */
public class HandlerAllUserIds implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerAllUserIds() { }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    String err = "";
    Set<String> ret = new HashSet<>();
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        ret = Hub.getUserDB().queryAllUserIds();
      } catch (SQLException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("userIds", ret, "err", err);

    return GSON.toJson(variables);
  }
}
