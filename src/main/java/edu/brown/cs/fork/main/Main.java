package edu.brown.cs.fork.main;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.handlers.restaurants.HandlerAllRestaurants;
import edu.brown.cs.fork.handlers.restaurants.HandlerGetRestByID;
import edu.brown.cs.fork.handlers.restaurants.HandlerGetRestByRad;
import edu.brown.cs.fork.handlers.users.HandlerAllUserIds;
import edu.brown.cs.fork.handlers.users.HandlerAllUsers;
import edu.brown.cs.fork.handlers.users.HandlerDeleteUser;
import edu.brown.cs.fork.handlers.users.HandlerGetUserByID;
import edu.brown.cs.fork.handlers.users.HandlerRegisterUser;
import edu.brown.cs.fork.handlers.users.HandlerUpsertUser;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
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
    new Main(args).run();
  }

  private String[] args;
  private final Hub hub = new Hub();

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    hub.run();
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("fork-react/build/");
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
    Spark.post("/getRestByID", new HandlerGetRestByID());
    Spark.post("/getRestByRad", new HandlerGetRestByRad());
    Spark.post("/getAllUsers", new HandlerAllUsers());
    Spark.post("/getUserByID", new HandlerGetUserByID());
    Spark.post("/getAllUserIds", new HandlerAllUserIds());
    Spark.post("/registerUser", new HandlerRegisterUser());
    Spark.post("/deleteUser", new HandlerDeleteUser());
    Spark.post("/upsertUser", new HandlerUpsertUser());
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
