package edu.brown.cs.fork.exceptions;

/**
 * thrown when there are no users to be handled.
 */
public class NoUserException extends ErrorException {
  private static final long serialVersionUID = 0L;
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public NoUserException(String message) {
    super(message);
  }
}
