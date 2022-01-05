package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Vertical decomposition of a plane containing non-overlapping
 * convex objects. The decomposition supports efficient modifications
 * and querying of the convex objects.
 * @author Roan
 * @author Emu
 */
public class VerticalDecomposition{
	
	private List<Trapezoid> trapezoids;
	
	private Rectangle2D bounds;
	/**
	 * Constructs a new vertical decomposition with
	 * the given bounding box.
	 * @param bounds A bounding box that all objects that will
	 *        ever be added will be contained in (strictly inside,
	 *        there will be no overlap with the edges).
	 */
	public VerticalDecomposition(Rectangle2D bounds){
		trapezoids = new ArrayList<Trapezoid>();
		Point2D topLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
		Point2D topRight = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
		Point2D botLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
		Point2D botRight = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
		
		trapezoids.add(new Trapezoid(topLeft, botRight, botLeft, botRight, topLeft, topRight, new ArrayList<Trapezoid>()));
		this.bounds = bounds;
	}
	
	/**
	 * Adds a new convex object to this vertical decomposition.
	 * @param obj The convex object to add.
	 */
	public void addObject(ConvexObject obj){
		//TODO
	}
	
	/**
	 * Removes a convex object from this vertical decomposition.
	 * @param obj The convex object to remove.
	 */
	public void removeObject(ConvexObject obj){
		//TODO
	}
	
	/**
	 * Rebuilds the vertical composition to match the
	 * current set of stored convex objects.
	 */
	public void rebuild(){
		//TODO optional, you can assume that multiple object additions/removals happen at the same time,
		//this method will be called when there will be no more changes meaning you
		//could rebuild once instead of on each object change. You can delete this method if you decide
		//its more efficient to just do everything in add/remove
	}
	
	/**
	 * Queries the convex object that is at the given position.
	 * If no such object exists <code>null</code> is returned.
	 * @param x The x-coordinate of the query point.
	 * @param y The y-coordinate of the query point.
	 * @return The convex object at the given position or
	 *         <code>null</code> is there is no point at
	 *         the given position.
	 */
	public ConvexObject queryObject(double x, double y){
		return null;//TODO
	}
	
	/**
	 * Gets a list of lines that represent the vertical
	 * decomposition lines. Lines to represent the bounding
	 * box will be included as well.
	 * @return The vertical decomposition lines.
	 */
	public List<Line2D> getDecompLines(){
		List<Line2D> lines = new ArrayList<Line2D>();
		
		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY()));
		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY()));
		lines.add(new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()));
		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY()));
		
		for(Trapezoid trap : trapezoids){
			lines.addAll(trap.getDecompLines());
		}
		
		return lines;
	}
	
	/**
	 * Defines the trapezoid structure that is used in the Vertical decomposition.
	 * @author Emu
	 */
	public class Trapezoid{
		/**
		 * The four points representing the lines that
		 * bound the trapezoid from the top and the bottom.
		 */
		private Point2D topLeft, topRight, botLeft, botRight;
		/**
		 * The points that bound the trapezoid from the left and right.
		 */
		private Point2D leftPoint, rightPoint;
		/**
		 * The neighbouring trapezoids of the trapezoid.
		 */
		private List<Trapezoid> neighbours;
		/**
		 * Constructs a trapezoid.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param bot1 One point of the bottom bounding line of the trapezoid.
		 * @param bot2 The other point of the bottom bounding line of the trapezoid.
		 * @param top1 One point of the top bounding line of the trapezoid.
		 * @param top2 The other point of the bottom bounding line of the trapezoid.
		 * @param neighbours The neighbouring trapezoids of the constructed trapezoid. 
		 */
		public Trapezoid (Point2D left, Point2D right, Point2D bot1, Point2D bot2, Point2D top1, Point2D top2, List<Trapezoid> neighbours){
			topLeft = top1.getX() == top2.getX() ? (top1.getY() < top2.getY() ? top1 : top2) : (top1.getX() < top2.getX() ? top1 : top2);  
			topRight = topLeft.equals(top1) ? top2 : top1;
			botLeft = bot1.getX() == bot2.getX() ? (bot1.getY() < bot2.getY() ? bot1 : bot2) : (bot1.getX() < bot2.getY() ? bot1 : bot2);
			botRight = botLeft.equals(bot1) ? bot2 : bot1;
			leftPoint = left;
			rightPoint = right;
			this.neighbours = neighbours;
		}
		
		/**
		 * Getter for the neighbours of the trapezoid
		 * @return the neighbours of the trapezoid
		 */
		public List<Trapezoid> getNeighbours(){
			return neighbours;
		}
		
		/**
		 * Adds a neighbour to the list of neighbours.
		 * @param neighbour the neighbour to be added to the list.
		 */
		public void addNeighbour(Trapezoid neighbour){
			neighbours.add(neighbour);
		}
		
		/**
		 * Removes a neighbour from the list of neighbours.
		 * @param neighbour the neighbour to be removed
		 */
		public void removeNeighbour (Trapezoid neighbour){
			neighbours.remove(neighbours.indexOf(neighbour));
		}
		
		/**
		 * Removes this trapezoid from the neighbour lists of all neighbours.
		 * Useful for when we want to delete a 
		 */
		public void freeNeighbours (){
			for(Trapezoid t : neighbours){
				t.removeNeighbour(this);
			}
		}
		
		/**
		 * Computes and outputs the decomposition lines related to this trapezoid
		 * @return The vertical decomposition lines, unless one or more of the bounding lines is vertical.
		 */
		public List<Line2D> getDecompLines(){
			List<Line2D> verticalLines = new ArrayList<Line2D>(); 
		
			//TODO: figure out a better way to handle vertical top or bottom lines.
			if(topLeft.getX() == topRight.getX() || botLeft.getX() == botRight.getX()){
				return verticalLines;
			}
			
			if(!(topLeft.equals(botLeft) && topLeft.equals(leftPoint))){
				double xRatioTop = (leftPoint.getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
				double xRatioBot = (leftPoint.getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
				verticalLines.add(new Line2D.Double(new Point2D.Double(leftPoint.getX(), xRatioTop * topLeft.getY() + (1 - xRatioTop) * topRight.getY()),
													new Point2D.Double(leftPoint.getX(), xRatioBot * botLeft.getY() + (1 - xRatioBot) * botRight.getY())));
			}
			
			if(!(topRight.equals(botRight) && topRight.equals(rightPoint))){
				double xRatioTop = (rightPoint.getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
				double xRatioBot = (rightPoint.getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
				verticalLines.add(new Line2D.Double(new Point2D.Double(rightPoint.getX(), xRatioTop * topLeft.getY() + (1 - xRatioTop) * topRight.getY()),
													new Point2D.Double(rightPoint.getX(), xRatioBot * botLeft.getY() + (1 - xRatioBot) * botRight.getY())));
			}
			
			return verticalLines;
		}
	}
}
