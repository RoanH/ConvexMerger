package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
	private KDTree parent = null;
	private KDTree low = null;
	private KDTree high = null;
	private Point2D point = null;
	private boolean xAxis;
	private Rectangle2D bounds = null;
	
	public KDTree(List<Point2D> points){
		this(null, points, false);
	}
	
	private KDTree(KDTree parent, List<Point2D> points, boolean xAxis){
		if(points.isEmpty()){
			throw new IllegalArgumentException("The kd-tree point set cannot be empty.");
		}
		
		this.parent = parent;
		this.xAxis = xAxis;
		this.point = points.get(points.size() / 2);
		
		if(points.size() > 1){
			points.sort(Comparator.comparing(xAxis ? Point2D::getX : Point2D::getY));
			low = new KDTree(this, new ArrayList<Point2D>(points.subList(0, points.size() / 2)), !xAxis);
		}
		
		if(points.size() > 2){
			high = new KDTree(this, new ArrayList<Point2D>(points.subList(points.size() / 2 + 1, points.size())), !xAxis);
		}
	}
	
	public Rectangle2D getBounds(){
		if(bounds == null){
			if(parent == null){
				bounds = new Rectangle2D.Double(0.0D, 0.0D, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			}else if(this == parent.low){
				Rectangle2D parentBounds = parent.getBounds();
				if(xAxis){
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parentBounds.getMinY(),
						parentBounds.getWidth(),
						parent.point.getY() - parentBounds.getMinY()
					);
				}else{
					//validated
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parentBounds.getMinY(),
						parent.point.getX() - parentBounds.getMinX(),
						parentBounds.getHeight()
					);
				}
			}else if(this == parent.high){
				Rectangle2D parentBounds = parent.getBounds();
				if(xAxis){
					//validated
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parent.point.getY(),
						parentBounds.getWidth(),
						parentBounds.getMaxY() - parent.point.getY()
					);
				}else{
					//validated
					bounds = new Rectangle2D.Double(
						parent.point.getX(),
						parentBounds.getMinY(),
						parentBounds.getMaxX() - parent.point.getX(),
						parentBounds.getHeight()
					);
				}
			}
		}
		
		return bounds;
	}
	
	public void render(Graphics2D g){
		g.setColor(Color.WHITE);
		g.setStroke(Theme.BORDER_STROKE);
		
		Rectangle2D bounds = getBounds();
		if(xAxis){
			g.draw(new Line2D.Double(point.getX(), bounds.getMinY(), point.getX(), bounds.getMaxY()));
		}else{
			g.draw(new Line2D.Double(bounds.getMinX(), point.getY(), bounds.getMaxX(), point.getY()));
		}
		
		g.setColor(new Color(0, 255, 255, 50));
		g.fill(bounds);
		
		if(low != null){
			low.render(g);
		}
		
		if(high != null){
			high.render(g);
		}
		
		g.setColor(parent == null ? Color.RED : Color.GREEN);
		g.fill(new Ellipse2D.Double(point.getX() - 5.0D, point.getY() - 5.0D, 10.0D, 10.0D));	
	}
}
