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
import java.util.List;
import java.util.Map;

/**
 * Handler for getting restaurants within a given bounding box.
 */
public class HandlerGetRestByRad implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerGetRestByRad() {  }

  @Override
  public Object handle(Request req, Response res) throws JSONException {
    JSONObject data = new JSONObject(req.body());
    Double rad = data.getDouble("radius");
    Double lat = data.getDouble("lat");
    Double lon = data.getDouble("lon");

    String err = "";
    List<Map<String, String>> rest = new ArrayList<>();
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        rest = Hub.getRestDB().queryRestByRad(rad, lat, lon);
      } catch (SQLException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("restaurants", rest, "err", err);

    return GSON.toJson(variables);
  }
}
