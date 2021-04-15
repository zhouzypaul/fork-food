package edu.brown.cs.fork.main;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class HubTest implements ITest {
  private Hub hub;
  /**
   * set up a hub
   */
  @Override
  public void setUp() {
    this.hub = new Hub();
    Hub.getRestDB().initRestaurants("data/restaurants.sqlite3");
    Hub.getUserDB().initUsers("data/test_users.sqlite3");
  }

  /**
   * tear down a hub
   */
  @Override
  public void tearDown() {
    Hub.getRestDB().close();
    Hub.getUserDB().close();
    this.hub = null;
  }

  /**
   * tests for recommendRestaurants.
   */
  @Test
  public void testRecommendRestaurants() {
    this.setUp();
    // test illegal arguments
    assertThrows(NoUserException.class,
            () -> Hub.recommendRestaurants(Collections.emptySet(), new double[]{4, 5}));
    Set<String> oneEltSet = new HashSet<>();
    oneEltSet.add("bogey");
    assertThrows(OutOfRangeException.class,
            () -> Hub.recommendRestaurants(oneEltSet, new double[]{2}));
    assertThrows(OutOfRangeException.class,
            () -> Hub.recommendRestaurants(oneEltSet, new double[]{2, 3, 4}));
    // test normal recommendation
    Set<String> paul = new HashSet<>();
    paul.add("paul");
    try {
      Hub.recommendRestaurants(paul, new double[]{42.359335, -71.059709});
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    this.tearDown();
  }
}
