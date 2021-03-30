package edu.brown.cs.fork.recommendation;

/**
 * an interface for objects that can be recommended.
 * Such objects must have fields that are comparable.
 */
public interface Recommendable {
  /**
   * return the number of attributes this type of object has.
   * @return number of attributes (dimension of the input features).
   */
  int getNumAttr();

  /**
   * how many classes should this type of object be classified into.
   * @return number of classes (dimension of the output space).
   */
  int getNumClasses();

  /**
   * get an array of ints representing the attributes of this type of recommendable.
   * The array array has length this.getNumAttr(), and each index of the array is an
   * int representing a possible value of that attribute.
   * @return an attributes array.
   */
  int[] getAttr();
}
