package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * this exception is used when an argument that's supposed to be integer is not.
 */
public class NoneIntegerException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message.
   */
  public NoneIntegerException(String message) {
    super(message);
  }
}
