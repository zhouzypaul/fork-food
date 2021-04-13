package edu.brown.cs.fork.exceptions;

/**
 * thrown when there are no users to be handled.
 */
public class NoUserException extends ErrorException {
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public NoUserException(String message) {
    super(message);
  }
}
