package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.recommendation.LabeledData;

import java.util.Objects;

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
   * @throws OutOfRangeException when the label is not 1 or 0
   */
  public LabeledRestaurant(Restaurant data, int label) throws OutOfRangeException {
    if ((label != 0) && (label != 1)) {
      throw new OutOfRangeException("Restaurant Label can only be 0 or 1");
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

  @Override
  public String toString() {
    return new StringBuilder().append("LabeledRestaurant{").append("data=").append(data)
        .append(", label=").append(label).append('}').toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LabeledRestaurant that = (LabeledRestaurant) o;
    return label == that.label && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, label);
  }
}
