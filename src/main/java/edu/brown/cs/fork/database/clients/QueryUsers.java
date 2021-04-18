package edu.brown.cs.fork.database.clients;

import edu.brown.cs.fork.database.Database;
import edu.brown.cs.fork.database.actions.Login;
import edu.brown.cs.fork.database.actions.Ranking;
import edu.brown.cs.fork.database.actions.Training;
import edu.brown.cs.fork.database.actions.UserPref;
import edu.brown.cs.fork.database.actions.recent.RecentRestaurants;
import edu.brown.cs.fork.database.actions.recent.RecentTimestamps;
import edu.brown.cs.fork.exceptions.NoRestaurantException;
import edu.brown.cs.fork.exceptions.NoUserException;
import edu.brown.cs.fork.exceptions.OutOfRangeException;
import edu.brown.cs.fork.exceptions.SQLErrorException;
import edu.brown.cs.fork.restaurants.LabeledRestaurant;
import edu.brown.cs.fork.users.Person;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class containing methods to query from the user database.
 */
public class QueryUsers {
  private final Database db = new Database();

  // actions
  private Ranking ranking;
  private Training training;
  private UserPref userPref;
  private RecentRestaurants recentRests;
  private RecentTimestamps recentTimes;
  private Login login;

  /**
   * Constructor.
   */
  public QueryUsers() {  }

  /**
   * Initializes the database connection.
   * @param dbPath path to database
   */
  public void initUsers(String dbPath) {
    try {
      db.initDatabase(dbPath);
      Connection conn = db.getConn();
      this.ranking = new Ranking(conn);
      this.training = new Training(conn);
      this.userPref = new UserPref(conn);
      this.recentRests = new RecentRestaurants(conn);
      this.recentTimes = new RecentTimestamps(conn);
      this.login = new Login(conn);
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Returns whether this database is connected.
   * @return if the user database is connected
   */
  public boolean isConnected() {
    return db.isConnected();
  }

  /**
   * Close connection to users db.
   */
  public void close() {
    try {
      this.db.close();
    } catch (SQLErrorException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Queries all userIds.
   * @return a list of string representing all user ids
   * @throws SQLException SQLException
   */
  public Set<String> queryAllUserIds() throws SQLException {
    return this.login.queryAllUserIds();
  }

  /**
   * Get user's gottenWay parameter.
   * @param userId user id
   * @return user's gottenWay parameter
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public double getUserGottenWay(String userId)
      throws SQLException, NoUserException {
    return this.ranking.getUserGottenWay(userId);
  }

  /**
   * Updates user's gottenWay parameter.
   * @param userId user id
   * @param gottenWay user's gottenWay parameter
   * @return whether the update is successful
   * @throws SQLException SQLException
   */
  public boolean updateUserGottenWay(String userId, double gottenWay) throws SQLException {
    return this.ranking.updateUserGottenWay(userId, gottenWay);
  }

  /**
   * Insert a blank row into training table where every field is empty.
   * @param id id of user
   * @return a boolean indicating if insertion is successful
   */
  public boolean insertBlankRow(String id) {
    return this.userPref.insertBlankRow(id);
  }

  /**
   * Get user preferences from the training table where business_id is empty.
   * @param id id of user
   * @return a map representing user preferences from preference survey
   * @throws SQLException SQLException
   */
  public Map<String, List<String>> getUserPref(String id) throws SQLException {
    return this.userPref.getUserPref(id);
  }

  /**
   * Insert rows representing user preferences from preferences survey.
   * Note that business_id must be "".
   * @param colsToSet columns to insert
   * @param info information for each column
   * @return a boolean indicating whether insertion is successful
   */
  public boolean insertUserPref(List<String> colsToSet, List<String> info) {
    return this.userPref.insertUserPref(colsToSet, info);
  }

  /**
   * Delete all user preferences from preferences survey.
   * @param id id of user
   * @return a boolean indicating whether deletion is successful
   */
  public boolean deleteUserPref(String id) {
    return this.userPref.deleteUserPref(id);
  }

  /**
   * Insert user swiping preferences.
   * @param userId user id
   * @param userLat latitude of user
   * @param userLon longitude of user
   * @param restIDs a list of recommended restaurant ids
   * @param likeOrDislike a list indicating if user likes a restaurant
   * @return a boolean indicating whether insertion is successful
   */
  public boolean insertUserSwipePref(String userId, Double userLat, Double userLon,
                                List<String> restIDs, List<String> likeOrDislike) {
    return this.userPref.insertUserSwipePref(userId, userLat, userLon, restIDs, likeOrDislike);
  }

  /**
   * Queries all preferences of a user and make them into a list of
   * LabeledRestaurant objects for naive bayes training.
   * @param userId user id
   * @return a list of LabeledRestaurants
   * @throws SQLException SQLException
   * @throws NumberFormatException NumberFormatException
   * @throws OutOfRangeException OutOfRangeException
   */
  public List<LabeledRestaurant> trainRowsToLabeledRests(String userId)
      throws SQLException, NumberFormatException, OutOfRangeException {
    return this.training.trainRowsToLabeledRests(userId);
  }

  /**
   * Queries all preferences of a user and make rows into a Person object.
   * @param userId user id
   * @return a Person object
   * @throws OutOfRangeException OutOfRangeException
   * @throws SQLException SQLException
   */
  public Person trainRowsToPerson(String userId) throws OutOfRangeException, SQLException {
    List<LabeledRestaurant> rests = trainRowsToLabeledRests(userId);
    return new Person(userId, rests, 1.0);
  }


  /**
   * Get a user's most recent top restaurants.
   * @param userId user id
   * @return a list of strings representing business ids
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public List<String> getMostRecentRests(String userId)
      throws SQLException, NoUserException {
    return this.recentRests.getMostRecentRests(userId);
  }

  /**
   * Gets the most recent timestamps for recent restaurants.
   * @param userId user id
   * @return a list of strings representing timestamps in MM/DD/YYYY format
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public List<String> getMostRecentTimes(String userId)
      throws SQLException, NoUserException {
    return this.recentTimes.getMostRecentTimes(userId);
  }

  /**
   * Updates a user's most recent top restaurants.
   * @param userId user id
   * @param restId top restaurant's id
   * @return a boolean representing whether the update action is successful
   */
  public boolean updateMostRecentRests(String userId, String restId) {
    return this.recentRests.updateMostRecentRests(userId, restId);
  }

  /**
   * Updates a user's most recent top restaurant's timestamp.
   * @param userId user id
   * @param timestamp time restaurant associated with a restaurant
   * @return a boolean representing whether the update action is successful
   */
  public boolean updateMostRecentTimes(String userId, String timestamp) {
    return this.recentTimes.updateMostRecentTimes(userId, timestamp);
  }

  /**
   * Deletes a specific recent restaurant id.
   * @param userId user id
   * @param restId rest id to delete
   * @return the restaurant id, -1 if unsuccessful
   */
  public int deleteRecentRest(String userId, String restId)
      throws NoRestaurantException {
    return this.recentRests.deleteRecentRest(userId, restId);
  }

  /**
   * Deletes a specific recent restaurant's timestamp.
   * @param userId user id
   * @param idx index of restaurant timestamp to delete
   * @return whether the update is successful
   */
  public boolean deleteRecentTime(String userId, int idx)
      throws NoRestaurantException {
    return this.recentTimes.deleteRecentTime(userId, idx);
  }

  /**
   * Sets the recentRests field in the login table.
   * @param userId user id
   * @param restIds ids to parse into a string for db
   * @return whether the update is successful
   */
  public boolean setRecentRests(String userId, List<String> restIds) {
    return this.recentRests.setRecentRests(userId, restIds);
  }

  /**
   * Sets the recentTimes field in the login table.
   * @param userId user id
   * @param timestamps timestamps to parse into a string for db
   * @return whether the update is successful
   */
  public boolean setRecentTimestamps(String userId, List<String> timestamps) {
    return this.recentTimes.setRecentTimestamps(userId, timestamps);
  }

  /**
   * Puts user login information in the database.
   * @param userId user id
   * @param pwd password (already encrypted with jwt)
   * @return true if registered successfully, false if otherwise
   */
  public boolean registerUser(String userId, String pwd) {
    return this.login.registerUser(userId, pwd);
  }

  /**
   * Get user password.
   * @param userId user id
   * @return password of user with userId
   * @throws SQLException SQLException
   * @throws NoUserException NoUserException
   */
  public String getPwd(String userId) throws SQLException, NoUserException {
    return this.login.getPwd(userId);
  }

  /**
   * Changes user's password.
   * @param userId id of user to update
   * @param newPwd new password
   * @return a boolean indicating whether the update is successful
   * @throws NoUserException if the user can't be found in the database
   */
  public boolean changePwd(String userId, String newPwd) throws NoUserException {
    return this.login.changePwd(userId, newPwd);
  }

  /**
   * Deletes user with userID from database.
   * @param userID id of user to delete
   * @return true if deletion is successful, false if otherwise
   */
  public boolean deleteUser(String userID) {
    return this.login.deleteUser(userID);
  }
}
