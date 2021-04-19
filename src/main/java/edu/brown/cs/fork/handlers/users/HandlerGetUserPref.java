package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.CategoryNotFoundException;
import edu.brown.cs.fork.exceptions.PriceRangeNotFoundException;
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
  private static final double TEN = 10.0;

  /**
   * Constructor.
   */
  public HandlerGetUserPref() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String username = data.getString("username");

    String err = "";
    Map<String, List<String>> user = new HashMap<>();
    List<String> foodTypes = new ArrayList<>();
    List<String> priceRanges = new ArrayList<>();
    double radius = TEN;
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        user = Hub.getUserDB().getUserPref(username);
        foodTypes = user.get("foodType");
        priceRanges = user.get("priceRange");
        foodTypes = new ArrayList<>(new HashSet<>(foodTypes));
        priceRanges = new ArrayList<>(new HashSet<>(priceRanges));
        if (foodTypes.size() == 1 && foodTypes.get(0).equals("")) {
          foodTypes = new ArrayList<>();
        }
        if (priceRanges.size() == 1 && priceRanges.get(0).equals("")) {
          priceRanges = new ArrayList<>();
        }
        if (!user.get("distance").get(0).equals("")) {
          radius = Double.parseDouble(user.get("distance").get(0));
        }
        if (priceRanges.size() > 0) {
          for (int i = 0; i < priceRanges.size(); i++) {
            priceRanges.set(i, Hub.backendPriceRangeToFrontend(priceRanges.get(i)));
          }
        }
        if (foodTypes.size() > 0) {
          for (int i = 0; i < foodTypes.size(); i++) {
            foodTypes.set(i, Hub.backendCategoryToFrontend(foodTypes.get(i)));
          }
        }
      } catch (SQLException | NumberFormatException | CategoryNotFoundException
            | PriceRangeNotFoundException e) {
        err = "ERROR: " + e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("types", foodTypes,
        "prices", priceRanges, "radius", radius, "err", err);

    return GSON.toJson(variables);
  }
}
