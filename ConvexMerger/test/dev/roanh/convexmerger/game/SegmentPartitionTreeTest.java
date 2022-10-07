package dev.roanh.convexmerger.game;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

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

	public void testSegmentDistribution(){
//		SegmentPartitionTree tree = SegmentPartitionTree.fromPoints(points);
//		LineSegment line = new LineSegment(points.get(0), points.get(5));
//		tree.addSegment(line);
//		
//		LineSegment query = new LineSegment(points.get(2), points.get(4));
//		tree.addSegment(query);
	}
}
