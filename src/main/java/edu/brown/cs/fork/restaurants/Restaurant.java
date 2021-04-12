package edu.brown.cs.fork.restaurants;

import edu.brown.cs.fork.exceptions.OutOfRangeException;
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
  private final double star;
  private final int numReviews;
  private final double distance;
  private final int priceRange;
  private final String[] allFoodTypes = new String[] {"Burgers", "Chinese", "Pizza", "Italian",
      "Sushi Bars", "Indian", "Vietnamese", "Steakhouses", "Breakfast & Brunch", "Desserts",
      "Coffee & Tea", "Greek", "Middle Eastern", "Vegan", "Mexican", "Thai", "American",
      "Salad", "Barbeque", "Seafood"};
  // dim of attributes
  // a restaurant object has 4 recommendable features, each with dimension
  // 1) the distance to the user: dimension of 4 (separated into 4 bins: close, walk, drive, far)
  // 2) the food type: dimension of 30 (separated into 17 bins, one for each type)
  // 3) the number of reviews: dimension of 3 (separated into 3 bins: low, med, high)
  // 4) the star review: dimension of 5 (separated into 5 bins, one for each integer star)
  // 5) the priceRange: dimension of 3 (low, med, high)
  private final int dimDistance = 4;
  private final int dimFoodType = this.allFoodTypes.length;
  private final int dimStar = 5;
  private final int dimNumReviews = 3;
  private final int dimPriceRange = 3;
  // mapping of attributes
  private final double closeDistance = 0.5;  // in miles
  private final double walkDistance = 1.5;
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
   * @param priceRange - the price range of the restaurant.
   *
   * @throws OutOfRangeException - when one of star, numReview, distance is negative.
   */
  public Restaurant(String id, String name, String foodType, double star, int numReviews,
                    double distance, int priceRange) throws OutOfRangeException {
    if (star < 0 || star > 5 || numReviews < 0 || distance < 0 || priceRange > 3
            || priceRange < 0) {
      throw new OutOfRangeException("all of stars, numReviews, distance have to be non-negative");
    }
    this.id = id;
    this.name = name;
    this.foodType = foodType;
    this.star = star;
    this.numReviews = numReviews;
    this.distance = distance;
    this.priceRange = priceRange;
  }

  /**
   * getter for the id field.
   * @return the id.
   */
  @Override
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
  public double getStar() {
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
   * getter for the price range field.
   * @return the price range.
   */
  public int getPriceRange() {
    return priceRange;
  }

  /**
   * get the number of recommendable attributes a restaurant have.
   * A restaurant object has 4 recommendable features:
   * 1) the distance to the user
   * 2) the food type
   * 3) the number of reviews
   * 4) the star review
   * 5) the price range
   *
   * @return 5
   */
  @Override
  public int getNumAttr() {
    return 5;
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
    attr[4] = this.mapPriceRange();
    return attr;
  }

  /**
   * map the distance attribute to an int, representing the index of the bin of the attribute.
   * @return a bin index.
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
   * @return a bin index.
   */
  private int mapFoodType() {
    // if one of the food types we care about
    for (int i = 0; i < this.dimFoodType; i++) {
      if (this.foodType.equals(this.allFoodTypes[i])) {
        return i;
      }
    }
    // if of food type "others"
    return this.dimFoodType;
  }

  /**
   * map the numReviews attribute to an int, representing the index of the bin of the attribute.
   * @return a bin index.
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
   * @return a bin index.
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
   * map the priceRange attribute to an int, representing the index of the bin of the attribute.
   * @return a bin index
   */
  private int mapPriceRange() {
    if (this.priceRange == 1) {
      return 0;
    } else if (this.priceRange == 2) {
      return 1;
    } else {
      return 2;
    }
  }

  /**
   * get the dimension of each of the attributes.
   *
   * @return [4, 17, 3, 5, 3]
   */
  @Override
  public int[] getAttrDim() {
    return new int[]{this.dimDistance, this.dimFoodType, this.dimNumReviews, this.dimStar,
        this.dimPriceRange};
  }
}
