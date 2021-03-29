package edu.brown.cs.fork.database;

import edu.brown.cs.fork.exceptions.NoSuchFileException;
import edu.brown.cs.fork.exceptions.SQLErrorException;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
   * @return whether it's new connection
   * @throws SQLException sql exception
   * @throws ClassNotFoundException class not found
   */
  public void initDatabase(String filename) throws SQLException, ClassNotFoundException {
    this.connected = true;
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
  }

  public Connection getConn() {
    return this.conn;
  }

  public boolean isConnected() {
    return this.connected;
  }

  /**
   * Creates a PreparedStatement with the database connection.
   *
   * @param sql content of the statement
   * @return the statement with the provided content
   * @throws SQLErrorException when a statement cannot be created (e.g. cannot obtain database lock)
   */
  public PreparedStatement createStatement(String sql) throws SQLErrorException {
    try {
      return this.conn.prepareStatement(sql);
    } catch (SQLException e) {
      throw new SQLErrorException("could not prepare a new SQL statement");
    }
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
