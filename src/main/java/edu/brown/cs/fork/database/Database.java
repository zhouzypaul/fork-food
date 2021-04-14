package edu.brown.cs.fork.database;

import edu.brown.cs.fork.exceptions.SQLErrorException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class representing a database. The database is able to make connection to a SQL database and
 * make prepared statements with the connected database.
 */
public class Database {
  private static Connection conn = null;
  private boolean connected = false;

  /**
   * Constructor.
   */
  public Database() { }

  /**
   * Initialize database connection, only wipes cache if new database is connected.
   * @param filename name of the database
   * @throws SQLException sql exception
   * @throws ClassNotFoundException class not found
   */
  public void initDatabase(String filename) throws SQLException, ClassNotFoundException {
    this.connected = true;
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
  }

  /**
   * Getter for the database connection.
   * @return the database connection
   */
  public Connection getConn() {
    return this.conn;
  }

  /**
   * Getter for if database is connected.
   * @return whether the database is connected
   */
  public boolean isConnected() {
    return this.connected;
  }

  /**
   * Closes connection to database (call on cleanup).
   *
   * @throws SQLErrorException when database connection cannot be closed
   */
  public void close() throws SQLErrorException {
    try {
      this.conn.close();
    } catch (SQLException e) {
      throw new SQLErrorException("could not close database connection");
    }
  }
}
