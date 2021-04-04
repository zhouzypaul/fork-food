package edu.brown.cs.fork.exceptions;

/**
 * thrown when the label of a labeled data is illegal.
 */
public class OutOfRangeException extends ErrorException {
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public OutOfRangeException(String message) {
    super(message);
  }
}
