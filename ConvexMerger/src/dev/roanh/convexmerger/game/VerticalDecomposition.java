package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Vertical decomposition of a plane containing non-overlapping
 * convex objects. The decomposition supports efficient modifications
 * and querying of the convex objects.
 * @author Roan
 * @author Emu
 */
public class VerticalDecomposition{
	
	/**
	 * The objects that are decomposed.
	 */
	private List<ConvexObject> objects;
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
		
		this.bounds = bounds;
		Point2D topLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
		Point2D topRight = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
		Point2D botLeft  = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
		Point2D botRight = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
		
		Trapezoid initialTrapezoid = new Trapezoid(topLeft, botRight, botLeft, botRight, topLeft, topRight);
		initialTrapezoid.addLeftPoint(botLeft);
		initialTrapezoid.addRightPoint(topRight);
		DecompVertex initialVertex =  new DecompVertex(initialTrapezoid);
		initialTrapezoid.setDecompVertex(initialVertex);
		trapezoids.add(initialTrapezoid);
		searchStructure.add(initialVertex);
	}
	
	/**
	 * Adds a new convex object to this vertical decomposition.
	 * @param obj The convex object to add.
	 */
	public void addObject(ConvexObject obj){
		objects.add(obj);
		List<Point2D> points = obj.getPoints();
		for(int i = 0; i < points.size(); i++){
			Point2D p1 = points.get(i), p2 = points.get((i+1) % points.size());
			Point2D leftp = Double.compare(p1.getX(), p2.getX()) == 0 ? 
					Double.compare(p1.getY(), p2.getY()) <= 0 ? p1 : p2
					: Double.compare(p1.getX(), p2.getX()) < 0 ? p1 : p2;
			Point2D rightp = leftp.equals(p1) ? p2 : p1;
			
			Line2D orientedSegment = new Line2D.Double(leftp, rightp);
			addSegment(orientedSegment, obj);
		}
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
		return searchStructure.get(0).queryPoint(new Point2D.Double(x, y)).getObject();
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
		
//		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY()));
		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY()));
//		lines.add(new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()));
		lines.add(new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY()));
		
		for(Trapezoid trap : trapezoids){
			lines.addAll(trap.getDecompLines());
		}
		
		return lines;
	}
	
	/**
	 * Adds a line segment belonging to an object to the vertical decomposition.
	 * @param seg The line segment to add to the decomposition.
	 * @param obj The object that the segment belongs to.
	 */
	public void addSegment(Line2D seg, ConvexObject obj){
		
		Point2D leftp = Double.compare(seg.getP1().getX(), seg.getP2().getX()) == 0 ? 
				Double.compare(seg.getP1().getY(), seg.getP2().getY()) <= 0 ? seg.getP1() : seg.getP2() 
				: Double.compare(seg.getP1().getX(), seg.getP2().getX()) < 0 ? seg.getP1() : seg.getP2();
		Point2D rightp = leftp.equals(seg.getP1()) ? seg.getP2() : seg.getP1();
		
		Trapezoid start = queryTrapezoid(leftp);
		Trapezoid end = queryTrapezoid(rightp);
		
		Line2D orientedSegment = new Line2D.Double(leftp, rightp);
		
		if(start.equals(end)){
			//Segment is contained entirely inside 1 trapezoid.
			boolean topBotExist = leftp.getX() != rightp.getX();
			boolean leftExists = leftp.getX() != start.leftPoints.get(0).getX();
			boolean rightExists = rightp.getX() != start.rightPoints.get(0).getX();
			if(topBotExist){//Not a vertical segment
				Trapezoid top = new Trapezoid(leftp, rightp, leftp, rightp, start.topLeft, start.topRight);
				Trapezoid bottom = new Trapezoid(leftp, rightp, start.botLeft, start.botRight, leftp, rightp);
				if(leftExists && rightExists){
					//completely inside.
					Trapezoid left = new Trapezoid(start.leftPoints, leftp, start.botLeft, start.botRight, start.topLeft, start.topRight);
					Trapezoid right = new Trapezoid(rightp, start.rightPoints, start.botLeft, start.botRight, start.topLeft, start.topRight);
					
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
					trapezoids.remove(start);
					start.freeNeighbours();
					trapezoids.add(top);
					trapezoids.add(bottom);
					trapezoids.add(left);
					trapezoids.add(right);
					
					DecompVertex vertex = start.getDecompVertex();
					DecompVertex topVertex = new DecompVertex(top);
					DecompVertex botVertex = new DecompVertex(bottom);
					DecompVertex leftVertex = new DecompVertex(left);
					DecompVertex rightVertex = new DecompVertex(right);
					top.setDecompVertex(topVertex);
					bottom.setDecompVertex(botVertex);
					left.setDecompVertex(leftVertex);
					right.setDecompVertex(rightVertex);
					
					DecompVertex segmentVertex = new DecompVertex(topVertex, botVertex, orientedSegment);
					DecompVertex rightPointVertex = new DecompVertex(segmentVertex, rightVertex, rightp);
					
					vertex.setType(1);
					vertex.setTrapezoid(null);
					vertex.setPoint(leftp);
					vertex.setLeftChild(leftVertex);
					vertex.setRightChild(rightPointVertex);
					return;
				}
				if(leftExists){
					//Segment ends on the right border.
					Trapezoid left = new Trapezoid(start.leftPoints, leftp, start.botLeft, start.botRight, start.topLeft, start.topRight);
					left.addNeighbour(top);
					left.addNeighbour(bottom);
					top.addNeighbour(left);
					bottom.addNeighbour(left);
					
					//Attribute the right bounding points to top and bottom.
					for (Point2D p : start.rightPoints){
						if(orientedSegment.relativeCCW(p) >= 0){
							top.addRightPoint(p);
						}
						if(orientedSegment.relativeCCW(p) <= 0){
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
						//Add leftp and rightp to the left bounding points of neighbours if they belong there.
						for(Line2D decompLine : neib.getDecompLines()){
							if(decompLine.relativeCCW(leftp) == 0 || decompLine.getP1().equals(leftp) || decompLine.getP2().equals(leftp)){
								neib.leftPoints.add(leftp);
							}
							if(decompLine.relativeCCW(rightp) == 0 || decompLine.getP1().equals(rightp) || decompLine.getP2().equals(rightp)){
								neib.leftPoints.add(rightp);
							}
						}
					}
					//Update trapezoid list and search structure.
					trapezoids.remove(start);
					start.freeNeighbours();
					trapezoids.add(top);
					trapezoids.add(bottom);
					trapezoids.add(left);
					
					DecompVertex vertex = start.getDecompVertex();
					DecompVertex topVertex = new DecompVertex(top);
					DecompVertex botVertex = new DecompVertex(bottom);
					DecompVertex leftVertex = new DecompVertex(left);
					top.setDecompVertex(topVertex);
					bottom.setDecompVertex(botVertex);
					left.setDecompVertex(leftVertex);
					
					DecompVertex segmentVertex = new DecompVertex(topVertex, botVertex, orientedSegment);
					
					vertex.setType(1);
					vertex.setTrapezoid(null);
					vertex.setPoint(leftp);
					vertex.setLeftChild(leftVertex);
					vertex.setRightChild(segmentVertex);
					return;
				}
				//If one of the endpoints is on the left end, then the segment would be considered as a 2-trapezoid intersection.
			}else{//vertical segment
				if(leftExists && rightExists){
					//Inside the trapezoid.
					Trapezoid left = new Trapezoid(start.leftPoints, leftp, start.botLeft, start.botRight, start.topLeft, start.topRight);
					Trapezoid right = new Trapezoid(rightp, start.rightPoints, start.botLeft, start.botRight, start.topLeft, start.topRight);
					
					//Assign neighbours.
					left.addRightPoint(rightp);
					right.addLeftPoint(leftp);
					
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
					left.setDecompVertex(leftVertex);
					right.setDecompVertex(rightVertex);
					
					DecompVertex segmentVertex = new DecompVertex(leftVertex, rightVertex, orientedSegment);
					DecompVertex rightPointVertex = new DecompVertex(segmentVertex, rightVertex, rightp);
					
					vertex.setType(1);
					vertex.setTrapezoid(null);
					vertex.setPoint(leftp);
					vertex.setLeftChild(leftVertex);
					vertex.setRightChild(rightPointVertex);
					return;
				}
				if(leftExists){
					//On the right border.
					
//					List<Point2D> toRemove = new ArrayList<Point2D>();
//					for(Point2D p : start.rightPoints){
//						if(orientedSegment.relativeCCW(p) == 0){
//							toRemove.add(p);
//						}
//					}
//					for(Point2D p : toRemove){
//						start.rightPoints.remove(p);
//					}
					//Add leftp and rightp to the right bounding points, and to the left boundign points of eligible neighbours.
					start.addRightPoint(leftp);
					start.addRightPoint(rightp);
					for(Trapezoid neib : start.getNeighbours()){
						for(Line2D decompLine : neib.getDecompLines()){
							if(decompLine.contains(leftp) || decompLine.getP1().equals(leftp) || decompLine.getP2().equals(leftp)){
								neib.leftPoints.add(leftp);
							}
							if(decompLine.contains(rightp) || decompLine.getP1().equals(rightp) || decompLine.getP2().equals(rightp)){
								neib.leftPoints.add(rightp);
							}
						}
					}
					
					//Remove neighbours to the right that might not share a side anymore
					List<Trapezoid> removeNeighbours = new ArrayList<Trapezoid>();
					for(Trapezoid neib : start.getNeighbours()){
						boolean remove = neib.rightPoints.get(0).getX() == start.rightPoints.get(0).getX();
						if(!remove){
							continue;
						}
						
						if(leftp.equals(start.botRight) && rightp.equals(start.topRight)){
							removeNeighbours.add(neib);
							continue;
						}
						
						for(Line2D decompLine : neib.getDecompLines()){
							boolean flag = false;
							for(Line2D decompStart : start.getDecompLines()){
								if(flag){
									break;
								}
								if(decompStart.getP1().getX() == start.rightPoints.get(0).getX()){
									Line2D topLine = new Line2D.Double(decompStart.getP1(), leftp);
									Line2D botLine = new Line2D.Double(rightp, decompStart.getP2());
									if(topLine.intersectsLine(decompLine) || botLine.intersectsLine(decompLine)){
										remove = false;
										flag = true;
										break;
									} 
								}
							}
						}
						if(remove){
							removeNeighbours.add(neib);
						}
					}
					for(Trapezoid neib : removeNeighbours){
						start.removeNeighbour(neib);
					}
					//No need to update search structure, no new trapezoids should be created.
					return;
				}
				//Cannot be on the left border, as that would be accounted to the neighbour to the left, and cannot bisect either.
				return;
			}
		}else{// Multiple trapezoids intersected by the segment.
			boolean leftExists = Double.compare(start.rightPoints.get(0).getX(), leftp.getX()) > 0;
			boolean rightBisected = Double.compare(end.rightPoints.get(0).getX(), rightp.getX()) == 0;
			//Linked list of the fully bisected trapezoids.
			Queue<Trapezoid> intersected = findIntersectedTrapezoids(orientedSegment);
			
			Trapezoid top = null, bot = null, left = null, right = null;
			Trapezoid oldTop = null, oldBot = null;
			if(leftExists){
				top = new Trapezoid(leftp, new ArrayList<Point2D>(), leftp, rightp, start.topLeft, start.topRight);
				bot = new Trapezoid(leftp, new ArrayList<Point2D>(), start.botLeft, start.botRight, leftp, rightp);
				left = new Trapezoid(start.leftPoints, leftp, start.botLeft, start.botRight, start.topLeft, start.topRight);
				
				for(Trapezoid neib : start.getNeighbours()){
					if(start.leftPoints.get(0).getX() == neib.rightPoints.get(0).getX()){
						left.addNeighbour(neib);
						neib.addNeighbour(left);
					}
				}
				left.addNeighbour(top);
				top.addNeighbour(left);
				left.addNeighbour(bot);
				bot.addNeighbour(left);
					
				//Assign right bounding points of start to top and bot.
				for (Point2D p : start.rightPoints){
					if(orientedSegment.relativeCCW(p) >= 0){
						top.addRightPoint(p);
					}
					if(orientedSegment.relativeCCW(p) <= 0){
						bot.addRightPoint(p);
					}			
				}
				
				if(top.rightPoints.size() > 0){
					for(Trapezoid neib : start.getNeighbours()){
						boolean flag = false;
						for(Line2D decompLine : neib.getDecompLines()){
							if(decompLine.getP1().getX() != start.rightPoints.get(0).getX())
								continue;
							if(flag) break;
							for(Line2D topDecomp : top.getDecompLines()){
								if(topDecomp.intersectsLine(decompLine)){
									top.addNeighbour(neib);
									neib.addNeighbour(top);
									flag = true;
									break;
								}
							}
						}
					}
				}
				if(bot.rightPoints.size() > 0){
					for(Trapezoid neib : start.getNeighbours()){
						boolean flag = false;
						for(Line2D decompLine : neib.getDecompLines()){
							if(decompLine.getP1().getX() != start.rightPoints.get(0).getX())
								continue;
							if(flag) break;
							for(Line2D topDecomp : bot.getDecompLines()){
								if(topDecomp.intersectsLine(decompLine)){
									bot.addNeighbour(neib);
									neib.addNeighbour(bot);
									flag = true;
									break;
								}
							}
						}
					}
				}
				
				DecompVertex topVertex = new DecompVertex(top);
				top.setDecompVertex(topVertex);
				DecompVertex botVertex = new DecompVertex(bot);
				bot.setDecompVertex(botVertex);
				
				DecompVertex vertex = start.getDecompVertex();
				vertex.setTrapezoid(null);
				start.freeNeighbours();
				trapezoids.remove(start);
				
				trapezoids.add(left);
				DecompVertex leftVertex = new DecompVertex(left);
				left.setDecompVertex(leftVertex);
				
				DecompVertex segmentVertex = new DecompVertex(topVertex, botVertex, orientedSegment);
				
				vertex.setType(1);
				vertex.setPoint(leftp);
				vertex.setLeftChild(leftVertex);
				vertex.setRightChild(segmentVertex);
				
				if(top.rightPoints.size() > 0){
					trapezoids.add(top);
					oldTop = top;
					top = null;
				}
				else{//Here to make decomp lines not crash, until the rest of the case is handled
					top.addRightPoint(start.rightPoints.get(0));
				}
				if(bot.rightPoints.size() > 0){
					trapezoids.add(bot);
					oldBot = bot;
					bot = null;
				}
				else{//Here to make decomp lines not crash
					bot.addRightPoint(start.rightPoints.get(0));
				}
				
			}else{
				//Only the leftpoint of the segment belongs to start.
				//Add leftp as a right bounding point of start, and as a left bounding point to relevant neighbours. 
				start.addRightPoint(leftp);
				for(Trapezoid neib : start.getNeighbours()){
					for(Line2D decompLine : neib.getDecompLines()){
						if(decompLine.contains(leftp) || decompLine.getP1().equals(leftp) || decompLine.getP2().equals(leftp)){
							neib.leftPoints.add(leftp);
						}
					}
				}
			}
				
			while(!intersected.isEmpty()){
				Trapezoid current = intersected.remove();
				
				if(top == null){
					assert oldTop == null || current.leftPoints.contains(oldTop.rightPoints.get(0)) || (oldTop.rightPoints.size() > 1 &&current.leftPoints.contains(oldTop.rightPoints.get(1))):
						"top\n" + current.leftPoints.toString() + " " + (oldTop != null ? oldTop.rightPoints.toString() : "null");
					top = new Trapezoid(new ArrayList<Point2D>(),new ArrayList<Point2D>(), leftp, leftp, leftp, leftp);
				}
				
				if(bot == null){
					assert oldBot == null || current.leftPoints.contains(oldBot.rightPoints.get(0)) || (oldBot.rightPoints.size() > 1 &&current.leftPoints.contains(oldBot.rightPoints.get(1))):
						"bot\n" + current.leftPoints.toString() + " " + (oldBot != null ? oldBot.rightPoints.toString() : "null");
					bot = new Trapezoid(new ArrayList<Point2D>(),new ArrayList<Point2D>(), leftp, leftp, leftp, leftp);
				}
				
			}
			//Right always exists
			
			if(rightBisected){
				
			}else{
				
			}
		}
		return;
	}
	
	/**
	 * Computes a list of trapezoids intersected by a line segment, excluding the leftmost and rightmost intersected trapezoids.
	 * @param seg The line segment.
	 * @return A list of trapezoids intersected by a line segment, excluding the leftmost and rightmost intersected trapezoids.
	 */
	public Queue<Trapezoid> findIntersectedTrapezoids(Line2D seg){
		Queue<Trapezoid>intersectedTraps = new LinkedList<Trapezoid>();
		
		Trapezoid start = queryTrapezoid(seg.getP1());
		Trapezoid end = queryTrapezoid(seg.getP2());
		
		Queue<Trapezoid> q = new LinkedList<Trapezoid>();
		q.add(start);
		while(!q.isEmpty()){
			Trapezoid current = q.remove();
			if(current.equals(end)){
				break;
			}
			if(current.intersectsSegment(seg)){
				if(current != start){
					intersectedTraps.add(current);
				}
				for(Trapezoid neib : current.getNeighbours()){
					if(!intersectedTraps.contains(neib)){
						q.add(neib);
					}
				}
			}
		}
		
		return intersectedTraps;
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
		 * The parent of the vertex in the search structure.
		 * Self if the root.
		 */
		private ArrayList<DecompVertex> parents;
		
		/**
		 * Constructs a vertex with no children or linked objects and only a type of 0.
		 */
		public DecompVertex(){
			this(0, null, null, null, null, null);
		}
		
		/**
		 * Constructs a Decomposition Vertex of type 0 (leaf) with a linked trapezoid.
		 * @param trapezoid The trapezoid to link to.
		 */
		public DecompVertex(Trapezoid trapezoid){
			this(0 , null, null, trapezoid, null, null);
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
			this.parents = new ArrayList<DecompVertex>();
			if(type == 0){
				this.left  = null;
				this.right = null;
				this.trapezoid = trapezoid;
				this.point = null;
				this.segment = null;
			}else if(type < 3){
				if(left != null){
					left.addParent(this);
				}
				if(right != null){
					right.addParent(this);
				}
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
				
				Line2D orientedSegment = new Line2D.Double(leftp, rightp);
				return orientedSegment.relativeCCW(query) >= 0 ? left.queryPoint(query) : right.queryPoint(query); //Can be subject to change.
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
			if(vert != null){
				vert.addParent(this);
			}
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
			if(vert != null){
				vert.addParent(this);
			}
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
			if(type == 0){
				this.trapezoid = trapezoid;
			}
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
			if(type == 1){
				this.point = point;
			}
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
			if(type == 2){
				this.segment = segment;
			}
		}
		
		/**
		 * Gets the parents of this vertex.
		 * @return The parents of this vertex
		 */
		public List<DecompVertex> getParents(){
			return parents;
		}
		
		/**
		 * Adds a parent of this vertex.
		 * @param parent The parent to add to this vertex.
		 */
		public void addParent(DecompVertex parent){
			this.parents.add(parent);
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
		 * The convex object that the trapezoid is a part of.
		 */
		private ConvexObject object;
		
		/**
		 * Constructs a trapezoid given a left and a right bounding point, and top and the defining points of the top and bottom segments.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param bot1 One point of the bottom bounding line of the trapezoid.
		 * @param bot2 The other point of the bottom bounding line of the trapezoid.
		 * @param top1 One point of the top bounding line of the trapezoid.
		 * @param top2 The other point of the bottom bounding line of the trapezoid.
		 */
		public Trapezoid(Point2D left, Point2D right, Point2D bot1, Point2D bot2, Point2D top1, Point2D top2){
			this(new ArrayList<Point2D>(Arrays.asList(left)), new ArrayList<Point2D>(Arrays.asList(right)), bot1, bot2, top1, top2);
		}
		
		/**
		 * Constructs a trapezoid given multiple left bounding points and a right bounding point, and top and the defining points of the top and bottom segments.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param bot1 One point of the bottom bounding line of the trapezoid.
		 * @param bot2 The other point of the bottom bounding line of the trapezoid.
		 * @param top1 One point of the top bounding line of the trapezoid.
		 * @param top2 The other point of the bottom bounding line of the trapezoid.
		 */
		public Trapezoid(List<Point2D> left, Point2D right, Point2D bot1, Point2D bot2, Point2D top1, Point2D top2){
			this(left, new ArrayList<Point2D>(Arrays.asList(right)),bot1, bot2, top1, top2);
		}
		
		/**
		 * Constructs a trapezoid given a left bounding point and multiple right bounding points, and top and the defining points of the top and bottom segments.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param bot1 One point of the bottom bounding line of the trapezoid.
		 * @param bot2 The other point of the bottom bounding line of the trapezoid.
		 * @param top1 One point of the top bounding line of the trapezoid.
		 * @param top2 The other point of the bottom bounding line of the trapezoid.
		 */
		public Trapezoid(Point2D left, List<Point2D>  right, Point2D bot1, Point2D bot2, Point2D top1, Point2D top2){
			this(new ArrayList<Point2D>(Arrays.asList(left)), right,bot1, bot2, top1, top2);
		}
		
		/**
		 * Constructs a trapezoid and links the corresponding vertex in the search structure and the object that the trapezoid is part of.
		 * @param left The left bounding point of the trapezoid.
		 * @param right The right bounding point of the trapezoid.
		 * @param bot1 One point of the bottom bounding line of the trapezoid.
		 * @param bot2 The other point of the bottom bounding line of the trapezoid.
		 * @param top1 One point of the top bounding line of the trapezoid.
		 * @param top2 The other point of the bottom bounding line of the trapezoid.
		 */
		public Trapezoid(List<Point2D> left, List<Point2D> right, Point2D bot1, Point2D bot2, Point2D top1, Point2D top2){
			this.topLeft = Double.compare(top1.getX(), top2.getX()) == 0 ? ((Double.compare(top1.getY(), top2.getY()) < 0) ? top1 : top2) : Double.compare(top1.getX(), top2.getX()) < 0 ? top1 : top2;  
			this.topRight = this.topLeft.equals(top1) ? top2 : top1;
			this.botLeft = Double.compare(bot1.getX(), bot2.getX()) == 0 ? ((Double.compare(bot1.getY(), bot2.getY()) < 0) ? bot1 : bot2) : Double.compare(bot1.getX(), bot2.getX()) < 0 ? bot1 : bot2;
			this.botRight = this.botLeft.equals(bot1) ? bot2 : bot1;
			this.leftPoints = left;
			this.rightPoints = right;
			
			this.neighbours = new ArrayList<Trapezoid>();
			this.vertex = null;
			this.object = null;
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
			this.neighbours.clear();;
		}
		
		/**
		 * Computes and outputs the decomposition lines related to this trapezoid.
		 * @return The vertical decomposition lines, unless one or more of the bounding lines is vertical.
		 */
		public List<Line2D> getDecompLines(){
			assert leftPoints.size() > 0;
			assert rightPoints.size() > 0;
			assert !this.getNeighbours().contains(this);
			List<Line2D> verticalLines = new ArrayList<Line2D>(); 
			if(leftPoints.size() > 0 && !(topLeft.equals(botLeft) && leftPoints.contains(topLeft))){//Draw vertical line between top and bottom on the left
				double xRatioTop = (leftPoints.get(0).getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX());
				double xRatioBot = (leftPoints.get(0).getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX());
				verticalLines.add(new Line2D.Double(new Point2D.Double(leftPoints.get(0).getX(), xRatioBot * botRight.getY() + (1 - xRatioBot) * botLeft.getY()),
													new Point2D.Double(leftPoints.get(0).getX(), xRatioTop * topRight.getY() + (1 - xRatioTop) * topLeft.getY())));
				
			}
			
			if(rightPoints.size() > 0 && !(topRight.equals(botRight) && rightPoints.contains(topRight))){//Draw vertical line between top and bottom on the right
				double xRatioTop = Math.abs((rightPoints.get(0).getX() - topLeft.getX()) / (topRight.getX() - topLeft.getX()));
				double xRatioBot = Math.abs((rightPoints.get(0).getX() - botLeft.getX()) / (botRight.getX() - botLeft.getX()));
				verticalLines.add(new Line2D.Double(new Point2D.Double(rightPoints.get(0).getX(), xRatioBot * botRight.getY() + (1 - xRatioBot) * botLeft.getY()),
								  					new Point2D.Double(rightPoints.get(0).getX(), xRatioTop * topRight.getY() + (1 - xRatioTop) * topLeft.getY())));
			}
			return verticalLines;
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
		 * Gets the convex object that this trapezoid is a part of.
		 * @return The convex object that this trapezoid is a part of.
		 */
		public ConvexObject getObject(){
			return object;
		}
		
		/**
		 * Sets the convex object that this trapezoid is a part of.
		 * @param object The convex object that this trapezoid is a part of.
		 */
		public void setObject(ConvexObject object){
			this.object = object;
		}
		
		/**
		 * Checks whether a given line segment intersects with this trapezoid.
		 * A 
		 * @param segment The line segment to check intersection with.
		 * @return True if the line segment is intersected, false otherwise.
		 */
		public boolean intersectsSegment(Line2D segment){
			List<Line2D> DecompLines = this.getDecompLines();
			for(Line2D line : DecompLines){
				if(line.intersectsLine(segment)){
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Adds a left bounding point to the trapezoid.
		 * @param point The new left bounding point.
		 */
		public void addLeftPoint(Point2D point){
			leftPoints.add(point);
		}
		
		/**
		 * Adds a right bounding point to the trapezoid
		 * @param point The new right bounding point
		 */
		public void addRightPoint(Point2D point){
			rightPoints.add(point);
		}
	}
}
