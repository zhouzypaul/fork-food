package edu.brown.cs.jli277zzhou62.database;

import edu.brown.cs.jli277zzhou62.ITest;
import edu.brown.cs.jli277zzhou62.exceptions.ErrorException;
import edu.brown.cs.jli277zzhou62.exceptions.NoSuchFileException;
import edu.brown.cs.jli277zzhou62.exceptions.SQLErrorException;
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
