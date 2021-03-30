package edu.brown.cs.fork.handlers.restaurants;

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

public class HandlerAllRestaurants implements Route {
  private static final Gson GSON = new Gson();

  public HandlerAllRestaurants() {  }

  @Override
  public Object handle(Request req, Response res) {
    String err = "";
    List<String> ret = new ArrayList<>();
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        ret = Hub.getRestDB().getAllRestaurants();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("restaurants", ret, "err", err);

    return GSON.toJson(variables);
  }
}
