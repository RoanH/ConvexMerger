package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.ui.Theme;

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
	
	public void render(Graphics2D g){
		g.setColor(Color.WHITE);
		g.setStroke(Theme.BORDER_STROKE);
		
		if(xAxis){
			g.draw(new Line2D.Double(point.getX(), 0.0D, point.getX(), Constants.PLAYFIELD_HEIGHT));
		}else{
			g.draw(new Line2D.Double(0.0D, point.getY(), Constants.PLAYFIELD_WIDTH, point.getY()));
		}
		
		g.setColor(Color.RED);
		g.fill(new Ellipse2D.Double(point.getX() - 5.0D, point.getY() - 5.0D, 10.0D, 10.0D));	
	}
}
