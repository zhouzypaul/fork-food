package edu.brown.cs.fork.handlers.login;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Map;

/**
 * Handles new user registration.
 */
public class RegistrationHandler implements Route {
  private static final Gson GSON = new Gson();

  @Override
  public String handle(Request req, Response res) {
    try {
      JSONObject json = new JSONObject(req.body());
      String username = json.getString("username");
      String password = json.getString("password");

      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[16];
      random.nextBytes(salt);

      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

      byte[] hash = factory.generateSecret(spec).getEncoded();

      // TODO: this line subject to change based on Sean, should store theses values in database
      // salt is to prevent duplicate password attack, should be stored as well
      tempFunc(username, hash, salt);

      Map<String, Object> variables = ImmutableMap.of("status", "complete");

      return GSON.toJson(variables);

    } catch (JSONException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      System.out.println("ERROR: JSON exception"); // replace with a good exception
    }

    return null;
  }

  private void tempFunc(String username, byte[] password, byte[] salt) {
    System.out.println(username + ": " + Arrays.toString(password) + ": " + Arrays.toString(salt));
  }
}