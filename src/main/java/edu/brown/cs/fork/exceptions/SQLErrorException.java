package edu.brown.cs.fork.exceptions;

/**
 * This is thrown when a bad SQL operation is performed.
 */
public class SQLErrorException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   *
   * @param message error message
   */
  public SQLErrorException(String message) {
    super(message);
  }
}
