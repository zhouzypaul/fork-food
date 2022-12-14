package edu.brown.cs.fork.main;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.CategoryNotFoundException;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.exceptions.PriceRangeNotFoundException;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class HubTest implements ITest {
  /**
   * set up a hub
   */
  @Override
  public void setUp() {
    Hub.getRestDB().initRestaurants("data/restaurants.sqlite3");
    Hub.getUserDB().initUsers("data/test_users.sqlite3");
  }

  /**
   * tear down a hub
   */
  @Override
  public void tearDown() {
    try {
      Hub.getUserDB().updateUserGottenWay("paul", 0.9);
      Hub.getUserDB().updateUserGottenWay("sean", 0.8);
      Hub.getUserDB().updateUserGottenWay("ed", 0.3);
      Hub.getUserDB().updateUserGottenWay("alan", 0.11);
      Hub.getUserDB().setRecentRests("paul", Collections.singletonList("denden"));
      Hub.getUserDB().setRecentRests("sean", Collections.singletonList("denden"));
      Hub.getUserDB().setRecentRests("ed", Collections.singletonList("denden"));
      Hub.getUserDB().setRecentRests("alan", Collections.singletonList("denden"));
      Hub.getUserDB().setRecentTimestamps("paul", Collections.singletonList("4/17/2021"));
      Hub.getUserDB().setRecentTimestamps("sean", Collections.singletonList("4/17/2021"));
      Hub.getUserDB().setRecentTimestamps("ed", Collections.singletonList("4/17/2021"));
      Hub.getUserDB().setRecentTimestamps("alan", Collections.singletonList("4/17/2021"));
    } catch (Exception e) {
      fail();
    }
    Hub.getRestDB().close();
    Hub.getUserDB().close();
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
    Set<String> sean = new HashSet<>();
    sean.add("sean");
    try {
      List<Restaurant> rec = Hub.recommendRestaurants(paul, new double[]{42.359335, -71.059709});
      assertEquals(10, rec.size());
      assertEquals("X3NWJlQLB-X1xlfkYaE9tw", rec.get(0).getId());
      assertEquals("3qR_bkrNs69KxMf231MWcg", rec.get(1).getId());
      assertEquals("7jJZ91A-tkOghVOrj3aE6A", rec.get(2).getId());
      assertEquals("2aN4re6qf32cC4GZHETxtQ", rec.get(3).getId());
      assertEquals("ZLi2vXbnoQXO1cifwkoQIw", rec.get(4).getId());
      assertEquals("w07VkLk8BQZncFy9VdRxXA", rec.get(5).getId());
      assertEquals("0exn6MoH5MVASUYkEj6XOg", rec.get(6).getId());
      assertEquals("U0XHkvLa_aegtuHJDaGuZg", rec.get(7).getId());
      assertEquals("XHE0nHI1k8zmJVW7Ls_lVg", rec.get(8).getId());
      assertEquals("DrpjkkW4Yk9pTrV1iafrJA", rec.get(9).getId());
      List<Restaurant> recSean = Hub.recommendRestaurants(sean, new double[]{42.359335, -71.059709});
      assertEquals(rec, recSean);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    this.tearDown();
  }

  /**
   * tests for rankRestaurants().
   */
  @Test
  public void testRankRestaurants() {
    this.setUp();
    Set<String> users = new HashSet<>();
    users.add("paul");
    users.add("alan");
    users.add("ed");
    users.add("sean");
    Hashtable<String, Integer> denden = new Hashtable<>();
    denden.put("paul", 1);
    denden.put("alan", 0);
    denden.put("ed", 0);
    denden.put("sean", 1);
    Hashtable<String, Integer> kfc = new Hashtable<>();
    kfc.put("paul", 0);
    kfc.put("ed", 1);
    kfc.put("alan", 1);
    kfc.put("sean", 1);
    Hashtable<String, Hashtable<String, Integer>> votes = new Hashtable<>();
    votes.put("denden", denden);
    votes.put("kfc", kfc);
    try {
      // test illegal arguments
      Set<String> empty = new HashSet<>();
      assertThrows(NoUserException.class, () -> Hub.rankRestaurants(empty, votes));
      // test normal ranking
      String ranked = Hub.rankRestaurants(users, votes);
      assertEquals("denden", ranked);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    this.tearDown();
  }

  /**
   * tests for isInCategories().
   */
  @Test
  public void testIsInCategories() {
    this.setUp();
    assertTrue(Hub.isInCategories("Burgers"));
    assertTrue(Hub.isInCategories("Breakfast & Brunch"));
    assertTrue(Hub.isInCategories("Seafood"));
    assertTrue(Hub.isInCategories("Middle Eastern"));
    assertTrue(Hub.isInCategories("Barbeque"));
    assertFalse(Hub.isInCategories("others"));
    this.tearDown();
  }

  /**
   * tests for getCategories().
   */
  @Test
  public void testGetCategories() {
    this.setUp();
    assertEquals(Arrays.asList(
            "Burgers", "Chinese", "Pizza", "Italian", "Japanese", "Indian", "Vietnamese", "Steakhouses",
            "Breakfast & Brunch", "Desserts", "Coffee & Tea", "Greek", "Middle Eastern", "Vegan",
            "Mexican", "Thai", "American (Traditional)", "Salad", "Barbeque", "Seafood", ""), Hub.getCategories());
    this.tearDown();
  }

  /**
   * tests for getOverlap().
   */
  @Test
  public void testGetOverlap() {
    this.setUp();
    assertEquals(Arrays.asList("Burgers", "Chinese", "Pizza"),
            Hub.getOverlap(Arrays.asList("Burgers", "Chinese", "Pizza")));
    assertEquals(Arrays.asList("Burgers", "Chinese"),
            Hub.getOverlap(Arrays.asList("Burgers", "Chinese", "Danny", "Wells")));
    assertEquals(Collections.emptyList(), Hub.getOverlap(Arrays.asList("NoNoNO", "Nothing")));
    this.tearDown();
  }

  /**
   * tests for frontendCategoryToBackend and backendCategoryToFrontend
   */
  @Test
  public void testMappingCategories() {
    try {
      String frontend = Hub.backendCategoryToFrontend("Japanese");
      assertEquals(frontend, "japanese");

      frontend = Hub.backendCategoryToFrontend("Italian");
      assertEquals(frontend, "italian");

      String backend = Hub.frontendCategoryToBackend("burgers");
      assertEquals(backend, "Burgers");

      backend = Hub.frontendCategoryToBackend("vegan");
      assertEquals(backend, "Vegan");
    } catch (CategoryNotFoundException e) {
      fail();
    }

    Exception exception = assertThrows(CategoryNotFoundException.class, () -> {
      Hub.backendCategoryToFrontend("hello");
    });

    String expectedMessage = "Category hello can't be found.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

    exception = assertThrows(CategoryNotFoundException.class, () -> {
      Hub.frontendCategoryToBackend("hello");
    });

    expectedMessage = "Category hello can't be found.";
    actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  /**
   * tests for frontendPriceRangeToBackend and backendPriceRangeToFrontend
   */
  @Test
  public void testMappingPriceRanges() {
    try {
      String frontend = Hub.backendPriceRangeToFrontend("2");
      assertEquals(frontend, "$$");

      String backend = Hub.frontendPriceRangeToBackend("$$$");
      assertEquals(backend, "3");
    } catch (PriceRangeNotFoundException e) {
      fail();
    }

    Exception exception = assertThrows(PriceRangeNotFoundException.class, () -> {
      Hub.backendPriceRangeToFrontend("5");
    });

    String expectedMessage = "Price range 5 can't be found.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

    exception = assertThrows(PriceRangeNotFoundException.class, () -> {
      Hub.frontendPriceRangeToBackend("$$$$");
    });

    expectedMessage = "Price range $$$$ can't be found.";
    actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }
}
