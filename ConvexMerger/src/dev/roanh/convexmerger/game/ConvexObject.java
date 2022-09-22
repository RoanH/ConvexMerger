package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dev.roanh.convexmerger.animation.Animation;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme;

/**
 * Defines a convex object shown in the game
 * the points on the boundary of the object
 * are given in counter clockwise order.
 * @author Roan
 */
public class ConvexObject implements Identity, Serializable{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 8124732728885600186L;
	/**
	 * The ID for this object.
	 */
	private int id;
	/**
	 * The points that make up this convex object, starting
	 * with the left most point in counter clockwise order.
	 */
	private List<Point2D> points;
	/**
	 * The shape of this convex object.
	 */
	private Path2D shape;
	/**
	 * The player that owns this object.
	 */
	private transient Player owner = null;
	/**
	 * The active animation for this object.
	 */
	private transient Animation animation = null;
	
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
			new Point2D.Double(x1, y1),
			new Point2D.Double(x2, y2),
			new Point2D.Double(x3, y3),
			new Point2D.Double(x4, y4)
		)));
	}
	
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
	public ConvexObject(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
		this(ConvexUtil.computeConvexHull(Arrays.asList(
			new Point2D.Double(x1, y1),
			new Point2D.Double(x2, y2),
			new Point2D.Double(x3, y3),
			new Point2D.Double(x4, y4)
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
			new Point2D.Double(x1, y1),
			new Point2D.Double(x2, y2),
			new Point2D.Double(x3, y3)
		)));
	}
	
	/**
	 * Constructs a new convex object defined by the given list of points.
	 * The points are assumed to define a valid convex polygon in counter
	 * clockwise order with the first point being the left most point.
	 * @param data The point data.
	 */
	public ConvexObject(List<Point2D> data){
		points = data;
		constructShape(data.size());
	}
	
	/**
	 * Constructs the shape object for the bounds of this object.
	 * @param size The number of points that define the bounds.
	 */
	private void constructShape(int size){
		shape = new Path2D.Double(Path2D.WIND_NON_ZERO, size);
		shape.moveTo(points.get(0).getX(), points.get(0).getY());
		for(int i = 1; i < points.size(); i++){
			shape.lineTo(points.get(i).getX(), points.get(i).getY());
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
	public List<Point2D> getPoints(){
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
	 * Merges this object with the given object.
	 * @param other The object to merge with.
	 * @return The resulting merged convex object.
	 * @see #merge(GameState, ConvexObject)
	 */
	public ConvexObject merge(ConvexObject other){
		return merge(null, other);
	}
	
	/**
	 * Attempts to merge this object with the given
	 * object while checking that the boundary of the
	 * resulting merged object does not intersect any
	 * other objects in the given game state.
	 * @param state The game state to check with if the
	 *        resulting object has any intersections. Can
	 *        be <code>null</code> to skip this check.
	 * @param other The object to merge with.
	 * @return The resulting merged convex object.
	 * @see #merge(ConvexObject)
	 */
	public ConvexObject merge(GameState state, ConvexObject other){
		List<Point2D> combined = new ArrayList<Point2D>();
		combined.addAll(points);
		combined.addAll(other.getPoints());
		
		//TODO remove hull
		//List<Point2D> hull = ConvexUtil.computeConvexHull(combined);
		Point2D[] lines = ConvexUtil.computeMergeLines(points, other.getPoints());
		//Point2D[] lines = ConvexUtil.computeMergeLines(points, other.getPoints(), hull);
		
		if(state != null){
			//check if the new hull is valid
			for(ConvexObject obj : state.getObjects()){
				if(!obj.equals(this) && !obj.equals(other) && (obj.intersects(lines[0], lines[1]) || obj.intersects(lines[2], lines[3]))){
					return null;
				}
			}
		}
		
		return new ConvexObject(ConvexUtil.mergeHulls(points, other.getPoints(), lines));
	}
	
	/**
	 * Checks if this convex object intersects the line segment
	 * defined by the given end points.
	 * @param a The first endpoint of the line segment.
	 * @param b The second endpoint of the line segment.
	 * @return True if this object intersects the given line segment.
	 */
	public boolean intersects(Point2D a, Point2D b){
		for(int i = 0; i < points.size(); i++){
			Point2D p = points.get(i);
			Point2D q = points.get((i + 1) % points.size());
			if(Line2D.linesIntersect(a.getX(), a.getY(), b.getX(), b.getY(), p.getX(), p.getY(), q.getX(), q.getY())){
				return true;
			}
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
		for(Point2D p : other.points){
			if(!shape.contains(p)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the given convex object intersects
	 * this convex object.
	 * @param other The object to check for
	 *        intersection with.
	 * @return True if this convex object intersects
	 *         the other given convex object.
	 */
	public boolean intersects(ConvexObject other){
		if(contains(other) || other.contains(this)){
			return true;
		}else{
			for(int i = 0; i < points.size(); i++){
				if(other.intersects(points.get(i), points.get((i + 1) % points.size()))){
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Computes the area of this convex object.
	 * @return The area for this convex object.
	 * @see <a href="https://en.wikipedia.org/wiki/Shoelace_formula">Shoelace formula</a>
	 */
	public double getArea(){
		double area = 0.0D;
		for(int i = 0; i < points.size(); i++){
			int j = (i + 1) % points.size();
			area += points.get(i).getX() * points.get(j).getY();
			area -= points.get(i).getY() * points.get(j).getX();
		}
		return area / 2.0D;
	}
	
	/**
	 * Tests if this convex object is owned by the given player.
	 * @param player The player to check.
	 * @return True if the given player owns this object.
	 */
	public boolean isOwnedBy(Player player){
		return player.equals(owner);
	}
	
	/**
	 * Computes the centroid of this convex object.
	 * @return The centroid of this convex object.
	 */
	public Point2D getCentroid(){
		double cx = 0.0D;
		double cy = 0.0D;
		for(int i = 0; i < points.size(); i++){
			Point2D p1 = points.get(i);
			Point2D p2 = points.get((i + 1) % points.size());
			double factor = (p1.getX() * p2.getY() - p2.getX() * p1.getY());
			cx += (p1.getX() + p2.getX()) * factor;
			cy += (p1.getY() + p2.getY()) * factor;
		}

		double area = 6.0D * getArea();
		return new Point2D.Double(cx / area, cy / area);
	}
	
	/**
	 * Checks if this convex object has an active animation.
	 * @return True if this convex object has an active animation.
	 */
	public boolean hasAnimation(){
		return animation != null;
	}
	
	/**
	 * Sets the active animation for this convex object.
	 * @param animation The new active animation.
	 */
	public void setAnimation(Animation animation){
		this.animation = animation;
	}
	
	/**
	 * Renders the animation for this convex object
	 * using the given graphics instance.
	 * @param g The graphics instance to use.
	 * @return True if the animation still has frames
	 *         remaining, false otherwise.
	 */
	public boolean runAnimation(Graphics2D g){
		if(animation.run(g)){
			return true;
		}else{
			animation = null;
			return false;
		}
	}
	
	/**
	 * Renders this convex object using the given
	 * graphics instance.
	 * @param g The graphics instance to use.
	 */
	public void render(Graphics2D g){
		g.setColor(Theme.getPlayerBody(this));
		g.fill(shape);
		g.setStroke(Theme.POLY_STROKE);
		g.setColor(Theme.getPlayerOutline(this));
		g.draw(shape);
	}
	
	/**
	 * Checks if this objects is unowned and
	 * thus claimable by any player.
	 * @return True if this object can be claimed.
	 */
	public boolean canClaim(){
		return owner == null;
	}
	
	/**
	 * Scales this object by the given scaling factor.
	 * @param factor The scaling factor.
	 */
	public void scale(double factor){
		Point2D centroid = getCentroid();
		Point2D origin = new Point2D.Double(centroid.getX() * factor, centroid.getY() * factor);
		
		for(Point2D p : points){
			p.setLocation(p.getX() * factor - origin.getX() + centroid.getX(), p.getY() * factor - origin.getY() + centroid.getY());
		}
		
		constructShape(points.size());
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object other){
		return other instanceof ConvexObject ? ((ConvexObject)other).id == id : false;
	}
	
	@Override
	public String toString(){
		return "ConvexObject[owner=" + owner + ",points={" + points.stream().map(p->("(" + p.getX() + "," + p.getY() + ")")).reduce((p, q)->(p + "," + q)).get() + "}]";
	}

	@Override
	public int getID(){
		return id;
	}

	@Override
	public void setID(int id){
		this.id = id;
	}
}
