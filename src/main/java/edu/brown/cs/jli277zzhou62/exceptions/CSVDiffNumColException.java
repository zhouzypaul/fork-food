package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * exception for when the different rows in the csv has different number of elements.
 */
public class CSVDiffNumColException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor for CSVDiffNumRowException.
   * @param s an error message
   */
  public CSVDiffNumColException(String s) {
    super(s);
  }
}
