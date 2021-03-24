package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * thrown when an arg is expected to be a double, but did not receive a valid double.
 */
public class NoneDoubleException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message.
   */
  public NoneDoubleException(String message) {
    super(message);
  }
}
