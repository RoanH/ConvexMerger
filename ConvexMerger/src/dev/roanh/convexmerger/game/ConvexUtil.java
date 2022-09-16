package dev.roanh.convexmerger.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	/**
	 * Computes the two lines that would be required to
	 * combine the two given convex hulls into a single
	 * convex hull.
	 * @param first The first convex hull.
	 * @param second The second convex hull.
	 * @return The points for the two line segments that
	 *         would be required to complete the convex
	 *         hull of the two given convex hulls. The
	 *         first two points make up one of the line
	 *         segments and the other two the other.
	 * @see #computeMergeLines(List, List, List)
	 */
	@Deprecated
	public static final Point2D[] computeMergeLines(List<Point2D> first, List<Point2D> second){
		List<Point2D> points = new ArrayList<Point2D>();
		points.addAll(first);
		points.addAll(second);
		return computeMergeLines(first, second, computeConvexHull(points));
	}
	
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
	public static final List<List<Point2D>> computePocketLids(List<Point2D> first, List<Point2D> second){
		//TODO for certain cases of co linearity the two calipers can overlap, this needs to be handled
		
		int lidx = 0;
		int ridx = 0;
		boolean isLeft = true;
		
		if(angleFromVertical(first.get(lidx), first.get(lidx + 1)) < angleFromVertical(second.get(ridx), second.get(ridx + 1))){
			
		}
		
		
		
		return null;//TODO
	}
	
	public static final class TestScreen extends Screen{
		private ConvexObject obj1 = new ConvexObject(computeConvexHull(Arrays.asList(
			new Point2D.Double(33, 118),
			new Point2D.Double(57, 178),
			new Point2D.Double(98, 236),
			new Point2D.Double(180, 270),
			new Point2D.Double(204, 171),
			new Point2D.Double(175, 106),
			new Point2D.Double(146, 77),
			new Point2D.Double(116, 65),
			new Point2D.Double(38, 70)
		)));
		private ConvexObject obj2 = new ConvexObject(computeConvexHull(Arrays.asList(
			new Point2D.Double(100 + 116, 177),
			new Point2D.Double(100 + 133, 212),
			new Point2D.Double(100 + 263, 217),
			new Point2D.Double(100 + 286, 150),
			new Point2D.Double(100 + 281, 65),
			new Point2D.Double(100 + 256, 24),
			new Point2D.Double(100 + 219, 42),
			new Point2D.Double(100 + 181, 71),
			new Point2D.Double(100 + 133, 113)
		)));
//		private ConvexObject obj1 = new ConvexObject(computeConvexHull(Arrays.asList(
//			new Point2D.Double(30, 100),
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
//		private ConvexObject obj2 = new ConvexObject(computeConvexHull(Arrays.asList(
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

		public TestScreen(ConvexMerger context){
			super(context);
			obj1.setAnimation(new CalliperAnimation(obj1));
		}
		
		private int max = 0;

		@Override
		protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
			super.renderMainInterface(g, width, height, null);

			g.translate(0.0D, 800.0D);
			g.scale(2.0D, -2.0D);
			obj1.render(g);
			obj1.runAnimation(g);
			obj2.render(g);
			
			List<Point2D> first = obj1.getPoints();
			List<Point2D> second = obj2.getPoints();

			g.setStroke(new BasicStroke(1.0F));
			
			int lidx = 0;
			int ridx = 0;
			int ccw = Line2D.relativeCCW(
				first.get(0).getX(), first.get(0).getY(),
				first.get(0).getX(), first.get(0).getY() - 1.0D,
				second.get(0).getX(), second.get(0).getY()
			);
			int nccw = ccw;
			
			Point2D[] lines = new Point2D[4];
			int found = 0;

			int i = 0;//TODO remove
			while(i < max){
				double nla = angleFromVertical(first.get(lidx), first.get((lidx + 1) % first.size()));
				double nra = angleFromVertical(second.get(ridx), second.get((ridx + 1) % second.size()));
				
				//our angle needs to increase even if we arrive back at vertical at the end, so wrap 0 degrees to 360 degrees
				if(lidx + ridx > 0){
					if(nla == 0.0D){
						nla = 2 * Math.PI;
					}
					if(nla == 0.0D){
						nra = 2 * Math.PI;
					}
				}
				
				if(nla < nra){
					//the first object provides the calliper line, the second only provides a point on its calliper
					nccw = Line2D.relativeCCW(
						first.get(lidx).getX(), first.get(lidx).getY(),
						first.get((lidx + 1) % first.size()).getX(), first.get((lidx + 1) % first.size()).getY(),
						second.get(ridx).getX(), second.get(ridx).getY()
					);
				}else{
					//translate the calliper line to the other object
					Point2D a = second.get(ridx);
					Point2D b = second.get((ridx + 1) % second.size());
					Point2D c = first.get(lidx);
					double dx = c.getX() - a.getX();
					double dy = c.getY() - a.getY();
					
					nccw = Line2D.relativeCCW(
						a.getX() + dx, a.getY() + dy,
						b.getX() + dx, b.getY() + dy,
						second.get(ridx).getX(), second.get(ridx).getY()
					);
				}
				
				if(nccw != 0 && nccw != ccw){
					ccw = nccw;
					g.setColor(Color.MAGENTA);
					
					//skip over collinear points if they exist
					if(found == 0){
						lines[0] = checkCollinear(first.get(lidx), first.get((lidx + 1) % first.size()), second.get(ridx)) ? first.get((lidx + 1) % first.size()) : first.get(lidx);
						lines[1] = second.get(ridx);
						found++;
						drawLineClosed(g, lines[0], lines[1]);
					}else if(found == 1){
						lines[2] = checkCollinear(second.get(ridx), second.get((ridx + 1) % second.size()), first.get(lidx)) ? second.get((ridx + 1) % second.size()) : second.get(ridx);
						lines[3] = first.get(lidx);
						found++;
						drawLineClosed(g, lines[2], lines[3]);
					}else{
						throw new IllegalStateException("More than 2 merge lines found.");
					}
				}
				
				if(lidx == first.size() - 1 && ridx == second.size() - 1){
					break;
				}
				
				if(nla <= nra){
					lidx++;
				}
				if(nla >= nra){
					ridx++;
				}
				
				i++;
			}
			
			g.setColor(Color.BLUE);
			drawLine(g, first.get(lidx), first.get((lidx + 1) % first.size()));
			drawLine(g, second.get(ridx), second.get((ridx + 1) % second.size()));
			
			g.setColor(Color.ORANGE);
			Point2D a = second.get(ridx);
			Point2D b = second.get((ridx + 1) % second.size());
			Point2D c = first.get(lidx);
			double dx = c.getX() - a.getX();
			double dy = c.getY() - a.getY();
			
			drawLine(
				g,
				a.getX() + dx, a.getY() + dy,
				b.getX() + dx, b.getY() + dy
			);
		}
		
		private void drawLineClosed(Graphics2D g, double x1, double y1, double x2, double y2){
			g.draw(new Line2D.Double(x1, y1, x2, y2));
		}
		
		private void drawLineClosed(Graphics2D g, Point2D a, Point2D b){
			g.draw(new Line2D.Double(a, b));
		}
		
		private void drawLine(Graphics2D g, Point2D a, Point2D b){
			drawLine(g, a.getX(), a.getY(), b.getX(), b.getY());
		}
		
		private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2){
			double coef = (y2 - y1) / (x2 - x1);
			double base = y1 - x1 * coef;
			g.draw(new Line2D.Double(0.0D, base, 1600.0D, base + coef * 1600.0D));
		}

		@Override
		protected boolean isLeftButtonEnabled(){
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		protected boolean isRightButtonEnabled(){
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		protected String getLeftButtonText(){
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String getRightButtonText(){
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void handleLeftButtonClick(){
			// TODO Auto-generated method stub
			max++;
		}

		@Override
		protected void handleRightButtonClick(){
			// TODO Auto-generated method stub
			max--;

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
		System.out.println(Math.atan2(1, 0) + " / " + (Math.PI / 2));
		System.out.println(Math.toDegrees(angleBetweenLines(
			new Line2D.Double(0, 0, 0, 10),
			new Line2D.Double(0, 0, 10, 0)
		)));
		System.out.println(Math.toDegrees(angleBetweenLines(
			new Line2D.Double(0, 0, 0, 10),
			new Line2D.Double(5, 5, 10, 10)
		)));
		System.out.println(Math.toDegrees(angleBetweenLines(
			new Line2D.Double(0, 0, 0, 10),
			new Line2D.Double(0, 0, -10, 0)
		)));
		System.out.println(Math.toDegrees(angleBetweenLines(
			new Line2D.Double(0, 0, 0, 10),
			new Line2D.Double(0, 0, -5, 5)
		)));
		System.out.println(Math.toDegrees(angleBetweenLines(
			new Line2D.Double(0, 0, 0, 10),
			new Line2D.Double(0, 0, 0, -10)
		)));
		System.out.println(Math.toDegrees(angleBetweenLines(
			new Line2D.Double(0, 0, 0, 10),
			new Line2D.Double(0, 0, -10, -10)
		)));
		System.out.println(Math.toDegrees(angleBetweenLines(
			new Line2D.Double(0, 0, 0, 10),
			new Line2D.Double(0, 0, 10, -10)
		)));
		System.out.println("---");
		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 0, -10)));
		System.out.println(Math.toDegrees(angleFromVertical(0, 0, -10, -10)));
		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 10, -10)));
		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 10, 0)));
		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 0, 10)));
		System.out.println(Math.toDegrees(angleFromVertical(0, 0, 10, 10)));
		System.out.println(Math.toDegrees(angleFromVertical(0, 0, -10, 0)));
		System.out.println(Math.toDegrees(angleFromVertical(0, 200, 0, 0)));
		
		Main.main(null);
	}
}
