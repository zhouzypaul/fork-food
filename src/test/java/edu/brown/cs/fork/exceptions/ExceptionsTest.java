package edu.brown.cs.fork.exceptions;

import edu.brown.cs.fork.ITest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * test all the custom exceptions
 */
public class ExceptionsTest implements ITest {
  private ErrorException emptyDb;
  private ErrorException err;
  private ErrorException illegalCmd;
  private ErrorException io;
  private ErrorException neg;
  private ErrorException noId;
  private ErrorException noneDouble;
  private ErrorException nonInt;
  private ErrorException noFile;
  private ErrorException db;
  private ErrorException sql;

  /**
   * set up all exceptions.
   */
  @Override
  public void setUp() {
    this.emptyDb = new EmptyDatabaseException("error message");
    this.err = new ErrorException("error message");
    this.illegalCmd = new IllegalCommandException("error message");
    this.io = new IOErrorException("error message");
    this.neg = new NegativeException("error message");
    this.noId = new NoIDException("error message");
    this.noneDouble = new NoneDoubleException("error message");
    this.nonInt = new NoneIntegerException("error message");
    this.noFile = new NoSuchFileException("error message");
    this.db = new NotInDbException("error message");
    this.sql = new SQLErrorException("error message");
  }

  /**
   * reset all exceptions.
   */
  @Override
  public void tearDown() {
    this.emptyDb = null;
    this.err = null;
    this.illegalCmd = null;
    this.io = null;
    this.neg = null;
    this.noId = null;
    this.noneDouble = null;
    this.nonInt = null;
    this.noFile = null;
    this.db = null;
    this.sql = null;
  }

  /**
   * tests all the constructors, see if they work properly.
   */
  @Test
  public void testConstructor() {
    this.setUp();
    assertEquals(EmptyDatabaseException.class, this.emptyDb.getClass());
    assertEquals(ErrorException.class, this.err.getClass());
    assertEquals(IllegalCommandException.class, this.illegalCmd.getClass());
    assertEquals(IOErrorException.class, this.io.getClass());
    assertEquals(NegativeException.class, this.neg.getClass());
    assertEquals(NoIDException.class, this.noId.getClass());
    assertEquals(NoneDoubleException.class, this.noneDouble.getClass());
    assertEquals(NoneIntegerException.class, this.nonInt.getClass());
    assertEquals(NoSuchFileException.class, this.noFile.getClass());
    assertEquals(NotInDbException.class, this.db.getClass());
    assertEquals(SQLErrorException.class, this.sql.getClass());
    this.tearDown();
  }
}
