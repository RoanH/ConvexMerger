package dev.roanh.convexmerger.game;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of a kd-tree for k = 2.
 * @author Roan
 */
public class KDTree{
	private KDTree low = null;
	private KDTree high = null;
	private Point2D point = null;
	private boolean xAxis;
	
	public KDTree(List<Point2D> points){
		this(points, true);
	}
	
	private KDTree(List<Point2D> points, boolean xAxis){
		if(points.isEmpty()){
			throw new IllegalArgumentException("The kd-tree point set cannot be empty.");
		}
		
		this.xAxis = xAxis;
		this.point = points.get(points.size() / 2);
		
		if(points.size() > 1){
			points.sort(Comparator.comparing(xAxis ? Point2D::getX : Point2D::getY));
			high = new KDTree(new ArrayList<Point2D>(points.subList(points.size() / 2 + 1, points.size())), !xAxis);
		}
		
		if(points.size() > 2){
			low = new KDTree(new ArrayList<Point2D>(points.subList(0, points.size() / 2)), !xAxis);
		}
	}
}
