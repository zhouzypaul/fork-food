package edu.brown.cs.fork.database;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.database.queries.QueryUsers;
import edu.brown.cs.fork.exceptions.NoUserException;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class QueryUsersTest implements ITest {
  private final QueryUsers db = new QueryUsers();

  @Override
  public void setUp() {
    this.db.initUsers("data/test_users.sqlite3");
  }

  @Override
  public void tearDown() {
    this.db.close();
  }

  @Test
  public void testGetUserGottenWay() {
    setUp();

    try {
      float gottenWay = this.db.getUserGottenWay("sean");
      assertEquals(gottenWay, 0.8, 0.00001);
      gottenWay = this.db.getUserGottenWay("paul");
      assertEquals(gottenWay, 0.9, 0.00001);
    } catch (SQLException | NoUserException e) {
      fail();
    }

    tearDown();
  }

  @Test
  public void testUpdateUserGottenWay() {
    setUp();

    try {
      boolean success = this.db.updateUserGottenWay("ed", 0.2f);
      assertTrue(success);
      float gottenWay = this.db.getUserGottenWay("ed");
      assertEquals(gottenWay, 0.2, 0.00001);
      success = this.db.updateUserGottenWay("ed", 0.3f);
      assertTrue(success);
      gottenWay = this.db.getUserGottenWay("ed");
      assertEquals(gottenWay, 0.3, 0.00001);
      success = this.db.updateUserGottenWay("alan", 0.65f);
      assertTrue(success);
      gottenWay = this.db.getUserGottenWay("alan");
      assertEquals(gottenWay, 0.65, 0.00001);
      success = this.db.updateUserGottenWay("alan", 0.11f);
      assertTrue(success);
      gottenWay = this.db.getUserGottenWay("alan");
      assertEquals(gottenWay, 0.11, 0.00001);
    } catch (SQLException | NoUserException e) {
      fail();
    }

    tearDown();
  }
}
