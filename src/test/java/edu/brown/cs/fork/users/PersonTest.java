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
 * tests for the Person class.
 */
public class PersonTest implements ITest {
  private Person p;
  private List<LabeledRestaurant> pref;
  /**
   * set up a legit person.
   */
  @Override
  public void setUp() {
    try {
      Restaurant kfc = new Restaurant("1", "KFC", "Burgers", 3,
              1000, 1);
      Restaurant denden = new Restaurant("2", "Den Den", "Chinese", 4,
              200, 1.5);
      LabeledRestaurant labelKfc = new LabeledRestaurant(kfc, 0);
      LabeledRestaurant labelDenden = new LabeledRestaurant(denden, 1);
      List<LabeledRestaurant> pref = new LinkedList<>();
      pref.add(labelKfc);
      pref.add(labelDenden);
      this.pref = pref;
      this.p = new Person("0", pref, 0.5);
    } catch (OutOfRangeException e) {
      this.tearDown();
      fail();
    }

  }

  /**
   * tear down the Person object.
   */
  @Override
  public void tearDown() {
    this.p = null;
    this.pref = null;
  }

  /**
   * test the exception in the constructor.
   */
  @Test
  public void testIllegalConstructor() {
    this.setUp();
    assertThrows(OutOfRangeException.class, () -> new Person("0", this.pref, 2));
    this.tearDown();
  }

  /**
   * test the getId method.
   */
  @Test
  public void testGetId() {
    this.setUp();
    assertEquals(this.p.getId(), "0");
    this.tearDown();
  }

  /**
   * test the getPreference method.
   */
  @Test
  public void testGetPreference() {
    this.setUp();
    assertEquals(this.p.getIndividualPreference(), this.pref);
    this.tearDown();
  }

  /**
   * test the getGottenWay method.
   */
  @Test
  public void testGetGottenWay() {
    this.setUp();
    assertEquals(Double.compare(this.p.getGottenWay(), 0.5), 0);
    this.tearDown();
  }
}
