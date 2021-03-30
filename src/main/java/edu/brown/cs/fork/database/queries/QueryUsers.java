package edu.brown.cs.fork.database.queries;

import com.google.gson.Gson;
import edu.brown.cs.fork.database.Database;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryUsers {
  private Database db = new Database();
  private Connection conn;
  private static final Gson GSON = new Gson();

  public QueryUsers() {

  }

  public void initUsers(String dbPath) {
    try {
      db.initDatabase(dbPath);
      this.conn = db.getConn();
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  public boolean isConnected() {
    return db.isConnected();
  }
}
