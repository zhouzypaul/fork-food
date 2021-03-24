package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * exception for when an argument is supposed to be non-negative, but got a negative argument.
 */
public class NegativeException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message.
   */
  public NegativeException(String message) {
    super(message);
  }
}
