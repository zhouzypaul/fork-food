package edu.brown.cs.fork.handlers.restaurants;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.SQLErrorException;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for getRestaurantByID in QueryRestaurants
 */
public class HandlerGetRestaurantByID implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerGetRestaurantByID() {  }

  @Override
  public Object handle(Request req, Response res) throws JSONException {
    JSONObject data = new JSONObject(req.body());
    String id = data.getString("id");

    String err = "";
    Map<String, String> rest = new HashMap<>();
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        rest = Hub.getRestDB().getRestaurantByID(id);
      } catch (SQLException | SQLErrorException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("restaurant", rest, "err", err);

    return GSON.toJson(variables);
  }
}
