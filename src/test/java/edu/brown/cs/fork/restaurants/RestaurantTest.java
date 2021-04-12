package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * tests for Restaurant.
 */
public class RestaurantTest implements ITest {
  private Restaurant r;

  /**
   * set up a restaurant.
   */
  @Override
  public void setUp() {
    try {
      this.r = new Restaurant("1", "kfc", "Ramen", 4.5,
              1000, 12.2, 3);
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
    this.r = null;
  }

  /**
   * test the exceptions in constructor.
   */
//  @Test
//  public void testConstructor() {
//    this.setUp();
//    assertThrows(OutOfRangeException.class, () -> new Restaurant("0", "kfc",
//            "Burges", -1, 10, 10, 3));
//    assertThrows(OutOfRangeException.class, () -> new Restaurant("0", "kfc",
//            "Burges", 10, 10, 10, 3));
//    assertThrows(OutOfRangeException.class, () -> new Restaurant("0", "kfc",
//            "Burges", 3, -3, 10, 3));
//    assertThrows(OutOfRangeException.class, () -> new Restaurant("0", "kfc",
//            "Burges", 3, 10, -10, 3));
//    this.tearDown();
//  }

  /**
   * test all the getters.
   */
  @Test
  public void testGetters() {
    this.setUp();
    assertEquals("1", this.r.getId());
    assertEquals("kfc", this.r.getName());
    assertEquals("Ramen", this.r.getFoodType());
    assertEquals(Double.compare(4.5, this.r.getStar()), 0);
    assertEquals(1000, this.r.getNumReviews());
    assertEquals(Double.compare(12.2, this.r.getDistance()), 0);
    assertEquals(5, this.r.getNumAttr());
    assertEquals(2, this.r.getNumClasses());
    assertEquals(Arrays.toString(new int[]{3, 20, 2, 4, 2}), Arrays.toString(this.r.getAttr()));
    assertEquals(Arrays.toString(new int[]{4, 20, 3, 5, 3}), Arrays.toString(this.r.getAttrDim()));
    this.tearDown();
  }
}
