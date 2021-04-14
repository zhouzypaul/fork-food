package edu.brown.cs.fork.database;

import edu.brown.cs.fork.exceptions.ErrorException;

import java.util.List;

/**
 * Class for a method to find distance between two sets of coordinates.
 */
public class DistanceCalculator {

  /**
   * Empty constructor.
   */
  public DistanceCalculator() { }

  /**
   * Finds Euclidean distance of 2 sets of coordinates.
   * @param c1 coordinate 1
   * @param c2 coordinate 2
   * @return distance between input coordinates
   * @throws ErrorException ErrorException
   */
  public double getEucDistance(List<Double> c1, List<Double> c2) throws ErrorException {
    if (c1.size() != c2.size()) {
      throw new ErrorException("ERROR: Can't calculate distance");
    }

    double dist = 0;
    for (int x = 0; x < c1.size(); x++) {
      dist += Math.pow(c1.get(x) - c2.get(x), 2);
    }
    return Math.sqrt(dist);
  }

  /**
   * Finds Haversine distance of 2 point.
   * Inspiration:
   * https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere
   * @param c1 coordinate 1
   * @param c2 coordinate 2
   * @param rad radius of sphere
   * @return distance between input coordinates
   */
  public double getHaversineDistance(List<Double> c1, List<Double> c2, double rad) {
    double lat1 = c1.get(0);
    double lon1 = c1.get(1);
    double lat2 = c2.get(0);
    double lon2 = c2.get(1);
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    // convert to radians
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);

    // apply formulae
    double a;
    a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2)
      * Math.cos(lat1) * Math.cos(lat2);
    double c = 2 * Math.asin(Math.sqrt(a));
    return rad * c;
  }
}
