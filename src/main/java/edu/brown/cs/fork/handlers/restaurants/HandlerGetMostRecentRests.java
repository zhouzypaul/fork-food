package edu.brown.cs.fork.handlers.restaurants;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class to get a user's most recent top restaurants.
 */
public class HandlerGetMostRecentRests implements Route {
  private static final Gson GSON = new Gson();

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String userId = data.getString("username");

    String err = "";
    List<Map<String, String>> rests = new ArrayList<>();
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        List<String> restIds = Hub.getUserDB().getMostRecentRests(userId);
        if (!restIds.isEmpty()) {
          for (String id : restIds) {
            rests.add(Hub.getRestDB().queryRestByID(id));
          }
        }
      } catch (SQLException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }

    // most recent restaurants are reversed so order is newest -> oldest
    Collections.reverse(rests);
    Map<String, Object> variables = ImmutableMap.of("restaurants", rests, "err", err);

    return GSON.toJson(variables);
  }
}
