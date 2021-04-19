package edu.brown.cs.fork.users;

import edu.brown.cs.fork.restaurants.LabeledRestaurant;

import java.util.LinkedList;
import java.util.List;

/**
 * A Group data structure. This holds a list of Person data structures.
 */
public class Group {
  private final List<Person> people;

  /**
   * Constructor for the Group.
   * @param people a list of Person in the group
   */
  public Group(List<Person> people) {
    this.people = people;
  }

  /**
   * get the collective preference of the group. THe collective preference is the concatenated
   * list of each individual's preference in the group.
   *
   * @return the collective preference as a list of LabeledRestaurant.
   */
  public List<LabeledRestaurant> getCollectivePreference() {
    List<LabeledRestaurant> collectivePref = new LinkedList<>();
    for (Person p : this.people) {
      List<LabeledRestaurant> individualPref = p.getIndividualPreference();
      collectivePref.addAll(individualPref);
    }
    return collectivePref;
  }
}
