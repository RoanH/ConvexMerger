package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.roanh.convexmerger.Constants;

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
			
			obj1.setID(0); obj2.setID(1); obj3.setID(2); merged.setID(3); merged2.setID(4);
			decomp.addObject(obj1); decomp.addObject(obj2); decomp.addObject(obj3); 
			decomp.rebuild();
			List<ConvexObject> contained = new ArrayList<ConvexObject>();
			
			decomp.addObject(merged);
			decomp.merge(null, obj1, obj2, merged, contained);
			
			decomp.addObject(merged2);
			decomp.merge(null, merged, obj3, merged2, contained);
			
			for(ConvexObject obj : Arrays.asList(obj1, obj2, obj3, merged, merged2)){
				assert decomp.queryObject(obj.getCentroid().getX(), obj.getCentroid().getY()) == merged2;
			}
	}
}
