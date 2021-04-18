package edu.brown.cs.fork.exceptions;

/**
 * When a category is not found in the list of categories of interest.
 */
public class CategoryNotFoundException extends ErrorException {
  /**
   * constructor for ErrorException.
   *
   * @param message a raw error message
   */
  public CategoryNotFoundException(String message) {
    super(message);
  }
}
