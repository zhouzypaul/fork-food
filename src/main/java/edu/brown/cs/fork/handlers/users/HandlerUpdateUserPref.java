package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.CategoryNotFoundException;
import edu.brown.cs.fork.exceptions.PriceRangeNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Updates users survey preferences.
 */
public class HandlerUpdateUserPref implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerUpdateUserPref() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String userId = data.getString("username");
    JSONArray types = data.getJSONArray("types");
    JSONArray price = data.getJSONArray("price");
    String distance = data.getString("radius");

    List<String> foodTypes = new ArrayList<>();
    List<String> priceRanges = new ArrayList<>();

    for (int i = 0; i < types.length(); i++) {
      foodTypes.add(types.getString(i));
    }

    for (int i = 0; i < price.length(); i++) {
      priceRanges.add(price.getString(i));
    }

    List<String> colsToSet = Arrays.asList("userId", "business_id", "foodType", "star",
        "priceRange", "distance", "label", "timestamp", "numReviews", "name");

    String err = "";
    boolean success = false;
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      if (priceRanges.size() == 0) {
        priceRanges.add("");
      }
      if (foodTypes.size() == 0) {
        foodTypes.add("");
      }
      if (!Hub.getUserDB().deleteUserPref(userId)) {
        err = "ERROR: Can't update user preference";
      } else {
        try {
          for (int i = 0; i < foodTypes.size(); i++) {
            for (int j = 0; j < priceRanges.size(); j++) {
              List<String> info = Arrays.asList(userId, "",
                  Hub.frontendCategoryToBackend(foodTypes.get(i)), "3.0",
                  Hub.frontendPriceRangeToBackend(priceRanges.get(j)), distance, "1", "", "60", "");
              success = Hub.getUserDB().insertUserPref(colsToSet, info);
            }
          }
        } catch (CategoryNotFoundException | PriceRangeNotFoundException e) {
          System.out.println("ERROR: " + e.getMessage());
          success = false;
          err = e.getMessage();
        }
      }
    }
    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }
}
