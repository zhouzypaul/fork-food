package edu.brown.cs.fork.recommendation;

import java.util.List;

/**
 * a general recommendation algorithm that is able to recommend Recommendable.
 * @param <R> a type of Recommendable.
 */
public interface Recommender<R extends Recommendable> {
  /**
   * recommend a list of recommendable.
   * @param n the number of items to recommend
   * @param classLabel which class label should the algorithm aim at recommending. (in other words,
   *                   the algorithm will recommend items that are more likely to have this class
   *                   label).
   *
   * @return a list of n recommendable that's recommended by the algorithm. If there are not enough
   * testing data to return n items, just return the whole testing set.
   */
  List<R> recommend(int n, int classLabel);
}
