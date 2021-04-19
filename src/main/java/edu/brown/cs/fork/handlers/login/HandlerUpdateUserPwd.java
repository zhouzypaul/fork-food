package edu.brown.cs.fork.handlers.login;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.NoUserException;
import org.json.JSONException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

/**
 * Updates user's password.
 */
public class HandlerUpdateUserPwd implements Route {
  private static final Gson GSON = new Gson();

  /**
   * Constructor.
   */
  public HandlerUpdateUserPwd() {  }

  @Override
  public String handle(Request req, Response res) {
    try {
      String username = HashInfo.hashInfo(req)[0];
      String hash = HashInfo.hashInfo(req)[1];

      String err = "";
      boolean success = false;
      if (!Hub.getUserDB().isConnected()) {
        err = "ERROR: No database connected";
      } else {
        success = Hub.getUserDB().changePwd(username, hash);
      }
      Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

      return GSON.toJson(variables);

    } catch (JSONException | NoSuchAlgorithmException | InvalidKeySpecException
        | NoUserException e) {
      System.out.println("ERROR: " + e);
    }
    return null;
  }
}
