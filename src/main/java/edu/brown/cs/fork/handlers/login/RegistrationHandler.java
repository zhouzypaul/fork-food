package edu.brown.cs.fork.handlers.login;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import org.json.JSONException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Handles new user registration.
 */
public class RegistrationHandler implements Route {
  private static final Gson GSON = new Gson();

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
        // filter non-unique usernames and insert into DB
        if (Hub.getUserDB().queryAllUserIds().contains(username)) {
          err = "ERROR: user already exists";
        } else {
          success = Hub.getUserDB().registerUser(username, hash)
              && Hub.getUserDB().insertBlankRow(username);
        }
      }
      Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

      return GSON.toJson(variables);

    } catch (JSONException | NoSuchAlgorithmException | InvalidKeySpecException | SQLException e) {
      System.out.println("ERROR: " + e); // replace with a good exception
    }
    return null;
  }
}
