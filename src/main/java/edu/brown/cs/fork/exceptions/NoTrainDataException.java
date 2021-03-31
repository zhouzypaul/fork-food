package edu.brown.cs.fork.exceptions;

/**
 * used by the recommendation algorithm, when no training data is provided.
 */
public class NoTrainDataException extends ErrorException {
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public NoTrainDataException(String message) {
    super(message);
  }
}
