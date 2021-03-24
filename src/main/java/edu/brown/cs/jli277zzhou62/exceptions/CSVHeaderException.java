package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * exception for when the header format of a csv is incorrect.
 */
public class CSVHeaderException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor for this class.
   * @param message an error message
   */
  public CSVHeaderException(String message) {
    super(message);
  }
}
