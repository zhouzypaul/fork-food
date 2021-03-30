package edu.brown.cs.fork.recommendation;

import java.util.List;

/**
 * a Naive Bayes classifier to recommend data.
 */
public class NaiveBayesClassifier<R extends Recommendable, L extends LabeledData>
        implements Recommender {
  public NaiveBayesClassifier(List<LabeledData> trainingData, List<Recommendable> testData) {
    // TODO:
  }

  @Override
  public List<Recommendable> recommend(int n) {
    return null;
  }

  public void train() {
    // TODO:
  }
}
