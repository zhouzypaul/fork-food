package edu.brown.cs.jli277zzhou62.exceptions;

import edu.brown.cs.jli277zzhou62.ITest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * test all the custom exceptions
 */
public class ExceptionsTest implements ITest {
  private ErrorException csvDiffCol;
  private ErrorException csvEmpty;
  private ErrorException csvEmptyLine;
  private ErrorException csvHeader;
  private ErrorException emptyDb;
  private ErrorException err;
  private ErrorException illegalCmd;
  private ErrorException io;
  private ErrorException nameQuotes;
  private ErrorException neg;
  private ErrorException nodeNotFound;
  private ErrorException noId;
  private ErrorException noneDouble;
  private ErrorException nonInt;
  private ErrorException noFile;
  private ErrorException db;
  private ErrorException numAttr;
  private ErrorException sql;

  /**
   * set up all exceptions.
   */
  @Override
  public void setUp() {
    this.csvDiffCol = new CSVDiffNumColException("error message");
    this.csvEmpty = new CSVEmptyException("error message");
    this.csvEmptyLine = new CSVEmptyLineException("error message");
    this.csvHeader = new CSVHeaderException("error message");
    this.emptyDb = new EmptyDatabaseException("error message");
    this.err = new ErrorException("error message");
    this.illegalCmd = new IllegalCommandException("error message");
    this.io = new IOErrorException("error message");
    this.nameQuotes = new NameNotInQuotesException("error message");
    this.neg = new NegativeException("error message");
    this.nodeNotFound = new NodeNotFoundException("error message");
    this.noId = new NoIDException("error message");
    this.noneDouble = new NoneDoubleException("error message");
    this.nonInt = new NoneIntegerException("error message");
    this.noFile = new NoSuchFileException("error message");
    this.db = new NotInDbException("error message");
    this.numAttr = new NumAttributesException("error message");
    this.sql = new SQLErrorException("error message");
  }

  /**
   * reset all exceptions.
   */
  @Override
  public void tearDown() {
    this.csvDiffCol = null;
    this.csvEmpty = null;
    this.csvEmptyLine = null;
    this.csvHeader = null;
    this.emptyDb = null;
    this.err = null;
    this.illegalCmd = null;
    this.io = null;
    this.nameQuotes = null;
    this.neg = null;
    this.nodeNotFound = null;
    this.noId = null;
    this.noneDouble = null;
    this.nonInt = null;
    this.noFile = null;
    this.db = null;
    this.numAttr = null;
    this.sql = null;
  }

  /**
   * tests all the constructors, see if they work properly.
   */
  @Test
  public void testConstructor() {
    this.setUp();
    assertEquals(CSVDiffNumColException.class, this.csvDiffCol.getClass());
    assertEquals(CSVEmptyException.class, this.csvEmpty.getClass());
    assertEquals(CSVEmptyLineException.class, this.csvEmptyLine.getClass());
    assertEquals(CSVHeaderException.class, this.csvHeader.getClass());
    assertEquals(EmptyDatabaseException.class, this.emptyDb.getClass());
    assertEquals(ErrorException.class, this.err.getClass());
    assertEquals(IllegalCommandException.class, this.illegalCmd.getClass());
    assertEquals(IOErrorException.class, this.io.getClass());
    assertEquals(NameNotInQuotesException.class, this.nameQuotes.getClass());
    assertEquals(NegativeException.class, this.neg.getClass());
    assertEquals(NodeNotFoundException.class, this.nodeNotFound.getClass());
    assertEquals(NoIDException.class, this.noId.getClass());
    assertEquals(NoneDoubleException.class, this.noneDouble.getClass());
    assertEquals(NoneIntegerException.class, this.nonInt.getClass());
    assertEquals(NoSuchFileException.class, this.noFile.getClass());
    assertEquals(NotInDbException.class, this.db.getClass());
    assertEquals(NumAttributesException.class, this.numAttr.getClass());
    assertEquals(SQLErrorException.class, this.sql.getClass());
    this.tearDown();
  }
}
