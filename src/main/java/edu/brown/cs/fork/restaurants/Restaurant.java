package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.recommendation.Recommendable;

import java.util.List;

/**
 * A restaurant data structure. This implements the Recommendable interface.
 * This data structure holds all the data a restaurant has.
 */
public class Restaurant implements Recommendable {
  private String id;
  private String name;
  private List<String> categories;

  @Override
  public int getNumAttr() {
    return 3;
  }

  @Override
  public int getNumClasses() {
    // TODO:
    return 0;
  }

  @Override
  public int[] getAttr() {
    // TODO:
    return null;
  }

  @Override
  public int[] getAttrDim() {
    return new int[0];
  }
}
