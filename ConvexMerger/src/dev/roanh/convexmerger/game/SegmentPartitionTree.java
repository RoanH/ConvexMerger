package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.roanh.convexmerger.ui.Theme;

/**
 * Segment partition tree for efficient detection
 * of line intersections with the stored line set.
 * @author Roan
 * @param <T> The partition tree type
 */
public class SegmentPartitionTree<T extends PartitionTree<SegmentPartitionTree.LineSegment, ?>>{
	public static final SegmentPartitionTreeConstructor<KDTree<LineSegment>> TYPE_KD_TREE = new SegmentPartitionTreeConstructor<KDTree<LineSegment>>(KDTree::new);
	public static final SegmentPartitionTreeConstructor<ConjugationTree<LineSegment>> TYPE_CONJUGATION_TREE = new SegmentPartitionTreeConstructor<ConjugationTree<LineSegment>>(ConjugationTree::new);

	private final PartitionTree<LineSegment, T> partitions;
	private final List<LineSegment> segments = new ArrayList<LineSegment>();
	
	/**
	 * Constructs a new segment partition tree with the given
	 * partitioning tree data structure.
	 * @param partitions The partitioning data structure.
	 * @see SegmentPartitionTree#TYPE_CONJUGATION_TREE
	 * @see SegmentPartitionTree#TYPE_KD_TREE
	 */
	private SegmentPartitionTree(PartitionTree<LineSegment, T> partitions){
		this.partitions = partitions;
	}
	
	public void addSegment(Point2D p1, Point2D p2){
		addSegment(new LineSegment(p1, p2));
	}
	
	public void addSegment(LineSegment line){
		addSegment(partitions, line);
		segments.add(line);
	}
	
	private void addSegment(PartitionTree<LineSegment, T> node, LineSegment line){
		//TODO general procedure
		//if full intersection -> clip -> store
		//else -> clip -> recurse on children
//		if(node.containsFully(line)){
//			if(node.isLeafCell()){//TODO also store at internal nodes
//				node.addData(line);
//			}else{
////				addSegment(node.getLowNode(), line);
////				addSegment(node.getHighNode(), line);
//				//TODO loop children
//			}
//		}
	}
	
	public boolean intersects(Point2D p1, Point2D p2){
		return intersects(new LineSegment(p1, p2));
	}
	
	public boolean intersects(LineSegment line){
		return intersects(partitions, line);
	}
	
	private boolean intersects(PartitionTree<LineSegment, T> node, LineSegment line){
		//TODO see #addSegment
		return false;
//		if(node.contains(line)){
//			if(node.isLeafCell()){
//				return intersectsAny(node.getData(), line);
//			}else{
//				return intersects(node.getLowNode(), line) || intersects(node.getHighNode(), line);
//			}
//		}else{
//			return false;
//		}
	}
	
	public Stream<T> streamCells(){
		return partitions.streamCells();
	}
	
	public void render(Graphics2D g){
		g.setColor(Color.BLUE);
		g.setStroke(Theme.BORDER_STROKE);
		for(LineSegment line : segments){
			g.draw(line);
		}
		partitions.render(g);
	}
	
	private static final boolean intersectsAny(List<LineSegment> lines, LineSegment line){
		for(LineSegment test : lines){
			//ensure exact endpoint matches are not intersections
			if(test.getP1() == line.getP1() || test.getP1() == line.getP2() || test.getP2() == line.getP1() || test.getP2() == line.getP2()){
				//the only way line segments that share an end point can still intersect is if they overlap
				return ConvexUtil.checkCollinear(
					(test.getP1() == line.getP1() || test.getP1() == line.getP2()) ? test.getP2() : test.getP1(),
					line.getP1(),
					line.getP2()
				);
			}else if(test.intersectsLine(line)){
				return true;
			}
		}
		return false;
	}
	
	//TODO
	public static final void conjugationTreeVisitor(ConjugationTree<LineSegment> tree, LineSegment line){
		if(!tree.isLeafCell()){
			Line2D bisector = tree.getBisector();
			assert bisector != null;
			assert line != null;
			assert line.p1 != null;
			assert line.p2 != null;
			Point2D intercept = ConvexUtil.intercept(bisector, line);
			
			if(intercept == null){
				if(bisector.relativeCCW(line.getP1()) == -1){
					System.out.println("left store");
					conjugationTreeVisitor(tree.getLeftChild(), line);
				}else{
					System.out.println("right store");
					conjugationTreeVisitor(tree.getRightChild(), line);
				}
			}else{
				System.out.println("both store");
				conjugationTreeVisitor(tree.getLeftChild(), line.derriveLine(-1, bisector, intercept));
				conjugationTreeVisitor(tree.getRightChild(), line.derriveLine(1, bisector, intercept));
			}
		}else{
			System.out.println("leaf store: " + tree.getPoints() + " / " + line);
		}
	}
	
	public static final class SegmentPartitionTreeConstructor<T extends PartitionTree<LineSegment, T>>{
		private Function<List<Point2D>, T> ctor;
		
		private SegmentPartitionTreeConstructor(Function<List<Point2D>, T> ctor){
			this.ctor = ctor;
		}
		
		//no overlap
		public final SegmentPartitionTree<T> fromObjects(List<ConvexObject> objects){
			SegmentPartitionTree<T> tree = fromPoints(objects.stream().flatMap(obj->obj.getPoints().stream()).collect(Collectors.toList()));
			
			for(ConvexObject obj : objects){
				List<Point2D> points = obj.getPoints();
				for(int i = 0; i < points.size(); i++){
					tree.addSegment(points.get(i), points.get((i + 1) % points.size()));
				}
			}
			
			return tree;
		}
		
		//no overlap
		public final SegmentPartitionTree<T> fromLines(List<Line2D> lines){
			SegmentPartitionTree<T> tree = fromPoints(lines.stream().flatMap(line->Stream.of(line.getP1(), line.getP2())).collect(Collectors.toList()));
			lines.forEach(line->tree.addSegment(line.getP1(), line.getP2()));
			return tree;
		}
		
		//TODO probably remove
		public final SegmentPartitionTree<T> fromPoints(List<Point2D> points){
			return new SegmentPartitionTree<T>(ctor.apply(points));
		}
	}
	
	/**
	 * Represents a line segment between two end points.
	 * @author Roan
	 */
	public static final class LineSegment extends Line2D{
		/**
		 * First end point of the line.
		 */
		private Point2D p1;
		/**
		 * Second end point of the line.
		 */
		private Point2D p2;
		private boolean p1Clipped = false;
		private boolean p2Clipped = false;

		/**
		 * Constructs a new line segment with the given end points. 
		 * @param p1 The first end point of the line.
		 * @param p2 The second end point of the line.
		 */
		public LineSegment(Point2D p1, Point2D p2){
			assert p1 != null;
			assert p2 != null;
			this.p1 = p1;
			this.p2 = p2;
		}
		
		private LineSegment derriveLine(int ccw, Line2D intersected, Point2D intersection){
			if(intersected.relativeCCW(p1) == ccw){
				LineSegment line = new LineSegment(p1, intersection);
				line.p2Clipped = true;
				line.p1Clipped = p1Clipped;
				return line;
			}else{
				LineSegment line = new LineSegment(intersection, p2);
				line.p1Clipped = true;
				line.p2Clipped = p2Clipped;
				return line;
			}
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
			p1 = new Point2D.Double(x1, y1);
			p2 = new Point2D.Double(x2, y2);
		}
		
		@Override
		public String toString(){
			return "Segment[p1=(" + p1.getX() + "," + p1.getY() + "),p2=(" + p2.getX() + "," + p2.getY() + ")]";
		}
	}
}
