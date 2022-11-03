package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
public class SegmentPartitionTree<T extends PartitionTree<SegmentPartitionTree.LineSegment, T>>{
	public static final SegmentPartitionTreeConstructor<KDTree<LineSegment>> TYPE_KD_TREE = new SegmentPartitionTreeConstructor<KDTree<LineSegment>>(KDTree::new, null);//TODO
	public static final SegmentPartitionTreeConstructor<ConjugationTree<LineSegment>> TYPE_CONJUGATION_TREE = new SegmentPartitionTreeConstructor<ConjugationTree<LineSegment>>(ConjugationTree::new, SegmentPartitionTree::visitConjugationTree);

	private final T partitions;
	private final VisitingFunction<T> partitionVisitor;
	private final List<LineSegment> segments = new ArrayList<LineSegment>();
	
	private boolean animated = true;//TODO false
	
	/**
	 * Constructs a new segment partition tree with the given
	 * partitioning tree data structure.
	 * @param partitions The partitioning data structure.
	 * @param partitionVisitor Function that can be used to visit
	 *        cells in the partition tree that either store or are
	 *        along the search path for a certain query line.
	 * @see SegmentPartitionTree#TYPE_CONJUGATION_TREE
	 * @see SegmentPartitionTree#TYPE_KD_TREE
	 */
	private SegmentPartitionTree(T partitions, VisitingFunction<T> partitionVisitor){
		this.partitions = partitions;
		this.partitionVisitor = partitionVisitor;
	}
	
	public boolean isAnimated(){
		return animated;
	}
	
	public void addSegment(Point2D p1, Point2D p2){
		addSegmentInternal(new LineSegment(p1, p2));
	}
	
	public void addSegment(Line2D line){
		addSegmentInternal(new LineSegment(line));
	}
	
	private void addSegmentInternal(LineSegment line){
		partitionVisitor.visitTree(partitions, line, PartitionTreeVisitor.terminal(T::addData));
		segments.add(line);
	}
	
	public boolean intersects(Point2D p1, Point2D p2){
		return intersectsInternal(new LineSegment(p1, p2));
	}
	
	public boolean intersects(Line2D line){
		return intersectsInternal(new LineSegment(line));
	}
	
	private boolean intersectsInternal(LineSegment line){
		return !partitionVisitor.visitTree(partitions, line, PartitionTreeVisitor.terminal((node, seg)->{
			return intersectsAny(node.getData(), line);
		}));
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
	
	public void renderQuery(Point2D a, Point2D b){
		
		
		
		
	}
	
	public void queryLine(Point2D a, Point2D b){
		
	}
	
	public void queryLine(Line2D line){
		
	}
	
	private void queryLine(LineSegment line){
		
	}
	
	
	
	//TODO update
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
	
	private static final void visitKDTree(ConjugationTree<LineSegment> tree, LineSegment line, int maxDepth, PartitionTreeVisitor<ConjugationTree<LineSegment>> visitor){
		if(maxDepth == 0){
			return;
		}
		
		//TODO
		
		//TODO general procedure
				//if full intersection -> clip -> store
				//else -> clip -> recurse on children
//				if(node.containsFully(line)){
//					if(node.isLeafCell()){//TODO also store at internal nodes
//						node.addData(line);
//					}else{
////						addSegment(node.getLowNode(), line);
////						addSegment(node.getHighNode(), line);
//						//TODO loop children
//					}
//				}
		
//		if(node.contains(line)){
//		if(node.isLeafCell()){
//			return intersectsAny(node.getData(), line);
//		}else{
//			return intersects(node.getLowNode(), line) || intersects(node.getHighNode(), line);
//		}
//	}else{
//		return false;
//	}
	}
	
	//TODO
	private static final boolean visitConjugationTree(ConjugationTree<LineSegment> tree, LineSegment line, int maxDepth, PartitionTreeVisitor<ConjugationTree<LineSegment>> visitor){
		if(maxDepth == 0){
			return false;
		}
		
		if(tree.isLeafCell() || (line.p1Clipped && line.p2Clipped)){
			return visitor.acceptTerminalNode(tree, line);
		}else{
			visitor.acceptInnerNode(tree, line);
			Line2D bisector = tree.getBisector();
			Point2D intercept = ConvexUtil.interceptClosed(bisector, line);
			
			if(intercept == null){
				if(bisector.relativeCCW(line.getP1()) == -1){
					return visitConjugationTree(tree.getLeftChild(), line, maxDepth - 1, visitor);
				}else{
					return visitConjugationTree(tree.getRightChild(), line, maxDepth - 1, visitor);
				}
			}else{
				if(visitConjugationTree(tree.getLeftChild(), line.derriveLine(-1, bisector, intercept), maxDepth - 1, visitor)){
					return visitConjugationTree(tree.getRightChild(), line.derriveLine(1, bisector, intercept), maxDepth - 1, visitor);
				}else{
					return false;
				}
			}
		}
	}
	
	@FunctionalInterface
	private static abstract interface VisitingFunction<T extends PartitionTree<LineSegment, T>>{
		
		public abstract boolean visitTree(T tree, LineSegment line, int maxDepth, PartitionTreeVisitor<T> visitor);
		
		public default boolean visitTree(T tree, LineSegment line, PartitionTreeVisitor<T> visitor){
			return visitTree(tree, line, Integer.MAX_VALUE, visitor);
		}
	}
	
	private static abstract interface PartitionTreeVisitor<T extends PartitionTree<LineSegment, T>>{
		
		public abstract boolean acceptTerminalNode(T node, LineSegment segment);
		
		public abstract void acceptInnerNode(T node, LineSegment segment);
		
		public static <T extends PartitionTree<LineSegment, T>> PartitionTreeVisitor<T> terminal(BiConsumer<T, LineSegment> consumer){
			return terminal((node, seg)->{
				consumer.accept(node, seg);
				return true;
			});
		}
		
		public static <T extends PartitionTree<LineSegment, T>> PartitionTreeVisitor<T> terminal(BiFunction<T, LineSegment, Boolean> fun){
			return new PartitionTreeVisitor<T>(){
				
				@Override
				public boolean acceptTerminalNode(T node, LineSegment segment){
					return fun.apply(node, segment);
				}
				
				@Override
				public void acceptInnerNode(T node, LineSegment segment){
				}
			};
		}
	}
	
	public static final class SegmentPartitionTreeConstructor<T extends PartitionTree<LineSegment, T>>{
		private Function<List<Point2D>, T> ctor;
		private VisitingFunction<T> visitFun;
		
		private SegmentPartitionTreeConstructor(Function<List<Point2D>, T> ctor, VisitingFunction<T> visitFun){
			this.ctor = ctor;
			this.visitFun = visitFun;
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
		
		public final SegmentPartitionTree<T> fromPoints(List<Point2D> points){
			return new SegmentPartitionTree<T>(ctor.apply(points), visitFun);
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
			this.p1 = p1;
			this.p2 = p2;
		}
		
		public LineSegment(Line2D line){
			this(line.getP1(), line.getP2());
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
