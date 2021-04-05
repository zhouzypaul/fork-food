package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HandlerAllUsers implements Route {
  private static final Gson GSON = new Gson();

  public HandlerAllUsers() {  }

  @Override
  public Object handle(Request req, Response res) {
    String err = "";
    List<Map<String, String>> ret = new ArrayList<>();
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        ret = Hub.getUserDB().queryAllUsers();
      } catch (SQLException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("users", ret, "err", err);

    return GSON.toJson(variables);
  }
}
