package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import dev.roanh.convexmerger.game.GameState.GameStateListener;
import dev.roanh.convexmerger.player.Player;

/**
 * Vertical decomposition of a plane containing non-overlapping
 * convex objects. The decomposition supports efficient modifications
 * and querying of the convex objects.
 * @author Roan
 * @author Emu
 */
public class VerticalDecomposition implements GameStateListener {
	/**
	 * The trapezoids of the decomposition.
	 */
	private List<Trapezoid> trapezoids;
	/**
	 * The bounding box that all objects that will ever be added will be contained in (strictly inside, there will be no overlap with the edges).
	 */
	private Rectangle2D bounds;
	/**
	 * The search structure of the decomposition. 
	 * It is a DAG with 3 types of vertices (leaf, point, and segment).
	 */
	private List<DecompVertex> searchStructure;
	/**
	 * The list of segments that have been added to the decomposition. 
	 */
	private List<Line> orientedSegments;
	/**
	 * The list of vertical segments
	 */
	private List<Line> verticalSegments;
	/**
	 * Map of segments to the object above them or to <code>null</code> if that object is the playing field.
	 */
	private Map<Line, ConvexObject> segToObj;

	/**
	 * The set of points added to the decomposition.
	 */
	private List<DecompositionPoint> points;
	
	/**
	 * All line segments ever added into the vertical decomposition.
	 */
	private Set<Line> lines = new HashSet<Line>();
	/**
	 * True the vertical decomposition is animated and showing
	 * individual segment updates.
	 */
	private boolean animate = false;
	
	/**
	 * Constructs a new vertical decomposition with
	 * the given bounding box.
	 * @param bounds A bounding box that all objects that will
	 *        ever be added will be contained in (strictly inside,
	 *        there will be no overlap with the edges).
	 */
	public VerticalDecomposition(Rectangle2D bounds){
		this.bounds = bounds;
		initializeDecomposition();
	}
	
	/**
	 * Constructs a new vertical decomposition with
	 * the given bounding box and objects. The decomposition
	 * is initialised and all objects get decomposed, i.e.
	 * their segments are added to the decomposition.
	 * @param bounds A bounding box that all objects that will
	 *        ever be added will be contained in (strictly inside,
	 *        there will be no overlap with the edges).
	 * @param objects The objects that the vertical decomposition
	 * 		  will contain at the beginning.
.
	 * @throws InterruptedException When the game is aborted.	 */
	public VerticalDecomposition(Rectangle2D bounds, List<ConvexObject> objects) throws InterruptedException{
		this.bounds = bounds;
		initializeDecomposition();
		for(ConvexObject obj : objects){
			addObject(obj);
		}
	}
	
	/**
	 * Gets all lines that were ever added to the vertical decomposition.
	 * @return All line segments ever added.
	 */
	public Set<Line> getLines(){
		return lines;
	}
	
	/**
	 * Gets the last line segment added into the vertical decomposition.
	 * @return The last line segment added to the vertical decomposition.
	 */
	public Line2D getLastLine(){
		return orientedSegments.isEmpty() ? null : orientedSegments.get(orientedSegments.size() - 1);
	}
	
	/**
	 * Sets whether the vertical decomposition is animated and
	 * showing individual segment updates.
	 * @param animated True to animate segment updates.
	 */
	public void setAnimated(boolean animated){
		animate = animated;
	}
	
	/**
	 * Clears all structures except of the objects,
	 * and initializes a blank vertical decomposition
	 * with a bounding box trapezoid and a corresponding search structure vertex.
	 */
	private void initializeDecomposition(){
		trapezoids = new ArrayList<Trapezoid>();
		searchStructure = new ArrayList<DecompVertex>();
		points = new ArrayList<DecompositionPoint>();
		orientedSegments = new ArrayList<Line>();
		verticalSegments = new ArrayList<Line>();
		segToObj = new HashMap<Line, ConvexObject>();
		Point2D botLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
		Point2D botRight = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
		Point2D topLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
		Point2D topRight = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());

		Line botSegment = new Line(botLeft, botRight);
		Line topSegment = new Line(topLeft, topRight);


		Trapezoid initialTrapezoid = new Trapezoid(topLeft, botRight, botSegment, topSegment);
		initialTrapezoid.addLeftPoint(botLeft);
		initialTrapezoid.addRightPoint(topRight);
		DecompVertex initialVertex = new DecompVertex(initialTrapezoid);
		trapezoids.add(initialTrapezoid);
		initialTrapezoid.computeDecompLines();
		searchStructure.add(initialVertex);
	}
	
	/**
	 * Gets all the trapezoids that make up the vertical decomposition
	 * @return All decomposition trapezoids.
	 */
	public List<Trapezoid> getTrapezoids(){
		return trapezoids;
	}
	
	/**
	 * Adds a trapezoid to the list of trapezoids.
	 * @param trap The trapezoid to be added to the list.
	 */
	public void addTrapezoid(Trapezoid trap){
		trap.sanitizeNeighbours();
		trapezoids.add(trap);
	}
	
	/**
	 * Removes a trapezoid from the structure, freeing its neighbours
	 * and disassociating it from its decomposition vertex.
	 * @param trap The trapezoid to be removed.
	 */
	public void removeTrapezoid(Trapezoid trap){
		trap.freeNeighbours();
		trap.getDecompVertex().setTrapezoid(null);
		trap.setDecompVertex(null);
		trapezoids.remove(trap);
	}
	
	/**
	 * Adds a new convex object to this vertical decomposition.
	 * @param obj The convex object to add.
	 * @throws InterruptedException When the game is aborted.
	 */
	public void addObject(ConvexObject obj) throws InterruptedException{
		List<Point2D> points = obj.getPoints();
		
		for(int i = 0; i < points.size(); i++){
			Point2D p1 = points.get(i), p2 = points.get((i+1)%points.size());

			Line2D segment = new Line(p1, p2);
			addSegment(segment, obj);
		}
	}
	
	/**
	 * Checks if the vertical decomposition is animated,
	 * meaning it is showing individual segment updates.
	 * @return True if the vertical decomposition is animated.
	 */
	public boolean isAnimated(){
		return animate;
	}
	
	/**
	 * Queries the convex object that is at the given position.
	 * If no such object exists <code>null</code> is returned.
	 * @param x The x-coordinate of the query point.
	 * @param y The y-coordinate of the query point.
	 * @return The convex object at the given position or
	 *         <code>null</code> if there is no convex object at
	 *         the given position.
	 */
	public ConvexObject queryObject(double x, double y){
//		Trapezoid trap = queryTrapezoid(x, y);
//		Line2D decomp1 = trap.getDecompLines().get(0);
//		Line2D decomp2 = trap.getDecompLines().get(1);
//		System.out.println(trap.getNeighbours().size() + " " + decomp1.getP1() + " " + decomp1.getP2() + " " + decomp2.getP1() + " " + decomp2.getP2() + " ");
		return segToObj.get(queryTrapezoid(x, y).botSegment);
	}
	
	/**
	 * Queries the trapezoid that is at the given position.
	 * If no such trapezoid exists <code>null</code> is returned.
	 * @param x The x-coordinate of the query point.	
	 * @param y The y-coordinate of the query point.
	 * @return The convex object at the given position or
	 *         <code>null</code> is there is no convex object at
	 *         the given position.
	 */
	public Trapezoid queryTrapezoid(double x, double y){
		return queryTrapezoid(new Point2D.Double(x, y));
	}
	
	/**
	 * Queries the trapezoid that is at the given position.
	 * If no such trapezoid exists <code>null</code> is returned.
	 * @param point The query point.
	 * @return The convex object at the given position or
	 *         <code>null</code> is there is no convex object at
	 *         the given position.
	 */
	public Trapezoid queryTrapezoid(Point2D point){
//		Trapezoid result = searchStructure.get(0).queryPoint(point);
//		System.out.println(result.getNeighbours().size());
//		System.out.println("Result l/r: " + result.getXLeft() + " " + result.getXRight());
//		for(Trapezoid neib : result.getNeighbours()){
//			System.out.println("Neighbour l/r: " + neib.getXLeft() + " " + neib.getXRight());
//		}
//		System.out.println("Result top: " + result.topSegment.getP1() + " " + result.topSegment.getP2());
//		System.out.println("Result bot: " + result.botSegment.getP1() + " " + result.botSegment.getP2());
		return searchStructure.get(0).queryPoint(point);
	}
	
	/**
	 * Gets a list of lines that represent the vertical
	 * decomposition lines. Lines to represent the bounding
	 * box will be included as well.
	 * @return The vertical decomposition lines.
	 */
	public List<Line2D> getDecompLines(){
		List<Line2D> lines = new ArrayList<Line2D>();

		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY()));
		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY()));

		for(Trapezoid trap : trapezoids){
			lines.addAll(trap.getDecompLines());
		}
 
		return lines;
	}
	
	/**
	 * Gets the decomposition point at the given point or adds a new point if it doesn't exist and returns it.
	 * @param point The point at which to add a new decomposition point.
	 * @return The added decomposition point or the existing decomposition point at this point.
	 */
	private DecompositionPoint addAndGetPoint(Point2D point){
		int idx = points.indexOf(point);
		if(idx != -1){
			return points.get(idx);
		}
		DecompositionPoint dp = new DecompositionPoint(point);
		addPoint(dp);
		return dp;
	}
	
	/**
	 * Adds a decomposition point to the list of decomposition points.
	 * @param point The decomposition point to add to the list.
	 */
	private void addPoint(DecompositionPoint point){
		points.add(point);
	}
	
	/**
	 * Gets a decomposition point for a given query point.
	 * @param point The query point to look for in the list.
	 * @return The query point if it is in the list, or <code>null</code> otherwise.
	 */
	private DecompositionPoint getPoint(Point2D point){
		int idx = points.indexOf(point);
		if(idx == -1){
			return null;
		}
		return points.get(idx);
	}
	
	/**
	 * Adds a line segment belonging to an object to
	 * the vertical decomposition and updates the structures.
	 * @param seg The line segment to add to the decomposition.
	 * @param obj The object that the segment belongs to.
	 * @throws InterruptedException When the game is aborted.
	 */
	private void addSegment(Line2D seg, ConvexObject obj) throws InterruptedException{
		if(animate){
			Thread.sleep(100);
		}

		synchronized(this){
			Point2D p1 = seg.getP1(), p2 = seg.getP2();
			
			Line orientedSegment = Line.orientedLine(p1, p2);
			
			addAndGetPoint(p1).addSegment(orientedSegment);
			addAndGetPoint(p2).addSegment(orientedSegment);
			
			orientedSegments.add(orientedSegment);
			lines.add(orientedSegment);
			ConvexObject toPut = p1.getX() < p2.getX() ? obj : null;
			segToObj.put(orientedSegment, toPut);
			
			Trapezoid start = queryTrapezoid(p1);
			Trapezoid end = queryTrapezoid(p2);

			if(p1.getX() == p2.getX()){
				verticalSegments.add(orientedSegment);
				addedVerticalSegment(orientedSegment);
			} else if(start.equals(end)){
				addedIntersectsSingleTrapezoid(orientedSegment);
			}else{
				addedIntersectsMultipleTrapezoids(orientedSegment, obj);
			}
		}
	}
	
	/**
	 * Updates the search structure for the case
	 * of an added vertical segment.
	 * @param segment The added segment. Its first point
	 * 				  is always the lower leftmost point.
	 */
	private void addedVerticalSegment(Line segment){
		Point2D leftp = segment.getP1(), rightp = segment.getP2();
		Trapezoid trap = queryTrapezoid(leftp);
		boolean rightExists = rightp.getX() != trap.getXRight();
		if(rightExists){
			addedInternalVerticalSegment(segment, trap);
		}else{
			addedBorderVerticalSegment(segment, trap);
		}
	}
	
	/**
	 * Handles the case where the added vertical segment is on the inside of a single trapezoid.
	 * @param segment The added vertical segment.
	 * @param trap The trapezoid that contains the segment.
	 */
	private void addedInternalVerticalSegment(Line segment, Trapezoid trap){
		Point2D leftp = segment.getP1(), rightp = segment.getP2();
		
		Trapezoid left = new Trapezoid(trap.leftPoints, leftp, trap.botSegment, trap.topSegment);
		Trapezoid right = new Trapezoid(rightp, trap.rightPoints, trap.botSegment, trap.topSegment);

		left.addRightPoint(rightp);
		right.addLeftPoint(leftp);

		left.addNeighbour(right);
		right.addNeighbour(left);
		for(Trapezoid neib : trap.getNeighbours()){
			if(trap.getXLeft() == neib.getXRight()){
				left.addNeighbour(neib);
				neib.addNeighbour(left);
			}
			if(trap.getXRight() == neib.getXLeft()){
				right.addNeighbour(neib);
				neib.addNeighbour(right);
			}
		}

		//Update trapezoid structure and update the search structure.
		DecompVertex vertex = trap.getDecompVertex();
		
		removeTrapezoid(trap);
		addTrapezoid(left);
		addTrapezoid(right);
		
		DecompVertex leftVertex = new DecompVertex(left);
		DecompVertex rightVertex = new DecompVertex(right);
		
		vertex.setToSegment(segment, leftVertex, rightVertex);
	}
	
	/**
	 * Handles the case where the added vertical
	 * segment is on the inside of a single trapezoid.
	 * @param segment The added vertical segment
	 * @param trap The trapezoid whose border
	 * 		  	   the segment lies on.
	 */
	private void addedBorderVerticalSegment(Line segment, Trapezoid trap){
		Point2D leftp = segment.getP1(), rightp = segment.getP2();
		//Add leftp and rightp to the right bounding points, and to the left bounding points of eligible neighbours.
		if(!trap.rightPoints.contains(leftp)){
			trap.addRightPoint(leftp);
		}
		if(!trap.rightPoints.contains(rightp)){
			trap.addRightPoint(rightp);
		}
		for(Trapezoid neib : trap.getNeighbours()){
			if(!neib.leftPoints.contains(leftp) && neib.getXLeft() == leftp.getX()){
				for(Line2D decompLine : neib.getDecompLines()){
					if(decompLine.relativeCCW(leftp) == 0 || decompLine.getP1() == leftp || decompLine.getP2() == leftp){
						neib.leftPoints.add(leftp);
					}
				}
			}
			if(!neib.leftPoints.contains(rightp) ){
				for(Line2D decompLine : neib.getDecompLines()){
					if(decompLine.relativeCCW(rightp) == 0 || decompLine.getP1().equals(rightp) || decompLine.getP2().equals(rightp)){
						neib.leftPoints.add(rightp);
					}
				}
			}
		}

		//Remove neighbours to the right that might not share a side anymore
		List<Trapezoid> removeNeighbours = new ArrayList<Trapezoid>();
		for(Trapezoid neib : trap.getNeighbours()){
			if(verticalSegments.contains(new Line(neib.botSegment.getP1(), neib.topSegment.getP1())) && neib.getXLeft() == trap.getXRight()){
				removeNeighbours.add(neib);
			}
			if(neib.getXLeft() == leftp.getX() &&(
					leftp.equals(trap.botSegment.getP2()) && rightp.equals(trap.topSegment.getP2()) ||
					rightp.equals(trap.botSegment.getP2()) && leftp.equals(trap.topSegment.getP2())
				)){
				removeNeighbours.add(neib);
			}
		}
		for(Trapezoid neib : removeNeighbours){
			trap.removeNeighbour(neib);
			neib.removeNeighbour(trap);
		}
	}
	
	
	/**
	 * Adds a segment that only crosses one trapezoid inside the decomposition.
	 * @param segment The segment to add to the decomposition.
	 */
	private void addedIntersectsSingleTrapezoid(Line segment){
		Point2D leftp = segment.getP1(), rightp = segment.getP2();
		Trapezoid trap = queryTrapezoid(leftp);
		boolean rightExists = rightp.getX() != trap.getXRight();
		
		if(rightExists){
			addedCompletelyInsideSingleTrapezoid(segment, trap);
		}else{
			//Segment ends on the right border.
			addedOnRightBorder(segment, trap);
		}
		//If one of the endpoints is on the left end, then the segment would be considered as a 2-trapezoid intersection.
	}
	
	/**
	 * Updates the structures for case when the added segment
	 * fits entirely inside one trapezoid inside the decomposition.
	 * @param segment The segment that was added
	 * @param trap The trapezoid that the segment is contained inside
	 */
	public void addedCompletelyInsideSingleTrapezoid(Line segment, Trapezoid trap){
		Point2D leftp = segment.getP1(), rightp = segment.getP2();
		Trapezoid left = new Trapezoid(trap.leftPoints, leftp, trap.botSegment, trap.topSegment);
		Trapezoid right = new Trapezoid(rightp, trap.rightPoints, trap.botSegment, trap.topSegment);
		
		Trapezoid top = new Trapezoid(leftp, rightp, segment, trap.topSegment);
		Trapezoid bottom = new Trapezoid(leftp, rightp, trap.botSegment, segment);
		
		left.addNeighbour(top);
		left.addNeighbour(bottom);
		right.addNeighbour(top);
		right.addNeighbour(bottom);
		top.addNeighbour(left);
		top.addNeighbour(right);
		bottom.addNeighbour(left);
		bottom.addNeighbour(right);
		for(Trapezoid neib : trap.getNeighbours()){
			if(trap.getXLeft() == neib.getXRight()){
				left.addNeighbour(neib);
				neib.addNeighbour(left);
			}
			if(trap.getXRight() == neib.getXLeft()){
				right.addNeighbour(neib);
				neib.addNeighbour(right);
			}
		}

		//Update trapezoid list and search structure.
		DecompVertex vertex = trap.getDecompVertex();
		removeTrapezoid(trap);

		DecompVertex topVertex = new DecompVertex(top);
		DecompVertex botVertex = new DecompVertex(bottom);
		DecompVertex leftVertex = new DecompVertex(left);
		DecompVertex rightVertex = new DecompVertex(right);

		DecompVertex segmentVertex = new DecompVertex(topVertex, botVertex, segment);
		DecompVertex rightPointVertex = new DecompVertex(segmentVertex, rightVertex, rightp);

		vertex.setToPoint(leftp, leftVertex, rightPointVertex);
		vertex.setType(1);

		addTrapezoid(top);
		addTrapezoid(bottom);
		addTrapezoid(left);
		addTrapezoid(right);
	}
	
	/**
	 * Updates the structures for case when the added segment
	 * fits inside one trapezoid of the decomposition, except
	 * for its right ending point.
	 * @param segment The segment that was added
	 * @param trap The trapezoid that the segment is contained inside
	 */
	public void addedOnRightBorder(Line segment, Trapezoid trap){
		Point2D leftp = segment.getP1(), rightp = segment.getP2();
		
		Trapezoid top = new Trapezoid(leftp, rightp, segment, trap.topSegment);
		Trapezoid bottom = new Trapezoid(leftp, rightp, trap.botSegment, segment);
		Trapezoid left = new Trapezoid(trap.leftPoints, leftp, trap.botSegment, trap.topSegment);
		
		left.addNeighbour(top);
		left.addNeighbour(bottom);
		top.addNeighbour(left);
		bottom.addNeighbour(left);

		//Attribute the right bounding points to top and bottom.
		for(Point2D p : trap.rightPoints){
			if(segment.relativeCCW(p) <= 0){
				top.addRightPoint(p);
			}
			if(segment.relativeCCW(p) >= 0){
				bottom.addRightPoint(p);
			}
		}

		//find neighbours of left, top, and bottom.
		for(Trapezoid neib : trap.getNeighbours()){
			if(trap.getXLeft() == neib.getXRight()){
				left.addNeighbour(neib);
				neib.addNeighbour(left);
			} else {
				if(segment.getP2() != neib.topSegment.getP1() && top.botSegment.getP2() != top.topSegment.getP2()){//TODO: Propagate this approach to other decompline places if possible
					top.addNeighbour(neib);
					neib.addNeighbour(top);
				}
				if(segment.getP2() != neib.botSegment.getP1() && bottom.botSegment.getP2() != bottom.topSegment.getP2()){
					bottom.addNeighbour(neib);
					neib.addNeighbour(bottom);
				}
			}

			//Add rightp to the left bounding points of neighbours if they belong there.
			if(!neib.leftPoints.contains(rightp)){
				for(Line2D decompLine : neib.getDecompLines()){
					if(decompLine.relativeCCW(rightp) == 0){
						neib.leftPoints.add(rightp);
					}
				}
			}
		}

		//Update trapezoid list and search structure.
		DecompVertex vertex = trap.getDecompVertex();
		removeTrapezoid(trap);
		DecompVertex topVertex = new DecompVertex(top);
		DecompVertex botVertex = new DecompVertex(bottom);
		DecompVertex leftVertex = new DecompVertex(left);

		DecompVertex segmentVertex = new DecompVertex(topVertex, botVertex, segment);
		
		vertex.setToPoint(leftp, leftVertex, segmentVertex);

		addTrapezoid(top);
		addTrapezoid(bottom);
		addTrapezoid(left);
	}
	
	/**
	 * Updates the structures for case when the added segment
	 * crosses more than one trapezoid inside the decomposition.
	 * @param orientedSegment The segment that was added to the decomposition.
	 * @param obj The object that orientedSegment should point towards.
	 */
	public void addedIntersectsMultipleTrapezoids(Line orientedSegment, ConvexObject obj){
		Point2D leftp = orientedSegment.getP1(), rightp = orientedSegment.getP2();
		Trapezoid start = queryTrapezoid(leftp);
		Trapezoid end = queryTrapezoid(rightp);

		Trapezoid top = null, bot = null, left = null, right = null;
		
		Queue<Trapezoid> intersected = findIntersectedTrapezoids(orientedSegment, obj);
		
		addSegmentEndpointsAsTrapezoidBoundingPoints(start,end, orientedSegment);

		//The first (leftmost) trapezoid that the segment intersects.
		Trapezoid first = intersected.peek();
		//The vertices for the running bottom and top trapezoids.
		DecompVertex botVertex = null, topVertex = null;
		while(!intersected.isEmpty()){
			Trapezoid current = intersected.remove();
			assert current.getXLeft() != orientedSegment.getX2();
			if(current == first && leftp.getX() != current.getXLeft() && leftp.getX() != current.getXRight()){
				//Split the first trapezoid into left and right of leftp. The right part can be handled as a fully-sliced trapezoid.
				right = handleFirstTrapezoid(current, leftp);
				current = right;
			}

			if(intersected.isEmpty() && rightp.getX() != current.getXLeft() && rightp.getX() != current.getXRight()){
				//Split the last trapezoid into left and right of rightp. The left part can be handled as a fully-sliced trapezoid.
				left = handleLastTrapezoid(current, rightp);
				current = left;
			}

			if(top == null){
				top = newTop(current, orientedSegment);
				topVertex = new DecompVertex(top);
			}

			if(bot == null){
				bot = newBot(current, orientedSegment);
				botVertex = new DecompVertex(bot);
			}

			distributeRightPoints(current, top, bot, orientedSegment, intersected.peek());

			if(top.rightPoints.size() > 0){
				finishTopTrapezoid(current, orientedSegment, top);
				top = null;
			}
			if(bot.rightPoints.size() > 0){
				finishBotTrapezoid(current, orientedSegment, bot);
				bot = null;
			}

			//Update search structure.
			DecompVertex vertex = current.getDecompVertex();
			removeTrapezoid(current);
			vertex.setTrapezoid(null);

			vertex.setType(2);
			vertex.setTrapezoid(null);
			vertex.setSegment(orientedSegment);
			vertex.setLeftChild(topVertex);
			vertex.setRightChild(botVertex);
		}
	}
	
	/**
	 * If the endpoints of the inserted segment are not bounding points of trapezoids
	 * whose boundaries they lie on, they are added to their bounding points.
	 * This only has effect if the segment's endpoints were not part of the
	 * decomposition until now.
	 * @param start The starting Trapezoid that was intersected by the segment.
	 * @param end The ending Trapezoid that was intersected by the segment.
	 * @param segment THe newly added segment.
	 */
	private void addSegmentEndpointsAsTrapezoidBoundingPoints(Trapezoid start, Trapezoid end, Line segment){
		Point2D leftp = segment.getP1(), rightp = segment.getP2();
		
		if(leftp.getX() == start.getXRight()){
			if(!start.rightPoints.contains(leftp)){
				start.addRightPoint(leftp);
			}
			for(Trapezoid neib : start.getNeighbours()){
				if(!neib.leftPoints.contains(leftp)){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.relativeCCW(leftp) == 0 || decompLine.getP1() == leftp || decompLine.getP2() == leftp){
							neib.leftPoints.add(leftp);
						}
					}
				}
			}
		}
		if(rightp.getX() == end.getXRight()){
			if(!end.rightPoints.contains(rightp)){
				end.addRightPoint(rightp);
			}
			for(Trapezoid neib : end.getNeighbours()){
				if(!neib.leftPoints.contains(rightp)){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.relativeCCW(rightp) == 0 || decompLine.getP1() == rightp || decompLine.getP2() == rightp){
							neib.leftPoints.add(rightp);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Handles the special case of the current trapezoid being the first
	 * of the many intersected by the inserted segment. Splits it into
	 * left and right. The right trapezoid can be handled as one that is
	 * entirely split by the segment.
	 * @param trap The trapezoid intersected by the inserted segment.
	 * @param leftp The left point of the inserted segment.
	 * @return The left trapezoid, to be split into top and bottom part.
	 */
	private Trapezoid handleFirstTrapezoid(Trapezoid trap, Point2D leftp){
		Trapezoid left = new Trapezoid(trap.leftPoints, leftp, trap.botSegment, trap.topSegment);
		Trapezoid right = new Trapezoid(leftp, trap.rightPoints, trap.botSegment, trap.topSegment);

		//Add neighbours to left and right.
		left.addNeighbour(right);
		right.addNeighbour(left);
		for(Trapezoid neib : trap.neighbours){
			if(trap.getXLeft() == neib.getXRight()){
				left.addNeighbour(neib);
				neib.addNeighbour(left);
			}
			if(trap.getXRight() == neib.getXLeft()){
				right.addNeighbour(neib);
				neib.addNeighbour(right);
			}
		}

		//Update search structure
		DecompVertex vertex = trap.getDecompVertex();
		removeTrapezoid(trap);
		
		DecompVertex leftVertex = new DecompVertex(left);
		DecompVertex rightVertex = new DecompVertex(right);
		
		vertex.setToPoint(leftp, leftVertex, rightVertex);

		addTrapezoid(left);
		addTrapezoid(right);
		
		return right;
	}
	
	
	/**
	 * Handles the special case of the current trapezoid being the last
	 * of the many intersected by the inserted segment. Splits it into
	 * left and right. The left trapezoid can be handled as as one that is
	 * entirely split by the segment.
	 * @param trap The trapezoid intersected by the inserted segment.
	 * @param rightp The right point of the inserted segment.
	 * @return The left trapezoid, to be split into top and bottom part.
	 */
	private Trapezoid handleLastTrapezoid(Trapezoid trap, Point2D rightp){
		Trapezoid left = new Trapezoid(trap.leftPoints, rightp, trap.botSegment, trap.topSegment);
		Trapezoid right = new Trapezoid(rightp, trap.rightPoints, trap.botSegment, trap.topSegment);

		left.addNeighbour(right);
		right.addNeighbour(left);
		for(Trapezoid neib : trap.neighbours){
			if(trap.getXLeft() == neib.getXRight()){
				left.addNeighbour(neib);
				neib.addNeighbour(left);
			}
			if(trap.getXRight() == neib.getXLeft()){
				right.addNeighbour(neib);
				neib.addNeighbour(right);
			}
		}

		//Update search structure.
		DecompVertex vertex = trap.getDecompVertex();
		removeTrapezoid(trap);

		DecompVertex leftVertex = new DecompVertex(left);
		DecompVertex rightVertex = new DecompVertex(right);
		
		vertex.setToPoint(rightp, leftVertex, rightVertex);

		addTrapezoid(left);
		addTrapezoid(right);
		
		return left;
	}
	
	/**
	 * Creates a new trapezoid from the part of a given
	 * trapezoid that lies above a given segment.
	 * @param trap The trapezoid that is split.
	 * @param segment The newly inserted segment that splits the trapezoid.
	 * @return A new trapezoid that spans the part of the trapezoid above the segment.
	 */
	private Trapezoid newTop(Trapezoid trap, Line segment){
		Trapezoid top = new Trapezoid(new ArrayList<Point2D>(), new ArrayList<Point2D>(), segment, trap.topSegment);

		for(Point2D p : trap.leftPoints){
			if(segment.relativeCCW(p) <= 0){
				top.addLeftPoint(p);
			}
		}
		assert top.leftPoints.size() > 0;
		
		for(Trapezoid neib : trap.getNeighbours()){
			if(top.topSegment.getP1() == top.botSegment.getP1() && top.getXLeft() == top.botSegment.getX1())
				break;
			assert neib.getDecompLines() != null;
			for(Line2D decompLine : neib.getDecompLines()){//TODO: better way of doing this?
				if(decompLine.getP1().getX() != trap.getXLeft())
					continue;
				if(segment.relativeCCW(decompLine.getP2()) < 0){//P2 is the top point
					top.addNeighbour(neib);
					neib.addNeighbour(top);
				}
			}
		}
		return top;
	}
	
	/**
	 * Creates a new trapezoid from the part of a given
	 * trapezoid that lies below a given segment.
	 * @param trap The trapezoid that is split.
	 * @param segment The newly inserted segment that splits the trapezoid.
	 * @return A new trapezoid that spans the part of the trapezoid below the segment.
	 */
	private Trapezoid newBot(Trapezoid trap, Line segment){
		Trapezoid bot = new Trapezoid(new ArrayList<Point2D>(), new ArrayList<Point2D>(), trap.botSegment, segment);

		
		for(Point2D p : trap.leftPoints){
			if(segment.relativeCCW(p) >= 0){
				bot.addLeftPoint(p);
			}
		}
		assert bot.leftPoints.size() > 0 : bot.leftPoints.size() + " " + trap.leftPoints.get(0) + " " + segment.relativeCCW(trap.leftPoints.get(0)) + " " + segment.getP1() + " " + segment.getP2();
		assert trap.getNeighbours().size() > 0;
		
		for(Trapezoid neib : trap.getNeighbours()){
			if(bot.topSegment.getP1() == bot.botSegment.getP1() && bot.getXLeft() == bot.botSegment.getX1())
				break;
			for(Line2D decompLine : neib.getDecompLines()){
				if(decompLine.getP1().getX() != trap.getXLeft())
					continue;
				if(segment.relativeCCW(decompLine.getP1()) > 0){//P1 is the bottom point
					bot.addNeighbour(neib);
					neib.addNeighbour(bot);
				}
			}
		}
		return bot;
	}
	
	private void distributeRightPoints(Trapezoid trap, Trapezoid top, Trapezoid bot, Line segment, Trapezoid nextIntersected){
		for(Point2D p : trap.rightPoints){
			if(nextIntersected != null && segment.getX1() != segment.getX2()){
				if(trap.rightPoints.stream().anyMatch(po -> segment.relativeCCW(po) <= 0)){
					if(!trap.getNeighbours().contains(nextIntersected)){
						assert trap.rightPoints.size() >= 2 : 
							"\n" + trap.getEndPoints() + " " + trap.rightPoints.size() + " " + trap.getNeighbours().size() +"\n" + 
							nextIntersected.getEndPoints() + " " + nextIntersected.leftPoints.size() + " " + nextIntersected.getNeighbours().size();
					} else {
						assert nextIntersected.leftPoints.stream().anyMatch(po -> segment.relativeCCW(po) <= 0);
					}
				}
				if(trap.rightPoints.stream().anyMatch(po -> segment.relativeCCW(po) >= 0)){
					if(!trap.getNeighbours().contains(nextIntersected)){
						assert trap.rightPoints.size() >= 2;
					} else {
						assert nextIntersected.leftPoints.stream().anyMatch(po -> segment.relativeCCW(po) >= 0) : "hmm " + trap.rightPoints.get(0) + " "+ nextIntersected.leftPoints.get(0) + " " + nextIntersected.rightPoints.get(0);
					}
				}
			}
			if(segment.relativeCCW(p) <= 0){
				top.addRightPoint(p);
			}
			if(segment.relativeCCW(p) >= 0){
				bot.addRightPoint(p);
			}
		}
	}
	
	private void finishTopTrapezoid(Trapezoid trap, Line segment, Trapezoid top){
		for(Trapezoid neib : trap.getNeighbours()){
			if(top.botSegment.getP2() == top.topSegment.getP2() && top.getXRight() == top.topSegment.getX2())
				break;
			for(Line2D decompLine : neib.getDecompLines()){//TODO: better way of doing this?
				if(decompLine.getP1().getX() != trap.getXRight())
					continue;
				if(segment.relativeCCW(decompLine.getP2()) < 0){//P2 is the top point
					top.addNeighbour(neib);
					neib.addNeighbour(top);
				}
			}
		}
		addTrapezoid(top);
	}
	
	private void finishBotTrapezoid(Trapezoid trap, Line segment, Trapezoid bot){
		for(Trapezoid neib : trap.getNeighbours()){
			if(bot.botSegment.getP2() == bot.topSegment.getP2() && bot.getXRight() == bot.botSegment.getX2())
				break;
			for(Line2D decompLine : neib.getDecompLines()){
				if(decompLine.getP1().getX() != trap.getXRight())
					continue;
				if(segment.relativeCCW(decompLine.getP1()) > 0){//P1 is the bottom point
					bot.addNeighbour(neib);
					neib.addNeighbour(bot);
				}
			}
		}
		addTrapezoid(bot);
	}
	
	/**
	 * Computes a list of trapezoids intersected by a line segment, excluding the leftmost and rightmost intersected trapezoids.
	 * @param seg The line segment.
	 * @param obj The object that the segment is added for.
	 * @return A list of trapezoids intersected by a line segment, excluding the leftmost and rightmost intersected trapezoids.
	 */
	public Queue<Trapezoid> findIntersectedTrapezoids(Line2D seg, ConvexObject obj){
		Queue<Trapezoid>intersectedTraps = new LinkedList<Trapezoid>();
		Trapezoid start = queryTrapezoid(seg.getP1());
		Queue<Trapezoid> q = new LinkedList<Trapezoid>();
		Set<Trapezoid> visited = new HashSet<Trapezoid>();
		if(segToObj.get(start.botSegment) == obj && start.botSegment.getP2() != seg.getP1() ||
		   segToObj.get(start.botSegment) == null){
			q.add(start);
		} else {
			Trapezoid last = start;
			while(q.isEmpty()){
				outer_for: for(Trapezoid onOtherSide : last.botSegment.trapsBelow){
					if(onOtherSide.leftPoints.contains(seg.getP1())){
						q.add(onOtherSide);
						break;
					}
					for(Trapezoid neib : onOtherSide.getNeighbours()){
						if(neib.leftPoints.contains(seg.getP1())){
							q.add(neib);
							break outer_for;
						}
					}
					last = onOtherSide;
				}
			}
		}
		assert q.size() == 1;
		while(!q.isEmpty()){
			Trapezoid current = q.remove();
			if(current.intersectsSegment(seg)){
				if(!intersectedTraps.contains(current)){
					int cnt = 0;
					for(Line2D decompLine : current.getDecompLines()){
						if(decompLine.intersectsLine(seg) || seg.relativeCCW(decompLine.getP1()) == 0 || seg.relativeCCW(decompLine.getP2()) == 0){
							cnt++;
						}
					}
					if(cnt > 1 || current.pointInside(seg.getP1()) || current.pointInside(seg.getP2())){
						intersectedTraps.add(current);
					}
				}
				for(Trapezoid neib : current.getNeighbours()){
					if(!intersectedTraps.contains(neib) && neib.intersectsSegment(seg)){
						if(!visited.contains(neib) && neib.getXLeft() == current.getXRight()){
							q.add(neib);
							visited.add(neib);
						}
					}
				}
			}
		}
		return intersectedTraps;
	}
	
	@Override
	public void claim(Player player, ConvexObject obj){
	}


	@Override
	public void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed){
		Point2D[] mergePoints = ConvexUtil.computeMergeLines(source.getPoints(), target.getPoints(), result.getPoints());
//		System.out.println(source.getPoints());
//		System.out.println(target.getPoints());
		Line firstLine =  new Line(mergePoints[0], mergePoints[1]);
		Line secondLine = new Line(mergePoints[2], mergePoints[3]);
//		System.out.println(firstLine.getP1() + "  " + firstLine.getP2());
//		System.out.println(secondLine.getP1() + "  " + secondLine.getP2());
		try{
			addSegment(firstLine, result);
			addSegment(secondLine, result);
		} catch (InterruptedException e){
			// TODO @Roan help
			e.printStackTrace();
		}
		Queue<Trapezoid> q = new LinkedList<Trapezoid>();
		Set<Trapezoid> visited = new HashSet<Trapezoid>();
		Set<Line2D> visitedSeg = new HashSet<Line2D>();
		List<Point2D> borderPoints = result.getPoints();
		Set<Line2D> borderSegments = new HashSet<Line2D>();
		for(int i = 0; i < borderPoints.size(); i++){
			Point2D p1 = borderPoints.get(i), p2 = borderPoints.get((i + 1) % borderPoints.size());
			Line segment = orientedSegments.get(orientedSegments.indexOf(new Line(p1, p2)));
			borderSegments.add(segment);
			if(p1.getX() == p2.getX()){
				continue;
			}
			
			if(segment.getP1() == p1){
				q.addAll(segment.getTrapsAbove());
			} else {
				q.addAll(segment.getTrapsBelow());
			}
			
		}

		while(!q.isEmpty()){
			Trapezoid curr = q.remove();

			if(segToObj.replace(curr.botSegment, result) == null){
				segToObj.put(curr.botSegment, result);
			}
			//Add unvisited neighbours to the queue
//			curr.sanitizeNeighbours();
			for(Trapezoid neib : curr.getNeighbours()){
				if(!visited.contains(neib)){
					q.add(neib);
					visited.add(neib);
				}
			}
			
			
			if(!visitedSeg.contains(curr.botSegment)){
				visitedSeg.add(curr.botSegment);
				if(!borderSegments.contains(curr.botSegment)){
					for(Trapezoid neib : curr.botSegment.getTrapsBelow()){
						if(!visited.contains(neib)){
							q.add(neib);
							visited.add(neib);
						}
					}
				}
			}
			if(!visitedSeg.contains(curr.topSegment)){
				visitedSeg.add(curr.topSegment);
				if(!borderSegments.contains(curr.topSegment)){
					for(Trapezoid neib : curr.topSegment.getTrapsAbove()){
						if(!visited.contains(neib)){
							q.add(neib);
							visited.add(neib);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void end(){
	}
	
	@Override
	public void abort(){
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
		 * Constructs a Decomposition Vertex of type 0 (leaf) with a linked trapezoid.
		 * @param trapezoid The trapezoid to link to.
		 */
		public DecompVertex(Trapezoid trapezoid){
			this(0, null, null, trapezoid, null, null);
			trapezoid.setDecompVertex(this);
		}

		/**
		 * Constructs a Decomposition Vertex of type 1 (point) with a corresponding point.
		 * @param left The  child of the vertex.
		 * @param right The right child of the vertex.
		 * @param point The corresponding point in the decomposition.
		 */
		public DecompVertex(DecompVertex left, DecompVertex right, Point2D point){
			this(1, left, right, null, point, null);
		}

		/**
		 * Constructs a Decomposition Vertex of expected type 2 (segment) with a corresponding line segment.
		 * @param left The left child of the vertex. (above the segment)
		 * @param right The right child of the vertex. (below the segment)
		 * @param segment The corresponding line segment in the decomposition.
		 */
		public DecompVertex(DecompVertex left, DecompVertex right, Line2D segment){
			this(2, left, right, null, null, segment);
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
			this.type = type;
			if(type == 0){
				this.left = null;
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
				assert point != null;
				if(query.getX() <= point.getX()){
					return left.queryPoint(query);
				}else{
					return right.queryPoint(query);
				}
			}else if(type == 2){
				assert segment != null;
				Point2D leftp = Double.compare(segment.getP1().getX(), segment.getP2().getX()) == 0 ? 
					(Double.compare(segment.getP1().getY(), segment.getP2().getY()) <= 0 ? segment.getP1() : segment.getP2()) 
					: (Double.compare(segment.getP1().getX(), segment.getP2().getX()) <= 0 ? segment.getP1() : segment.getP2());
				Point2D rightp = leftp.equals(segment.getP1()) ? segment.getP2() : segment.getP1();

				Line2D orientedSegment = new Line(leftp, rightp);
				return orientedSegment.relativeCCW(query) <= 0 ? left.queryPoint(query) : right.queryPoint(query);
			}else{
				return null;
			}
		}

		/**
		 * Sets the type to an integer (0 is vertex, 1 is point, 2 is segment).
		 * @param type The type to set
		 */
		public void setType(int type){
			this.type = type;
			if(this.type != 0){
				this.trapezoid = null;
			}
		}
		
		/**
		 * Converts the vertex to a point vertex, associating it to a
		 * point, assigning children to the left and right, and
		 * removing the associated trapezoid and segment.
		 * @param point The point to associate the vertex to.
		 * @param left The left child of the vertex, corresponding to
		 * 		  the area to the left of the point.
		 * @param right The right child of the vertex, corresponding 
		 * 		  to the area to the right of the point.
		 */
		public void setToPoint(Point2D point, DecompVertex left, DecompVertex right){
			setTrapezoid(null);
			setSegment(null);
			setType(1);
			setPoint(point);
			setLeftChild(left);
			setRightChild(right);
		}
		
		/**
		 * Converts the vertex to a segment vertex, associating it to a
		 * segment, assigning children to the left and right (bottom and
		 * top respectively), and removing the associated trapezoid and
		 * point, if any.
		 * @param segment The segment to associate the vertex to.
		 * @param bottom The left child of the vertex, corresponding to
		 * 		  the area below the segment.
		 * @param top The right child of the vertex, corresponding to
		 * 		  the area above the segment.
		 */
		public void setToSegment(Line segment, DecompVertex bottom, DecompVertex top){
			setTrapezoid(null);
			setPoint(null);
			setType(2);
			setSegment(segment);
			setLeftChild(bottom);
			setRightChild(top);
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
		 * Gets the right child of the vertex.
		 * @return The right child of the vertex.
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
			this.trapezoid = trapezoid;
		}

		/**
		 * Gets the point that the vertex points to.
		 * @return The point that the vertex points to.
		 */
		public Point2D getPoint(){
			return point;
		}

		/**
		 Sets the point that the vertex points to to a given point, if the vertex is a point vertex.
		 * @param point The point to associate with the vertex.
		 */
		public void setPoint(Point2D point){
			this.point = point;
		}

		/**
		 * Gets the segment that the vertex points to.
		 * @return The segment that the vertex points to.
		 */
		public Line2D getSegment(){
			return segment;
		}

		/**
		 Sets the segment that the vertex points to to a given seg e t, if the vertex is a segment vertex.
		 * @param segment The point to associate with the vertex.
		 */
		public void setSegment(Line2D segment){
			this.segment = segment;
		}
	}

	/**
	 * Defines the trapezoid structure that is used in the Vertical decomposition.
	 * @author Emu
	 */
	public class Trapezoid{
		/**
		 * The segments that bound the trapezoid from the top and bottom
		 */
		private Line botSegment, topSegment;
		/**
		 * The points that bound the trapezoid from the left and right.
		 */
		private List<Point2D> leftPoints, rightPoints;
		/**
		 * The neighbouring trapezoids of the trapezoid.
		 */
		private List<Trapezoid> neighbours;
		/**
		 * The vertex that this trapezoid is linked to
		 */
		private DecompVertex vertex;
		/**
		 * The decomposition lines of the trapezoid.
		 */
		private List<Line2D> decompLines;

		/**
		 * Constructs a trapezoid given a left and a right bounding point, and the top and bottom bounding segments.
		 * Also computes the vertical decomposition lines if at least one left and right point are passed.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param botSegment The bottom bounding segment of the trapezoid.
		 * @param topSegment The top bounding segment of the trapezoid.
		 */
		public Trapezoid(Point2D left, Point2D right, Line botSegment, Line topSegment){
			this(new ArrayList<Point2D>(Arrays.asList(left)), new ArrayList<Point2D>(Arrays.asList(right)), botSegment, topSegment);
		}

		/**
		 * Constructs a trapezoid given a list of left bounding points and a right bounding point, and the top and bottom bounding segments.
		 * Also computes the vertical decomposition lines if at least one left and right point are passed.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param botSegment The bottom bounding segment of the trapezoid.
		 * @param topSegment The top bounding segment of the trapezoid.
		 */
		public Trapezoid(List<Point2D> left, Point2D right, Line botSegment, Line topSegment){
			this(left, new ArrayList<Point2D>(Arrays.asList(right)), botSegment, topSegment);
		}
		
		/**
		 * Constructs a trapezoid given a left bounding point and a list of right bounding points, and the top and bottom bounding segments.
		 * Also computes the vertical decomposition lines if at least one left and right point are passed.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param botSegment The bottom bounding segment of the trapezoid.
		 * @param topSegment The top bounding segment of the trapezoid.
		 */
		public Trapezoid(Point2D left, List<Point2D>  right, Line botSegment, Line topSegment){
			this(new ArrayList<Point2D>(Arrays.asList(left)), right, botSegment, topSegment);
		}

		/**
		 * Constructs a trapezoid given lists of left and right bounding points, and the top and bottom bounding segments.
		 * Also computes the vertical decomposition lines if at least one left and right point are passed.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param botSegment The bottom bounding segment of the trapezoid.
		 * @param topSegment The top bounding segment of the trapezoid.
		 */
		public Trapezoid(List<Point2D> left, List<Point2D> right, Line botSegment, Line topSegment){
			this.botSegment = botSegment;
			this.topSegment = topSegment;
			topSegment.addTrapBelow(this);
			botSegment.addTrapAbove(this);
			this.leftPoints = left;
			this.rightPoints = right;

			this.neighbours = new ArrayList<Trapezoid>();
			this.vertex = null;

			if(!leftPoints.isEmpty() && !rightPoints.isEmpty()){
				computeDecompLines();
			}
		}
		
		/**
		 * Getter for the X coordinate of the left wall of the trapezoid.
		 * @return The X coordinate of the left wall of the trapezoid.
		 */
		public double getXLeft(){
			return leftPoints.get(0).getX();
		}
		
		/**
		 * Getter for the X coordinate of the right wall of the trapezoid.
		 * @return The X coordinate of the right wall of the trapezoid.
		 */
		public double getXRight(){
			return rightPoints.get(0).getX();
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
		 * Adds a collection of neighbours to the list of neighbours.
		 * @param neighbours The neighbours to add to the list.
		 */
		public void addNeighbours(Collection<Trapezoid> neighbours){
			this.neighbours.addAll(neighbours);
		}

		/**
		 * Removes a neighbour from the list of neighbours.
		 * @param neighbour the neighbour to be removed.
		 */
		public void removeNeighbour(Trapezoid neighbour){
			while(neighbours.contains(neighbour)){
				neighbours.remove(neighbour);
			}
		}
		
		/**
		 * Removes this trapezoid from the neighbour lists
		 * of all neighbours and clears the neighbour list
		 * of this trapezoid.
		 */
		public void freeNeighbours(){
			for(Trapezoid t : neighbours){
				t.removeNeighbour(this);
			}
			neighbours.clear();
			topSegment.removeTrapBelow(this);
			botSegment.removeTrapAbove(this);
		}
		
		/**
		 * Removes undesired neighbourhoods of trapezoids.
		 * This includes:
		 *   - Neigbhours on the left (right) in case the 
		 *     top and bottom segment meet at the corresponding
		 *     left or right bounding point of this trapezoid
		 *   - Neighbours on the left (right) in case the
		 *     top and bottom segment share the same X coordinate
		 *     and there is a segment between the left (right)
		 *     endpoints of the top and bottom segments.
		 *   - Neighbours who are somehow connected through the
		 *     top(bottom) bounding segment.
		 */
		public void sanitizeNeighbours(){
			List<Trapezoid> removeNeighbours = new ArrayList<Trapezoid>();
			if(getXLeft() == getXRight()){
				freeNeighbours();
			}
			for(Trapezoid neib: getNeighbours()){
				if(
//					topSegment.getP1() == botSegment.getP1() && topSegment.getX1() == getXLeft() &&(
//						neib.getXRight() == botSegment.getX1()
//					)||
//					topSegment.getP2() == botSegment.getP2() && topSegment.getX2() == getXRight() &&(
//						neib.getXLeft() == botSegment.getX2()
//					)||
					topSegment.getX1() == botSegment.getX1() && topSegment.getX1() == getXLeft() &&(
						neib.getXRight() == getXLeft() &&
						verticalSegments.contains(new Line(botSegment.getP1(), topSegment.getP1()))
					)||
					topSegment.getX2() == botSegment.getX2() && topSegment.getX2() == getXRight() &&(
						neib.getXLeft() == getXRight() && 
						verticalSegments.contains(new Line(botSegment.getP2(), topSegment.getP2()))
					)||
//					(neib.rightPoints.contains(topSegment.getP1()) || neib.rightPoints.contains(botSegment.getP1())) &&(
//						orientedSegments.indexOf(new Line(botSegment.getP1(), topSegment.getP1())) != -1
//					)||
//					(neib.leftPoints.contains(botSegment.getP2()) || neib.leftPoints.contains(topSegment.getP2())) &&(
//						orientedSegments.indexOf(new Line(botSegment.getP2(), topSegment.getP2())) != -1
//					)||
					neib.topSegment == botSegment || topSegment == neib.botSegment){
					removeNeighbours.add(neib);
				}
			}
			for(Trapezoid neib : removeNeighbours){
				removeNeighbour(neib);
				neib.removeNeighbour(this);
			}
		}

		/**
		 * Gets the vertical lines of this trapezoid.
		 * @return The vertical lines of this trapezoid.
		 */
		public List<Line2D> getDecompLines(){
			return decompLines;
		}
		
		/**
		 * Gets the left vertical line of this trapezoid.
		 * @return The left vertical line of this trapezoid.
		 */
		public Line2D getLeftDecompLine(){
			return decompLines.get(0);
		}
		
		/**
		 * Gets the right vertical line of this trapezoid.
		 * @return The left vertical line of this trapezoid.
		 */
		public Line2D getRightDecompLine(){
			return decompLines.get(1);
		}

		/**
		 * Computes and sets the decomposition lines related to this trapezoid.
		 * The decomposition lines have x coordinates equal to the left and right
		 * bounding points, and they end at the bottomsegment and topsegment
		 * of the trapezoid.
		 * The endpoints of the decomposition lines can be used as points for the
		 * construction of a convex object that covers the trapezoid.
		 */
		public void computeDecompLines(){
			Point2D botLeft = botSegment.getP1(), botRight = botSegment.getP2();
			Point2D topLeft = topSegment.getP1(), topRight = topSegment.getP2();
			List<Line2D> verticalLines = new ArrayList<Line2D>();
			if(leftPoints.size() > 0){//Draw vertical line between top and bottom on the left
				double xRatioTop = (getXLeft() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
				double xRatioBot = (getXLeft() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
				verticalLines.add(new Line(
					new Point2D.Double(getXLeft(), xRatioBot * botRight.getY() + (1 - xRatioBot) * botLeft.getY()),
					new Point2D.Double(getXLeft(), xRatioTop * topRight.getY() + (1 - xRatioTop) * topLeft.getY()))
				);
			}

			if(rightPoints.size() > 0){//Draw vertical line between top and bottom on the right
				double xRatioTop = Math.abs((getXRight() - topLeft.getX()) / (topRight.getX() - topLeft.getX()));
				double xRatioBot = Math.abs((getXRight() - botLeft.getX()) / (botRight.getX() - botLeft.getX()));
				verticalLines.add(new Line(
					new Point2D.Double(getXRight(), xRatioBot * botRight.getY() + (1 - xRatioBot) * botLeft.getY()),
					new Point2D.Double(getXRight(), xRatioTop * topRight.getY() + (1 - xRatioTop) * topLeft.getY()))
				);
			}
			decompLines = verticalLines;
		}
		
		/**
		 * Gets the decomposition vertex that this trapezoid is linked to.
		 * @return The decomposition vertex that this trapezoid is linked to.
		 */
		public DecompVertex getDecompVertex(){
			return vertex;
		}

		/**
		 * Links the trapezoid to a Decomposition vertex.
		 * @param vertex The Decomposition vertex to link to.
		 */
		public void setDecompVertex(DecompVertex vertex){
			this.vertex = vertex;
		}
		
		/**
		 * Checks whether a given line segment intersects with this trapezoid.
		 * A 
		 * @param segment The line segment to check intersection with.
		 * @return True if the line segment is intersected, false otherwise.
		 */
		public boolean intersectsSegment(Line2D segment){
			List<Line2D> decompLines = this.getDecompLines();
			for(Line2D line : decompLines){
				if(line.intersectsLine(segment)){
					return true;
				}
			}
			return false;
		}

		/**
		 * Checks whether a given point is strictly inside a trapezoid
		 * @param p The point to check.
		 * @return true if the point is contained in the trapezoid and not on the boundary, false otherwise.
		 */
		public boolean pointInside(Point2D p){
			if(botSegment.relativeCCW(p) < 0 && topSegment.relativeCCW(p) > 0 && p.getX() > getXLeft() && p.getX() < getXRight()){
				return true;
			}
			return false;
		}
		
		/**
		 * Adds a left bounding point to the trapezoid.
		 * Also computes the vertical decomposition lines if this is the first left bounding point and at least 1 right bounding point exists.
		 * @param point The new left bounding point.
		 */
		public void addLeftPoint(Point2D point){
			leftPoints.add(point);
			if(leftPoints.size() == 1 && !rightPoints.isEmpty()){
				computeDecompLines();
			}
		}

		/**
		 * Adds a right bounding point to the trapezoid.
		 * Also computes the vertical decomposition lines if this is the first right bounding point and at least 1 left bounding point exists.
		 * @param point The new right bounding point
		 */
		public void addRightPoint(Point2D point){
			rightPoints.add(point);
			if(rightPoints.size() == 1 && !leftPoints.isEmpty()){
				computeDecompLines();
			}
		}
		
		/**
		 * Returns the centroid of the trapezoid.
		 * @return the centroid of the trapezoid.
		 */
		public Point2D getCentroid(){
			return ConvexUtil.computeCentroid(getEndPoints());
		}
		
		/**
		 * Returns the points of the trapezoid in counterclockwise order
		 * @return points of the trapezoid in counterclockwise order as a list.
		 */
		public List<Point2D> getEndPoints(){
			if(decompLines == null){
				computeDecompLines();
			}
			return Arrays.asList(decompLines.get(0).getP1(), decompLines.get(1).getP1(), decompLines.get(1).getP2(), decompLines.get(0).getP2());
		}
		
		/**
		 * Returns the convex object that contains this trapezoid. Returns <code>null</code> if no convex object contains it.
		 * @return The convex object that contains this trapezoid, or <code>null</code> if it is not part of any convex object.
		 */
		public ConvexObject getObject(){
			return segToObj.get(botSegment);
		}
	}
	
	/**
	 * A line instance with equality based
	 * on its end points.
	 * @author Roan
	 */
	public static class Line extends Line2D{
		/**
		 * First end point of the line.
		 */
		private Point2D p1;
		/**
		 * Second end point of the line.
		 */
		private Point2D p2;
		/**
		 * Trapezoids with this line as its top segment.
		 */
		List<Trapezoid> trapsBelow;
		/**
		 * Trapezoids with this line as its bottom segment.
		 */
		List<Trapezoid> trapsAbove;

		/**
		 * Constructs a new line with the given
		 * end points. Any line with the exact
		 * same objects as end points is considered
		 * equal to this line.
		 * @param p1 The first end point of the line.
		 * @param p2 The second end point of hte line.
		 */
		public Line(Point2D p1, Point2D p2){
			this.p1 = p1;
			this.p2 = p2;
			trapsAbove = new ArrayList<Trapezoid>();
			trapsBelow = new ArrayList<Trapezoid>();
		}

		@Override
		public Rectangle2D getBounds2D(){
			return new Rectangle2D.Double(
				Math.min(p1.getX(), p2.getX()),
				Math.min(p1.getY(), p2.getY()),
				Math.abs(p1.getX() - p2.getX()),
				Math.abs(p1.getY() - p2.getY())
			);
		}

		@Override
		public double getX1(){
			return p1.getX();
		}

		@Override
		public double getY1(){
			return p1.getY();
		}

		@Override
		public Point2D getP1(){
			return p1;
		}

		@Override
		public double getX2(){
			return p2.getX();
		}

		@Override
		public double getY2(){
			return p2.getY();
		}

		@Override
		public Point2D getP2(){
			return p2;
		}

		@Override
		public void setLine(double x1, double y1, double x2, double y2){
			throw new IllegalStateException("Unsupported operation");
		}

		@Override
		public int hashCode(){
			return Objects.hash(p1, p2);
		}

		@Override
		public boolean equals(Object other){
			if(other instanceof Line){
				Line line = (Line)other;
				return (line.p1 == p1 && line.p2 == p2) || (line.p1 == p2 && line.p2 == p1);
			}else{
				return false;
			}
		}
		
		/**
		 * Create an oriented line segment given two points.
		 * The orientation is from left-bottom to right-top.
		 * @param p1 The first point
		 * @param p2 The second point
		 * @return An oriented line segment where the first point
		 * 		   is the leftmost-lower point, and the second
		 * 		   point is the rightmost-upper point.
		 */
		public static Line orientedLine(Point2D p1, Point2D p2){
			Point2D leftp = java.lang.Double.compare(p1.getX(), p2.getX()) == 0 ? 
					(java.lang.Double.compare(p1.getY(), p2.getY()) <= 0 ? p1 : p2)
					: (java.lang.Double.compare(p1.getX(), p2.getX()) < 0 ? p1 : p2);
			Point2D rightp = leftp.equals(p1) ? p2 : p1;
			return new Line(leftp, rightp);
		}
		/**
		 * Retrieves the list of trapezoids above this line.
		 * @return The list of trapezoids above this line.
		 */
		public List<Trapezoid> getTrapsAbove(){
			return trapsAbove;
		}
		
		/**
		 * Retrieves the list of trapezoids below this line.
		 * @return The list of trapezoids below this line.
		 */
		public List<Trapezoid> getTrapsBelow(){
			return trapsBelow;
		}
		
		/**
		 * Adds a given trapezoid to the list of trapezoids below this line.
		 * @param trap The trapezoid to add.
		 */
		public void addTrapAbove(Trapezoid trap){
			trapsAbove.add(trap);
		}
		
		/**
		 * Adds a given trapezoid to the list of trapezoids below this line.
		 * @param trap The trapezoid to add.
		 */
		public void addTrapBelow(Trapezoid trap){
			trapsBelow.add(trap);
		}
		
		/**
		 * Removes a given trapezoid from the list of trapezoids above this line.
		 * @param trap The trapezoid to remove.
		 */
		public void removeTrapAbove(Trapezoid trap){
			trapsAbove.remove(trap);
		}
		
		/**
		 * Removes a given trapezoid from the list of trapezoids below this line.
		 * @param trap The trapezoid to remove.
		 */
		public void removeTrapBelow(Trapezoid trap){
			trapsBelow.remove(trap);
		}
		
		/**
		 * Gets the leftmost trapezoid that is above this line.
		 * @return The leftmost trapezoid that is above this line or <code>null</code> if the line is vertical.
		 */
		public Trapezoid getLeftmostTrapAbove(){
			if(getX1() == getX2()){
				return null;
			}else if(getX1() < getX2()){
				return trapsAbove.get(0);
			}else{
				return trapsAbove.get(trapsAbove.size()-1);
			}
		}
		
		/**
		 * Gets the rightmost trapezoid that is above this line.
		 * @return the rightmost trapezoid that is above this line or <code>null</code> if the line is vertical.
		 */
		public Trapezoid getRightmostTrapAbove(){
			if(getX1() == getX2()){
				return null;
			}else if(getX1() < getX2()){
				return trapsAbove.get(trapsAbove.size()-1);
			}else{
				return trapsAbove.get(0);
			}
		}
		
		/**
		 * Gets the leftmost trapezoid that is below this line.
		 * @return The leftmost trapezoid that is below this line or <code>null</code> if the line is vertical.
		 */
		public Trapezoid getLeftmostTrapBelow(){
			if(getX1() == getX2()){
				return null;
			}else if(getX1() < getX2()){
				return trapsBelow.get(0);
			}else{
				return trapsBelow.get(trapsBelow.size()-1);
			}
		}
		
		/**
		 * Gets the rightmost trapezoid that is below this line.
		 * @return the rightmost trapezoid that is below this line or <code>null</code> if the line is vertical.
		 */
		public Trapezoid getRightmostTrapBelow(){
			if(getX1() == getX2()){
				return null;
			}else if(getX1() < getX2()){
				return trapsBelow.get(trapsBelow.size()-1);
			}else{
				return trapsBelow.get(0);
			}
		}

	}
	
	/**
	 * A structure to represent a point in the vertical decomposition.
	 * Equality is based on the equality of the point location.
	 * Contains references to segments that include it.
	 * @author Emu
	 */
	public class DecompositionPoint extends Point2D{
		/**
		 * The point of this structure.
		 */
		private Point2D point;
		/**
		 * The segments that this point bounds.
		 */
		private List<Line> segments;
		/**
		 * The trapezoids that this point bounds.
		 */
		private List<Trapezoid> trapezoids;
		
		/**
		 * Constructs a new decomposition point
		 * on the given coordinates.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 */
		public DecompositionPoint(double x, double y){
			this(new Point2D.Double(x,y));
		}
		
		/**
		 * Constructs a new decomposition point
		 * on the given point in space.
		 * @param point The point this structure represents.
		 */
		public DecompositionPoint(Point2D point){
			this.point = point;
			segments = new ArrayList<Line>();
			trapezoids = new ArrayList<Trapezoid>();
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof DecompositionPoint){
				return ((DecompositionPoint) other).getPoint() == point;
			}else{
				return false;
			}
		}
		
		/**
		 * Gets the point of this structure.
		 * @return the point of this structure.
		 */
		public Point2D getPoint(){
			return point;
		}
		
		/**
		 * Gets the list of segments that this point is part of.
		 * @return The list of segments that this point is part of.
		 */
		public List<Line> getSegments(){
			return segments;
		}
		
		/**
		 * Adds a segment to the list of segments that this point is part of.
		 * @param seg the segment to add to the list of segments that this point is part of.
		 */
		public void addSegment(Line seg){
			segments.add(seg);
		}
		
		/**
		 * Gets the trapezoid to the right that contains the given segment.
		 * Useful for determining the trapezoid to start splitting up during
		 * segment addition.
		 * @param seg The query segment.
		 * @return The trapezoid on the right that (partially) contains the segment, or <code>null</code> if this point is not the left point of the segment.
		 */
		public Trapezoid getIntersectedTrapezoidToTheRight(Line seg){
			if(point != seg.getP1() || seg.getX1() == seg.getX2()){
				return null;
			}
			for(Line segment : segments){
				if(segment.getP1().getX() == point.getX() && segment.getX1() != segment.getX2()){
					Trapezoid trap = segment.getLeftmostTrapAbove();
					if(trap.getXLeft() == point.getX() && trap.getRightDecompLine().intersectsLine(seg)){
						return trap;
					}
					
					trap = segment.getLeftmostTrapBelow();
					if(trap.getXLeft() == point.getX() && trap.getRightDecompLine().intersectsLine(seg)){
						return trap;
					}
				}
			}
			return null;
		}

		@Override
		public double getX(){
			return point.getX();
		}

		@Override
		public double getY(){
			return point.getY();
		}

		@Override
		public void setLocation(double x, double y){
			point.setLocation(x, y);
		}
	}
}
