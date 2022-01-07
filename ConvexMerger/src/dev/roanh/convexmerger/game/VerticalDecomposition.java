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
	 *         <code>null</code> is there is no convex object at
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
	 * Represents a vertex in the search structure of the decomposition.
	 * @author Emu
	 */
	public class DecompVertex{
		/**
		 * The type of the vertex. 0 denotes leaf, 1 denotes point, 2 denotes line segment.
		 */
		private int type;
		
		/**
		 * The children of the vertex in the search structure.
		 */
		private DecompVertex left, right;
		
		/**
		 * If the vertex is a leaf, it points to a trapezoid.
		 */
		private Trapezoid trapezoid;
		
		/**
		 * If the vertex represents a point, it points to it.
		 */
		private Point2D point;
		
		/**
		 * If the vertex represents a segment, it points to it.
		 */
		private Line2D segment;
		
		/**
		 * Constructs a vertex with no children and only a type.
		 * @param type The type of the vertex. 0 denotes leaf, 1 denotes point, 2 denotes line segment.
		 */
		public DecompVertex(int type){
			this.type  = type;
			this.left  = null;
			this.right = null;
		}
		
		/**
		 * 
		 * @param type The type of the vertex. 0 denotes leaf, 1 denotes point, 2 denotes line segment.
		 * @param left The left child of the vertex.
		 * @param right The right child of the vertex.
		 */
		
		/**
		 * Constructs a Decomposition Vertex of expected type 0 (leaf) with a linked trapezoid.
		 * @param type The type of the vertex. 0 denotes leaf, 1 denotes point, 2 denotes line segment.
		 * @param trapezoid The trapezoid to link to.
		 */
		public DecompVertex(int type, Trapezoid trapezoid){
			this(type, null, null, trapezoid, null, null);
		}
		
		/**
		 * Constructs a Decomposition Vertex of expected type 1 (point) with a corresponding point.
		 * @param type The type of the vertex. 0 denotes leaf, 1 denotes point, 2 denotes line segment.
		 * @param left The left child of the vertex.
		 * @param right The right child of the vertex.
		 * @param point The corresponding point in the decomposition.
		 */
		public DecompVertex(int type, DecompVertex left, DecompVertex right, Point2D point){
			this(type, left, right, null, point, null);
		}
		
		/**
		 * Constructs a Decomposition Vertex of expected type 2 (segment) with a corresponding line segment.
		 * @param type The type of the vertex. 0 denotes leaf, 1 denotes point, 2 denotes line segment.
		 * @param left The left child of the vertex.
		 * @param right The right child of the vertex.
		 * @param segment The corresponding line segment in the decomposition.
		 */
		public DecompVertex(int type, DecompVertex left, DecompVertex right, Line2D segment){
			this(type, left, right, null, null, segment);
		}
		
		/**
		 * Unified constructor for Decomposition vertices.
		 * @param type The type of the vertex. 0 denotes leaf, 1 denotes point, 2 denotes line segment.
		 * @param left The left child of the vertex.
		 * @param right The right child of the vertex.
		 * @param trapezoid The trapezoid for leaf nodes to link to.
		 * @param point The point for point nodes to link to.
		 * @param segment The line segment for segment nodes to link to.
		 */
		public DecompVertex(int type, DecompVertex left, DecompVertex right, Trapezoid trapezoid, Point2D point, Line2D segment){
			this.type  = type;
			if(type == 0){
				this.left  = null;
				this.right = null;
				this.trapezoid = trapezoid;
				this.point = null;
				this.segment = null;
			}else if(type < 3){
				this.left = left;
				this.right = right;
				if(type == 1){
					this.point = point;
					this.trapezoid = null;
					this.segment = null;
				}
				if(type == 2){
					this.trapezoid = null;
					this.point = null;
					this.segment = segment;
				}
			}
		}
		
		/**
		 * Queries which trapezoid a point lies in.
		 * Leaf nodes return their linked trapezoid.
		 * Point nodes check whether the point lies to the left or right 
		 * and return the result for the corresponding child.
		 * Segment nodes check whether the poitn lies above or below the segment
		 * and returns the result of the left child if above, or the right child if below.  
		 * @param query The point whose containing trapezoid is to be determined.
		 * @return The trapezoid that contains the query point.
		 */
		public Trapezoid queryPoint(Point2D query){
			if(type == 0){
				return trapezoid;
			}else if(type == 1){
				return null;//TODO
			}else if(type == 2){
				return null;//TODO
			}else{
				return null;
			}
		}
		
		/**
		 * Gets the left child of the vertex
		 * @return The left child of the vertex, or null if there is none.
		 */
		public DecompVertex getLeftChild(){
			return left;
		}
		
		/**
		 * Sets the right child of the vertex
		 * @param vert The vertex to set the left child to.
		 */
		public void setLeftChild(DecompVertex vert){
			this.left = vert;
		}
		
		/**
		 * Sets the left child of the vertex to null.
		 */
		public void removeLeftChild(){
			this.left = null;
		}
		
		/**
		 * Gets the right child of the vertex.
		 * @return
		 */
		public DecompVertex getRightChild(){
			return right;
		}
		
		/**
		 * Sets the right child of the vertex.
		 * @param vert The vertex to set the right child to.
		 */
		public void setRightChild(DecompVertex vert){
			this.right = vert;
		}
		
		/**
		 * Sets the right child of the vertex to null.
		 */
		public void removeRightChild(){
			this.right = null;
		}
		
		/**
		 * Gets the trapezoid that the vertex points to.
		 * @return The trapezoid that the vertex points to. Null if not leaf.
		 */
		public Trapezoid getTrapezoid(){
			return trapezoid;
		}
		
		/**
		 * Sets the trapezoid that the vertex points to to a given trapezoid, if the vertex is a leaf.
		 * @param trapezoid The trapezoid to associate with the vertex.
		 */
		public void setTrapezoid(Trapezoid trapezoid){
			if(type == 0){
				this.trapezoid = trapezoid;
			}
		}
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
		public Trapezoid(Point2D left, Point2D right, Point2D bot1, Point2D bot2, Point2D top1, Point2D top2, List<Trapezoid> neighbours){
			topLeft = Double.compare(top1.getX(), top2.getX()) == 0 ? (top1.getY() < top2.getY() ? top1 : top2) : (top1.getX() < top2.getX() ? top1 : top2);  
			topRight = topLeft.equals(top1) ? top2 : top1;
			botLeft = Double.compare(bot1.getX(), bot2.getX()) == 0 ? (bot1.getY() < bot2.getY() ? bot1 : bot2) : (bot1.getX() < bot2.getY() ? bot1 : bot2);
			botRight = botLeft.equals(bot1) ? bot2 : bot1;
			leftPoint = left;
			rightPoint = right;
			this.neighbours = neighbours;
		}
		
		/**
		 * Getter for the neighbours of the trapezoid.
		 * @return the neighbours of the trapezoid.
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
		 * @param neighbour the neighbour to be removed.
		 */
		public void removeNeighbour (Trapezoid neighbour){
			neighbours.remove(neighbours.indexOf(neighbour));
		}
		
		/**
		 * Removes this trapezoid from the neighbour lists of all neighbours.
		 * Useful for when we want to delete a Trapezoid.
		 */
		public void freeNeighbours (){
			for(Trapezoid t : neighbours){
				t.removeNeighbour(this);
			}
		}
		
		/**
		 * Computes and outputs the decomposition lines related to this trapezoid.
		 * @return The vertical decomposition lines, unless one or more of the bounding lines is vertical.
		 */
		public List<Line2D> getDecompLines(){
			List<Line2D> verticalLines = new ArrayList<Line2D>(); 
			
			if(Double.compare(topLeft.getX(), topRight.getX()) == 0 &&  Double.compare(botLeft.getX(), botRight.getX()) == 0){//both lines vertical
				Point2D topPoint = Double.compare(topLeft.getY(), topRight.getY()) >= 0 ? topRight : topLeft; 
				Point2D botPoint = Double.compare(botLeft.getY(), botRight.getY()) <= 0 ? botRight : botLeft; 
				verticalLines.add(new Line2D.Double(topPoint, botPoint));
			}else if(Double.compare(topLeft.getX(), topRight.getX()) == 0){//only top line vertical
				double xRatioBot = (topLeft.getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
				Point2D topPoint = Double.compare(topLeft.getY(), topRight.getY()) >= 0 ? topRight : topLeft; 
				Point2D botPoint = new Point2D.Double(topLeft.getX(), xRatioBot * botLeft.getY() + (1 - xRatioBot) * botRight.getY());
				verticalLines.add(new Line2D.Double(topPoint, botPoint));
			}else if(Double.compare(botLeft.getX(), botRight.getX()) == 0){//only bottom line vertical
				double xRatioTop = (botLeft.getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
				Point2D botPoint = Double.compare(botLeft.getY(), botRight.getY()) >= 0 ? botRight : botLeft; 
				Point2D topPoint = new Point2D.Double(botLeft.getX(), xRatioTop * topLeft.getY() + (1 - xRatioTop) * topRight.getY());
			}else{
				if(!(topLeft.equals(botLeft) && topLeft.equals(leftPoint))){//vertical line between top and bottom on the left
					double xRatioTop = (leftPoint.getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
					double xRatioBot = (leftPoint.getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
					verticalLines.add(new Line2D.Double(new Point2D.Double(leftPoint.getX(), xRatioTop * topLeft.getY() + (1 - xRatioTop) * topRight.getY()),
														new Point2D.Double(leftPoint.getX(), xRatioBot * botLeft.getY() + (1 - xRatioBot) * botRight.getY())));
				}
				
				if(!(topRight.equals(botRight) && topRight.equals(rightPoint))){//vertical line between top and bottom on the right
					double xRatioTop = (rightPoint.getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
					double xRatioBot = (rightPoint.getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
					verticalLines.add(new Line2D.Double(new Point2D.Double(rightPoint.getX(), xRatioTop * topLeft.getY() + (1 - xRatioTop) * topRight.getY()),
														new Point2D.Double(rightPoint.getX(), xRatioBot * botLeft.getY() + (1 - xRatioBot) * botRight.getY())));
				}
			}
			return verticalLines;
		}
	}
}
