package edu.brown.cs.fork.handlers.restaurants;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

/**
 * Deletes a recent restaurant id.
 */
public class HandlerDeleteMostRecentRest implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerDeleteMostRecentRest() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String userId = data.getString("username");
    String restId = data.getString("business_id");

    String err = "";
    boolean success = false;
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      success = Hub.getUserDB().deleteRecentRest(userId, restId);
    }

    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }
}
