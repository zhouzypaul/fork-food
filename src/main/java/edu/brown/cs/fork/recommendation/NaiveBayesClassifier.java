package edu.brown.cs.fork.recommendation;

import java.util.List;

/**
 * a Naive Bayes classifier to recommend data.
 * @param <R> a type of recommendable
 * @param <L> a type of labeled Data
 */
public class NaiveBayesClassifier<R extends Recommendable, L extends LabeledData<R>>
        implements Recommender<R> {
  public NaiveBayesClassifier(List<LabeledData<R>> trainingData, List<Recommendable> testData) {
    // TODO:
  }

  @Override
  public List<R> recommend(int n) {
    return null;
  }

  public void train() {
    // TODO:
  }
}
