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
		ConjugateData data = computeConjugate(leftPoints, rightPoints, bisector);
		left = new ConjugationTree(this, leftPoints, data.leftOn, data.conjugate);
		right = new ConjugationTree(this, rightPoints, data.rightOn, data.conjugate);
	}
	
	private ConjugationTree(ConjugationTree parent, List<Point2D> points, List<Point2D> on, Line2D bisector){
		this.parent = parent;
		this.bisector = bisector;
		this.on = on;
		
		List<Point2D> leftPoints = new ArrayList<Point2D>(points.size() / 2);
		List<Point2D> rightPoints = new ArrayList<Point2D>(points.size() / 2);
		for(Point2D p : points){
			if(!on.contains(p)){
				int rel = bisector.relativeCCW(p);
				switch(rel){
				case -1:
					leftPoints.add(p);
					break;
				case 1:
					rightPoints.add(p);
					break;
				case 0:
				default:
					assert false : "Impossible CCW";
				}
			}
		}
		
		//construct children
		ConjugateData data = computeConjugate(leftPoints, rightPoints, bisector);
		left = new ConjugationTree(this, leftPoints, data.leftOn, data.conjugate);
		right = new ConjugationTree(this, rightPoints, data.rightOn, data.conjugate);
	}
	
	private static final ConjugateData computeConjugate(List<Point2D> left, List<Point2D> right, Line2D bisector){
		//TODO this is a naive temporary solution
		
		ConjugateData data = new ConjugateData();
		for(Point2D p1 : left){
			for(Point2D p2 : right){
				int val = 0;
				for(Point2D lp : left){
					if(lp != p1){
						int rel = bisector.relativeCCW(lp);
						val += rel;
						data.leftOn.add(lp);
					}
				}
				
				if(Math.abs(val) > 1){
					data.leftOn.clear();
					continue;
				}
				
				val = 0;
				for(Point2D rp : right){
					if(p2 != rp){
						int rel = bisector.relativeCCW(rp);
						val += rel;
						data.rightOn.add(rp);
					}
				}
				
				if(Math.abs(val) <= 1){
					data.conjugate = new Line2D.Double(p1, p2);
					data.leftOn.add(p1);
					data.rightOn.add(p2);
					return data;
				}else{
					data.leftOn.clear();
					data.rightOn.clear();
				}
			}
		}
		
		//TODO handle empty left/right cases
		assert false : "Not implemented";
		return null;
	}
	
	private static class ConjugateData{
		private Line2D conjugate;
		private List<Point2D> leftOn = new ArrayList<Point2D>(2);
		private List<Point2D> rightOn = new ArrayList<Point2D>(2);
	}
}
