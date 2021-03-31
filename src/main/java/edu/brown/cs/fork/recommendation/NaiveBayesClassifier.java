package edu.brown.cs.fork.recommendation;

import edu.brown.cs.fork.exceptions.NoTrainDataException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
  private List<List<List<Double>>> classCondProb;
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
    // TODO:
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
   * The 1st index of this.classCondProb indexes into the class label, while the 2nd index
   * indexes into the attribute, and the 3rd index indexes into the possible value of the attribute
   * (the dimension of the given attribute).
   */
  private void getClassCondProb() {

    // iterate through the training data
    for (L labeledData : this.trainingData) {
      int label = labeledData.getLabel();
    }

    // init
    this.classCondProb = new LinkedList<>();

    // iterate through each of the class labels
    for (int c = 0; c < this.numClasses; c++) {

      // init conditional prob for current class
      List<List<Double>> classProb = new LinkedList<>();

      // get all training data with label c
      int finalC = c;
      List<L> dataInClass = this.trainingData.stream().filter(data -> data.getLabel() == finalC)
              .collect(Collectors.toList());

      // iterate over all the attributes
      for (int attr = 0; attr < this.numAttr; attr++) {
        List<Double> attrProb = new LinkedList<>();

        // iterate over all the possible value of the current attribute
        for (int attrValue = 0; attrValue < this.attrDim[attr]; attrValue++) {
          int finalAttr = attr;
          int finalAttrValue = attrValue;
          // number of data points in classData that have attrValue for attr
          int numDataAttr = (int) dataInClass.stream()
                  .filter(data -> data.getData().getAttr()[finalAttr] == finalAttrValue).count();
          attrProb.add(attrValue,
                  (double) (numDataAttr + 1) / (dataInClass.size() + this.attrDim[attr]));
        }

        // record attrProb at the end of iterating over the current attr
        classProb.add(attr, attrProb);
      }

      // record condProb at the end of iterating over the current class c
      this.classCondProb.add(c, classProb);
    }
  }
}
