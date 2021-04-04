package edu.brown.cs.fork.users;

import edu.brown.cs.fork.restaurants.LabeledRestaurant;

import java.util.List;

/**
 * TODO:
 */
public class Group {
  private List<Person> people;

  public Group() {
    // TODO:
  }

  /**
   * Add a person to the group.
   * @param p - the Person to add to the group.
   */
  public void addPerson(Person p) {
    people.add(p);
  }


  public List<LabeledRestaurant> getCollectivePreference() {
    // TODO:
    // I imagine we get go through the list of people, run it through the algo, and get some list of
    // restaurants returned.
    return null;
  }
}
