package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class KDTreeTest{
	private static final List<Point2D> testPoints = Arrays.asList(
		new Point2D.Double(400.0D, 400.0D),
		new Point2D.Double(200.0D, 200.0D),
		new Point2D.Double(100.0D, 300.0D)
	);

	@Test
	public void simpleConstruction(){
		KDTree<Void> tree = new KDTree<Void>(new ArrayList<Point2D>(testPoints));
		
		assertEquals(testPoints.get(1), tree.getPoint());
		assertEquals(testPoints.get(2), tree.getLowNode().getPoint());
		assertEquals(testPoints.get(0), tree.getHighNode().getPoint());
		
		tree.streamLeafCells().forEach(cell->{
			assertTrue(cell.getData().isEmpty());
		});
	}
}
