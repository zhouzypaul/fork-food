package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * This is thrown when an unexpected IO error occurs.
 */
public class IOErrorException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   *
   * @param message error message
   */
  public IOErrorException(String message) {
    super(message);
  }
}
