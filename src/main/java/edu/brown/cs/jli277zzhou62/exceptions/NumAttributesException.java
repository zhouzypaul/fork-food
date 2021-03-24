package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * this is thrown when a data type has the incorrect number of attributes.
 */
public class NumAttributesException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message
   */
  public NumAttributesException(String message) {
    super(message);
  }
}
