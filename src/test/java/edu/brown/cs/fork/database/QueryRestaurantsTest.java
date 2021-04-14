package edu.brown.cs.fork.database;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.database.queries.QueryRestaurants;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.restaurants.Restaurant;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class QueryRestaurantsTest implements ITest {
  private final QueryRestaurants db = new QueryRestaurants();

  @Override
  public void setUp() {
    this.db.initRestaurants("data/restaurants.sqlite3");
  }

  @Override
  public void tearDown() {
    this.db.close();
  }

  @Test
  public void testGetAllRestaurants() {
    setUp();

    try {
      List<Map<String, String>> results = this.db.getAllRestaurants();
      assertEquals(results.size(), 23529);
    } catch (SQLException throwables) {
      fail();
    }

    tearDown();
  }

  @Test
  public void testQueryRestByID() {
    setUp();

    try {
      List<Map<String, String>> result = this.db.queryRestByID("6iYb2HFDywm3zjuRg0shjw");
      assertEquals(result.size(), 1);
      Map<String, String> rest = result.get(0);
      assertEquals(rest.get("name"), "Oskar Blues Taproom");
      assertEquals(rest.get("city"), "Boulder");

      result = this.db.queryRestByID("HPA_qyMEddpAEtFof02ixg");
      assertEquals(result.size(), 1);
      rest = result.get(0);
      assertEquals(rest.get("name"), "Mr G's Pizza & Subs");
      assertEquals(rest.get("numReviews"), "39");
    } catch (SQLException throwables) {
      fail();
    }

    tearDown();
  }

  @Test
  public void testQueryRadByID() {
    setUp();

    try {
      List<Map<String, String>> results = this.db.queryRestByRad(1.0, 42.359335, -71.059709);
      assertEquals(results.size(), 576);
      for (Map rest : results) {
        assertEquals(rest.get("state"), "MA");
      }
    } catch (SQLException e) {
      fail();
    }

    tearDown();
  }

  @Test
  public void testGetTestingResults() {
    setUp();

    try {
      List<Restaurant> results = this.db.getTestingRests(1.0, 42.359335, -71.059709);
      assertEquals(results.size(), 736);
      for (Restaurant rest : results) {
        // Star Market doesn't have any categories that we are interested in
        assertNotEquals("Star Market", rest.getName());
        assertTrue(rest.getDistance() <= 1.2);
      }
    } catch (SQLException | OutOfRangeException e) {
      fail();
    }

    tearDown();
  }
}