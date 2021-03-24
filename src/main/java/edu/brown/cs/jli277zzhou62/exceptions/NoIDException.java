package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * thrown when a data type has no ID.
 */
public class NoIDException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message
   */
  public NoIDException(String message) {
    super(message);
  }
}
