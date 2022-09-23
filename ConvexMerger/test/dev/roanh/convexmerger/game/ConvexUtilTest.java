package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class ConvexUtilTest{

	@Test
	public void normalTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(33, 118),
				new Point2D.Double(57, 178),
				new Point2D.Double(98, 236),
				new Point2D.Double(180, 270),
				new Point2D.Double(204, 171),
				new Point2D.Double(175, 106),
				new Point2D.Double(146, 77),
				new Point2D.Double(116, 65),
				new Point2D.Double(38, 70)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100 + 116, 177),
				new Point2D.Double(100 + 133, 212),
				new Point2D.Double(100 + 263, 217),
				new Point2D.Double(100 + 286, 150),
				new Point2D.Double(100 + 281, 65),
				new Point2D.Double(100 + 256, 24),
				new Point2D.Double(100 + 219, 42),
				new Point2D.Double(100 + 181, 71),
				new Point2D.Double(100 + 133, 113)
			)))
		);
	}
	
	@Test
	public void endPointTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30, 250),
				new Point2D.Double(100, 100),
				new Point2D.Double(100, 200)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100 + 30, 100),
				new Point2D.Double(100 + 100, 110),
				new Point2D.Double(100 + 100, 200)
			)))
		);
	}
	
	@Test
	public void parallelTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30, 100),
				new Point2D.Double(100, 80),
				new Point2D.Double(100, 200)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100 + 30, 100),
				new Point2D.Double(100 + 100, 110),
				new Point2D.Double(100 + 100, 200),
				new Point2D.Double(100 + 50, 150)
			)))
		);
	}
	
	@Test
	public void horizontalBoxColinTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30, 200),
				new Point2D.Double(30, 100),
				new Point2D.Double(100, 100),
				new Point2D.Double(100, 200)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100 + 30, 200),
				new Point2D.Double(100 + 30, 100),
				new Point2D.Double(100 + 100, 100),
				new Point2D.Double(100 + 100, 200)
			)))
		);
	}
	
	@Test
	public void verticalBoxColinTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30, 200),
				new Point2D.Double(30, 100),
				new Point2D.Double(100, 100),
				new Point2D.Double(100, 200)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30, 130 + 200),
				new Point2D.Double(30, 130 + 100),
				new Point2D.Double(100, 130 + 100),
				new Point2D.Double(100, 130 + 200)
			)))
		);
	}
	
	@Test
	public void mergeLineOverlapTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(50, -200),
				new Point2D.Double(30, 100),
				new Point2D.Double(100, 100),
				new Point2D.Double(100, 300)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100 + 50, 140),
				new Point2D.Double(100 + 30, 100),
				new Point2D.Double(100 + 300, 150)
			)))
		);
	}
	
	@Test
	public void overflowTest(){
		mergeTest(
			new ConvexObject(Arrays.asList(
				new Point2D.Double(68.13547921930717, 823.6810526743893),
				new Point2D.Double(71.27273417269157, 695.053599585629),
				new Point2D.Double(209.31195212160515, 690.3477171555523),
				new Point2D.Double(172.44920641933845, 816.6222290292744)
			)),
			new ConvexObject(Arrays.asList(
				new Point2D.Double(48.22851710262876, 607.3220363481171),
				new Point2D.Double(68.62067429962735, 477.12595578266445),
				new Point2D.Double(198.81675486507993, 519.4788976533539),
				new Point2D.Double(148.62067561092954, 612.0279187781937)
			))
		);
	}
	
	@Test
	public void verticalAngleTest(){
		assertEquals(0.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 0, 0, -10)));
		assertEquals(315.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 0, -10, -10)));
		assertEquals(45.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 0, 10, -10)));
		assertEquals(90.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 0, 10, 0)));
		assertEquals(180.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 0, 0, 10)));
		assertEquals(135.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 0, 10, 10)));
		assertEquals(270.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 0, -10, 0)));
		assertEquals(0.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0, 200, 0, 0)));
	}
	
	private void mergeTest(ConvexObject obj1, ConvexObject obj2){
		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints());
		assertTrue(ConvexUtil.checkInvariants(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines)));
	}
}
