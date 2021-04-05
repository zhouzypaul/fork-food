package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerGetUserByID implements Route {
  private static final Gson GSON = new Gson();

  public HandlerGetUserByID() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String id = data.getString("id");

    String err = "";
    List<Map<String, String>> users = new ArrayList<>();
    Map<String, String> user = new HashMap<>();
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        users = Hub.getUserDB().queryUserByID(id);
        if (users.size() == 0) {
          err = "ERROR: No user with ID: " + id + " is found.";
        } else if (users.size() == 1) {
          user = users.get(0);
        } else {
          err = "ERROR: Something is wrong, requested ID: " + id + " has duplicates.";
        }
      } catch (SQLException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("user", user, "err", err);

    return GSON.toJson(variables);
  }
}
