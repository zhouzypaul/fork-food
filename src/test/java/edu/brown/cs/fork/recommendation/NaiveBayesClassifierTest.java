package edu.brown.cs.fork.recommendation;

import edu.brown.cs.fork.ITest;
import edu.brown.cs.fork.exceptions.NoTestDataException;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * tests for NaiveBayesClassifier.
 */
public class NaiveBayesClassifierTest implements ITest {

  /**
   * coordinates on a 2D plane. This serves as a test type of recommendable.
   */
  private class Coor implements Recommendable {

    private final double x;
    private final double y;

    public Coor(double x, double y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String getId() {
      return Double.toString(x) + Double.toString(y);
    }

    @Override
    public int getNumAttr() {
      return 2;
    }

    @Override
    public int getNumClasses() {
      return 2;
    }

    @Override
    public int[] getAttr() {
      int[] attr = new int[2];
      if (x > 0) {
        attr[0] = 1;
      }
      if (y > 0) {
        attr[1] = 1;
      }
      return attr;
    }

    @Override
    public int[] getAttrDim() {
      int[] attrDim = new int[2];
      Arrays.fill(attrDim, 2);
      return attrDim;
    }

    @Override
    public String toString() {
      return "Coor {"
              + "x=" + x
              + ", y=" + y
              + '}';
    }
  }

  /**
   * a tuple of 2D coordinates and their label.
   * This is a test data structure for LabeledData.
   */
  private class LabeledCoor implements LabeledData<Coor> {

    private final Coor data;
    private final int label;

    public LabeledCoor(Coor data, int label) {
      this.data = data;
      this.label = label;
    }

    @Override
    public Coor getData() {
      return this.data;
    }

    @Override
    public int getLabel() {
      return this.label;
    }
  }

  private NaiveBayesClassifier<Coor, LabeledCoor> classifier;

  /**
   * set up a regular naive bayes classifier.
   */
  @Override
  public void setUp() {
    // top right
    Coor tr1 = new Coor(1, 1);
    Coor tr2 = new Coor(2, 2);
    Coor tr3 = new Coor(3, 3);
    LabeledCoor labelTr1 = new LabeledCoor(tr1, 1);
    LabeledCoor labelTr2 = new LabeledCoor(tr2, 1);
    LabeledCoor labelTr3 = new LabeledCoor(tr3, 1);
    // bottom left
    Coor bl1 = new Coor(-1, -1);
    Coor bl2 = new Coor(-2, -2);
    Coor bl3 = new Coor(-3, -3);
    LabeledCoor labelBl1 = new LabeledCoor(bl1, 1);
    LabeledCoor labelBl2 = new LabeledCoor(bl2, 1);
    LabeledCoor labelBl3 = new LabeledCoor(bl3, 1);
    // top left
    Coor tl1 = new Coor(-1, 1);
    Coor tl2 = new Coor(-2, 2);
    Coor tl3 = new Coor(-3, 3);
    LabeledCoor labelTl1 = new LabeledCoor(tl1, 0);
    LabeledCoor labelTl2 = new LabeledCoor(tl2, 0);
    LabeledCoor labelTl3 = new LabeledCoor(tl3, 0);
    // bottom right
    Coor br1 = new Coor(1, -1);
    Coor br2 = new Coor(2, -2);
    Coor br3 = new Coor(3, -3);
    LabeledCoor labelBr1 = new LabeledCoor(br1, 0);
    LabeledCoor labelBr2 = new LabeledCoor(br2, 0);
    LabeledCoor labelBr3 = new LabeledCoor(br3, 0);
    // training data
    List<LabeledCoor> train = Arrays.asList(labelTr1, labelTr2, labelTl1, labelTl2,
            labelBl1, labelBl2, labelBr1, labelBr2);
    List<Coor> test = Arrays.asList(tr3, bl2, bl3, tl2, tl3, br3);
    // init recommender
    try {
      this.classifier = new NaiveBayesClassifier<>(train, test);
    } catch (NoTestDataException e) {
      this.tearDown();
      fail();
    }
  }

  /**
   * tear down.
   */
  @Override
  public void tearDown() {
    this.classifier = null;
  }

  /**
   * test the recommend method.
   */
  @Test
  public void testRecommend() {
    this.setUp();
    // illegal set up
    assertThrows(NoTestDataException.class,
            () -> new NaiveBayesClassifier<>(new LinkedList<>(), new LinkedList<>()));
    // recommend negative
    List<Coor> neg = this.classifier.recommend(-1, 1);
    assertEquals(neg.size(), 0);
    // recommend 0
    List<Coor> zero = this.classifier.recommend(0, 1);
    assertEquals(zero.size(), 0);
    // recommend 1
    List<Coor> one = this.classifier.recommend(1, 1);
    assertEquals(one.size(), 1);
    assertEquals(Double.compare(one.get(0).x, 3.0), 0);
    assertEquals(Double.compare(one.get(0).y, 3.0), 0);
    // recommend 3
    List<Coor> three = this.classifier.recommend(3, 1);
    assertEquals(three.size(), 3);
    assertEquals(Double.compare(three.get(1).x, -2.0), 0);
    assertEquals(Double.compare(three.get(1).y, -2.0), 0);
    assertEquals(Double.compare(three.get(2).x, -3.0), 0);
    assertEquals(Double.compare(three.get(2).x, -3.0), 0);
    // recommend too much
    List<Coor> much = this.classifier.recommend(10, 1);
    assertEquals(much.size(), 6);
    this.tearDown();
  }
}
