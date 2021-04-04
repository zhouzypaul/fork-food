package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.exceptions.IllegalLabelException;
import edu.brown.cs.fork.recommendation.LabeledData;

/**
 * A restaurant with label of whether the restaurant is liked or not liked.
 * This Labeled Restaurant is used as training data for the recommendation package.
 */
public class LabeledRestaurant implements LabeledData<Restaurant> {
  private final Restaurant data;
  private final int label;

  /**
   * constructor for a LabeledRestaurant.
   * @param data - the Restaurant object.
   * @param label - the label for the data (either 1 or 0). 1 corresponds to the user liking the
   *              restaurant, and 0 corresponds to dislike.
   *
   * @throws IllegalLabelException when the label is not 1 or 0
   */
  public LabeledRestaurant(Restaurant data, int label) throws IllegalLabelException {
    if ((label != 0) && (label != 1)) {
      throw new IllegalLabelException("Restaurant Label can only be 0 or 1");
    }
    this.data = data;
    this.label = label;
  }

  @Override
  public Restaurant getData() {
    return this.data;
  }

  @Override
  public int getLabel() {
    return this.label;
  }
}
