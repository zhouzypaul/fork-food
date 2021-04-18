package edu.brown.cs.fork.database.actions;

import edu.brown.cs.fork.exceptions.NoUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Supports the ranking algorithm for top restaurant.
 */
public class Ranking {
  private final Connection conn;

  /**
   * Establishes database connection.
   * @param conn connection
   */
  public Ranking(Connection conn) {
    this.conn = conn;
  }

  /**
   * Get user's gottenWay parameter.
   * @param userId user id
   * @return user's gottenWay parameter
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public double getUserGottenWay(String userId)
    throws SQLException, NoUserException {
    String sql = "SELECT gottenWay FROM login WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setString(1, userId);
    double gottenWay = -1.0;
    ResultSet rs = prep.executeQuery();
    while (rs.next()) {
      gottenWay = rs.getDouble(1);
    }
    prep.close();
    rs.close();
    if (gottenWay == -1) {
      throw new NoUserException("User " + userId + " not found.");
    }
    return gottenWay;
  }

  /**
   * Updates user's gottenWay parameter.
   * @param userId user id
   * @param gottenWay user's gottenWay parameter
   * @return whether the update is successful
   * @throws SQLException SQLException
   */
  public boolean updateUserGottenWay(String userId, double gottenWay) throws SQLException {
    String sql = "UPDATE login SET gottenWay = ? WHERE userId = ?;";
    PreparedStatement prep = this.conn.prepareStatement(sql);
    prep.setDouble(1, gottenWay);
    prep.setString(2, userId);
    int affectedRows = prep.executeUpdate();
    return (affectedRows == 1);
  }
}
