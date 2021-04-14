package edu.brown.cs.fork.database;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.ErrorException;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Tests Database.
 */
public class DatabaseTest implements ITest {
  private Database db;

  @Override
  public void setUp() {
    this.db = new Database();
  }

  @Override
  public void tearDown() {
    try {
      this.db.close();
    } catch (ErrorException e) {
      fail();
    }
  }

  @Test
  public void testDatabase() {
    try {
      setUp();
      this.db.initDatabase("data/test_users.sqlite3");
      assertTrue(this.db.isConnected());
      assertFalse(this.db.getConn().isClosed());
      tearDown();
      assertTrue(this.db.getConn().isClosed());
    } catch (SQLException | ClassNotFoundException e) {
      fail();
    }
  }
}
