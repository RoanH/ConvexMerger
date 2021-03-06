package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
