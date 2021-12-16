package dev.roanh.convexmerger.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ConvexUtil{

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
				if(endpoint.equals(hullPoint) || computeOrientation(hullPoint, point, endpoint) < 0){
					endpoint = point;
				}
			}
			
			hullPoint = endpoint;
		}while(!hull.get(0).equals(hullPoint));
		
		return hull;
	}
	
	/**
	 * Finds the orientation of an ordered triplet of points.
	 * The orientation being, collinear, clockwise or counterclockwise.
	 * @param p The first point.
	 * @param q The second point.
	 * @param r The third point.
	 * @return A value equal to 0 if the three points are collinear, a
	 *         value less than 0 if the points are counter clockwise and
	 *         a value greater than 0 if the points are clockwise.
	 */
	private static final int computeOrientation(Point p, Point q, Point r){
		return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
	}
}
