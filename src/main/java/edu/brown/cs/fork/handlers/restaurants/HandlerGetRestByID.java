package edu.brown.cs.fork.handlers.restaurants;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for getting a restaurant by its id.
 */
public class HandlerGetRestByID implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerGetRestByID() {  }

  @Override
  public Object handle(Request req, Response res) throws JSONException {
    JSONObject data = new JSONObject(req.body());
    String id = data.getString("id");

    String err = "";
    List<Map<String, String>> rests = new ArrayList<>();
    Map<String, String> rest = new HashMap<>();
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        rests = Hub.getRestDB().queryRestByID(id);
        if (rests.size() == 0) {
          err = "ERROR: No restaurant with ID: " + id + " is found.";
        } else if (rests.size() == 1) {
          rest = rests.get(0);
        } else {
          err = "ERROR: Something is wrong, requested ID: " + id + " has duplicates.";
        }
      } catch (SQLException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("restaurants", rest, "err", err);

    return GSON.toJson(variables);
  }
}
