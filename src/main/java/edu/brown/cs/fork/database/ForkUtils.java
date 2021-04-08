package edu.brown.cs.fork.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForkUtils {
  private static final List<String> CATEGORIES = Arrays.asList(
      "Burgers", "Chinese", "Pizza", "Italian", "Sushi Bars", "Indian", "Vietnamese", "Steakhouses",
      "Breakfast & Brunch", "Desserts", "Coffee & Tea", "Greek", "Middle Eastern", "Vegan",
      "Mexican", "Thai", "American", "Salad", "Barbeque", "Seafood");

  public ForkUtils() {  }

  public List<String> getOverlap(List<String> allRestCategories) {
    List<String> overlap = new ArrayList<>();
    for (String str : allRestCategories) {
      if (CATEGORIES.contains(str)) {
        overlap.add(str);
      }
    }
    return overlap;
  }

  public boolean isInCategories(String anyCategory) {
    return CATEGORIES.contains(anyCategory);
  }
}
