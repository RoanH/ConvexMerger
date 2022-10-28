package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.roanh.convexmerger.Constants;

public class ConjugationTree{
	private ConjugationTree parent;
	private Line2D bisector;
	private List<Point2D> on = new ArrayList<Point2D>(2);
	private ConjugationTree left;
	private ConjugationTree right;
	
	public ConjugationTree(List<Point2D> points){
		//only root bisector finding requires O(n log n) time
		points.sort(Comparator.comparingDouble(Point2D::getX));
		int idx = points.size() / 2;
		
		double mid = points.get(idx).getX();
		bisector = new Line2D.Double(mid, 0.0D, mid, Constants.PLAYFIELD_HEIGHT);
		
		//by assumption we have few points on the bisector, but for other applications this could be false
		List<Point2D> leftPoints = new ArrayList<Point2D>(idx);
		List<Point2D> rightPoints = new ArrayList<Point2D>(idx);
		on.add(points.get(idx));
		for(int i = idx - 1; i >= 0; i--){
			Point2D p = points.get(i);
			if(p.getX() >= mid){
				on.add(p);
			}else{
				leftPoints.add(p);
			}
		}
		for(int i = idx + 1; i < points.size(); i++){
			Point2D p = points.get(i);
			if(p.getX() <= mid){
				on.add(p);
			}else{
				rightPoints.add(p);
			}
		}
		
		//construct children
		Line2D conjugate = null;//TODO
		left = new ConjugationTree(this, leftPoints, rightPoints, conjugate);
		right = new ConjugationTree(this, rightPoints, leftPoints, conjugate);
	}
	
	private ConjugationTree(ConjugationTree parent, List<Point2D> self, List<Point2D> other, Line2D bisector){
		
	}
	
	
	
	
	
	
}
