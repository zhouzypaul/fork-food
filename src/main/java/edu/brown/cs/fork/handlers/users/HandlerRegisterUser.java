package edu.brown.cs.fork.handlers.users;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

/**
 * Registers a user by adding their username and password into login table.
 */
public class HandlerRegisterUser implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerRegisterUser() { }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JSONObject data = new JSONObject(req.body());
    String id = data.getString("id");
    String pwd = data.getString("pwd");

    String err = "";
    boolean success = false;
    if (!Hub.getUserDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      success = Hub.getUserDB().registerUser(id, pwd) && Hub.getUserDB().insertBlankRow(id);
    }
    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }
}
