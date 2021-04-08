package edu.brown.cs.fork.users;

import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;

import java.util.List;

/**
 * A Person data structure. This contains the data and methods a person in the fork app needs.
 */
public class Person {
  private final String userId;
  private final List<LabeledRestaurant> preference;
  private final double gottenWay;

  /**
   * constructor for the Person object.
   * @param userId - id for the Person.
   * @param preference - a list of labeled restaurants, representing the preference of the person,
   *                   and used as training data for that person's group.
   * @param gottenWay - a double indicating if that person has gotten their way recently. This
   *                  double is between 0 and 1, with 0 meaning haven't gotten their way at all,
   *                  and 1 meaning always gotten their way.
   * @throws OutOfRangeException if gotten way if not between 0 and 1.
   */
  public Person(String userId, List<LabeledRestaurant> preference, double gottenWay)
    throws OutOfRangeException {
    if (gottenWay < 0 || gottenWay > 1) {
      throw new OutOfRangeException("gottenWay should be between 0 and 1");
    }
    this.userId = userId;
    this.preference = preference;
    this.gottenWay = gottenWay;
  }

  /**
   * getter for the id field.
   * @return the user id of the user.
   */
  public String getId() {
    return this.userId;
  }

  /**
   * getter for the preference field.
   * @return the preference list of the user.
   */
  public List<LabeledRestaurant> getIndividualPreference() {
    return this.preference;
  }

  /**
   * getter for the gottenWay field.
   * @return the gottenWay double.
   */
  public double getGottenWay() {
    return this.gottenWay;
  }
}
