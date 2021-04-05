package edu.brown.cs.fork.handlers.login;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.users.Person;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
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

      // TODO: this line subject to change based on Sean
      boolean authenticated = authenticate(username, password);

      Map<String, Object> variables = ImmutableMap.of("status", authenticated);

      return GSON.toJson(variables);

    } catch (JSONException e) {
      System.out.println("ERROR: JSON exception"); // replace with a good exception
    }

    return null;
  }

  private boolean authenticate(String username, String password) {
    Person user = getUserByName(username);

    // TODO: need to connect with Sean
    /*
    KeySpec spec = new PBEKeySpec(password.toCharArray(), user.getSalt(), 65536, 128);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] hash = factory.generateSecret(spec).getEncoded();

    return user.getHash().equals(hash);
    */
     return username.startsWith("A");
  }

  // TODO: all of the following we will need Sean to do for us
  private Person getUserByName(String username) {
    return new Person();
  }
}