package edu.brown.cs.fork.exceptions;

/**
 * exceptions whose error message always starts with "ERROR: ".
 */
public class ErrorException extends Exception {
  private static final long serialVersionUID = 0L;

  /**
   * constructor for ErrorException.
   * @param message a raw error message
   */
  public ErrorException(String message) {
    super("ERROR: " + message);
  }
}
