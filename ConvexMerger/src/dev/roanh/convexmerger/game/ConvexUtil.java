package dev.roanh.convexmerger.game;

import java.awt.Point;
import java.awt.geom.Line2D;
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
				if(endpoint.equals(hullPoint) || Line2D.relativeCCW(hullPoint.x, hullPoint.y, endpoint.x, endpoint.y, point.x, point.y) == 1){
					endpoint = point;
				}
			}
			
			hullPoint = endpoint;
		}while(!hull.get(0).equals(hullPoint));
		
		return hull;
	}
}
