package dev.roanh.convexmerger.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import dev.roanh.convexmerger.Main;
import dev.roanh.convexmerger.animation.CalliperAnimation;
import dev.roanh.convexmerger.ui.ConvexMerger;
import dev.roanh.convexmerger.ui.Screen;

/**
 * Class containing various utilities related
 * to convex objects and hulls.
 * @author Roan
 */
public class ConvexUtil{

	/**
	 * Computes the convex hull of the given point set
	 * using the gift wrapping algorithm. The returned
	 * hull has the left most point as the first point.
	 * @param points The points to compute the convex hull of.
	 * @return The convex hull for the given set of points.
	 */
	public static final List<Point2D> computeConvexHull(List<Point2D> points){
		List<Point2D> hull = new ArrayList<Point2D>();
		
		Point2D hullPoint = points.get(0);
		for(int i = 1; i < points.size(); i++){
			int val = Double.compare(points.get(i).getX(), hullPoint.getX());
			if(val < 0){
				hullPoint = points.get(i);
			}else if(val == 0){
				if(Double.compare(points.get(i).getY(), hullPoint.getY()) < 0){
					hullPoint = points.get(i);
				}
			}
		}
		
		do{
			hull.add(hullPoint);
			
			Point2D endpoint = points.get(0);
			for(Point2D point : points){
				if(endpoint.equals(hullPoint) || Line2D.relativeCCW(hullPoint.getX(), hullPoint.getY(), endpoint.getX(), endpoint.getY(), point.getX(), point.getY()) == 1){
					endpoint = point;
				}
			}
			
			hullPoint = endpoint;
		}while(!hull.get(0).equals(hullPoint));
		
		return hull;
	}
	
//	/**
//	 * Computes the two lines that would be required to
//	 * combine the two given convex hulls into a single
//	 * convex hull.
//	 * @param first The first convex hull.
//	 * @param second The second convex hull.
//	 * @return The points for the two line segments that
//	 *         would be required to complete the convex
//	 *         hull of the two given convex hulls. The
//	 *         first two points make up one of the line
//	 *         segments and the other two the other.
//	 * @see #computeMergeLines(List, List, List)
//	 */
//	@Deprecated
//	public static final Point2D[] computeMergeLines(List<Point2D> first, List<Point2D> second){
//		List<Point2D> points = new ArrayList<Point2D>();
//		points.addAll(first);
//		points.addAll(second);
//		return computeMergeLines(first, second, computeConvexHull(points));
//	}
	
	/**
	 * Computes the two lines that would be required to
	 * combine the two given convex hulls into a single
	 * convex hull using the convex hull of both objects.
	 * It is required that the left most point of the
	 * combined hull is the first point in the given hull.
	 * @param first The first convex hull.
	 * @param second The second convex hull.
	 * @param hull The convex hull constructed by merging
	 *        the other two convex hulls.
	 * @return The points for the two line segments that
	 *         would be required to complete the convex
	 *         hull of the two given convex hulls. The
	 *         first two points make up one of the line
	 *         segments and the other two the other.
	 * @see #computeMergeLines(List, List)
	 */
	@Deprecated
	public static final Point2D[] computeMergeLines(List<Point2D> first, List<Point2D> second, List<Point2D> hull){
		if(!first.contains(hull.get(0))){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}

		int idx = 0;
		while(!first.get(idx).equals(hull.get(0))){
			idx++;
		}

		//first merge line
		Point2D a = null;
		Point2D b = null;

		int hullIdx = 0;
		while(true){
			hullIdx++;
			idx = (idx + 1) % first.size();
			if(!first.get(idx).equals(hull.get(hullIdx))){
				a = hull.get((hullIdx == 0 ? hull.size() : hullIdx) - 1);
				b = hull.get(hullIdx);
				break;
			}
		}

		idx = 0;
		for(int i = 0; i < second.size(); i++){
			if(second.get(i).equals(b)){
				idx = i;
				break;
			}
		}

		//second merge line
		Point2D c = null;
		Point2D d = null;

		while(true){
			hullIdx = (hullIdx + 1) % hull.size();
			idx = (idx + 1) % second.size();
			if(!hull.get(hullIdx).equals(second.get(idx))){
				c = hull.get((hullIdx == 0 ? hull.size() : hullIdx) - 1);
				d = hull.get(hullIdx);
				break;
			}
		}

		return new Point2D[]{a, b, c, d};
	}
	
	/**
	 * Splits the given convex objects into segments that are
	 * either on the outside or on the inside of the convex
	 * object that is created when merging them.
	 * @param first The first convex object, should have the
	 *        smallest x-coordinate of the two objects. If not
	 *        the two objects will be swapped automatically.
	 * @param second The second convex object.
	 * @return The specific segments, index 0 has the part of the
	 *         first object that would be contained inside the
	 *         resulting hull, index 1 has the part of the first
	 *         object that would be part of the outside of the
	 *         resulting hull, index 2 has the part of the second
	 *         object that would be contained inside the resulting
	 *         hull, index 3 has the part of the second object that
	 *         would be part of the outside of the resulting hull.
	 * @see #computeMergeLines(List, List)
	 * @see #computeMergeLines(List, List, List)
	 * @see #computeMergeBounds(List, List, Point2D[])
	 */
	public static final List<List<Point2D>> computeMergeBounds(List<Point2D> first, List<Point2D> second){
		return computeMergeBounds(first, second, computeMergeLines(first, second));
	}
	
	/**
	 * Splits the given convex objects into segments that are
	 * either on the outside or on the inside of the convex
	 * object that is created when merging them.
	 * @param first The first convex object, should have the
	 *        smallest x-coordinate of the two objects. If not
	 *        the two objects will be swapped automatically.
	 * @param second The second convex object.
	 * @param mergeLines The points describing the merge lines
	 *        that would be added to merge the two objects as
	 *        computed by {@link #computeMergeLines(List, List)}.
	 * @return The specific segments, index 0 has the part of the
	 *         first object that would be contained inside the
	 *         resulting hull, index 1 has the part of the first
	 *         object that would be part of the outside of the
	 *         resulting hull, index 2 has the part of the second
	 *         object that would be contained inside the resulting
	 *         hull, index 3 has the part of the second object that
	 *         would be part of the outside of the resulting hull.
	 * @see #computeMergeLines(List, List)
	 * @see #computeMergeLines(List, List, List)
	 * @see #computeMergeBounds(List, List)
	 */
	public static final List<List<Point2D>> computeMergeBounds(List<Point2D> first, List<Point2D> second, Point2D[] mergeLines){
		if(!first.contains(mergeLines[0])){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}
		
		List<List<Point2D>> left = computeMergeBounds(first, mergeLines[0], mergeLines[3]);
		List<List<Point2D>> right = computeMergeBounds(second, mergeLines[1], mergeLines[2]);
		
		return Arrays.asList(
			left.get(0),
			left.get(1),
			right.get(1),
			right.get(0)
		);
	}
	
	/**
	 * Splits the given hull into two parts, one part
	 * that is contained between the two points going
	 * from <code>a</code> to <code>b</code> and one
	 * part that is contained between the two points
	 * going from <code>b</code> to <code>a</code>.
	 * @param hull The hull to split.
	 * @param a The first split point.
	 * @param b The second split point.
	 * @return The specific segments with the part from
	 *         <code>a</code> to <code>b</code> at index
	 *         0 and the part from <code>b</code> to <code>
	 *         a</code> at index 1.
	 * @see #computeMergeBounds(List, List, Point2D[])
	 * @see #computeMergeBounds(List, List)
	 */
	public static final List<List<Point2D>> computeMergeBounds(List<Point2D> hull, Point2D a, Point2D b){
		List<Point2D> first = new ArrayList<Point2D>();
		List<Point2D> second = new ArrayList<Point2D>();
		
		//inner segment
		int idx = 0;
		while(true){
			if(hull.get(idx).equals(a)){
				first.add(hull.get(idx));
				break;
			}
			idx++;
		}
		
		//outer segment
		second.add(b);
		
		//extend both
		if(!a.equals(b)){
			do{
				idx = (idx + 1) % hull.size();
				first.add(hull.get(idx));
			}while(!hull.get(idx).equals(b));
			
			do{
				idx = (idx + 1) % hull.size();
				second.add(hull.get(idx));
			}while(!hull.get(idx).equals(a));
		}
		
		return Arrays.asList(first, second);
	}
	
	/**
	 * Function to check if 3 points are collinear.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param x3 The x coordinate of the third point.
	 * @param y3 The y coordinate of the third point.
	 * @return True if the given points are (close to) collinear.
	 */
	public static boolean checkCollinear(int x1, int y1, int x2, int y2, int x3, int y3){
		return Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) == 0;
	}
	
	/**
	 * Function to check if 3 points are collinear.
	 * @param p1 The first point.
	 * @param p2 The second point.
	 * @param p3 The third point.
	 * @return True if the given points are (close to) collinear.
	 */
	public static boolean checkCollinear(Point2D p1, Point2D p2, Point2D p3){
		return checkCollinear(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
	}
	
	/**
	 * Function to check if 3 points are collinear.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param x3 The x coordinate of the third point.
	 * @param y3 The y coordinate of the third point.
	 * @return True if the given points are (close to) collinear.
	 */
	public static boolean checkCollinear(double x1, double y1, double x2, double y2, double x3, double y3){
		return Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) < 0.000006D;//account for FP rounding errors
	}
	
	//first points are left most by game invariant
	//ref: http://cgm.cs.mcgill.ca/~godfried/teaching/cg-projects/97/Plante/CompGeomProject-EPlante/algorithm.html
	public static final Point2D[] computeMergeLines(List<Point2D> first, List<Point2D> second){
		//ensure the first object has the bottom leftmost point
		int cmp = Double.compare(first.get(0).getX(), second.get(0).getX());
		if(cmp > 0 || (cmp == 0 && first.get(0).getY() >= second.get(0).getY())){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}
		
		Point2D[] lines = new Point2D[4];
		int ccw = Line2D.relativeCCW(
			first.get(0).getX(),
			first.get(0).getY(),
			first.get(0).getX(),
			first.get(0).getY() - 1.0D,//initial vertical position
			second.get(0).getX(),
			second.get(0).getY()
		);
		int nccw = ccw;
		int lidx = 0;
		int ridx = 0;

		do{
			Point2D lp1 = first.get(lidx % first.size());
			Point2D lp2 = first.get((lidx + 1) % first.size());
			Point2D rp1 = second.get(ridx % second.size());
			Point2D rp2 = second.get((ridx + 1) % second.size());

			double nla = angleFromVertical(lp1, lp2);
			double nra = angleFromVertical(rp1, rp2);
			
			//our angle needs to increase even if we pass the vertical at the end
			if(lidx >= first.size() && nla <= Math.PI){
				nla += 2.0D * Math.PI;
			}
			if(ridx >= second.size() && nra <= Math.PI){
				nra += 2.0D * Math.PI;
			}
			
			//compute relative calliper positions
			if(nla <= nra){
				lidx++;
				if(nla >= nra){
					ridx++;
				}
				
				//the first object provides the calliper line, the second only provides a point on its calliper
				nccw = Line2D.relativeCCW(
					lp1.getX(),
					lp1.getY(),
					lp2.getX(),
					lp2.getY(),
					rp1.getX(),
					rp1.getY()
				);
			}else{
				ridx++;

				//translate the calliper line to the other object
				nccw = Line2D.relativeCCW(
					lp1.getX(),
					lp1.getY(),
					rp2.getX() + lp1.getX() - rp1.getX(), 
					rp2.getY() + lp1.getY() - rp1.getY(),
					rp1.getX(),
					rp1.getY()
				);
			}
			
			//record merge line if the relative calliper order changed
			if(nccw != ccw){
				ccw = nccw;
				
				//skip over collinear points if they exist
				if(lines[0] == null){
					lines[0] = checkCollinear(lp1, lp2, rp1) ? lp2 : lp1;
					lines[1] = rp1;
				}else{
					assert lines[2] == null : "More than 2 merge lines found";
					lines[2] = checkCollinear(rp1, rp2, lp1) ? rp2 : rp1;
					lines[3] = lp1;
					break;
				}
			}
		}while(lidx <= first.size() && ridx <= second.size());
		
		assert lines[0] != null && lines[1] != null && lines[2] != null && lines[3] != null : "Not enough merge lines found";
		return lines;
	}
	
	//merge line points have to be exact object references to the given hulls
	//the output will not contain any colinear segments
	//the first point in the output will be (bottom) leftmost
	public static List<Point2D> mergeHulls(List<Point2D> first, List<Point2D> second, Point2D[] mergeLines){
		//ensure the first object has the bottom leftmost point
		if(second.contains(mergeLines[0])){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}
		
		List<Point2D> hull = new ArrayList<Point2D>();
		int lidx = 0;
		int ridx = 0;
		Point2D p;
		
		//first object till merge line
		do{
			p = first.get(lidx);
			lidx++;
			
			if(hull.size() >= 2 && checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p)){
				hull.remove(hull.size() - 1);
			}
			
			hull.add(p);
		}while(p != mergeLines[0]);
		
		//end of the first merge line
		if(hull.size() >= 2 && checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), mergeLines[1])){
			hull.remove(hull.size() - 1);
		}
		hull.add(mergeLines[1]);
		
		//skip to the end of first merge line on the second object
		do{
			p = second.get(ridx);
			ridx++;
		}while(p != mergeLines[1]);
		if(ridx == second.size()){
			ridx = 0;
		}
		
		//add second object till second merge line
		while(p != mergeLines[2]){
			p = second.get(ridx);
			ridx = ridx == second.size() - 1 ? 0 : ridx + 1;
			
			if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p)){
				hull.remove(hull.size() - 1);
			}
			
			hull.add(p);
		}
		
		//end of the second merge line
		if(hull.get(0) != mergeLines[3]){
			if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), mergeLines[3])){
				hull.remove(hull.size() - 1);
			}
			hull.add(mergeLines[3]);
		}
		
		//skip to the end of the second merge line on the first object
		lidx--;
		while(lidx < first.size() && first.get(lidx) != mergeLines[3]){
			lidx++;
		}
		
		//add remainder of the first object
		while(lidx < first.size()){
			p = first.get(lidx);
			lidx++;
			
			if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p)){
				hull.remove(hull.size() - 1);
			}
			
			hull.add(p);
		}

		//check for collinearity with respect to the start
		if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), hull.get(0))){
			hull.remove(hull.size() - 1);
		}

		return hull;
	}
	
	public static final class TestScreen extends Screen{
//		private ConvexObject obj1 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(33, 118),
//			new Point2D.Double(57, 178),
//			new Point2D.Double(98, 236),
//			new Point2D.Double(180, 270),
//			new Point2D.Double(204, 171),
//			new Point2D.Double(175, 106),
//			new Point2D.Double(146, 77),
//			new Point2D.Double(116, 65),
//			new Point2D.Double(38, 70)
//		)));
//		private ConvexObject obj2 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(100 + 116, 177),
//			new Point2D.Double(100 + 133, 212),
//			new Point2D.Double(100 + 263, 217),
//			new Point2D.Double(100 + 286, 150),
//			new Point2D.Double(100 + 281, 65),
//			new Point2D.Double(100 + 256, 24),
//			new Point2D.Double(100 + 219, 42),
//			new Point2D.Double(100 + 181, 71),
//			new Point2D.Double(100 + 133, 113)
//		)));
//		private ConvexObject obj1n = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(30, 250),
//			new Point2D.Double(100, 100),
//			new Point2D.Double(100, 200)
//		)));
//		private ConvexObject obj1 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(30, 100),
//			new Point2D.Double(100, 130),
//			new Point2D.Double(100, 200)
//		)));
//		private ConvexObject obj1 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(30, 100),
//			new Point2D.Double(100, 80),
//			new Point2D.Double(100, 200)
//			//,new Point2D.Double(50, 150)
//		)));
//		private ConvexObject obj2n = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(100 + 30, 100),
//			new Point2D.Double(100 + 100, 110),
//			new Point2D.Double(100 + 100, 200)
//			//,new Point2D.Double(100 + 50, 150)
//		)));
//		private ConvexObject obj1 = new ConvexObject(computeConvexHull(Arrays.asList(
//			//new Point2D.Double(10, 150),
//			new Point2D.Double(30, 200),
//			new Point2D.Double(30, 100),
//			new Point2D.Double(100, 100),
//			new Point2D.Double(100, 200)
//		)));
//		private ConvexObject obj2 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(100 + 30, 200),
//			new Point2D.Double(100 + 30, 100),
//			new Point2D.Double(100 + 100, 100),
//			new Point2D.Double(100 + 100, 200)
//		)));
//		private ConvexObject obj2 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(30, 130 + 200),
//			new Point2D.Double(30, 130 + 100),
//			new Point2D.Double(100, 130 + 100),
//			new Point2D.Double(100, 130 + 200)
//		)));
//		private ConvexObject obj1 = new ConvexObject(computeConvexHull(Arrays.asList(
//			//new Point2D.Double(10, 150),
//			new Point2D.Double(50, -200),
//			new Point2D.Double(30, 100),
//			new Point2D.Double(100, 100),
//			new Point2D.Double(100, 300)
//		)));
//		private ConvexObject obj2 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(100 + 50, 140),
//			new Point2D.Double(100 + 30, 100),
//			new Point2D.Double(100 + 300, 150)
//		)));
		private ConvexObject obj1 = new ConvexObject(Arrays.asList(
			new Point2D.Double(68.13547921930717, 823.6810526743893),
			new Point2D.Double(71.27273417269157, 695.053599585629),
			new Point2D.Double(209.31195212160515, 690.3477171555523),
			new Point2D.Double(172.44920641933845, 816.6222290292744)
		));
		private ConvexObject obj2 = new ConvexObject(Arrays.asList(
			new Point2D.Double(48.22851710262876, 607.3220363481171),
			new Point2D.Double(68.62067429962735, 477.12595578266445),
			new Point2D.Double(198.81675486507993, 519.4788976533539),
			new Point2D.Double(148.62067561092954, 612.0279187781937)
		));
				
		public TestScreen(ConvexMerger context){
			super(context);
			obj1.setAnimation(new CalliperAnimation(obj1));
			obj2.setAnimation(new CalliperAnimation(obj2));
		}
		
		@Override
		protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
			super.renderMainInterface(g, width, height, null);

//			g.translate(0.0D, 400.0D);
//			g.scale(1.0D, -1.0D);
			obj1.render(g);
			if(obj1.hasAnimation()){
				obj1.runAnimation(g);
			}
			obj2.render(g);
			if(obj2.hasAnimation()){
				obj2.runAnimation(g);
			}
			
			g.setColor(Color.RED);
			g.fillOval((int)obj1.getPoints().get(0).getX() - 2, (int)obj1.getPoints().get(0).getY() - 2, 4, 4);
			g.fillOval((int)obj2.getPoints().get(0).getX() - 2, (int)obj2.getPoints().get(0).getY() - 2, 4, 4);
			
			g.setColor(Color.BLUE);
			g.draw(new Line2D.Double(obj1.getPoints().get(0), obj1.getPoints().get(1)));
			g.draw(new Line2D.Double(obj2.getPoints().get(0), obj2.getPoints().get(1)));

			
			g.setStroke(new BasicStroke(1.0F));

			try{
				Point2D[] lines = computeMergeLines(obj1.getPoints(), obj2.getPoints());
				g.setColor(Color.MAGENTA);
				g.draw(new Line2D.Double(lines[0], lines[1]));
				if(lines[2] != null){
					g.draw(new Line2D.Double(lines[2], lines[3]));
				}
				
				ConvexObject merged = new ConvexObject(mergeHulls(obj1.getPoints(), obj2.getPoints(), lines));
				g.translate(600, 0);
				merged.render(g);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		protected boolean isLeftButtonEnabled(){
			return true;
		}

		@Override
		protected boolean isRightButtonEnabled(){
			return true;
		}

		@Override
		protected String getLeftButtonText(){
			return null;
		}

		@Override
		protected String getRightButtonText(){
			return null;
		}

		@Override
		protected void handleLeftButtonClick(){
			//CalliperAnimation.elap += 10;
		}

		@Override
		protected void handleRightButtonClick(){
			//CalliperAnimation.elap -= 10;
		}
	}
	
	public static final double angleFromVertical(Line2D b){
		return angleFromVertical(b.getX1(), b.getY1(), b.getX2(), b.getY2());
	}
	
	public static final double angleFromVertical(Point2D p1, Point2D p2){
		return angleFromVertical(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		//return 2.0D * Math.PI - angleBetweenLines(new Line2D.Double(0.0D, 0.0, 0.0D, -1.0D), new Line2D.Double(p1, p2));
	}
	
	public static final double angleFromVertical(double x1, double y1, double x2, double y2){
		double relative = Math.atan2(y2 - y1, x2 - x1) + 0.5D * Math.PI;
		return relative < 0.0D ? (relative + 2.0D * Math.PI) : relative;
	}
	
	//angle from line a to line b in radians in clockwise direction and as a positive number
	public static final double angleBetweenLines(Line2D a, Line2D b){
		//System.out.println("a: " + Math.toDegrees(Math.atan2(b.getY2() - b.getY1(), b.getX2() - b.getX1())) + " / " + b.getP1() + " / " + b.getP2());
		double relative = Math.atan2(b.getY2() - b.getY1(), b.getX2() - b.getX1()) - Math.atan2(a.getY2() - a.getY1(), a.getX2() - a.getX1());
		return relative < 0.0D ? (relative + 2.0D * Math.PI) : relative;
	}
	
	public static void main(String[] args){
//		System.out.println(Math.atan2(1, 0) + " / " + (Math.PI / 2));
//		System.out.println(Math.toDegrees(angleBetweenLines(
//			new Line2D.Double(0, 0, 0, 10),
//			new Line2D.Double(0, 0, 10, 0)
//		)));
//		System.out.println(Math.toDegrees(angleBetweenLines(
//			new Line2D.Double(0, 0, 0, 10),
//			new Line2D.Double(5, 5, 10, 10)
//		)));
//		System.out.println(Math.toDegrees(angleBetweenLines(
//			new Line2D.Double(0, 0, 0, 10),
//			new Line2D.Double(0, 0, -10, 0)
//		)));
//		System.out.println(Math.toDegrees(angleBetweenLines(
//			new Line2D.Double(0, 0, 0, 10),
//			new Line2D.Double(0, 0, -5, 5)
//		)));
//		System.out.println(Math.toDegrees(angleBetweenLines(
//			new Line2D.Double(0, 0, 0, 10),
//			new Line2D.Double(0, 0, 0, -10)
//		)));
//		System.out.println(Math.toDegrees(angleBetweenLines(
//			new Line2D.Double(0, 0, 0, 10),
//			new Line2D.Double(0, 0, -10, -10)
//		)));
//		System.out.println(Math.toDegrees(angleBetweenLines(
//			new Line2D.Double(0, 0, 0, 10),
//			new Line2D.Double(0, 0, 10, -10)
//		)));
//		System.out.println("---");
//		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 0, -10)));
//		System.out.println(Math.toDegrees(angleFromVertical(0, 0, -10, -10)));
//		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 10, -10)));
//		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 10, 0)));
//		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 0, 10)));
//		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 10, 10)));
//		System.out.println(Math.toDegrees(angleFromVertical(0, 0, -10, 0)));
//		System.out.println(Math.toDegrees(angleFromVertical(0, 200, 0, 0)));
		
		Main.main(null);
	}
}
