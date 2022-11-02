package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import dev.roanh.convexmerger.game.SegmentPartitionTree.LineSegment;

@Disabled
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
	public void testSegmentDistribution(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(5));
		tree.addSegment(testPoints.get(2), testPoints.get(4));
	
		//TODO this will be wrong after proper storage is implemented
		assertArrayEquals(new int[]{0, 0, 0, 0, 0, 1, 2, 2}, tree.streamCells().map(PartitionTree::getData).mapToInt(List::size).sorted().toArray());
	}
	
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
}
