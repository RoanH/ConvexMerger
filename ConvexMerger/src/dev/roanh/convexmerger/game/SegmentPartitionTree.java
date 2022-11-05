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
	public static final SegmentPartitionTreeConstructor<KDTree<LineSegment>> TYPE_KD_TREE = new SegmentPartitionTreeConstructor<KDTree<LineSegment>>(KDTree::new, SegmentPartitionTree::visitKDTree);
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
		partitionVisitor.visitTree(partitions, line, false, PartitionTreeVisitor.terminal(T::addData));
		segments.add(line);
	}
	
	public boolean intersects(Point2D p1, Point2D p2){
		return intersectsInternal(new LineSegment(p1, p2));
	}
	
	public boolean intersects(Line2D line){
		return intersectsInternal(new LineSegment(line));
	}
	
	private boolean intersectsInternal(LineSegment line){
		return !partitionVisitor.visitTree(partitions, line, true, PartitionTreeVisitor.all((node, seg)->{
			System.out.println("cell check: " + node.getData() + " / " + seg);
			node.setMarked(true);
			return !intersectsAny(node.getData(), line);
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
		//TODO
	}
	
	private static final boolean intersectsAny(List<LineSegment> lines, LineSegment line){
		for(LineSegment test : lines){
			test.flag = true;
			//ensure exact endpoint matches are not intersections
			boolean p1Either = ConvexUtil.approxEqual(test.getP1(), line.getP1()) || ConvexUtil.approxEqual(test.getP1(), line.getP2());
			if(p1Either || ConvexUtil.approxEqual(test.getP2(), line.getP1()) || ConvexUtil.approxEqual(test.getP2(), line.getP2())){
				//the only way line segments that share an end point can still intersect is if they overlap
				if(ConvexUtil.checkCollinear(p1Either ? test.getP2() : test.getP1(), line.getP1(), line.getP2())){
					return true;
				}
			}else if(test.intersectsLine(line)){
				return true;
			}
		}
		return false;
	}
	
	private static final boolean visitKDTree(KDTree<LineSegment> tree, LineSegment line, int maxDepth, boolean ignoreInnerTerminals, PartitionTreeVisitor<KDTree<LineSegment>> visitor){
		if(maxDepth == 0 || line == null || ConvexUtil.approxEqual(line.getP1(), line.getP2())){
			return true;
		}
		
		if(tree.isLeafCell() || (!ignoreInnerTerminals && line.p1Clipped && line.p2Clipped)){
			//assert tree.getBounds().contains(line.getBounds2D()) : "bounds off";
			return visitor.acceptTerminalNode(tree, line);
		}else{
			if(!visitor.acceptInnerNode(tree, line)){
				return false;
			}
			
			for(KDTree<LineSegment> node : tree.getChildren()){
				if(node.intersects(line)){
					if(!visitKDTree(node, line.deriveLine(node.getBounds()), maxDepth - 1, ignoreInnerTerminals, visitor)){
						return false;
					}
				}
			}
			
			return true;
		}
	}
	
	private static final boolean visitConjugationTree(ConjugationTree<LineSegment> tree, LineSegment line, int maxDepth, boolean ignoreInnnerTerminals, PartitionTreeVisitor<ConjugationTree<LineSegment>> visitor){
		if(maxDepth == 0 || ConvexUtil.approxEqual(line.getP1(), line.getP2())){
			return true;
		}
		
		if(tree.isLeafCell() || (!ignoreInnnerTerminals && line.p1Clipped && line.p2Clipped)){
			return visitor.acceptTerminalNode(tree, line);
		}else{
			if(!visitor.acceptInnerNode(tree, line)){
				return false;
			}
			
			Line2D bisector = tree.getBisector();
			Point2D intercept = ConvexUtil.interceptClosed(bisector, line);
			
			if(intercept == null){
				if(bisector.relativeCCW(line.getP1()) == -1){
					return visitConjugationTree(tree.getLeftChild(), line, maxDepth - 1, ignoreInnnerTerminals, visitor);
				}else{
					return visitConjugationTree(tree.getRightChild(), line, maxDepth - 1, ignoreInnnerTerminals, visitor);
				}
			}else{
				if(visitConjugationTree(tree.getLeftChild(), line.deriveLine(-1, bisector, intercept), maxDepth - 1, ignoreInnnerTerminals, visitor)){
					return visitConjugationTree(tree.getRightChild(), line.deriveLine(1, bisector, intercept), maxDepth - 1, ignoreInnnerTerminals, visitor);
				}else{
					return false;
				}
			}
		}
	}
	
	@FunctionalInterface
	private static abstract interface VisitingFunction<T extends PartitionTree<LineSegment, T>>{
		
		public abstract boolean visitTree(T tree, LineSegment line, int maxDepth, boolean ignoreInnnerTerminals, PartitionTreeVisitor<T> visitor);
		
		public default boolean visitTree(T tree, LineSegment line, boolean ignoreInnnerTerminals, PartitionTreeVisitor<T> visitor){
			return visitTree(tree, line, Integer.MAX_VALUE, ignoreInnnerTerminals, visitor);
		}
	}
	
	private static abstract interface PartitionTreeVisitor<T extends PartitionTree<LineSegment, T>>{
		
		public abstract boolean acceptTerminalNode(T node, LineSegment segment);
		
		public abstract boolean acceptInnerNode(T node, LineSegment segment);
		
		public static <T extends PartitionTree<LineSegment, T>> PartitionTreeVisitor<T> terminal(BiConsumer<T, LineSegment> consumer){
			return new PartitionTreeVisitor<T>(){
				
				@Override
				public boolean acceptTerminalNode(T node, LineSegment segment){
					consumer.accept(node, segment);
					return true;
				}
				
				@Override
				public boolean acceptInnerNode(T node, LineSegment segment){
					return true;
				}
			};
		}
		
		public static <T extends PartitionTree<LineSegment, T>> PartitionTreeVisitor<T> all(BiFunction<T, LineSegment, Boolean> fun){
			return new PartitionTreeVisitor<T>(){
				
				@Override
				public boolean acceptTerminalNode(T node, LineSegment segment){
					return fun.apply(node, segment);
				}
				
				@Override
				public boolean acceptInnerNode(T node, LineSegment segment){
					return fun.apply(node, segment);
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
		public boolean flag = false;

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
		
		private LineSegment deriveLine(Rectangle2D bounds){
			if(bounds.contains(p1) && bounds.contains(p2)){
				return this;
			}
			
			Point2D p1 = this.p1;
			Point2D p2 = this.p2;
			int out1 = bounds.outcode(p1);
			int out2 = bounds.outcode(p2);
			
			Point2D tl = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
			Point2D tr = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
			Point2D bl = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
			Point2D br = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
			
			if(out1 != 0){
				p1 = boundClip(tl, tr, bl, br, out1);
			}
			
			if(out2 != 0){
				p2 = boundClip(tl, tr, bl, br, out2);
			}
			
			if(p1 == null || p2 == null){
				//one endpoint of the line segment is exactly on the boundary and the other possibly outside
				return null;
			}else{
				LineSegment line = new LineSegment(p1, p2);
				line.p1Clipped = p1Clipped | (p1 != this.p1);
				line.p2Clipped = p2Clipped | (p2 != this.p2);
				return line;
			}
		}
		
		//out not 0
		private Point2D boundClip(Point2D tl, Point2D tr, Point2D bl, Point2D br, int out){
			Point2D p = null;
			
			if((out & Rectangle2D.OUT_BOTTOM) > 0){
				p = ConvexUtil.interceptClosed(tl, tr, p1, p2);
			}
			
			if(p == null && (out & Rectangle2D.OUT_TOP) > 0){
				p = ConvexUtil.interceptClosed(bl, br, p1, p2);
			}
			
			if(p == null && (out & Rectangle2D.OUT_LEFT) > 0){
				p = ConvexUtil.interceptClosed(tl, bl, p1, p2);
			}
			
			if(p == null && (out & Rectangle2D.OUT_RIGHT) > 0){
				p = ConvexUtil.interceptClosed(tr, br, p1, p2);
			}
			
			return p;
		}
		
		/**
		 * Derives a new line from this line segment that is exactly the
		 * part of this line that lies to the given direction of the given
		 * intersected line segment.
		 * @param ccw The direction of this line segment to returned this is specified
		 *        with the passed CCW value, which has to be either -1 or 1 (for details
		 *        see {@link Line2D#relativeCCW(double, double, double, double, double, double)}.
		 *        In this data structure a value of -1 is generally referred to
		 *        as 'left' and a value of '1' as right.
		 * @param intersected The intersected line segment, this line segment is required
		 *        to intersect this line segment in the given intersection point.
		 * @param intersection The intersection point of this line segment and the
		 *        given intersected line.
		 * @return The part of this line segment to the requested direction of the given intersected line.
		 */
		private LineSegment deriveLine(int ccw, Line2D intersected, Point2D intersection){
			boolean leftFurthest = intersected.ptLineDistSq(p1) > intersected.ptLineDistSq(p2);
			boolean ccwCorrect = intersected.relativeCCW(leftFurthest ? p1 : p2) == ccw;
			
			//use p1 only if it is furthest and on the correct side or closest but p2 is on the wrong side
			if(leftFurthest ^ ccwCorrect){
				LineSegment line = new LineSegment(intersection, p2);
				line.p1Clipped = true;
				line.p2Clipped = p2Clipped;
				return line;
			}else{
				LineSegment line = new LineSegment(p1, intersection);
				line.p2Clipped = true;
				line.p1Clipped = p1Clipped;
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
