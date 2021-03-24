package edu.brown.cs.fork.database;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.ErrorException;
import edu.brown.cs.fork.exceptions.NoSuchFileException;
import edu.brown.cs.fork.exceptions.SQLErrorException;
import org.junit.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

/**
 * Tests Database.
 */
public class DatabaseTest implements ITest {
  private Database db;

  @Override
  public void setUp() {
    try {
      this.db = new Database("data/maps/smallMaps.sqlite3");
    } catch (ErrorException e) {
      fail();
    }
  }

  @Override
  public void tearDown() {
    try {
      this.db.close();
    } catch (ErrorException e) {
      fail();
    }
  }

  /**
   * Tests constructor.
   */
  @Test
  public void testConstructor() {
    assertThrows(NoSuchFileException.class, () -> {
      new Database("hello");
    });
  }

  /**
   * Tests createStatement.
   */
  @Test
  public void testCreateStatement() {
    this.setUp();
    try {
      this.db.createStatement("SELECT * FROM node;");
    } catch (ErrorException e) {
      fail();
    }
    this.tearDown();
    assertThrows(SQLErrorException.class, () -> {
      this.db.createStatement("SELECT * FROM node;");
    });
  }
}
