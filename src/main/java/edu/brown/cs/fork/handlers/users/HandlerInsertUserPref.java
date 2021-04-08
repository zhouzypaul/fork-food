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
import java.util.List;
import java.util.Map;

public class HandlerInsertUserPref implements Route {
  private static final Gson GSON = new Gson();

  public HandlerInsertUserPref() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String userId = data.getString("username");
    Double userLat = data.getDouble("latitude");
    Double userLon = data.getDouble("longitude");
    JSONArray restIdsJSON = data.getJSONArray("business_id_arr");
    JSONArray likesOrDislikesJSON = data.getJSONArray("swipe_decision_arr");

    List<String> restIds = new ArrayList<>();
    List<String> likesOrDislikes = new ArrayList<>();

    for (int i = 0; i < restIdsJSON.length(); i++) {
      restIds.add(restIdsJSON.getString(i));
    }

    for (int i = 0; i < likesOrDislikesJSON.length(); i++) {
      likesOrDislikes.add(likesOrDislikesJSON.getString(i));
    }

    String err = "";
    boolean success = false;
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      success = Hub.getUserDB().insertUserPref(userId, userLat, userLon, restIds, likesOrDislikes);
    }
    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }
}
