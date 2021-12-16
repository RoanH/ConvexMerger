package dev.roanh.convexmerger.game;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Defines a convex object shown in the game
 * the points on the boundary of the object
 * are given in clockwise order.
 * @author Roan
 */
public class ConvexObject{
	private List<Point> points;
	private Path2D shape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
	private Player owner = null;
	private boolean selected = false;
	
	public ConvexObject(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
		this(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point(x1, y1),
			new Point(x2, y2),
			new Point(x3, y3),
			new Point(x4, y4)
		)));
	}
	
	public ConvexObject(int x1, int y1, int x2, int y2, int x3, int y3){
		this(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point(x1, y1),
			new Point(x2, y2),
			new Point(x3, y3)
		)));
	}
	
	public ConvexObject(List<Point> data){
		points = data;
		shape.moveTo(data.get(0).x, data.get(0).y);
		for(int i = 1; i < data.size(); i++){
			shape.lineTo(data.get(i).x, data.get(i).y);
		}
		shape.closePath();
	}
	
	/**
	 * Gets a closed path representing the shape of
	 * this convex object. The shape is guaranteed
	 * to be convex.
	 * @return The shape of this convex object.
	 */
	public Path2D getShape(){
		return shape;
	}
	
	public List<Point> getPoints(){
		return points;
	}
	
	public Player getOwner(){
		return owner;
	}
	
	public boolean isOwned(){
		return owner != null;
	}
	
	public void setOwner(Player player){
		owner = player;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public boolean contains(double x, double y){
		return shape.contains(x, y);
	}
	
	public boolean intersects(Point a, Point b){
		Iterator<Point> iter = points.iterator();
		Point last = iter.next();
		
		while(iter.hasNext()){
			Point point = iter.next();
			if(Line2D.linesIntersect(a.x, a.y, b.x, b.y, last.x, last.y, point.x, point.y)){
				return true;
			}
			last = point;
		}
		
		return false;
	}
}
