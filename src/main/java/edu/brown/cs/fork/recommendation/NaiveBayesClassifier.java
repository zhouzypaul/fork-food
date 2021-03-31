package edu.brown.cs.fork.recommendation;

import edu.brown.cs.fork.exceptions.NoTrainDataException;

import java.util.Arrays;
import java.util.List;

/**
 * a Naive Bayes classifier to recommend data. This generative classifier assumes that the input
 * features are independent with each other.
 *
 * @param <R> a type of recommendable
 * @param <L> a type of labeled Data
 */
public class NaiveBayesClassifier<R extends Recommendable, L extends LabeledData<R>>
        implements Recommender<R> {

  private final int numAttr;
  private final int[] attrDim;
  private final int numClasses;
  private double[] priorProb;
  private List<List<Double>> classCondProb;
  private final List<L> trainingData;
  private final List<R> testData;

  /**
   * constructor for NaiveBayes Classifier.
   * A new instance of this class should be initialized when the training data or the test data
   * is different.
   * @param trainingData - a training dataset for the recommendation, using past experiences to
   *                     train the naive bayes classifier.
   * @param testData - a testing dataset, which is the data that the classifier will try to
   *                 predict on.
   * @throws NoTrainDataException when the trainingData is an empty list.
   */
  public NaiveBayesClassifier(List<L> trainingData, List<R> testData) throws NoTrainDataException {
    if (trainingData.isEmpty()) {
      throw new NoTrainDataException("no training data for naive bayes classifier");
    }
    this.trainingData = trainingData;
    this.testData = testData;
    this.numAttr = this.trainingData.get(0).getData().getNumAttr();
    this.attrDim = this.trainingData.get(0).getData().getAttrDim();
    this.numClasses = this.trainingData.get(0).getData().getNumAttr();
  }

  @Override
  public List<R> recommend(int n) {
    this.train();
    return null;
  }

  /**
   * train the naive bayes classifier on the training dataset by getting the prior probabilities
   * and the class conditional probabilities.
   * This method is called at the start of recommend(), so that the classifier is trained before
   * making any recommendations.
   */
  private void train() {
    this.getPriors();
    this.getClassCondProb();
  }

  /**
   * get the prior probabilities (P(Y=y) for all y) from the training dataset.
   * This calculates the empirical probability of appearance of each of the class labels.
   * Iterate through the training data, and look at each label, record their distribution.
   * The prior probabilities are saved to this.priorProb
   *
   * Laplace smoothing is used in this method: we pretend to have observed 1 training example from
   * each class.
   */
  private void getPriors() {

    // init a counter array, count occurrences from each class
    int[] classCounter = new int[this.numClasses];
    // laplace smoothing
    Arrays.fill(classCounter, 1);

    // iterate through the training data
    for (L labeledData : this.trainingData) {
      int label = labeledData.getLabel();
      // update prior prob
      // prob += 1/n
      classCounter[label] = classCounter[label] + 1;
    }

    // calculate prior prob
    this.priorProb = new double[this.numClasses];
    for (int i = 0; i < this.numClasses; i++) {
      int count = classCounter[i];
      this.priorProb[i] = (double) count / (this.trainingData.size() + this.numClasses);
    }
  }

  /**
   * get the class conditional probabilities (P(X=x|Y=y) for all x, y) from the training set.
   * This class conditional probabilities are saved to this.classCondProb.
   *
   * The 1st index of this.classCondProb indexes into the class label, while the second index
   * indexes into the attribute dimension.
   */
  private void getClassCondProb() {

    // iterate through the training data
    for (L labeledData : this.trainingData) {
      int label = labeledData.getLabel();
    }

    // iterate through each of the class labels
    for (int c = 0; c < this.numClasses; c++) {
      // iterate through the
    }
  }
}
