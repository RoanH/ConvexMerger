package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.roanh.convexmerger.ui.Theme;

/**
 * Segment partition tree for efficient detection
 * of line intersections with the stored line set.
 * @author Roan
 */
public class SegmentPartitionTree{
	private final KDTree<LineSegment> kdTree;
	private final List<LineSegment> segments = new ArrayList<LineSegment>();
	
	private SegmentPartitionTree(List<Point2D> points){
		kdTree = new KDTree<LineSegment>(points);
	}
	
	public void addSegment(Point2D p1, Point2D p2){
		addSegment(new LineSegment(p1, p2));
	}
	
	public void addSegment(LineSegment line){
		addSegment(kdTree, line);
		segments.add(line);
	}
	
	private void addSegment(KDTree<LineSegment> node, LineSegment line){
		if(node.contains(line)){
			if(node.isLeafCell()){
				node.addData(line);
			}else{
				addSegment(node.getLowNode(), line);
				addSegment(node.getHighNode(), line);
			}
		}
	}
	
	public boolean intersects(Point2D p1, Point2D p2){
		return intersects(new LineSegment(p1, p2));
	}
	
	public boolean intersects(LineSegment line){
		return intersects(kdTree, line);
	}
	
	private boolean intersects(KDTree<LineSegment> node, LineSegment line){
		if(node.contains(line)){
			if(node.isLeafCell()){
				return intersectsAny(node.getData(), line);
			}else{
				return intersects(node.getLowNode(), line) || intersects(node.getHighNode(), line);
			}
		}else{
			return false;
		}
	}
	
	public Stream<KDTree<LineSegment>> streamCells(){
		return kdTree.streamCells();
	}
	
	public void render(Graphics2D g){
		g.setColor(Color.BLUE);
		g.setStroke(Theme.BORDER_STROKE);
		for(LineSegment line : segments){
			g.draw(line);
		}
		kdTree.render(g);
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
	
	//no overlap
	public static final SegmentPartitionTree fromObjects(List<ConvexObject> objects){
		SegmentPartitionTree tree = new SegmentPartitionTree(objects.stream().flatMap(obj->obj.getPoints().stream()).collect(Collectors.toList()));
		
		for(ConvexObject obj : objects){
			List<Point2D> points = obj.getPoints();
			for(int i = 0; i < points.size(); i++){
				tree.addSegment(points.get(i), points.get((i + 1) % points.size()));
			}
		}
		
		return tree;
	}
	
	//no overlap
	public static final SegmentPartitionTree fromLines(List<Line2D> lines){
		SegmentPartitionTree tree = new SegmentPartitionTree(lines.stream().flatMap(line->Stream.of(line.getP1(), line.getP2())).collect(Collectors.toList()));
		lines.forEach(line->tree.addSegment(line.getP1(), line.getP2()));
		return tree;
	}
	
	//TODO probably remove
	public static final SegmentPartitionTree fromPoints(List<Point2D> points){
		return new SegmentPartitionTree(points);
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

		/**
		 * Constructs a new line segment with the given end points. 
		 * @param p1 The first end point of the line.
		 * @param p2 The second end point of the line.
		 */
		public LineSegment(Point2D p1, Point2D p2){
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
			p1 = new Point2D.Double(x1, y1);
			p2 = new Point2D.Double(x2, y2);
		}
		
		@Override
		public String toString(){
			return "Segment[p1=(" + p1.getX() + "," + p1.getY() + "),p2=(" + p2.getX() + "," + p2.getY() + ")]";
		}
	}
}
