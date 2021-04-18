package edu.brown.cs.fork.database.actions;

import edu.brown.cs.fork.exceptions.NoUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
  private final Connection conn;

  public Login(Connection conn) {
    this.conn = conn;
  }

  /**
   * Puts user login information in the database.
   * @param userId user id
   * @param pwd password (already encrypted with jwt)
   * @return true if registered successfully, false if otherwise
   */
  public boolean registerUser(String userId, String pwd) {
    String sql = "INSERT INTO login VALUES (?, ?, 1, ?, ?);";
    try {
      PreparedStatement prep = this.conn.prepareStatement(sql);
      prep.setString(1, userId);
      prep.setString(2, pwd);
      prep.setString(3, "");
      prep.setString(4, "");
      prep.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: Could not add a new user to the database");
      return false;
    }
  }

  /**
   * Get user password.
   * @param userId user id
   * @return password of user with userId
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public String getPwd(String userId) throws SQLException, NoUserException {
    String result = "";
    String sql = "SELECT password FROM login WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, userId);
    ResultSet rs = prep.executeQuery();
    int count = 0;
    while (rs.next()) {
      count += 1;
      result = rs.getString(1);
    }
    if (count == 0) {
      throw new NoUserException("User: " + userId + " doesn\'t exist.");
    }
    prep.close();
    rs.close();
    return result;
  }

  /**
   * Changes user's password.
   * @param userId id of user to update
   * @param newPwd new password
   * @return a boolean indicating whether the update is successful
   * @throws NoUserException if the user can't be found in the database
   */
  public boolean changePwd(String userId, String newPwd)
    throws NoUserException {
    String sql = "UPDATE login SET password = ? WHERE userId = ?;";
    PreparedStatement prep = null;
    try {
      prep = this.conn.prepareStatement(sql);
      prep.setString(1, newPwd);
      prep.setString(2, userId);
      int affectedRows = prep.executeUpdate();
      if (affectedRows == 0) {
        throw new NoUserException("User: " + userId + " doesn\'t exist.");
      }
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  /**
   * Deletes user with userID from database.
   * @param userID id of user to delete
   * @return true if deletion is successful, false if otherwise
   */
  public boolean deleteUser(String userID) {
    String sql = "DELETE FROM login WHERE userId = ?;";
    String sql2 = "DELETE FROM training WHERE userId = ?;";
    try {
      PreparedStatement prep = conn.prepareStatement(sql);
      PreparedStatement prep2 = conn.prepareStatement(sql2);
      prep.setString(1, userID);
      prep2.setString(1, userID);
      prep.executeUpdate();
      prep2.executeUpdate();
      return true;
    } catch (SQLException e) {
      System.out.println("ERROR: Could not delete user " + userID + " from the database");
      return false;
    }
  }
}
