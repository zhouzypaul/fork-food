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
import java.util.Arrays;
import java.util.Map;

public class HandlerUpdateUserPwd implements Route {
  private static final Gson GSON = new Gson();

  public HandlerUpdateUserPwd() {  }

  @Override
  public String handle(Request req, Response res) {
    try {
      JSONObject json = new JSONObject(req.body());
      String username = json.getString("username");
      String password = json.getString("password");

      // use a fixed salt for now, defeats the purpose but will come back to this
//      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[16];
      Arrays.fill(salt, (byte) 0);
//      random.nextBytes(salt);

      // hash user password before storing in db
      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      String hash = Arrays.toString(factory.generateSecret(spec).getEncoded());

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
