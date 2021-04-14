package edu.brown.cs.fork.actions;

/**
 * Interface for a trigger action.
 */
public interface TriggerAction {
  /**
   * A string representing the command of the trigger action.
   * @return nothing
   */
  String command();

  /**
   * Performs action with user arguments.
   * @param args String List representing user arguments
   */
  void run(String[] args);
}
