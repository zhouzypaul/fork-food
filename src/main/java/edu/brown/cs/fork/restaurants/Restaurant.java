package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.recommendation.Recommendable;

import java.util.List;

/**
 * TODO:
 */
public class Restaurant implements Recommendable {
  private int id;
  private String name;
  private List<String> categories;

  @Override
  public int getNumAttr() {
    // TODO:
    return 0;
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
