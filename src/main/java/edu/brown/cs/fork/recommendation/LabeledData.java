package edu.brown.cs.fork.recommendation;

/**
 * An interface for a tuple of Recommendable and its label.
 */
public interface LabeledData {
  /**
   * get the data portion of the tuple.
   * @return the data.
   */
  Recommendable getData();

  /**
   * get the label portion of the tuple.
   * @return the int label.
   */
  int getLabel();
}
