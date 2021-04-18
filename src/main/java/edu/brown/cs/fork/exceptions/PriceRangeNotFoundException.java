package edu.brown.cs.fork.exceptions;

/**
 * When a price range isn't found in our price range list of interest.
 */
public class PriceRangeNotFoundException extends ErrorException {
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public PriceRangeNotFoundException(String message) {
    super(message);
  }
}
