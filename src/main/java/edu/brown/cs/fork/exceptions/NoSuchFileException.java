package edu.brown.cs.fork.exceptions;

/**
 * exception for when file is not found.
 */
public class NoSuchFileException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   * @param message err message
   */
  public NoSuchFileException(String message) {
    super(message);
  }
}
