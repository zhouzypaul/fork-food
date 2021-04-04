package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * tests for LabeledRestaurant.
 */
public class LabeledRestaurantTest implements ITest {

  private LabeledRestaurant l;
  private Restaurant r;

  /**
   * set up a labeled restaurant.
   */
  @Override
  public void setUp() {
    try {
      this.r = new Restaurant("1", "kfc", "Ramen", 4.5,
              1000, 12.2);
      this.l = new LabeledRestaurant(r, 1);
    } catch (OutOfRangeException e) {
      this.tearDown();
      fail();
    }
  }

  /**
   * tear down a restaurant.
   */
  @Override
  public void tearDown() {
    this.l = null;
  }

  /**
   * tests the exceptions in the constructor.
   */
  @Test
  public void testConstructor() {
    this.setUp();
    assertThrows(OutOfRangeException.class, () -> new LabeledRestaurant(this.r, 2));
    this.tearDown();
  }

  /**
   * tests for getters.
   */
  @Test
  public void testGetters() {
    this.setUp();
    assertEquals(this.r, this.l.getData());
    assertEquals(1, this.l.getLabel());
    this.tearDown();
  }
}
