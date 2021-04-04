package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.recommendation.Recommendable;

/**
 * A restaurant data structure. This implements the Recommendable interface.
 * This data structure holds all the data a restaurant has.
 *
 * The recommendable features of a restaurant is:
 * 1) the distance to the user
 * 2) the food type
 * 3) the number of reviews
 * 4) the star review
 *
 * A recommendable restaurants is recommended into 2 classes:
 * 1) the user likes it
 * 2) the user doesn't like it
 */
public class Restaurant implements Recommendable {
  // data
  private final String id;
  private final String name;
  private final String foodType;
  private final int star;
  private final int numReviews;
  private final double distance;
  // dim of attributes
  // a restaurant object has 4 recommendable features, each with dimension
  // 1) the distance to the user: dimension of 4 (separated into 4 bins: close, walk, drive, far)
  // 2) the food type: dimension of 30 (separated into 17 bins, one for each type)
  // 3) the number of reviews: dimension of 3 (separated into 3 bins: low, med, high)
  // 4) the star review: dimension of 5 (separated into 5 bins, one for each integer star)
  private final int dimDistance = 4;
  private final int dimFoodType = 17;
  private final int dimStar = 5;
  private final int dimNumReviews = 3;
  // mapping of attributes
  private final double closeDistance = 0.2;  // in kilometers
  private final double walkDistance = 1;
  private final double driveDistance = 10;
  private final int lowNumReview = 10;
  private final int highNumReview = 100;


  /**
   * constructor for a Restaurant.
   * @param id - restaurant id
   * @param name - name of the restaurant.
   * @param foodType - the type of food the restaurant serves.
   * @param star - the average star (review) the restaurant has.
   * @param numReviews - number of review the restaurant received.
   * @param distance - distance of the restaurant to the user.
   */
  public Restaurant(String id, String name, String foodType, int star, int numReviews,
                    double distance) {
    this.id = id;
    this.name = name;
    this.foodType = foodType;
    this.star = star;
    this.numReviews = numReviews;
    this.distance = distance;
  }

  /**
   * getter for the id field.
   * @return the id.
   */
  public String getId() {
    return this.id;
  }

  /**
   * getter for the name field.
   * @return the name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * getter for the foodType field.
   * @return the foodType.
   */
  public String getFoodType() {
    return this.foodType;
  }

  /**
   * getter for the star field.
   * @return the star.
   */
  public int getStar() {
    return this.star;
  }

  /**
   * getter for the numReview field.
   * @return the numReviews.
   */
  public int getNumReviews() {
    return this.numReviews;
  }

  /**
   * getter for the distance field.
   * @return the distance.
   */
  public double getDistance() {
    return distance;
  }

  /**
   * get the number of recommendable attributes a restaurant have.
   * A restaurant object has 4 recommendable features:
   * 1) the distance to the user
   * 2) the food type
   * 3) the number of reviews
   * 4) the star review
   *
   * @return 4
   */
  @Override
  public int getNumAttr() {
    return 4;
  }

  /**
   * get the number of classes a restaurant can be classified into.
   * A recommendable restaurants is recommended into 2 classes:
   * 1) the user likes it
   * 2) the user doesn't like it
   *
   * @return 2
   */
  @Override
  public int getNumClasses() {
    return 2;
  }

  /**
   * get an array of ints to represent each of the attribute value of this restaurants.
   * Each attribute field in mapped from its original data type to an int representing it's index.
   *
   * @return an array of int (of length this.getNumAttr()) representing the attribute value of the
   * specific restaurant object.
   */
  @Override
  public int[] getAttr() {
    int[] attr = new int[this.getNumAttr()];
    attr[0] = this.mapDistance();
    attr[1] = this.mapFoodType();
    attr[2] = this.mapNumReviews();
    attr[3] = this.mapStar();
    return attr;
  }

  /**
   * map the distance attribute to an int, representing the index of the bin of the attribute.
   * @return an bin index.
   */
  private int mapDistance() {
    if (this.distance <= this.closeDistance) {
      return 0;
    } else if (this.distance <= this.walkDistance) {
      return 1;
    } else if (this.distance <= this.driveDistance) {
      return 2;
    } else {
      return 3;
    }
  }

  /**
   * map the foodType attribute to an int, representing the index of the bin of the attribute.
   * @return an bin index.
   */
  private int mapFoodType() {
    if (this.foodType.contains("Burgers")) {
      return 0;
    } else if (this.foodType.contains("Chinese")) {
      return 1;
    } else if (this.foodType.contains("Pizza")) {
      return 2;
    } else if (this.foodType.contains("Italian")) {
      return 3;
    } else if (this.foodType.contains("Sushi Bars")) {
      return 4;
    } else if (this.foodType.contains("Indian")) {
      return 5;
    } else if (this.foodType.contains("Vietnamese")) {
      return 6;
    } else if (this.foodType.contains("Steakhouses")) {
      return 7;
    } else if (this.foodType.contains("Chicken Wings")) {
      return 8;
    } else if (this.foodType.contains("Barbeque")) {
      return 9;
    } else if (this.foodType.contains("Tacos")) {
      return 10;
    } else if (this.foodType.contains("American")) {
      return 11;
    } else if (this.foodType.contains("Falafel")) {
      return 12;
    } else if (this.foodType.contains("Salad")) {
      return 13;
    } else if (this.foodType.contains("Thai")) {
      return 14;
    } else if (this.foodType.contains("Ramen")) {
      return 15;
    } else {
      return 16;
    }
  }

  /**
   * map the numReviews attribute to an int, representing the index of the bin of the attribute.
   * @return an bin index.
   */
  private int mapNumReviews() {
    if (this.numReviews < this.lowNumReview) {
      return 0;
    } else if (this.numReviews < this.highNumReview) {
      return 1;
    } else {
      return 2;
    }
  }

  /**
   * map the star attribute to an int, representing the index of the bin of the attribute.
   * @return an bin index.
   */
  private int mapStar() {
    if (this.star < 1) {
      return 0;
    } else if (this.star < 2) {
      return 1;
    } else if (this.star < 3) {
      return 2;
    } else if (this.star < 4) {
      return 3;
    } else {
      return 4;
    }
  }

  /**
   * get the dimension of each of the attributes.
   *
   * @return [4, 30, 3, 5]
   */
  @Override
  public int[] getAttrDim() {
    return new int[]{this.dimDistance, this.dimFoodType, this.dimNumReviews, this.dimStar};
  }
}
