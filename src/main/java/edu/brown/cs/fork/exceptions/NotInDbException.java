package edu.brown.cs.fork.exceptions;

/**
 * thrown when a specified data is not found in the database.
 */
public class NotInDbException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message
   */
  public NotInDbException(String message) {
    super(message);
  }
}
