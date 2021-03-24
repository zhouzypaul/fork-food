package edu.brown.cs.fork.exceptions;

/**
 * thrown when a data name is not surrounded by quotation marks.
 */
public class NameNotInQuotesException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message error message.
   */
  public NameNotInQuotesException(String message) {
    super(message);
  }
}
