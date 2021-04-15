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

  @Override
  public String handle(Request req, Response res) {
    try {
      JSONObject json = new JSONObject(req.body());
      String username = json.getString("username");
      String password = json.getString("password");

      // validate password
      boolean authenticated = authenticate(username, password);

      Map<String, Object> variables = ImmutableMap.of("success", authenticated);

      return GSON.toJson(variables);

    } catch (JSONException | NoSuchAlgorithmException | InvalidKeySpecException
        | SQLException | NoUserException e) {
      System.out.println("ERROR: " + e); // replace with a good exception
    }
    return null;
  }

  private boolean authenticate(String username, String password) throws NoSuchAlgorithmException,
    InvalidKeySpecException, SQLException, NoUserException {
    // get stored password
    String storedHash =  Hub.getUserDB().getPwd(username);

    // hash input password
    byte[] salt = new byte[16];
    Arrays.fill(salt, (byte) 0);

    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] hash = factory.generateSecret(spec).getEncoded();

    // compare stored and input passwords
    return storedHash.equals(Arrays.toString(hash));
  }
}
