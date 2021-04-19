package edu.brown.cs.fork.handlers.login;

import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * Utility class for hashing user's password.
 */
public final class HashInfo {
  private static final int SALT_LENGTH = 16;
  private static final int PWD_ITERATION_COUNT = 65536;
  private static final int PWD_KEY_LENGTH = 128;

  /**
   * Constructor.
   */
  private HashInfo() {  }

  /**
   * Hashes user's password.
   * @param req a request
   * @return a string array containing username and hashed password
   * @throws NoSuchAlgorithmException NoSuchAlgorithmException
   * @throws InvalidKeySpecException InvalidKeySpecException
   * @throws JSONException JSONException
   */
  public static String[] hashInfo(Request req) throws NoSuchAlgorithmException,
      InvalidKeySpecException, JSONException {
    JSONObject json = new JSONObject(req.body());
    String username = json.getString("username");
    String password = json.getString("password");

    byte[] salt = new byte[SALT_LENGTH];
    Arrays.fill(salt, (byte) 0);

    // hash user password before storing in db
    KeySpec spec =
        new PBEKeySpec(password.toCharArray(), salt, PWD_ITERATION_COUNT, PWD_KEY_LENGTH);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    String hash = Arrays.toString(factory.generateSecret(spec).getEncoded());

    return new String[]{username, hash};
  }
}
