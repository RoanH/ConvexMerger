package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.player.GreedyPlayer;

public class VerticalDecompTest{
	
	@Test
	public void mergeEdgeCase1() throws InterruptedException{
		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);
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

		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints());
		ConvexObject merged = new ConvexObject(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(merged.getPoints(), obj3.getPoints());
		ConvexObject merged2 = new ConvexObject(ConvexUtil.mergeHulls(merged.getPoints(), obj3.getPoints(), lines));

		obj1.setID(0);
		obj2.setID(1);
		obj3.setID(2);
		merged.setID(3);
		merged2.setID(4);
		decomp.addObject(obj1);
		decomp.addObject(obj2);
		decomp.addObject(obj3);
		decomp.rebuild();
		List<ConvexObject> contained = new ArrayList<ConvexObject>();

		decomp.addObject(merged);
		decomp.merge(null, obj1, obj2, merged, contained);

		decomp.addObject(merged2);
		decomp.merge(null, merged, obj3, merged2, contained);

		for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, merged, merged2)){
			assertEquals(merged2, decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()), "Object: " + obj.getID());
		}
	}
	
	@Test
	public void mergeInternalEdgeCase() throws InterruptedException{
		VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);
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

		Point2D[] lines = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints());
		ConvexObject merged = new ConvexObject(ConvexUtil.mergeHulls(obj1.getPoints(), obj2.getPoints(), lines));

		lines = ConvexUtil.computeMergeLines(merged.getPoints(), obj3.getPoints());
		ConvexObject merged2 = new ConvexObject(ConvexUtil.mergeHulls(merged.getPoints(), obj3.getPoints(), lines));
		
		lines = ConvexUtil.computeMergeLines(obj4.getPoints(), obj5.getPoints());
		ConvexObject merged3 = new ConvexObject(ConvexUtil.mergeHulls(obj4.getPoints(), obj5.getPoints(), lines));
		
		lines = ConvexUtil.computeMergeLines(merged2.getPoints(), merged3.getPoints());
		ConvexObject merged4 = new ConvexObject(ConvexUtil.mergeHulls(merged2.getPoints(), merged3.getPoints(), lines));

		obj1.setID(0);
		obj2.setID(1);
		obj3.setID(2);
		obj4.setID(3);
		obj5.setID(4);
		merged.setID(5);
		merged2.setID(6);
		decomp.addObject(obj1);
		decomp.addObject(obj2);
		decomp.addObject(obj3);
		decomp.addObject(obj4);
		decomp.addObject(obj5);
		decomp.rebuild();
		List<ConvexObject> contained = new ArrayList<ConvexObject>();

		decomp.addObject(merged);
		decomp.merge(null, obj1, obj2, merged, contained);

		decomp.addObject(merged2);
		decomp.merge(null, merged, obj3, merged2, contained);
		
		decomp.addObject(merged3);
		decomp.merge(null, obj4, obj5, merged3, contained);
		
		decomp.addObject(merged4);
		decomp.merge(null, merged2, merged3, merged4, contained);

		for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, obj4, obj5, merged, merged2, merged3, merged4)){
			assertEquals(merged4, decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()), "Object: " + obj.getID());
		}
	}
	
	@Test
	public void edgeCaseSeed() throws InterruptedException{
		testSeed("3Y64YQ01S7B35T82PK9G");
	}
	
	@Test
	public void testRandom() throws InterruptedException{
		GameState game = new GameState(new PlayfieldGenerator(), Arrays.asList(new GreedyPlayer(), new GreedyPlayer()));
		System.out.println("seed: " + game.getSeed());
		
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
		decomp.rebuild();
		
		testPlayfield(objects, decomp);
	}
	
	private void testPlayfield(List<ConvexObject> objects, VerticalDecomposition decomp){
		long seed = ThreadLocalRandom.current().nextLong();
		System.out.println("seed: " + seed);
		Random random = new Random(seed);
		
		for(ConvexObject obj : objects){
			testObject(obj, decomp, random);
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
				assertEquals(obj, decomp.queryObject(x, y));
			}else{
				assertNotEquals(obj, decomp.queryObject(x, y));
			}
		}
	}
}
