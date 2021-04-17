package edu.brown.cs.fork.database;

import edu.brown.cs.fork.Hub;
import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.database.queries.QueryUsers;
import edu.brown.cs.fork.exceptions.NoRestaurantException;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.users.Person;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;

public class QueryUsersTest implements ITest {
  private final QueryUsers db = new QueryUsers();

  @Override
  public void setUp() {
    this.db.initUsers("data/test_users.sqlite3");
  }

  @Override
  public void tearDown() {
    this.db.close();
  }

  @Test
  public void testUpdateUserGottenWay() {
    setUp();

    try {
      boolean success = this.db.updateUserGottenWay("ed", 0.2);
      assertTrue(success);
      double gottenWay = this.db.getUserGottenWay("ed");
      assertEquals(gottenWay, 0.2, 0.00001);
      success = this.db.updateUserGottenWay("ed", 0.3);
      assertTrue(success);
      gottenWay = this.db.getUserGottenWay("ed");
      assertEquals(gottenWay, 0.3, 0.00001);
      success = this.db.updateUserGottenWay("alan", 0.65);
      assertTrue(success);
      gottenWay = this.db.getUserGottenWay("alan");
      assertEquals(gottenWay, 0.65, 0.00001);
      success = this.db.updateUserGottenWay("alan", 0.11);
      assertTrue(success);
      gottenWay = this.db.getUserGottenWay("alan");
      assertEquals(gottenWay, 0.11, 0.00001);
    } catch (SQLException | NoUserException e) {
      fail();
    }

    tearDown();
  }

  @Test
  public void testQueryAllUserIds() {
    setUp();

    try {
      Set<String> allIds = this.db.queryAllUserIds();
      assertEquals(allIds.size(), 4);
      assertTrue(allIds.contains("ed"));
      assertTrue(allIds.contains("sean"));
      assertTrue(allIds.contains("paul"));
      assertTrue(allIds.contains("alan"));
    } catch (SQLException e) {
      fail();
    }

    tearDown();
  }

  @Test
  public void testInsertBlankRow() {
    setUp();

    boolean success = this.db.insertBlankRow("stranger");
    assertTrue(success);

    try {
      Map<String, List<String>> pref = this.db.getUserPref("stranger");
      assertEquals(pref.get("star"), Arrays.asList(""));
      assertEquals(pref.get("star").size(), 1);
    } catch (SQLException e) {
      fail();
    }

    success = this.db.deleteUserPref("stranger");
    assertTrue(success);

    tearDown();
  }

  @Test
  public void testInsertUserPref() {
    setUp();

    List<String> colsToSet = Arrays.asList("userId", "business_id", "foodType", "label");
    List<String> info = Arrays.asList("stranger", "", "Sushi Bars", "1");
    boolean success = this.db.insertUserPref(colsToSet, info);
    assertTrue(success);

    try {
      Map<String, List<String>> pref = this.db.getUserPref("stranger");
      assertEquals(pref.get("foodType"), Arrays.asList("Sushi Bars"));
      assertEquals(pref.get("foodType").size(), 1);
    } catch (SQLException e) {
      fail();
    }

    success = this.db.deleteUserPref("stranger");
    assertTrue(success);

    tearDown();
  }

  @Test
  public void testSwiping() {
    setUp();
    Hub.getRestDB().initRestaurants("data/restaurants.sqlite3");

    String userId = "stranger";
    boolean success = this.db.registerUser(userId, "stranger");
    assertTrue(success);

    Double userLat = 42.359335;
    Double userLon = -71.059709;
    List<String> restIDs = Arrays.asList("jGennaZUr2MsJyRhijNBfA", "ecqjHw3jeEApa9eZ622hAA");
    List<String> likesOrDislikes = Arrays.asList("1", "1");

    success = this.db.insertUserSwipePref(userId, userLat, userLon, restIDs, likesOrDislikes);
    assertTrue(success);

    try {
      List<LabeledRestaurant> rests = this.db.trainRowsToLabeledRests("stranger");
      assertEquals(rests.size(), 6);
      assertEquals(rests.get(0).getData().getName(), "Legal Sea Foods");
      assertEquals(rests.get(0).getData().getStar(), 3.5, 0.0001);
      assertTrue(Hub.isInCategories(rests.get(0).getData().getFoodType()));
      assertEquals(rests.get(0).getData().getPriceRange(), 2);

      assertEquals(rests.get(5).getData().getName(), "Fin Point Oyster Bar + Grille");
      assertEquals(rests.get(5).getData().getStar(), 4.5, 0.0001);
      assertTrue(Hub.isInCategories(rests.get(5).getData().getFoodType()));
      assertEquals(rests.get(5).getData().getPriceRange(), 3);

      Person person = this.db.trainRowsToPerson("stranger");
      assertEquals(person.getId(), "stranger");
      assertEquals(person.getIndividualPreference(), rests);
    } catch (SQLException | OutOfRangeException e) {
      fail();
    }

    success = this.db.deleteUser("stranger");
    assertTrue(success);

    Hub.getRestDB().close();
    tearDown();
  }

  @Test
  public void testUserLogin() {
    setUp();

    try {
      boolean success = this.db.registerUser("stranger", "hi");
      assertTrue(success);

      String pwd = this.db.getPwd("stranger");
      assertEquals(pwd, "hi");

      success = this.db.changePwd("stranger", "hello");
      assertTrue(success);

      pwd = this.db.getPwd("stranger");
      assertEquals(pwd, "hello");

      success = this.db.deleteUser("stranger");
      assertTrue(success);
    } catch (SQLException | NoUserException e) {
      System.out.println(e.getMessage());
      fail();
    }

    Exception exception = assertThrows(NoUserException.class, () -> {
      this.db.getPwd("stranger");
    });

    String expectedMessage = "User: stranger doesn't exist.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

    tearDown();
  }

  @Test
  public void testRecentRestaurants() {
    setUp();

    try {
      boolean success = this.db.registerUser("stranger", "hi");
      assertTrue(success);

      List<String> recentRests = this.db.getMostRecentRests("stranger");
      assertEquals(recentRests.size(), 0);

      success = this.db.updateMostRecentRests("stranger", "12345");
      assertTrue(success);

      recentRests = this.db.getMostRecentRests("stranger");
      assertEquals(recentRests.size(), 1);
      assertEquals(recentRests.get(0), "12345");

      success = this.db.updateMostRecentRests("stranger", "abcde");
      assertTrue(success);

      recentRests = this.db.getMostRecentRests("stranger");
      assertEquals(recentRests.size(), 2);
      assertEquals(recentRests.get(0), "12345");
      assertEquals(recentRests.get(1), "abcde");

      success = this.db.updateMostRecentRests("stranger", "54321");
      assertTrue(success);

      recentRests = this.db.getMostRecentRests("stranger");
      assertEquals(recentRests.size(), 3);
      assertEquals(recentRests.get(0), "12345");
      assertEquals(recentRests.get(1), "abcde");
      assertEquals(recentRests.get(2), "54321");

      // unique recent restaurants
      success = this.db.updateMostRecentRests("stranger", "abcde");
      assertTrue(success);

      recentRests = this.db.getMostRecentRests("stranger");
      assertEquals(recentRests.size(), 3);
      assertEquals(recentRests.get(0), "12345");
      assertEquals(recentRests.get(1), "abcde");
      assertEquals(recentRests.get(2), "54321");

      // delete a recent restaurant
      success = this.db.deleteRecentRest("stranger", "abcde");
      assertTrue(success);

      recentRests = this.db.getMostRecentRests("stranger");
      assertEquals(recentRests.size(), 2);
      assertEquals(recentRests.get(0), "12345");
      assertEquals(recentRests.get(1), "54321");

      success = this.db.deleteUser("stranger");
      assertTrue(success);
    } catch (SQLException | NoUserException | NoRestaurantException e) {
      System.out.println(e.getMessage());
      fail();
    }

    Exception exception = assertThrows(NoUserException.class, () -> {
      this.db.getMostRecentRests("stranger");
    });

    String expectedMessage = "User: stranger doesn't exist.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

    tearDown();
  }

  @Test
  public void testRecentTimes() {
    setUp();

    try {
      boolean success = this.db.registerUser("stranger", "hi");
      assertTrue(success);

      List<String> recentTimes = this.db.getMostRecentTimes("stranger");
      assertEquals(recentTimes.size(), 0);

      success = this.db.updateMostRecentTimes("stranger", "2/1/2021");
      assertTrue(success);

      recentTimes = this.db.getMostRecentTimes("stranger");
      assertEquals(recentTimes.size(), 1);
      assertEquals(recentTimes.get(0), "2/1/2021");

      success = this.db.updateMostRecentTimes("stranger", "3/1/2021");
      assertTrue(success);

      recentTimes = this.db.getMostRecentTimes("stranger");
      assertEquals(recentTimes.size(), 2);
      assertEquals(recentTimes.get(0), "2/1/2021");
      assertEquals(recentTimes.get(1), "3/1/2021");

      success = this.db.updateMostRecentTimes("stranger", "4/1/2021");
      assertTrue(success);

      recentTimes = this.db.getMostRecentTimes("stranger");
      assertEquals(recentTimes.size(), 3);
      assertEquals(recentTimes.get(0), "2/1/2021");
      assertEquals(recentTimes.get(1), "3/1/2021");
      assertEquals(recentTimes.get(2), "4/1/2021");

      // unique recent restaurants
      success = this.db.updateMostRecentTimes("stranger", "3/1/2021");
      assertTrue(success);

      recentTimes = this.db.getMostRecentTimes("stranger");
      assertEquals(recentTimes.size(), 3);
      assertEquals(recentTimes.get(0), "2/1/2021");
      assertEquals(recentTimes.get(1), "3/1/2021");
      assertEquals(recentTimes.get(2), "4/1/2021");

      // delete a recent restaurant
      success = this.db.deleteRecentTime("stranger", "3/1/2021");
      assertTrue(success);

      recentTimes = this.db.getMostRecentTimes("stranger");
      assertEquals(recentTimes.size(), 2);
      assertEquals(recentTimes.get(0), "2/1/2021");
      assertEquals(recentTimes.get(1), "4/1/2021");

      success = this.db.deleteUser("stranger");
      assertTrue(success);
    } catch (SQLException | NoUserException | NoRestaurantException e) {
      System.out.println(e.getMessage());
      fail();
    }

    Exception exception = assertThrows(NoUserException.class, () -> {
      this.db.getMostRecentTimes("stranger");
    });

    String expectedMessage = "User: stranger doesn't exist.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

    tearDown();
  }
}
