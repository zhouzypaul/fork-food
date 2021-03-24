package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * this exception is thrown when a csv file is completely empty.
 */
public class CSVEmptyException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message.
   */
  public CSVEmptyException(String message) {
    super(message);
  }
}
