package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.roanh.convexmerger.game.SegmentPartitionTree.LineSegment;

public class SegmentPartitionTreeTest{
	private static final List<Point2D> testPoints = Arrays.asList(
		new Point2D.Double(100.0D, 300.0D),
		new Point2D.Double(200.0D, 200.0D),
		new Point2D.Double(400.0D, 400.0D),
		new Point2D.Double(600.0D, 600.0D),
		new Point2D.Double(800.0D, 800.0D),
		new Point2D.Double(1000.0D, 800.0D),
		new Point2D.Double(900.0D, 700.0D)
	);
	
	@Test
	public void intersectTest(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(5));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void noIntersectTest(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(4), testPoints.get(5));
		assertFalse(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void edgeIntersectTest(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(3), testPoints.get(5));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void edgeCaseSeed0(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromObjects(
			new PlayfieldGenerator("3Y657EW3LVKQ9LHX178Z").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(133.692809343338D, 690.875816822052D, 107.05947849314808D, 518.3026268080998D)));
		assertFalse(tree.intersects(new Line2D.Double(238.82418653529285D, 617.9104715780545D, 249.7712426185608D, 668.1307184100151D)));
	}
	
	@Test
	public void simpleIntersectionTest(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(6));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(3)));
	}
	
	@Test
	public void overlapIsNotIntersectionTest(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(6));
		assertFalse(tree.intersects(testPoints.get(0), testPoints.get(3)));
	}
	
	@Test
	public void edgeCaseSeed1(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(
			new PlayfieldGenerator("3Y64QTK1WI14ZQ79GFMW").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(444.31914618102513D, 194.85131929075814D, 628.4039710577933D, 331.7503860849795D)));
		assertFalse(tree.intersects(new Line2D.Double(514.6784789976089D, 402.33862253612847D, 350.20149757949315D, 306.22387013590435D)));
	}
}
