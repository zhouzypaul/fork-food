package edu.brown.cs.fork.handlers.users;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class HandlerUpdateUser implements Route {
  private static final Gson GSON = new Gson();

  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }
}
