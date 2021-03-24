package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * exception for when an empty line is in the csv.
 */
public class CSVEmptyLineException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor for EmptyLinInCSVException.
   * @param s an error message
   */
  public CSVEmptyLineException(String s) {
    super(s);
  }
}
