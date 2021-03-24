package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * exception for when the user enters an invalid command.
 */
public class IllegalCommandException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor for IllegalCommandException.
   * @param s an error message
   */
  public IllegalCommandException(String s) {
    super(s);
  }
}
