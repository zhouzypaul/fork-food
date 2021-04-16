package edu.brown.cs.fork.exceptions;

/**
 * Error for when no restaurants are found.
 */
public class NoRestaurantException extends ErrorException {

  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public NoRestaurantException(String message) {
    super(message);
  }
}
