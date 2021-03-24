package edu.brown.cs.jli277zzhou62.exceptions;

/**
 * This is thrown when a Node (e.g. a Way's start or end Node) is not found.
 */
public class NodeNotFoundException extends ErrorException {
  private static final long serialVersionUID = 0L;

  /**
   * constructor.
   *
   * @param message error message
   */
  public NodeNotFoundException(String message) {
    super(message);
  }
}
