package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.VerticalDecomposition.Line;
import dev.roanh.convexmerger.player.GreedyPlayer;

public class VerticalDecompTest{
	
	@Test
	public void mergeEdgeCase1() throws InterruptedException{
		ConvexObject obj1 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(438.02112433303597D, 472.340820332763D),
			new Point2D.Double(489.78583106387856D, 506.06631108164527D),
			new Point2D.Double(485.86426237214806D, 586.8506261312936D),
			new Point2D.Double(438.80543807138207D, 548.4192529523347D)
		)));
		ConvexObject obj2 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(417.4349211301877D, 586.9787980622357D),
			new Point2D.Double(524.8859032836034D, 626.9787987178868D),
			new Point2D.Double(506.8466873016431D, 727.3709572261876D),
			new Point2D.Double(462.1408042159154D, 672.4689955419606D)
		)));
		ConvexObject obj3 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(498.32328864713327D, 850.2905201583827D),
			new Point2D.Double(540.6762305178227D, 799.3101271658862D),
			new Point2D.Double(602.6370158471645D, 790.6826760440791D),
			new Point2D.Double(568.9115250982823D, 880.8787559538806D)
		)));

		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints(), false);
		ConvexObject merged = new ConvexObject(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(merged.getPoints(), obj3.getPoints(), false);
		ConvexObject merged2 = new ConvexObject(ConvexUtil.mergeHulls(merged.getPoints(), obj3.getPoints(), lines));

		obj1.setID(0);
		obj2.setID(1);
		obj3.setID(2);
		merged.setID(3);
		merged2.setID(4);

		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS, Arrays.asList(obj1, obj2, obj3));
		List<ConvexObject> contained = new ArrayList<ConvexObject>();

		decomp.merge(null, obj1, obj2, merged, contained);
		decomp.merge(null, merged, obj3, merged2, contained);

		for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, merged, merged2)){
			assertEquals(merged2, decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()), "Object: " + obj.getID());
		}
	}
	
	@Test
	public void mergeInternalEdgeCase() throws InterruptedException{
		ConvexObject obj1 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(438.02112433303597D, 472.340820332763D),
			new Point2D.Double(489.78583106387856D, 506.06631108164527D),
			new Point2D.Double(485.86426237214806D, 586.8506261312936D),
			new Point2D.Double(438.80543807138207D, 548.4192529523347D)
		)));
		ConvexObject obj2 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(417.4349211301877D, 586.9787980622357D),
			new Point2D.Double(524.8859032836034D, 626.9787987178868D),
			new Point2D.Double(506.8466873016431D, 727.3709572261876D),
			new Point2D.Double(462.1408042159154D, 672.4689955419606D)
		)));
		ConvexObject obj3 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(498.32328864713327D, 850.2905201583827D),
			new Point2D.Double(540.6762305178227D, 799.3101271658862D),
			new Point2D.Double(602.6370158471645D, 790.6826760440791D),
			new Point2D.Double(568.9115250982823D, 880.8787559538806D)
		)));
		ConvexObject obj4 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(500.3398687839508D, 459.5424826145172D),
			new Point2D.Double(567.0065365433693D, 534.8366014957428D),
			new Point2D.Double(524.6535946726799D, 575.62091588974D)
		)));
		ConvexObject obj5 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(546.5439389444573, 614.4461613759188D),
			new Point2D.Double(594.3870769835694, 605.8187102541117D),
			new Point2D.Double(595.1713907219155, 721.1128297909884D),
			new Point2D.Double(560.6615862346871, 657.5834169849543D)
		)));

		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints(), false);
		ConvexObject merged = new ConvexObject(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(merged.getPoints(), obj3.getPoints(), false);
		ConvexObject merged2 = new ConvexObject(ConvexUtil.mergeHulls(merged.getPoints(), obj3.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(obj4.getPoints(), obj5.getPoints(), false);
		ConvexObject merged3 = new ConvexObject(ConvexUtil.mergeHulls(obj4.getPoints(), obj5.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(merged2.getPoints(), merged3.getPoints(), false);
		ConvexObject merged4 = new ConvexObject(ConvexUtil.mergeHulls(merged2.getPoints(), merged3.getPoints(), lines));

		obj1.setID(0);
		obj2.setID(1);
		obj3.setID(2);
		obj4.setID(3);
		obj5.setID(4);
		merged.setID(5);
		merged2.setID(6);
		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS, Arrays.asList(obj1, obj2, obj3, obj4, obj5));
		List<ConvexObject> contained = new ArrayList<ConvexObject>();

		decomp.merge(null, obj1, obj2, merged, contained);
		decomp.merge(null, merged, obj3, merged2, contained);
		decomp.merge(null, obj4, obj5, merged3, contained);
		decomp.merge(null, merged2, merged3, merged4, contained);

		for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, obj4, obj5, merged, merged2, merged3, merged4)){
			assertEquals(merged4, decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()), "Object: " + obj.getID());
		}
	}
	
	@Test
	public void horizontalMergeCase() throws InterruptedException{
		ConvexObject obj1 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(420D, 134D),
			new Point2D.Double(494D, 134D),
			new Point2D.Double(494D, 271D),
			new Point2D.Double(420D, 271D)
		)));
		ConvexObject obj2 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(527D, 134D),
			new Point2D.Double(607D, 134D),
			new Point2D.Double(607D, 271D),
			new Point2D.Double(527D, 271D)
		)));
		ConvexObject obj3 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(667D, 134D),
			new Point2D.Double(749D, 134D),
			new Point2D.Double(749D, 271D),
			new Point2D.Double(667D, 271D)
		)));

		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints(), false);
		ConvexObject merged = new ConvexObject(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(merged.getPoints(), obj3.getPoints(), false);
		ConvexObject merged2 = new ConvexObject(ConvexUtil.mergeHulls(merged.getPoints(), obj3.getPoints(), lines));

		obj1.setID(0);
		obj2.setID(1);
		obj3.setID(2);
		merged.setID(3);
		merged2.setID(4);
		List<ConvexObject> contained = new ArrayList<ConvexObject>();
		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS, Arrays.asList(obj1, obj2, obj3));

		testPlayfield(Arrays.asList(obj1, obj2, obj3), decomp);

		decomp.merge(null, obj1, obj2, merged, contained);
		testPlayfield(Arrays.asList(merged, obj3), decomp);

		decomp.merge(null, merged, obj3, merged2, contained);
		testPlayfield(Arrays.asList(merged2), decomp);

		for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, merged, merged2)){
			assertEquals(merged2, decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()), "Object: " + obj.getID());
		}
	}
	
	@Test
	public void verticalMergeCase() throws InterruptedException{
		ConvexObject obj1 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(134.97237278180245D, 420.3156037808325D),
			new Point2D.Double(134.97237278180245D, 494.55090032824535D),
			new Point2D.Double(271.14884502844933D, 494.55090032824535D),
			new Point2D.Double(271.14884502844933D, 420.3156037808325D)
		)));
		ConvexObject obj2 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(134.97237278180245D, 527.9794331301437D),
			new Point2D.Double(134.97237278180245D, 607.1951207030997D),
			new Point2D.Double(271.14884502844933D, 607.1951207030997D),
			new Point2D.Double(271.14884502844933D, 527.9794331301437D)
		)));
		ConvexObject obj3 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(134.97237278180245D, 667.5037849870058D),
			new Point2D.Double(134.97237278180245D, 749.0724137750002D),
			new Point2D.Double(271.14884502844933D, 749.0724137750002D),
			new Point2D.Double(271.14884502844933D, 667.5037849870058D)
		)));
		
		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints(), false);
		ConvexObject merged = new ConvexObject(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(merged.getPoints(), obj3.getPoints(), false);
		ConvexObject merged2 = new ConvexObject(ConvexUtil.mergeHulls(merged.getPoints(), obj3.getPoints(), lines));

		obj1.setID(0);
		obj2.setID(1);
		obj3.setID(2);
		merged.setID(3);
		merged2.setID(4);
		List<ConvexObject> contained = new ArrayList<ConvexObject>();
		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS, Arrays.asList(obj1, obj2, obj3));

		testPlayfield(Arrays.asList(obj1, obj2, obj3), decomp);

		decomp.merge(null, obj1, obj2, merged, contained);
		testPlayfield(Arrays.asList(merged, obj3), decomp);

		decomp.merge(null, merged, obj3, merged2, contained);
		testPlayfield(Arrays.asList(merged2), decomp);

		for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, merged, merged2)){
			assertEquals(merged2, decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()), "Object: " + obj.getID());
		}
	}
	
	@Test
	public void testInternalVerticalSegments() throws InterruptedException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<Point2D> points = Arrays.asList(
			new Point2D.Double(420D, 271D),
			new Point2D.Double(420D, 134D),
			new Point2D.Double(494D, 134D),
			new Point2D.Double(494D, 271D),
			new Point2D.Double(527D, 271D),
			new Point2D.Double(527D, 134D),
			new Point2D.Double(607D, 134D),
			new Point2D.Double(607D, 271D),
			new Point2D.Double(667D, 271D),
			new Point2D.Double(667D, 134D),
			new Point2D.Double(749D, 134D),
			new Point2D.Double(749D, 271D)
		);
		
		ConvexObject obj1 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(points.get(0), points.get(2), points.get(1), points.get(3))));
		ConvexObject obj2 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(points.get(4), points.get(6), points.get(5), points.get(7))));
		ConvexObject obj3 = new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(points.get(8), points.get(10), points.get(9), points.get(11))));

		obj1.setID(1);
		obj2.setID(2);
		obj3.setID(3);

		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS, new ArrayList<ConvexObject>());
		Method method = VerticalDecomposition.class.getDeclaredMethod("addSegment", Line2D.class, ConvexObject.class);
		method.setAccessible(true);
		method.invoke(decomp, new Line(points.get(0), points.get(1)), obj1);
		method.invoke(decomp, new Line(points.get(2), points.get(3)), obj1);
		method.invoke(decomp, new Line(points.get(1), points.get(2)), obj1);
		method.invoke(decomp, new Line(points.get(3), points.get(0)), obj1);
		method.invoke(decomp, new Line(points.get(4), points.get(5)), obj2);
		method.invoke(decomp, new Line(points.get(6), points.get(7)), obj2);
		method.invoke(decomp, new Line(points.get(5), points.get(6)), obj2);
		method.invoke(decomp, new Line(points.get(7), points.get(4)), obj2);
		method.invoke(decomp, new Line(points.get(8), points.get(9)), obj3);
		method.invoke(decomp, new Line(points.get(10), points.get(11)), obj3);
		method.invoke(decomp, new Line(points.get(9), points.get(10)), obj3);
		method.invoke(decomp, new Line(points.get(11), points.get(8)), obj3);

		Point2D[] mergeLines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints(), false);
		ConvexObject merged = new ConvexObject(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), mergeLines));

		mergeLines = ConvexUtil.computeMergeLines(ConvexUtil.computeConvexHull(merged.getPoints()), obj3.getPoints(), false);
		ConvexObject merged2 = new ConvexObject(ConvexUtil.mergeHulls(merged.getPoints(), obj3.getPoints(), mergeLines));

		merged.setID(4);
		merged2.setID(5);
		List<ConvexObject> contained = new ArrayList<ConvexObject>();

		testPlayfield(Arrays.asList(obj1, obj2, obj3), decomp);

		decomp.merge(null, obj1, obj2, merged, contained);
		testPlayfield(Arrays.asList(merged, obj3), decomp);

		decomp.merge(null, merged, obj3, merged2, contained);
		testPlayfield(Arrays.asList(merged2), decomp);

		for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, merged, merged2)){
			assertEquals(merged2, decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()), "Object: " + obj.getID());
		}
	}
	
	@Test
	public void edgeCaseSeed() throws InterruptedException{
		testSeed("3Y64YQ01S7B35T82PK9G");
	}

	@Test
	public void edgeCaseSeed2() throws InterruptedException{
		testSeed("3Y657GF10E9XRWYN64ZU");
	}

	@Test
	public void edgeCaseSeed3() throws InterruptedException{
		testSeed("3Y657GF3SVVAK592WVM5");
	}

	@Test
	public void edgeCaseSeed4() throws InterruptedException{
		//Failed due to a convex object merging error
		testSpecific("3Y657GF2ENJVQR6KVR2I");
	}

	@Test 
	public void edgeCaseSeed5() throws InterruptedException{
		//The start trapezoid of the merge is the one on the left of the line that was newly added to the object
		testSpecific("3Y64YQO2OCQZUAWQT6OQ");// 9->18->25->15->31
	}

	@Test
	public void edgeCaseInternalUpdates() throws InterruptedException{
		testSpecific("3Y657GF0UKKHHRZ2NZD1");
	}

	@Test
	public void edgeCaseVerticalLinePlusWeirdMerge() throws InterruptedException{
		testSpecific("3Y657GF0Y4N8SEJO4BFO");// 80->52
	}

	@Test
	public void edgeCaseUnderInvestigation() throws InterruptedException{
		testSpecific("3Y64YQ02J1WPAP9NM7O8");// 3->39
	}

	@Test
	public void edgeCasesFixedWithGetLeftmostBelow() throws InterruptedException{
		testSpecific("3Y657GF3UY8PP82I89LY");// 8->12->63; 31->47->91
		testSpecific("3Y657GF2ZJSAPANVG50E");// 52->91
		testSpecific("3Y657GF162MOIVY34A16");
		testSpecific("3Y657GF39AOQSOYS0Y0C");
	}

	@Test
	public void shortCuttingEdgeCases() throws InterruptedException{
		testSpecific("3Y64YQ039ZZB4UZQQ680");
		testSpecific("3Y64YQ00WHENOONSIUGC");
		testSpecific("3Y64YQ01OUDN6ZGF6D6V");
		testSpecific("3Y64YQ02I2KJYKBFBBPE");// 2->31->8
		testSpecific("3Y64YQ02J1WPAP9NM7O8");// 3->39
	}

	@RepeatedTest(10)
	public void testRandom() throws InterruptedException{
		testSpecific(new PlayfieldGenerator());
	}
	
	public void testSpecific(String seed) throws IllegalArgumentException, InterruptedException{
		testSpecific(new PlayfieldGenerator(seed));
	}
	
	public void testSpecific(PlayfieldGenerator gen) throws InterruptedException{
		GameState game = new GameState(gen, Arrays.asList(new GreedyPlayer(), new GreedyPlayer()));
		game.init();
		System.out.println("Game seed: " + game.getSeed());

		while(!game.isFinished()){
			game.executePlayerTurn();
			testPlayfield(game.getObjects(), game.getVerticalDecomposition());
		}
	}

	private void testSeed(String seed) throws InterruptedException{
		List<ConvexObject> objects = new PlayfieldGenerator(seed).generatePlayfield();
		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);

		int id = 1;
		for(ConvexObject obj : objects){
			obj.setID(id++);
			decomp.addObject(obj);
		}

		testPlayfield(objects, decomp);
	}
	
	private void testPlayfield(List<ConvexObject> objects, VerticalDecomposition decomp){
		for(ConvexObject obj : objects){
			testObject(obj, decomp, ThreadLocalRandom.current());
		}
	}

	private void testObject(ConvexObject obj, VerticalDecomposition decomp, Random random){
		Rectangle2D bounds = obj.getShape().getBounds2D();
		OfDouble xs = random.doubles(bounds.getMinX() - 1.0D, bounds.getMaxX() + 1.0D).iterator();
		OfDouble ys = random.doubles(bounds.getMinY() - 1.0D, bounds.getMaxY() + 1.0D).iterator();
		for(int i = 0; i < 1000; i++){
			double x = xs.nextDouble();
			double y = ys.nextDouble();
			if(obj.contains(x, y)){
				assertEquals(obj, decomp.queryObject(x, y), obj.getID() + " " + decomp.queryObject(x, y).getID());
			}else{
				assertNotEquals(obj, decomp.queryObject(x, y), x + " " + y);
			}
		}
	}
}
