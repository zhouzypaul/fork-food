package edu.brown.cs.fork.main;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.handlers.login.HandlerUpdateUserPwd;
import edu.brown.cs.fork.handlers.login.LoginHandler;
import edu.brown.cs.fork.handlers.login.RegistrationHandler;

import edu.brown.cs.fork.handlers.restaurants.*;
import edu.brown.cs.fork.handlers.room.RoomCheckHandler;
import edu.brown.cs.fork.sockets.GroupSocket;

import edu.brown.cs.fork.handlers.users.HandlerAllUserIds;
import edu.brown.cs.fork.handlers.login.HandlerDeleteUser;
import edu.brown.cs.fork.handlers.users.HandlerGetUserPref;
import edu.brown.cs.fork.handlers.users.HandlerGetUserPwd;
import edu.brown.cs.fork.handlers.users.HandlerInsertUserPref;
import edu.brown.cs.fork.handlers.users.HandlerUpdateUserPref;
import com.google.gson.Gson;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main().run();
  }
  private final Hub hub = new Hub();

  private Main() {  }

  private void run() {
    runSparkServer(DEFAULT_PORT);
    hub.run();
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("fork-react/build/");

    // websocket
    Spark.webSocket("/socket", GroupSocket.class);

    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    // Setup Spark Routes
    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    Spark.exception(Exception.class, new ExceptionPrinter());

    Spark.post("/test", new HandlerAllRestaurants());
    // handles registering new users
    Spark.post("/register", new RegistrationHandler());
    // handles user login
    Spark.post("/login", new LoginHandler());
    Spark.post("/updatePwd", new HandlerUpdateUserPwd());

    Spark.post("/getRestByID", new HandlerGetRestByID());
    Spark.post("/getRestByRad", new HandlerGetRestByRad());
    Spark.post("/deleteUser", new HandlerDeleteUser());
    Spark.post("/getUserPwd", new HandlerGetUserPwd());
    Spark.post("/getAllUserIds", new HandlerAllUserIds());
    Spark.post("/getUserPref", new HandlerGetUserPref());
    Spark.post("/insertUserPref", new HandlerInsertUserPref());
    Spark.post("/updateUserPref", new HandlerUpdateUserPref());
    Spark.post("/getMostRecentRests", new HandlerGetMostRecentRests());
    Spark.post("/deleteMostRecentRest", new HandlerDeleteMostRecentRest());

    // using other registration endpoint
    // Spark.post("/registerUser", new HandlerRegisterUser());

    Spark.post("/verifyCode", new RoomCheckHandler());

  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

}
