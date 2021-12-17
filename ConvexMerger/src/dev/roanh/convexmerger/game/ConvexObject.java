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
 * are given in counter clockwise order.
 * @author Roan
 */
public class ConvexObject{
	/**
	 * The points that make up this convex object, starting
	 * with the left most point in counter clockwise order.
	 */
	private List<Point> points;
	/**
	 * The shape of this convex object.
	 */
	private Path2D shape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
	/**
	 * The player that owns this object.
	 */
	private Player owner = null;
	/**
	 * Whether or not this object is selected by the active player.
	 */
	private boolean selected = false;
	
	/**
	 * Constructs a new convex object defined by the given four points.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param x3 The x coordinate of the third point.
	 * @param y3 The y coordinate of the third point.
	 * @param x4 The x coordinate of the fourth point.
	 * @param y4 The y coordinate of the fourth point.
	 */
	public ConvexObject(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
		this(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point(x1, y1),
			new Point(x2, y2),
			new Point(x3, y3),
			new Point(x4, y4)
		)));
	}
	
	/**
	 * Constructs a new convex object defined by the given three points.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param x3 The x coordinate of the third point.
	 * @param y3 The y coordinate of the third point.
	 */
	public ConvexObject(int x1, int y1, int x2, int y2, int x3, int y3){
		this(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point(x1, y1),
			new Point(x2, y2),
			new Point(x3, y3)
		)));
	}
	
	/**
	 * Constructs a new convex object defined by the given list of points.
	 * The points are assumed to define a valid convex polygon in counter
	 * clockwise order with the first point being the left most point.
	 * @param data The point data.
	 */
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
	
	/**
	 * Gets the points that define this convex object. The
	 * points define the convex polygon in counter clockwise
	 * order and the first point is the leftmost point.
	 * @return The points that define this convex object.
	 */
	public List<Point> getPoints(){
		return points;
	}
	
	/**
	 * Gets the player that owns this object.
	 * @return The player that owns this object
	 *         or <code>null</code> if this object
	 *         is currently unowned.
	 * @see #isOwned()
	 */
	public Player getOwner(){
		return owner;
	}
	
	/**
	 * Checks if this object is owned by a player.
	 * @return True if this object is owned by a player.
	 * @see #getOwner()
	 */
	public boolean isOwned(){
		return owner != null;
	}
	
	/**
	 * Sets the player that owns this object.
	 * @param player The player to own this object.
	 */
	public void setOwner(Player player){
		owner = player;
	}
	
	/**
	 * Gets if this object is currently selected
	 * by the active player.
	 * @return True if this object is selected
	 *         by the active player.
	 */
	public boolean isSelected(){
		return selected;
	}
	
	/**
	 * Sets if this object is currently selected by
	 * the active player.
	 * @param selected True if this object is selected.
	 */
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	/**
	 * Checks if the given point is contained in this
	 * convex object.
	 * @param x The x coordinate of the point to test.
	 * @param y The y coordinate of the point to test.
	 * @return True if the given point is contained in this object.
	 */
	public boolean contains(double x, double y){
		return shape.contains(x, y);
	}
	
	/**
	 * Checks if this convex object intersects the line segment
	 * defined by the given end points.
	 * @param a The first endpoint of the line segment.
	 * @param b The second endpoint of the line segment.
	 * @return True if this object intersects the given line segment.
	 */
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
	
	/**
	 * Checks if the given convex object is fully
	 * contained within this convex object.
	 * @param other The object to check.
	 * @return True if this convex object contains
	 *         the other given convex object.
	 */
	public boolean contains(ConvexObject other){
		for(Point p : other.points){
			if(!shape.contains(p)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "ConvexObject[owner=" + owner + ",points=" + points + "]";
	}
}
