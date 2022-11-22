/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dev.roanh.convexmerger.game.SegmentPartitionTree.LineSegment;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme;

/**
 * Defines a convex object shown in the game
 * the points on the boundary of the object
 * are given in counter clockwise order.
 * @author Roan
 */
public class ConvexObject extends RenderableObject implements Identity, Serializable{
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
		assert ConvexUtil.checkInvariants(data) : "Game invariants violated for convex objects";
		constructShape();
	}
	
	/**
	 * Constructs the shape object for the bounds of this object.
	 */
	private void constructShape(){
		shape = new Path2D.Double(Path2D.WIND_NON_ZERO, points.size());
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
		try{
			return merge(state, other, false);
		}catch(InterruptedException e){
			//only saving merges can be interrupted
			throw new RuntimeException(e);
		}
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
	 * @param saveSegments True if the merge lines for the
	 *        merge should be added to the game state.
	 * @return The resulting merged convex object.
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 * @see #merge(ConvexObject)
	 */
	public ConvexObject merge(GameState state, ConvexObject other, boolean saveSegments) throws InterruptedException{
		Point2D[] lines = ConvexUtil.computeMergeLines(points, other.getPoints());
		
		//check if the new hull is valid
		if(state != null){
			SegmentPartitionTree<ConjugationTree<LineSegment>> treeC = state.getSegmentTreeConj();
			SegmentPartitionTree<KDTree<LineSegment>> treeK = state.getSegmentTreeKD();
			
			if(treeC.intersects(lines[0], lines[1]) || treeC.intersects(lines[2], lines[3]) || treeK.intersects(lines[0], lines[1]) || treeK.intersects(lines[2], lines[3])){
				return null;
			}else if(saveSegments){
				if(treeC.isAnimated()){
					treeC.showAnimation(lines[0], lines[1]).waitFor();
					treeC.showAnimation(lines[2], lines[3]).waitFor();
				}
				
				if(treeK.isAnimated()){
					treeK.showAnimation(lines[0], lines[1]).waitFor();
					treeK.showAnimation(lines[2], lines[3]).waitFor();
				}
				
				treeC.addSegment(lines[0], lines[1]);
				treeC.addSegment(lines[2], lines[3]);
				treeK.addSegment(lines[0], lines[1]);
				treeK.addSegment(lines[2], lines[3]);
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
	 */
	public double getArea(){
		return ConvexUtil.computeArea(points);
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
		return ConvexUtil.computeCentroid(points);
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
		
		constructShape();
	}
	
	@Override
	public void render(Graphics2D g){
		g.setColor(Theme.getPlayerBody(this));
		g.fill(shape);
		g.setStroke(Theme.POLY_STROKE);
		g.setColor(Theme.getPlayerOutline(this));
		g.draw(shape);
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
