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
   *
   * @return a list of n recommendable that's recommended by the algorithm.
   */
  List<R> recommend(int n);
}
