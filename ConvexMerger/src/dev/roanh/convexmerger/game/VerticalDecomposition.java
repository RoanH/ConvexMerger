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
public class VerticalDecomposition implements GameStateListener{
	/**
	 * The objects that are decomposed.
	 */
	private List<ConvexObject> objects;
	/**
	 * The trapezoids of the decomposition.
	 */
	public List<Trapezoid> trapezoids;
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
	public List<Line2D> orientedSegments;
	/**
	 * Map of segments to the object above them or to <code>null</code> if that object is the playing field.
	 */
	private Map<Line, ConvexObject> segToObj;
	private boolean needsRebuild;
	private Set<Line> lines = new HashSet<Line>();
	
	/**
	 * Constructs a new vertical decomposition with
	 * the given bounding box.
	 * @param bounds A bounding box that all objects that will
	 *        ever be added will be contained in (strictly inside,
	 *        there will be no overlap with the edges).
	 */
	public VerticalDecomposition(Rectangle2D bounds){
		trapezoids = new ArrayList<Trapezoid>();
		searchStructure = new ArrayList<DecompVertex>();
		objects = new ArrayList<ConvexObject>();
		orientedSegments = new ArrayList<Line2D>();
		segToObj = new HashMap<Line, ConvexObject>();
		
		this.bounds = bounds;
		initializeDecomposition();
	}
	
	public Set<? extends Line2D> getLines(){
		return lines;
	}
	
	public Line2D getLastLine(){
		return orientedSegments.isEmpty() ? null : orientedSegments.get(orientedSegments.size() - 1);
	}
	
	/**
	 * Clears all structures except of the objects,
	 * and initializes a blank vertical decomposition
	 * with a bounding box trapezoid and a corresponding search structure vertex.
	 */
	private void initializeDecomposition(){
		trapezoids.clear();
		searchStructure.clear();
		orientedSegments.clear();
		segToObj.clear();
		Point2D botLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
		Point2D botRight = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
		Point2D topLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
		Point2D topRight = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
		
		Line2D botSegment = new Line(botLeft, botRight);
		Line2D topSegment = new Line(topLeft, topRight);

		Trapezoid initialTrapezoid = new Trapezoid(topLeft, botRight, botSegment, topSegment);
		initialTrapezoid.addLeftPoint(botLeft);
		initialTrapezoid.addRightPoint(topRight);
		DecompVertex initialVertex =  new DecompVertex(initialTrapezoid);
		trapezoids.add(initialTrapezoid);
		initialTrapezoid.computeDecompLines();
		searchStructure.add(initialVertex);
	}
	
	/**
	 * Adds a new convex object to this vertical decomposition.
	 * @param obj The convex object to add.
	 */
	public void addObject(ConvexObject obj){
		if(!objects.contains(obj)){
			objects.add(obj);
			needsRebuild = true;
		}
	}
	
	/**
	 * Adds all segments of a given object to the vertical decomposition, if the object is part of the decomposition.
	 * @param obj The object whose segments to add to the vertical decomposition.
	 */
	private void decomposeObject(ConvexObject obj){
		if(!objects.contains(obj)){
			return;
		}
		List<Point2D> points = obj.getPoints();
		for(int i = 0; i < points.size(); i++){
			Point2D p1 = points.get(i), p2 = points.get((i + 1) % points.size());
			
			Line2D segment = new Line(p1, p2);
			addSegment(segment, obj);
		}
	}
	
	/**
	 * Removes a convex object from this vertical decomposition.
	 * @param obj The convex object to remove.
	 */
	public void removeObject(ConvexObject obj){
		objects.remove(obj);
		needsRebuild = true;
	}
	
	public boolean needsRebuild(){
		return needsRebuild;
	}
	
	/**
	 * Rebuilds the vertical composition to match the
	 * current set of stored convex objects.
	 */
	public void rebuild(){
		trapezoids.clear();
		searchStructure.clear();
		initializeDecomposition();
		for(ConvexObject obj : objects){
			decomposeObject(obj);
		}
		needsRebuild = false;
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
		return segToObj.get(queryTrapezoid(x,y).botSegment);
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
		return searchStructure.get(0).queryPoint(new Point2D.Double(x,y));
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
	 * Adds a line segment belonging to an object to the vertical decomposition and updates the structures.
	 * @param seg The line segment to add to the decomposition.
	 * @param obj The object that the segment belongs to.
	 */
	public void addSegment(Line2D seg, ConvexObject obj){
		try{
			Thread.sleep(100);
		}catch(InterruptedException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(this){
			Point2D p1 = seg.getP1(), p2 = seg.getP2();
			Point2D leftp = Double.compare(p1.getX(), p2.getX()) == 0 ? 
				Double.compare(p1.getY(), p2.getY()) <= 0 ? p1 : p2
					: Double.compare(p1.getX(), p2.getX()) < 0 ? p1 : p2;
			Point2D rightp = leftp.equals(p1) ? p2 : p1;

			Line orientedSegment = new Line(leftp, rightp);

			Trapezoid start = queryTrapezoid(orientedSegment.getP1());
			Trapezoid end = queryTrapezoid(orientedSegment.getP2());

			orientedSegments.add(orientedSegment);
			lines.add(orientedSegment);
			ConvexObject toPut = p1.getX() < p2.getX() ? obj : null;
			segToObj.put(orientedSegment, toPut);

			if(start.equals(end)){
				handleSingleIntersectedTrapezoid(orientedSegment);
			}else{
				handleMultipleIntersectedTrapezoids(orientedSegment);
			}
		}
	}
	
	/**
	 * Adds a segment that only crosses one trapezoid inside the decomposition.
	 * @param orientedSegment The segment to add to the decomposition.
	 */
	private void handleSingleIntersectedTrapezoid(Line2D orientedSegment){
		Point2D leftp = orientedSegment.getP1(), rightp = orientedSegment.getP2();
		
		Trapezoid start = queryTrapezoid(leftp);
		//Segment is contained entirely inside 1 trapezoid.
		boolean topBotExist = leftp.getX() != rightp.getX();
		boolean rightExists = rightp.getX() != start.rightPoints.get(0).getX();
		if(topBotExist){//Not a vertical segment
			Trapezoid top = new Trapezoid(leftp, rightp, orientedSegment, start.topSegment);
			Trapezoid bottom = new Trapezoid(leftp, rightp, start.botSegment, orientedSegment);
			if(rightExists){
				//completely inside.
				Trapezoid left = new Trapezoid(start.leftPoints, leftp, start.botSegment, start.topSegment);
				Trapezoid right = new Trapezoid(rightp, start.rightPoints, start.botSegment, start.topSegment);
				
				//Add neighbours.
				left.addNeighbour(top);
				left.addNeighbour(bottom);
				right.addNeighbour(top);
				right.addNeighbour(bottom);
				top.addNeighbour(left);
				top.addNeighbour(right);
				bottom.addNeighbour(left);
				bottom.addNeighbour(right);
				for(Trapezoid neib : start.getNeighbours()){
					if(start.leftPoints.get(0).getX() == neib.rightPoints.get(0).getX()){
						left.addNeighbour(neib);
						neib.addNeighbour(left);
					}
					if(start.rightPoints.get(0).getX() == neib.leftPoints.get(0).getX()){
						right.addNeighbour(neib);
						neib.addNeighbour(right);
					}
				}
				
				//Update trapezoid list and search structure.
				
				DecompVertex vertex = start.getDecompVertex();
				start.freeNeighbours();
				start.setDecompVertex(null);
				
				DecompVertex topVertex = new DecompVertex(top);
				DecompVertex botVertex = new DecompVertex(bottom);
				DecompVertex leftVertex = new DecompVertex(left);
				DecompVertex rightVertex = new DecompVertex(right);
				
				DecompVertex segmentVertex = new DecompVertex(topVertex, botVertex, orientedSegment);
				DecompVertex rightPointVertex = new DecompVertex(segmentVertex, rightVertex, rightp);
				
				vertex.setType(1);
				vertex.setTrapezoid(null);
				vertex.setPoint(leftp);
				vertex.setLeftChild(leftVertex);
				vertex.setRightChild(rightPointVertex);
				
				trapezoids.remove(start);
				trapezoids.add(top);
				trapezoids.add(bottom);
				trapezoids.add(left);
				trapezoids.add(right);
				return;
			}
			else{
				//Segment ends on the right border.
				Trapezoid left = new Trapezoid(start.leftPoints, leftp, start.botSegment, start.topSegment);
				
				left.addNeighbour(top);
				left.addNeighbour(bottom);
				top.addNeighbour(left);
				bottom.addNeighbour(left);
				
				//Attribute the right bounding points to top and bottom.
				for (Point2D p : start.rightPoints){
					if(orientedSegment.relativeCCW(p) <= 0){
						top.addRightPoint(p);
					}
					if(orientedSegment.relativeCCW(p) >= 0){
						bottom.addRightPoint(p);
					}
				}
				//find neighbours of left, top, and bottom.
				for(Trapezoid neib : start.getNeighbours()){
					boolean flagTop = false;
					boolean flagBot = false;
					for(Line2D decompLine : neib.getDecompLines()){
						if(flagTop) break;
						for(Line2D topDecomp : top.getDecompLines()){
							if(topDecomp.intersectsLine(decompLine)){
								top.addNeighbour(neib);
								neib.addNeighbour(top);
								flagTop = true;
								break;
							}
						}
					}
					for(Line2D decompLine : neib.getDecompLines()){
						if(flagBot) break;
						for(Line2D botDecomp : bottom.getDecompLines()){
							if(botDecomp.intersectsLine(decompLine)){
								bottom.addNeighbour(neib);
								neib.addNeighbour(bottom);
								flagBot = true;
								break;
							}
						}
					}
					if(start.leftPoints.get(0).getX() == neib.rightPoints.get(0).getX()){
						left.addNeighbour(neib);
						neib.addNeighbour(left);
					}
					//Add rightp to the left bounding points of neighbours if they belong there.
					if(!neib.leftPoints.contains(rightp)){
						for(Line2D decompLine : neib.getDecompLines()){
							if(decompLine.relativeCCW(rightp) == 0 || decompLine.getP1().equals(rightp) || decompLine.getP2().equals(rightp)){
								neib.leftPoints.add(rightp);
							}
						}
					}
				}
				
				//Update trapezoid list and search structure.
				DecompVertex vertex = start.getDecompVertex(); 
				start.setDecompVertex(null);
				start.freeNeighbours();
				DecompVertex topVertex = new DecompVertex(top);
				DecompVertex botVertex = new DecompVertex(bottom);
				DecompVertex leftVertex = new DecompVertex(left);
				
				DecompVertex segmentVertex = new DecompVertex(topVertex, botVertex, orientedSegment);
				
				vertex.setType(1);
				vertex.setTrapezoid(null);
				vertex.setPoint(leftp);
				vertex.setLeftChild(leftVertex);
				vertex.setRightChild(segmentVertex);
				
				trapezoids.remove(start);
				trapezoids.add(top);
				trapezoids.add(bottom);
				trapezoids.add(left);
				return;
			}
			//If one of the endpoints is on the left end, then the segment would be considered as a 2-trapezoid intersection.
		}else{//vertical segment
			if(rightExists){
				//Inside the trapezoid.
				//Create left and right trapezoids.
				Trapezoid left = new Trapezoid(start.leftPoints, leftp, start.botSegment, start.topSegment);
				Trapezoid right = new Trapezoid(rightp, start.rightPoints, start.botSegment, start.topSegment);
				
				left.addRightPoint(rightp);
				right.addLeftPoint(leftp);
				
				//Assign neighbours.
				left.addNeighbour(right);
				right.addNeighbour(left);
				for(Trapezoid neib : start.getNeighbours()){
					if(start.leftPoints.get(0).getX() == neib.rightPoints.get(0).getX()){
						left.addNeighbour(neib);
						neib.addNeighbour(left);
					}
					if(start.rightPoints.get(0).getX() == neib.leftPoints.get(0).getX()){
						right.addNeighbour(neib);
						neib.addNeighbour(right);
					}
				}
				
				//Update trapezoid structure and update the search structure.
				trapezoids.remove(start);
				start.freeNeighbours();
				trapezoids.add(left);
				trapezoids.add(right);
				
				DecompVertex vertex = start.getDecompVertex();
				DecompVertex leftVertex = new DecompVertex(left);
				DecompVertex rightVertex = new DecompVertex(right);
									
				vertex.setType(2);
				vertex.setTrapezoid(null);
				vertex.setSegment(orientedSegment);
				vertex.setLeftChild(leftVertex);
				vertex.setRightChild(rightVertex);
				return;
			}else{
				//On the right border.
				//Add leftp and rightp to the right bounding points, and to the left bounding points of eligible neighbours.
				start.addRightPoint(leftp);
				start.addRightPoint(rightp);
				for(Trapezoid neib : start.getNeighbours()){
					if(!neib.leftPoints.contains(leftp)){
						for(Line2D decompLine : neib.getDecompLines()){
							if(decompLine.relativeCCW(leftp) == 0 || decompLine.getP1().equals(leftp) || decompLine.getP2().equals(leftp)){
								neib.leftPoints.add(leftp);
							}
						}	
					}
					if(!neib.leftPoints.contains(rightp)){
						for(Line2D decompLine : neib.getDecompLines()){
							if(decompLine.relativeCCW(rightp) == 0 || decompLine.getP1().equals(rightp) || decompLine.getP2().equals(rightp)){
								neib.leftPoints.add(rightp);
							}
						}
					}
				}
				
				//Remove neighbours to the right that might not share a side anymore
				List<Trapezoid> removeNeighbours = new ArrayList<Trapezoid>();
				for(Trapezoid neib : start.getNeighbours()){
					if(neib.leftPoints.get(0).getX() == start.rightPoints.get(0).getX() && 
							leftp.equals(start.botSegment.getP2()) && rightp.equals(start.topSegment.getP2())){
						removeNeighbours.add(neib);
						continue;
					}
				}
				for(Trapezoid neib : removeNeighbours){
					start.removeNeighbour(neib);
				}
				//No need to update search structure, no new trapezoids should be created.
				return;
			}
			//Cannot be on the left border, as that would be accounted to the neighbour to the left, and cannot bisect either.
		}
	}
	
	/**
	 * Adds a segment that crosses more than one trapezoid inside the decomposition.
	 * @param orientedSegment The segment to add to the decomposition.
	 */
	public void handleMultipleIntersectedTrapezoids(Line2D orientedSegment){
		Point2D leftp = orientedSegment.getP1(), rightp = orientedSegment.getP2();
		Trapezoid start = queryTrapezoid(leftp);
		Trapezoid end = queryTrapezoid(rightp);
		//The top, bottom, left, and right trapezoids to be used when splitting the trapezoids.
		Trapezoid top = null, bot = null, left = null, right = null;
		//The trapezoids that are intersected by the segment.
		Queue<Trapezoid> intersected = findIntersectedTrapezoids(orientedSegment);
		
		//Add leftp to the bounding points of any neighbouring trapezoids that might not have it yet.
		if(leftp.getX() == start.rightPoints.get(0).getX()){
			if(!start.rightPoints.contains(leftp)){
				start.addRightPoint(leftp);
			}
			for(Trapezoid neib : start.getNeighbours()){
				if(!neib.leftPoints.contains(leftp)){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.relativeCCW(leftp) == 0 || decompLine.getP1().equals(leftp) || decompLine.getP2().equals(leftp)){
							neib.leftPoints.add(leftp);
						}
					}
				}
			}
		}
		//Add rightp to the bounding points of any neighbouring trapezoids that might not have it yet.
		if(rightp.getX() == end.rightPoints.get(0).getX()){
			if(!end.rightPoints.contains(rightp)){
				end.addRightPoint(rightp);
			}
			for(Trapezoid neib : end.getNeighbours()){
				if(!neib.leftPoints.contains(rightp)){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.relativeCCW(rightp) == 0 || decompLine.getP1().equals(rightp) || decompLine.getP2().equals(rightp)){
							neib.leftPoints.add(rightp);
						}
					}
				}
			}
		}
		//Vertical segment whose endpoints are considered part of 2 different trapezoids. The traps should be divided by a segment i.e. one above the other. 
		if(intersected.size() == 0){
			//End is the trapezoid whose right side the segment is on.
			//Add leftp and rightp to the right bounding points, and to the left bounding points of eligible neighbours.
			if(!end.rightPoints.contains(leftp)){
				end.addRightPoint(leftp);
			}
			if(!end.rightPoints.contains(rightp)){
				end.addRightPoint(rightp);
			}
			for(Trapezoid neib : end.getNeighbours()){
				if(!neib.leftPoints.contains(leftp)){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.relativeCCW(leftp) == 0 || decompLine.getP1().equals(leftp) || decompLine.getP2().equals(leftp)){
							neib.leftPoints.add(leftp);
						}
					}	
				}
				if(!neib.leftPoints.contains(rightp)){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.relativeCCW(rightp) == 0 || decompLine.getP1().equals(rightp) || decompLine.getP2().equals(rightp)){
							neib.leftPoints.add(rightp);
						}
					}
				}
			}
			//Remove neighbours to the right that might not share a side anymore
			//Impossible to close of only a part, for then convexity will be broken.
			List<Trapezoid> removeNeighbours = new ArrayList<Trapezoid>();
			for(Trapezoid neib : end.getNeighbours()){
				if(neib.leftPoints.get(0).getX() == end.rightPoints.get(0).getX() && 
						leftp.equals(end.botSegment.getP2()) && rightp.equals(end.topSegment.getP2())){
					removeNeighbours.add(neib);
					continue;
				}
			}
			for(Trapezoid neib : removeNeighbours){
				end.removeNeighbour(neib);
				neib.removeNeighbour(end);
			}
			//No need to update search structure, no new trapezoids should be created.
			return;
		}
		//The current trapezoid that is being split up.
		Trapezoid current = null;
		//The first (leftmost) trapezoid that the segment intersects.
		Trapezoid first = intersected.peek();
		//The vertices for the running bottom and top trapezoids.
		DecompVertex botVertex = null, topVertex = null;
		while(!intersected.isEmpty()){
			current = intersected.remove();
			//First trapezoid. If the leftpoint is at the left border, no need to split horizontally.
			if(current == first && leftp.getX() != current.leftPoints.get(0).getX() && leftp.getX() != current.rightPoints.get(0).getX()){
				//Split current into left and right of leftp.
				left = new Trapezoid(current.leftPoints, leftp, current.botSegment, current.topSegment);
				right = new Trapezoid(leftp, current.rightPoints, current.botSegment, current.topSegment);
				
				//Add neighbours to left and right.
				left.addNeighbour(right);
				right.addNeighbour(left);
				for(Trapezoid neib : current.neighbours){
					if(current.leftPoints.get(0).getX() == neib.rightPoints.get(0).getX()){
						left.addNeighbour(neib);
						neib.addNeighbour(left);
					}
					if(current.rightPoints.get(0).getX() == neib.leftPoints.get(0).getX()){
						right.addNeighbour(neib);
						neib.addNeighbour(right);
					}
				}
				
				//Update search structure
				DecompVertex vertex = current.getDecompVertex();
				DecompVertex leftVertex = new DecompVertex(left);
				DecompVertex rightVertex = new DecompVertex(right);
				
				current.freeNeighbours();
				current.setDecompVertex(null);
				vertex.setTrapezoid(null);
				vertex.setType(1);
				vertex.setPoint(leftp);
				vertex.setLeftChild(leftVertex);
				vertex.setRightChild(rightVertex);
				
				//Update trapezoids
				trapezoids.remove(current);
				trapezoids.add(left);
				trapezoids.add(right);
				//Right has to be bisected.
				current = right;
			}
			//Last trapezoid. If rightp is on the right border, no need to split.
			if(intersected.isEmpty() && rightp.getX() != current.leftPoints.get(0).getX() && rightp.getX() != current.rightPoints.get(0).getX()){
				//Split current into left and right of rightp.
				left = new Trapezoid(current.leftPoints, rightp, current.botSegment, current.topSegment);
				right = new Trapezoid(rightp, current.rightPoints, current.botSegment, current.topSegment);
				
				//Add neighbours to left and right.
				left.addNeighbour(right);
				right.addNeighbour(left);
				for(Trapezoid neib : current.neighbours){
					if(current.leftPoints.get(0).getX() == neib.rightPoints.get(0).getX()){
						left.addNeighbour(neib);
						neib.addNeighbour(left);
					}
					if(current.rightPoints.get(0).getX() == neib.leftPoints.get(0).getX()){
						right.addNeighbour(neib);
						neib.addNeighbour(right);
					}
				}
				//Update search structure.
				DecompVertex vertex = current.getDecompVertex();
				current.freeNeighbours();
				current.setDecompVertex(null);
				vertex.setTrapezoid(null);
				
				DecompVertex leftVertex = new DecompVertex(left);
				DecompVertex rightVertex = new DecompVertex(right);
				
				vertex.setType(1);
				vertex.setTrapezoid(null);
				vertex.setPoint(rightp);
				vertex.setLeftChild(leftVertex);
				vertex.setRightChild(rightVertex);
				//Update trapezoids list.
				trapezoids.remove(current);
				trapezoids.add(left);
				trapezoids.add(right);
				//Left has to be bisected.
				current = left;
			}
			//Split along the segment.
			if(top == null){
				//Create a new top trapezoid
				top = new Trapezoid(new ArrayList<Point2D>(), new ArrayList<Point2D>(), orientedSegment, current.topSegment);
				//Assign left points to top.
				for(Point2D p : current.leftPoints){
					if(orientedSegment.relativeCCW(p) <= 0){
						top.addLeftPoint(p);
					}
				}
				//Assign left neighbours to top.
				for(Trapezoid neib : current.getNeighbours()){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.getP1().getX() != current.leftPoints.get(0).getX())
							continue;
						if(orientedSegment.relativeCCW(decompLine.getP2()) < 0){//P2 is the top point
							top.addNeighbour(neib);
							neib.addNeighbour(top);
						}
					}
				}
				topVertex = new DecompVertex(top);
			}
			if(bot == null){
				//Create a new bot trapezoid.
				bot = new Trapezoid(new ArrayList<Point2D>(), new ArrayList<Point2D>(), current.botSegment, orientedSegment);
				//Assign left points to bot.
				for(Point2D p : current.leftPoints){
					if(orientedSegment.relativeCCW(p) >= 0){
						bot.addLeftPoint(p);
					}
				}
				//Assign left neighbours to bot.
				for(Trapezoid neib : current.getNeighbours()){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.getP1().getX() != current.leftPoints.get(0).getX())
							continue;
						if(orientedSegment.relativeCCW(decompLine.getP1()) > 0){//P1 is the bottom point
							bot.addNeighbour(neib);
							neib.addNeighbour(bot);
						}
					}
				}
				botVertex = new DecompVertex(bot);
			}
			//Distribute right points among top and bot.
			for(Point2D p : current.rightPoints){
				if(orientedSegment.relativeCCW(p) <= 0){
					top.addRightPoint(p);
				}
				if(orientedSegment.relativeCCW(p) >= 0){
					bot.addRightPoint(p);
				}
			}
			//Assign right neighbours to top if it is bound on the right.
			if(top.rightPoints.size() > 0){
				for(Trapezoid neib : current.getNeighbours()){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.getP1().getX() != current.rightPoints.get(0).getX())
							continue;
						if(orientedSegment.relativeCCW(decompLine.getP2()) < 0){//P2 is the top point
							top.addNeighbour(neib);
							neib.addNeighbour(top);
						}
					}
				}
			}
			if(bot.rightPoints.size() > 0){
				for(Trapezoid neib : current.getNeighbours()){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.getP1().getX() != current.rightPoints.get(0).getX())
							continue;
						if(orientedSegment.relativeCCW(decompLine.getP1()) > 0){//P1 is the bottom point
							
							bot.addNeighbour(neib);
							neib.addNeighbour(bot);
						}
					}
				}
			}
			
			//Update search structure.
			DecompVertex vertex = current.getDecompVertex();
			current.freeNeighbours();
			current.setDecompVertex(null);
			vertex.setTrapezoid(null);
			
			vertex.setType(2);
			vertex.setTrapezoid(null);
			vertex.setSegment(orientedSegment);
			vertex.setLeftChild(topVertex);
			vertex.setRightChild(botVertex);
			//Update trapezoid list
			trapezoids.remove(current);
			if(top.rightPoints.size() > 0){
				trapezoids.add(top);
				top = null;
			}
			if(bot.rightPoints.size() > 0){
				trapezoids.add(bot);
				bot = null;
			}
		}
	}
	
	/**
	 * Computes a list of trapezoids intersected by a line segment, excluding the leftmost and rightmost intersected trapezoids.
	 * @param seg The line segment.
	 * @return A list of trapezoids intersected by a line segment, excluding the leftmost and rightmost intersected trapezoids.
	 */
	public Queue<Trapezoid> findIntersectedTrapezoids(Line2D seg){
		Queue<Trapezoid>intersectedTraps = new LinkedList<Trapezoid>();
		Trapezoid start = queryTrapezoid(seg.getP1());
		
		Queue<Trapezoid> q = new LinkedList<Trapezoid>();
		Set<Trapezoid> visited = new HashSet<Trapezoid>();
		q.add(start);
		while(!q.isEmpty()){
			Trapezoid current = q.remove();
			if(current.intersectsSegment(seg)){
				if(!intersectedTraps.contains(current)){
					int cnt = 0;
					for(Line2D decompLine : current.getDecompLines()){
						if(decompLine.intersectsLine(seg) || seg.relativeCCW(decompLine.getP1()) == 0 || seg.relativeCCW(decompLine.getP2()) == 0){
							cnt ++;
						}
					}
					if(cnt > 1 || current.pointInside(seg.getP1()) || current.pointInside(seg.getP2())){
						intersectedTraps.add(current);
					}
				}
				for(Trapezoid neib : current.getNeighbours()){
					if(!intersectedTraps.contains(neib) && neib.intersectsSegment(seg)){
						if(!visited.contains(neib) && neib.leftPoints.get(0).getX()== current.rightPoints.get(0).getX()){
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
		// TODO Auto-generated method stub
		
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
			this(0 , null, null, trapezoid, null, null);
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
			}else if(type == 1 && !point.equals(null)){
				if(query.getX() <= point.getX()){
					return left.queryPoint(query);
				} else {
					return right.queryPoint(query);
				}
			}else if(type == 2 && !segment.equals(null)){
				Point2D leftp = Double.compare(segment.getP1().getX(), segment.getP2().getX()) == 0 ? 
								Double.compare(segment.getP1().getY(), segment.getP2().getY()) <= 0 ? segment.getP1() : segment.getP2() 
								: Double.compare(segment.getP1().getX(), segment.getP2().getX()) < 0 ? segment.getP1() : segment.getP2();
				Point2D rightp = leftp.equals(segment.getP1()) ? segment.getP2() : segment.getP1();
				
				Line2D orientedSegment = new Line(leftp, rightp);
				return orientedSegment.relativeCCW(query) <= 0 ? left.queryPoint(query) : right.queryPoint(query); //Can be subject to change.
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
		private Line2D botSegment, topSegment;
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
		public Trapezoid(Point2D left, Point2D right, Line2D botSegment, Line2D topSegment){
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
		public Trapezoid(List<Point2D> left, Point2D right, Line2D botSegment, Line2D topSegment){
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
		public Trapezoid(Point2D left, List<Point2D>  right, Line2D botSegment, Line2D topSegment){
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
		public Trapezoid(List<Point2D> left, List<Point2D> right, Line2D botSegment, Line2D topSegment){
			this.botSegment = botSegment;
			this.topSegment = topSegment;
			this.leftPoints = left;
			this.rightPoints = right;

			this.neighbours = new ArrayList<Trapezoid>();
			this.vertex = null;
			
			if(!leftPoints.isEmpty() && !rightPoints.isEmpty()){
				computeDecompLines();
			}
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
		public void removeNeighbour (Trapezoid neighbour){
			while(neighbours.contains(neighbour)){
				neighbours.remove(neighbour);
			}
		}
		
		/**
		 * Removes this trapezoid from the neighbour lists of all neighbours and clears the neighbour list.
		 * Useful for when we want to delete a Trapezoid.
		 */
		public void freeNeighbours (){
			for(Trapezoid t : neighbours){
				t.removeNeighbour(this);
			}
			this.neighbours.clear();
		}
		
		/**
		 * Gets the vertical decomposition lines of this trapezoid.
		 * @return The vertical decomposition lines of this trapezoid.
		 */
		public List<Line2D> getDecompLines(){
			return decompLines;
		}
		
		/**
		 * Computes and sets the decomposition lines related to this trapezoid.
		 */
		public void computeDecompLines(){
			Point2D botLeft = botSegment.getP1(), botRight = botSegment.getP2();
			Point2D topLeft = topSegment.getP1(), topRight = topSegment.getP2();
			List<Line2D> verticalLines = new ArrayList<Line2D>(); 
			if(leftPoints.size() > 0){//Draw vertical line between top and bottom on the left
				double xRatioTop = (leftPoints.get(0).getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
				double xRatioBot = (leftPoints.get(0).getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
				verticalLines.add(new Line(
					new Point2D.Double(leftPoints.get(0).getX(), xRatioBot * botRight.getY() + (1 - xRatioBot) * botLeft.getY()),
					new Point2D.Double(leftPoints.get(0).getX(), xRatioTop * topRight.getY() + (1 - xRatioTop) * topLeft.getY()))
				);
			}
			
			if(rightPoints.size() > 0){//Draw vertical line between top and bottom on the right
				double xRatioTop = Math.abs((rightPoints.get(0).getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX()));
				double xRatioBot = Math.abs((rightPoints.get(0).getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX()));
				verticalLines.add(new Line(
					new Point2D.Double(rightPoints.get(0).getX(), xRatioBot * botRight.getY() + (1 - xRatioBot) * botLeft.getY()),
					new Point2D.Double(rightPoints.get(0).getX(), xRatioTop * topRight.getY() + (1 - xRatioTop) * topLeft.getY()))
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
			if(botSegment.relativeCCW(p) < 0 && topSegment.relativeCCW(p) > 0 && p.getX() > leftPoints.get(0).getX() && p.getX() < rightPoints.get(0).getX()){//TODO: change up relatives?
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
	}
	
	/**
	 * A line instance with equality based
	 * on its end points.
	 * @author Roan
	 */
	public class Line extends Line2D{
		/**
		 * First end point of the line.
		 */
		private Point2D p1;
		/**
		 * Second end point of the line.
		 */
		private Point2D p2;
		
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
	}
}
