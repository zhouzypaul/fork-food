package edu.brown.cs.fork.main;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.handlers.login.HandlerUpdateUserPwd;
import edu.brown.cs.fork.handlers.login.LoginHandler;
import edu.brown.cs.fork.handlers.login.RegistrationHandler;

import edu.brown.cs.fork.handlers.restaurants.HandlerDeleteMostRecentRests;
import edu.brown.cs.fork.handlers.restaurants.HandlerGetMostRecentRests;
import edu.brown.cs.fork.handlers.restaurants.HandlerGetRestByID;
import edu.brown.cs.fork.handlers.restaurants.HandlerGetRestByRad;
import edu.brown.cs.fork.handlers.room.RoomCheckHandler;

import edu.brown.cs.fork.handlers.users.HandlerAllUserIds;
import edu.brown.cs.fork.handlers.login.HandlerDeleteUser;
import edu.brown.cs.fork.handlers.users.HandlerGetUserPref;
import edu.brown.cs.fork.handlers.users.HandlerGetUserPwd;
import edu.brown.cs.fork.handlers.users.HandlerInsertUserPref;
import edu.brown.cs.fork.handlers.users.HandlerUpdateUserPref;
import edu.brown.cs.fork.sockets.Groups;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;

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

  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return DEFAULT_PORT; //return default port if heroku-port isn't set (i.e. on localhost)
  }

  private void runSparkServer(int port) {
    Spark.port(getHerokuAssignedPort());
    Spark.externalStaticFileLocation("fork-react/build/");

    // websocket
    Spark.webSocket("/socket", Groups.class);

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

    // authentication
    Spark.post("/register", new RegistrationHandler());
    Spark.post("/login", new LoginHandler());
    Spark.post("/updatePwd", new HandlerUpdateUserPwd());
    Spark.post("/deleteUser", new HandlerDeleteUser());
    Spark.post("/getUserPwd", new HandlerGetUserPwd());
    Spark.post("/getAllUserIds", new HandlerAllUserIds());

    // query restaurants
    Spark.post("/getRestByID", new HandlerGetRestByID());
    Spark.post("/getRestByRad", new HandlerGetRestByRad());
    Spark.post("/getMostRecentRests", new HandlerGetMostRecentRests());
    Spark.post("/deleteMostRecentRests", new HandlerDeleteMostRecentRests());

    // query user preferences
    Spark.post("/getUserPref", new HandlerGetUserPref());
    Spark.post("/insertUserPref", new HandlerInsertUserPref());
    Spark.post("/updateUserPref", new HandlerUpdateUserPref());

    // handle hosting / joining room
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
