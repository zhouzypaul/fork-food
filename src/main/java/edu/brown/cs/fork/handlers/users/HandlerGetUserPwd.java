package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.Map;

/**
 * Queries user's password.
 */
public class HandlerGetUserPwd implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerGetUserPwd() {  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String id = data.getString("id");

    String err = "";
    String pwd = "";
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        pwd = Hub.getUserDB().getPwd(id);
      } catch (SQLException e) {
        err = "ERROR: " + e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("pwd", pwd, "err", err);

    return GSON.toJson(variables);
  }
}
