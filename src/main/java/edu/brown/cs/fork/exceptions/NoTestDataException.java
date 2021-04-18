package edu.brown.cs.fork.exceptions;

/**
 * used by the recommendation algorithm, when no training data is provided.
 */
public class NoTestDataException extends ErrorException {
  private static final long serialVersionUID = 0L;
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public NoTestDataException(String message) {
    super(message);
  }
}
