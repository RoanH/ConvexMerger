package dev.roanh.convexmerger.game;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
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
	public static final List<Point> computeConvexHull(List<Point> points){
		List<Point> hull = new ArrayList<Point>();
		
		Point hullPoint = points.get(0);
		for(int i = 1; i < points.size(); i++){
			if(points.get(i).x < hullPoint.x){
				hullPoint = points.get(i);
			}
		}
		
		do{
			hull.add(hullPoint);
			
			Point endpoint = points.get(0);
			for(Point point : points){
				if(endpoint.equals(hullPoint) || Line2D.relativeCCW(hullPoint.x, hullPoint.y, endpoint.x, endpoint.y, point.x, point.y) == 1){
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
	 */
	public static final Point[] computeMergeLines(List<Point> first, List<Point> second){
		List<Point> points = new ArrayList<Point>();
		points.addAll(first);
		points.addAll(second);
		return computeMergeLines(first, second, computeConvexHull(points));
	}
	
	/**
	 * Computes the two lines that would be required to
	 * combine the two given convex hulls into a single
	 * convex hull using the convex hull of both objects.
	 * @param first The first convex hull.
	 * @param second The second convex hull.
	 * @param hull The convex hull constructed by merging
	 *        the other two convex hulls.
	 * @return The points for the two line segments that
	 *         would be required to complete the convex
	 *         hull of the two given convex hulls. The
	 *         first two points make up one of the line
	 *         segments and the other two the other.
	 */
	public static final Point[] computeMergeLines(List<Point> first, List<Point> second, List<Point> hull){
		if(!first.contains(hull.get(0))){
			List<Point> tmp = first;
			first = second;
			second = tmp;
		}

		int idx = 0;
		while(!first.get(idx).equals(hull.get(0))){
			idx++;
		}

		//first merge line
		Point a = null;
		Point b = null;

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
		Point c = null;
		Point d = null;

		while(true){
			hullIdx = (hullIdx + 1) % hull.size();
			idx = (idx + 1) % second.size();
			if(!hull.get(hullIdx).equals(second.get(idx))){
				c = hull.get((hullIdx == 0 ? hull.size() : hullIdx) - 1);
				d = hull.get(hullIdx);
				break;
			}
		}

		return new Point[]{a, b, c, d};
	}
}
