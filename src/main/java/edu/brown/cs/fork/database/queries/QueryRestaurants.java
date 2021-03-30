package edu.brown.cs.fork.database.queries;

import com.google.gson.Gson;
import edu.brown.cs.fork.database.Database;
import edu.brown.cs.fork.exceptions.NoSuchFileException;
import edu.brown.cs.fork.exceptions.SQLErrorException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryRestaurants {
  private Database db = new Database();
  private Connection conn;
  private static final Gson GSON = new Gson();

  public QueryRestaurants() { }

  public void initRestaurants(String dbPath) {
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

  public List<String> getAllRestaurants() throws SQLException {
    List<String> results = new ArrayList<>();
    String sql = "SELECT res.name, res.state FROM yelp_academic_dataset_business as res WHERE res.categories LIKE '%Food%';";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      String resName = rs.getString(1);
      String state = rs.getString(2);
      Map<String, String> hMap = new HashMap<>();
      hMap.put("name", resName);
      hMap.put("state", state);
      results.add(GSON.toJson(hMap));
    }
    prep.close();
    rs.close();
    return results;
  }
}
