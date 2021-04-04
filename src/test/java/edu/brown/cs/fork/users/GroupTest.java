package edu.brown.cs.fork.users;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * tests for the Group class.
 */
public class GroupTest implements ITest {

  private Group group;
  private Person p1;
  private Person p2;

  /**
   * set up a group.
   */
  @Override
  public void setUp() {
    try {
      Restaurant kfc = new Restaurant("1", "KFC", "Burgers", 3,
              1000, 1);
      Restaurant denden = new Restaurant("2", "Den Den", "Chinese", 4,
              200, 1.5);
      // person 1
      LabeledRestaurant labelKfc1 = new LabeledRestaurant(kfc, 0);
      LabeledRestaurant labelDenden1 = new LabeledRestaurant(denden, 1);
      List<LabeledRestaurant> pref1 = new LinkedList<>();
      pref1.add(labelKfc1);
      pref1.add(labelDenden1);
      this.p1 = new Person("1", pref1, 0.5);
      // person 2
      LabeledRestaurant labelKfc2 = new LabeledRestaurant(kfc, 1);
      LabeledRestaurant labelDenden2 = new LabeledRestaurant(denden, 0);
      List<LabeledRestaurant> pref2 = new LinkedList<>();
      pref2.add(labelKfc2);
      pref2.add(labelDenden2);
      this.p2 = new Person("2", pref2, 0.7);
      // group
      List<Person> personList = new LinkedList<>();
      personList.add(this.p1);
      personList.add(this.p2);
      this.group = new Group(personList);
    } catch (OutOfRangeException e) {
      this.tearDown();
      fail();
    }
  }

  /**
   * tear down the group.
   */
  @Override
  public void tearDown() {
    this.group = null;
    this.p1 = null;
    this.p2 = null;
  }

  /**
   * test the getCollectivePreference method.
   */
  @Test
  public void testGetCollectivePreference() {
    this.setUp();
    List<LabeledRestaurant> collective = new LinkedList<>();
    collective.addAll(this.p1.getIndividualPreference());
    collective.addAll(this.p2.getIndividualPreference());
    assertEquals(collective, this.group.getCollectivePreference());
    this.tearDown();
  }
}
