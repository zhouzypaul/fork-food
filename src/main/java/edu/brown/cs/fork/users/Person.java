package edu.brown.cs.fork.users;

import edu.brown.cs.fork.restaurants.LabeledRestaurant;

import java.util.List;

/**
 * TODO:
 */
public class Person {

  private List<LabeledRestaurant> preference;

  /**
   * TODO:
   */
  public Person() {
  }

  public List<LabeledRestaurant> getIndividualPreference() {
    return this.preference;
  }
}
