package edu.brown.cs.fork.exceptions;

/**
 * thrown when the label of a labeled data is illegal.
 */
public class IllegalLabelException extends ErrorException {
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public IllegalLabelException(String message) {
    super(message);
  }
}
