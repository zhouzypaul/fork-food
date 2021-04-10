package edu.brown.cs.fork.recommendation;

/**
 * An interface for a tuple of Recommendable and its label.
 * @param <R> a type of recommendable.
 */
public interface LabeledData<R extends Recommendable> {
  /**
   * get the data portion of the tuple.
   * @return the data.
   */
  R getData();

  /**
   * get the label portion of the tuple. The label should be in range(0, R.getNumClasses()).
   * @return the int label.
   */
  int getLabel();
}
