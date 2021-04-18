package edu.brown.cs.fork.handlers.restaurants;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Map;

/**
 * Deletes a recent restaurant id.
 */
public class HandlerDeleteMostRecentRests implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerDeleteMostRecentRests() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String userId = data.getString("username");

    String err = "";
    boolean success = false;
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      // delete all the recent restaurant ids and timestamps
      success = Hub.getUserDB().setRecentRests(userId, new ArrayList<>())
          && Hub.getUserDB().setRecentTimestamps(userId, new ArrayList<>());
    }

    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }
}
