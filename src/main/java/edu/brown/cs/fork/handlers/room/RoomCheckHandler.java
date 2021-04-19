package edu.brown.cs.fork.handlers.room;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.fork.sockets.Groups;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class RoomCheckHandler implements Route {
  private static final Gson GSON = new Gson();

  @Override
  public String handle(Request req, Response res) {
    try {
      JSONObject json = new JSONObject(req.body());
      int code = json.getInt("code");
      boolean exists = Groups.valid(code);
      Map<String, Object> variables = ImmutableMap.of("exists", exists);
      return GSON.toJson(variables);
    } catch (JSONException e) {
      System.out.println("ERROR: JSON exception"); // replace with a good exception
    }
    return null;
  }
}
