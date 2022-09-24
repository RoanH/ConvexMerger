package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class ConvexUtilTest{

	@Test
	public void normalMergeTest(){
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
	public void endPointMergeTest(){
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
	public void parallelMergeTest(){
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
	public void horizontalBoxColinMergeTest(){
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
	public void verticalBoxColinMergeTest(){
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
	public void mergeLineOverlapMergeTest(){
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
	public void overflowMergeTest(){
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
	public void earlyVerticalMergeTest(){
		mergeTest(
			new ConvexObject(Arrays.asList(
				new Point2D.Double(866.2229972503868, 367.14396161730934),
				new Point2D.Double(943.8700573466507, 331.84984339173485),
				new Point2D.Double(972.8896656654564, 436.94788433011223),
				new Point2D.Double(907.7916253827301, 482.438081154186)
			)),
			new ConvexObject(Arrays.asList(
				new Point2D.Double(1079.1028371711282, 322.30642495494754),
				new Point2D.Double(1123.8087202568558, 261.91426710229786),
				new Point2D.Double(1131.6518576403168, 351.32603327375324),
				new Point2D.Double(1079.1028371711282, 389.75740645271213)
			))
		);
	}
	
	@Test
	public void leftSinglePointMergeTest(){
		mergeTest(
			new ConvexObject(Arrays.asList(
				new Point2D.Double(1020.2946945766233, 336.08131352685535),
				new Point2D.Double(1145.4590573351688, 147.65612980830525),
				new Point2D.Double(1259.1845493953533, 109.22475662934636),
				new Point2D.Double(1282.7139615457363, 223.73456242787694),
				new Point2D.Double(1096.3731271961951, 378.43425539754475),
				new Point2D.Double(1041.4711655119681, 376.86562792085255)
			)),
			new ConvexObject(Arrays.asList(
				new Point2D.Double(982.9658142458002, 193.4409184196394),
				new Point2D.Double(1059.0442468653719, 178.5389573910635),
				new Point2D.Double(1093.5540513526003, 220.89189926175288),
				new Point2D.Double(1020.612873686413, 255.40170374898128)
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
