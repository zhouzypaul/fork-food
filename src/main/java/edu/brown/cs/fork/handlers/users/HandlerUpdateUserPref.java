package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HandlerUpdateUserPref implements Route {
  private static final Gson GSON = new Gson();

  public HandlerUpdateUserPref() {  }

  public String matchFoodTypes(String frontendType) {
    switch (frontendType) {
      case "burgers":
        return "Burgers";
      case "chinese":
        return "Chinese";
      case "pizza":
        return "Pizza";
      case "italian":
        return "Italian";
      case "sushi":
        return "Sushi Bars";
      case "indian":
        return "Indian";
      case "vietnamese":
        return "Vietnamese";
      case "steak":
        return "Steakhouses";
      case "breakfast":
        return "Breakfast & Brunch";
      case "dessert":
        return "Desserts";
      default:
        return "NONE";
    }
  }

  public String matchPriceRanges(String frontendPriceRange) {
    switch (frontendPriceRange) {
      case "$":
        return "1";
      case "$$":
        return "2";
      case "$$$":
        return "3";
      default:
        return "NONE";
    }
  }

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
        "priceRange", "distance", "label");

    String err = "";
    boolean success = false;
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      if (!Hub.getUserDB().deleteUserPref(userId)) {
        err = "ERROR: Can't update user preference";
      } else {
        for (int i = 0; i < priceRanges.size(); i++) {
          for (int j = 0; j < foodTypes.size(); j++) {
            List<String> info = Arrays.asList(userId, "", matchFoodTypes(foodTypes.get(i)), "3.0",
                matchPriceRanges(priceRanges.get(j)), distance, "1");
            success = Hub.getUserDB().insertUserPref(userId, colsToSet, info);
          }
        }
      }
    }
    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }
}
