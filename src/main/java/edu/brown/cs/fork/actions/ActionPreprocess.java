package edu.brown.cs.fork.actions;

import edu.brown.cs.fork.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains miscellaneous methods to get to know the database.
 */
public class ActionPreprocess implements TriggerAction {
  private final Database restDB = new Database();
  private Connection restConn;

  /**
   * Constructor.
   */
  public ActionPreprocess() { }

  @Override
  public String command() {
    return "prep";
  }

  /**
   * Get unique categories from the database.
   * @return a list of string representing unique categories sorted in alphabetical order
   * @throws SQLException SQLException
   */
  public List<String> getUniqueCategories() throws SQLException {
    List<String> cats = new ArrayList<>();
    String sql = "SELECT res.categories FROM restaurants as res;";
    PreparedStatement prep = this.restConn.prepareStatement(sql);
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      String pattern = "[^,]+";
      Pattern r = Pattern.compile(pattern);
      String cat = rs.getString(1);
      Matcher m = r.matcher(cat);
      if (m.find()) {
        List<String> categories = Collections.singletonList(m.group());
        cats.addAll(categories);
      }
    }
    prep.close();
    rs.close();

    Set<String> uniqueCats = new HashSet<>(cats);
    List<String> catList = new ArrayList<>(uniqueCats);
    Collections.sort(catList);
    return catList;
  }

  /**
   * Prints unique categories.
   * @param cats a list of strings representing unique categories in alphabetical order
   */
  public void printUniqueCategories(List<String> cats) {
    for (String cat : cats) {
      System.out.println(cat);
    }
  }

  @Override
  public void run(String[] args) {
    if (args.length != 2) {
      System.out.println("ERROR: Wrong number of arguments");
    }

    String command = args[1];

    if (command.equals("get_cats")) {
      try {
        this.restDB.initDatabase("data/restaurants.sqlite3");
        this.restConn = this.restDB.getConn();
        printUniqueCategories(getUniqueCategories());
      } catch (SQLException | ClassNotFoundException e) {
        System.out.println("ERROR: " + e.getMessage());
      }
    }
  }
}
