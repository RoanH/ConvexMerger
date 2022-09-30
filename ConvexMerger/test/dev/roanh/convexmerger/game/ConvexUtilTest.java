package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ConvexUtilTest{

	@Test
	public void normalMergeTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(33.0D, 118.0D),
				new Point2D.Double(57.0D, 178.0D),
				new Point2D.Double(98.0D, 236.0D),
				new Point2D.Double(180.0D, 270.0D),
				new Point2D.Double(204.0D, 171.0D),
				new Point2D.Double(175.0D, 106.0D),
				new Point2D.Double(146.0D, 77.0D),
				new Point2D.Double(116.0D, 65.0D),
				new Point2D.Double(38.0D, 70.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100.0D + 116.0D, 177.0D),
				new Point2D.Double(100.0D + 133.0D, 212.0D),
				new Point2D.Double(100.0D + 263.0D, 217.0D),
				new Point2D.Double(100.0D + 286.0D, 150.0D),
				new Point2D.Double(100.0D + 281.0D, 65.0D),
				new Point2D.Double(100.0D + 256.0D, 24.0D),
				new Point2D.Double(100.0D + 219.0D, 42.0D),
				new Point2D.Double(100.0D + 181.0D, 71.0D),
				new Point2D.Double(100.0D + 133.0D, 113.0D)
			)))
		);
	}
	
	@Test
	public void endPointMergeTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30.0D, 250.0D),
				new Point2D.Double(100.0D, 100.0D),
				new Point2D.Double(100.0D, 200.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100.0D + 30.0D, 100.0D),
				new Point2D.Double(100.0D + 100.0D, 110.0D),
				new Point2D.Double(100.0D + 100.0D, 200.0D)
			)))
		);
	}
	
	@Test
	public void parallelMergeTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30.0D, 100.0D),
				new Point2D.Double(100.0D, 80.0D),
				new Point2D.Double(100.0D, 200.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100.0D + 30.0D, 100.0D),
				new Point2D.Double(100.0D + 100.0D, 110.0D),
				new Point2D.Double(100.0D + 100.0D, 200.0D),
				new Point2D.Double(100.0D + 50.0D, 150.0D)
			)))
		);
	}
	
	@Test
	public void horizontalBoxColinMergeTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30.0D, 200.0D),
				new Point2D.Double(30.0D, 100.0D),
				new Point2D.Double(100.0D, 100.0D),
				new Point2D.Double(100.0D, 200.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100.0D + 30.0D, 200.0D),
				new Point2D.Double(100.0D + 30.0D, 100.0D),
				new Point2D.Double(100.0D + 100.0D, 100.0D),
				new Point2D.Double(100.0D + 100.0D, 200.0D)
			)))
		);
	}
	
	@Test
	public void verticalBoxColinMergeTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30.0D, 200.0D),
				new Point2D.Double(30.0D, 100.0D),
				new Point2D.Double(100.0D, 100.0D),
				new Point2D.Double(100.0D, 200.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(30.0D, 130.0D + 200.0D),
				new Point2D.Double(30.0D, 130.0D + 100.0D),
				new Point2D.Double(100.0D, 130.0D + 100.0D),
				new Point2D.Double(100.0D, 130.0D + 200.0D)
			)))
		);
	}
	
	@Test
	public void mergeLineOverlapMergeTest(){
		mergeTest(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(50.0D, -200.0D),
				new Point2D.Double(30.0D, 100.0D),
				new Point2D.Double(100.0D, 100.0D),
				new Point2D.Double(100.0D, 300.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(100.0D + 50.0D, 140.0D),
				new Point2D.Double(100.0D + 30.0D, 100.0D),
				new Point2D.Double(100.0D + 300.0D, 150.0D)
			)))
		);
	}
	
	@Test
	public void overflowMergeTest(){
		mergeTest(
			new ConvexObject(Arrays.asList(
				new Point2D.Double(68.13547921930717D, 823.6810526743893D),
				new Point2D.Double(71.27273417269157D, 695.053599585629D),
				new Point2D.Double(209.31195212160515D, 690.3477171555523D),
				new Point2D.Double(172.44920641933845D, 816.6222290292744D)
			)),
			new ConvexObject(Arrays.asList(
				new Point2D.Double(48.22851710262876, 607.3220363481171D),
				new Point2D.Double(68.62067429962735, 477.12595578266445D),
				new Point2D.Double(198.81675486507993, 519.4788976533539D),
				new Point2D.Double(148.62067561092954, 612.0279187781937D)
			))
		);
	}
	
	@Test
	public void earlyVerticalMergeTest(){
		mergeTest(
			new ConvexObject(Arrays.asList(
				new Point2D.Double(866.2229972503868D, 367.14396161730934D),
				new Point2D.Double(943.8700573466507D, 331.84984339173485D),
				new Point2D.Double(972.8896656654564D, 436.94788433011223D),
				new Point2D.Double(907.7916253827301D, 482.438081154186D)
			)),
			new ConvexObject(Arrays.asList(
				new Point2D.Double(1079.1028371711282D, 322.30642495494754D),
				new Point2D.Double(1123.8087202568558D, 261.91426710229786D),
				new Point2D.Double(1131.6518576403168D, 351.32603327375324D),
				new Point2D.Double(1079.1028371711282D, 389.75740645271213D)
			))
		);
	}
	
	@Test
	public void leftSinglePointMergeTest(){
		mergeTest(
			new ConvexObject(Arrays.asList(
				new Point2D.Double(1020.2946945766233D, 336.08131352685535D),
				new Point2D.Double(1145.4590573351688D, 147.65612980830525D),
				new Point2D.Double(1259.1845493953533D, 109.22475662934636D),
				new Point2D.Double(1282.7139615457363D, 223.73456242787694D),
				new Point2D.Double(1096.3731271961951D, 378.43425539754475D),
				new Point2D.Double(1041.4711655119681D, 376.86562792085255D)
			)),
			new ConvexObject(Arrays.asList(
				new Point2D.Double(982.9658142458002D, 193.4409184196394D),
				new Point2D.Double(1059.0442468653719D, 178.5389573910635D),
				new Point2D.Double(1093.5540513526003D, 220.89189926175288D),
				new Point2D.Double(1020.612873686413D, 255.40170374898128D)
			))
		);
	}
	
	@Test
	public void mergeWithPointTest(){
		//this is honestly more accidental correct behaviour, but we currently rely on it for helper line computation
		List<Point2D> obj1 = ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(30.0D, 100.0D),
			new Point2D.Double(100.0D, 80.0D),
			new Point2D.Double(100.0D, 200.0D)
		));
		List<Point2D> obj2 = Arrays.asList(new Point2D.Double(400.0D, 400.0D));
		Point2D[] lines = ConvexUtil.computeMergeLines(obj1, obj2);
		assertTrue(ConvexUtil.checkInvariants(ConvexUtil.mergeHulls(obj1, obj2, lines)));
	}
	
	@Test
	public void verticalAngleTest(){
		assertEquals(0.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 0.0D, 0.0D, -10.0D)));
		assertEquals(315.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 0.0D, -10.0D, -10.0D)));
		assertEquals(45.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 0.0D, 10.0D, -10.0D)));
		assertEquals(90.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 0.0D, 10.0D, 0.0D)));
		assertEquals(180.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 0.0D, 0.0D, 10.0D)));
		assertEquals(135.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 0.0D, 10.0D, 10.0D)));
		assertEquals(270.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 0.0D, -10.0D, 0.0D)));
		assertEquals(0.0D, Math.toDegrees(ConvexUtil.angleFromVertical(0.0D, 200.0D, 0.0D, 0.0D)));
	}
	
	@Test
	public void lineAngleTest(){
		assertEquals(270.0D, Math.toDegrees(ConvexUtil.angleBetweenLines(
			new Line2D.Double(0.0D, 0.0D, 0.0D, 10.0D),
			new Line2D.Double(0.0D, 0.0D, 10.0D, 0.0D)
		)));
		assertEquals(315.0D, Math.toDegrees(ConvexUtil.angleBetweenLines(
			new Line2D.Double(0.0D, 0.0D, 0.0D, 10.0D),
			new Line2D.Double(5.0D, 5.0D, 10.0D, 10.0D)
		)));
		assertEquals(90.0D, Math.toDegrees(ConvexUtil.angleBetweenLines(
			new Line2D.Double(0.0D, 0.0D, 0.0D, 10.0D),
			new Line2D.Double(0.0D, 0.0D, -10.0D, 0.0D)
		)));
		assertEquals(45.0D, Math.toDegrees(ConvexUtil.angleBetweenLines(
			new Line2D.Double(0.0D, 0.0D, 0.0D, 10.0D),
			new Line2D.Double(0.0D, 0.0D, -5.0D, 5.0D)
		)));
		assertEquals(135.0D, Math.toDegrees(ConvexUtil.angleBetweenLines(
			new Line2D.Double(0.0D, 0.0D, 0.0D, 10.0D),
			new Line2D.Double(0.0D, 0.0D, -10.0D, -10.0D)
		)));
		assertEquals(225.0D, Math.toDegrees(ConvexUtil.angleBetweenLines(
			new Line2D.Double(0.0D, 0.0D, 0.0D, 10.0D),
			new Line2D.Double(0.0D, 0.0D, 10.0D, -10.0D)
		)));
	}
	
	private void mergeTest(ConvexObject obj1, ConvexObject obj2){
		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints());
		assertTrue(ConvexUtil.checkInvariants(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines)));
	}
}
