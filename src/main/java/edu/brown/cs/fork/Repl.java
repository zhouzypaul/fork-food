package edu.brown.cs.fork;

import edu.brown.cs.fork.actions.TriggerAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that handles REPL.
 */
public class Repl {

  private final HashMap<String, TriggerAction> registered = new HashMap<>();

  /**
   * Constructor.
   */
  public Repl() { }

  /**
   * Adds a TriggerAction to this.registered.
   * @param action a TriggerAction
   */
  public void registerAction(TriggerAction action) {
    registered.put(action.command(), action);
  }

  /**
   * Runs the REPL.
   */
  public void run() {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;

      while ((input = br.readLine()) != null) {
        boolean validCommand = false;
        // match with white space, valid if there's a match
        String whiteSpace = "\\s*";
        Matcher matcherWhiteSpace = Pattern.compile(whiteSpace).matcher(input);
        boolean matchesWhiteSpace = matcherWhiteSpace.matches();
        if (matchesWhiteSpace) {
          validCommand = true;
        }
        // reference: https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
        String[] parsed = input.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        // if parsed is not empty
        if (!Arrays.equals(parsed, new String[]{})) {
          TriggerAction triggerAction = this.registered.get(parsed[0]);
          if (triggerAction != null) {
            // trigger the action
            triggerAction.run(parsed);
            validCommand = true;
          }
        }
        if (!validCommand) {
          System.out.println("ERROR: Invalid command");
        }
      }
    } catch (IOException e) {
      System.out.println("ERROR: Invalid input for REPL (IOException)");
    }
  }
}