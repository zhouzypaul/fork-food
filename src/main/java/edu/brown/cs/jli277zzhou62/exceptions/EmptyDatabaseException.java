package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * thrown when the database is empty but the user tries to query.
 */
public class EmptyDatabaseException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor for EmptyDatabaseException.
   * @param message error message.
   */
  public EmptyDatabaseException(String message) {
    super(message);
  }
}
