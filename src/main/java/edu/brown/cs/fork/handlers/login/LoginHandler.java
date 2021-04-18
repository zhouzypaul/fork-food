package edu.brown.cs.fork.handlers.login;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.exceptions.NoUserException;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 * Handles user login.
 */
public class LoginHandler implements Route {
  private static final Gson GSON = new Gson();
  private static final int SALT_LENGTH = 16;
  private static final int PWD_ITERATION_COUNT = 65536;
  private static final int PWD_KEY_LENGTH = 128;

  @Override
  public String handle(Request req, Response res) throws JSONException {
    JSONObject json = new JSONObject(req.body());
    String username = json.getString("username");
    String password = json.getString("password");

    String err = "";
    boolean success = false;
    if (!Hub.getRestDB().isConnected()) {
      err = "ERROR: No database connected";
    } else {
      try {
        success = authenticate(username, password);
      } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException
          | NoUserException e) {
        err = e.getMessage();
        System.out.println(e.getMessage());
      }
    }
    Map<String, Object> variables = ImmutableMap.of("success", success, "err", err);

    return GSON.toJson(variables);
  }

  private boolean authenticate(String username, String password) throws NoSuchAlgorithmException,
    InvalidKeySpecException, SQLException, NoUserException {
    // get stored password
    String storedHash =  Hub.getUserDB().getPwd(username);

    // hash input password
    byte[] salt = new byte[SALT_LENGTH];
    Arrays.fill(salt, (byte) 0);

    KeySpec spec =
            new PBEKeySpec(password.toCharArray(), salt, PWD_ITERATION_COUNT, PWD_KEY_LENGTH);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] hash = factory.generateSecret(spec).getEncoded();

    // compare stored and input passwords
    return storedHash.equals(Arrays.toString(hash));
  }
}
