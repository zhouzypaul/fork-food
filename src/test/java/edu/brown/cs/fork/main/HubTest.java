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
  /**
   * set up a hub
   */
  @Override
  public void setUp() {

  }

  /**
   * tear down a hub
   */
  @Override
  public void tearDown() {

  }

  @Test
  public void testRecommendRestaurants() {
    this.setUp();
    assertThrows(NoUserException.class,
            () -> Hub.recommendRestaurants(Collections.emptySet(), new double[]{4, 5}));
    Set<String> oneEltSet = new HashSet<>();
    oneEltSet.add("bogey");
    assertThrows(OutOfRangeException.class,
            () -> Hub.recommendRestaurants(oneEltSet, new double[]{2}));
    assertThrows(OutOfRangeException.class,
            () -> Hub.recommendRestaurants(oneEltSet, new double[]{2, 3, 4}));
    this.tearDown();
  }
}
