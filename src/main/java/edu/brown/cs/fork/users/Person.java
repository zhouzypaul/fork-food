package edu.brown.cs.fork.users;

import edu.brown.cs.fork.restaurants.LabeledRestaurant;

import java.util.List;

/**
 * TODO:
 */
public class Person {
  private int userId;
  private String name;

  private List<LabeledRestaurant> preference;

  /**
   * TODO:
   */
  public Person() {
  }

  /**
   * TODO:
   * @return
   */
  public int getId() {
    return 0;
  }

  /**
   * TODO:
   * @return
   */
  public String getName() {
    return null;
  }

  public List<LabeledRestaurant> getIndividualPreference() {
    return this.preference;
  }
}
