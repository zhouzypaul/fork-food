package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Queries user survey preferences.
 */
public class HandlerGetUserPref implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerGetUserPref() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String id = data.getString("id");

    String err = "";
    Map<String, List<String>> user = new HashMap<>();
    List<String> foodTypes = new ArrayList<>();
    List<String> priceRanges = new ArrayList<>();
    double radius = 0.0;
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        user = Hub.getUserDB().getUserPref(id);
        foodTypes = user.get("foodType");
        priceRanges = user.get("priceRange");
        foodTypes = new ArrayList<>(new HashSet<>(foodTypes));
        priceRanges = new ArrayList<>(new HashSet<>(priceRanges));
        if (!user.get("distance").get(0).equals("")) {
          radius = Double.parseDouble(user.get("distance").get(0));
        }
      } catch (SQLException | NumberFormatException e) {
        err = "ERROR: " + e.getMessage();
        System.out.println("ERROR: " + e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("types", foodTypes,
        "prices", priceRanges, "radius", radius, "err", err);

    return GSON.toJson(variables);
  }
}
