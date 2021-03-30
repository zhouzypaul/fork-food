package edu.brown.cs.fork.recommendation;

import java.util.List;

/**
 * a recommendation algorithm that is able to recommend Recommendable.
 */
public interface Recommender {
  /**
   * recommend a list of recommendable.
   * @param n the number of items to recommend
   *
   * @return a list of n recommendable that's recommended by the algorithm.
   */
  List<Recommendable> recommend(int n);
}
