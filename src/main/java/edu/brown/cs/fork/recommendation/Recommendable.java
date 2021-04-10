package edu.brown.cs.fork.recommendation;

/**
 * an interface for objects that can be recommended.
 * Such objects must have fields that are comparable.
 */
public interface Recommendable {
  /**
   * return the number of attributes this type of object has.
   *
   * @return number of attributes (dimension of the input features).
   */
  int getNumAttr();

  /**
   * how many classes should this type of object be classified into.
   *
   * @return number of classes (dimension of the output space).
   */
  int getNumClasses();

  /**
   * get an array of ints representing the attributes of this type of recommendable.
   * The array array has length this.getNumAttr(), and each index of the array is an
   * int representing a possible value of that attribute.
   *
   * @return an attributes array.
   */
  int[] getAttr();

  /**
   * get the dimension (possible number of different values) of each of the input attribute.
   * Assume each input attribute is discrete (if there is a continuous domain, separate it into
   * several bins to discretize it).
   * For example, if the 0th attribute can take on possible values (5, 6, 7), then the 0th index
   * of the returned array is 3.
   *
   * @return a array representing the dimension of each of the attribute.
   */
  int[] getAttrDim();
}
