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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.animation.Animation;
import dev.roanh.convexmerger.ui.Theme;

/**
 * Segment partition tree for efficient detection
 * of line intersections with the stored line set.
 * Inspired by a paper by Mark Overmars et al.
 * @author Roan
 * @param <T> The partition tree type
 * @see <a href="https://doi.org/10.1007/BF01931656">Overmars, M.H., Schipper, H. and Sharir, M.,
 *      "Storing line segments in partition trees" in BIT Numerical Mathematics, vol. 30, 1990, pp. 385–403</a>
 */
public class SegmentPartitionTree<T extends PartitionTree<SegmentPartitionTree.LineSegment, T>> extends RenderableObject{
	/**
	 * Constructor for kd-tree based segment partition trees.
	 * @see KDTree
	 */
	public static final SegmentPartitionTreeConstructor<KDTree<LineSegment>> TYPE_KD_TREE = new SegmentPartitionTreeConstructor<KDTree<LineSegment>>(KDTree::new, SegmentPartitionTree::visitKDTree);
	/**
	 * Constructor for conjugation tree based segment partition trees.
	 * @see ConjugationTree
	 */
	public static final SegmentPartitionTreeConstructor<ConjugationTree<LineSegment>> TYPE_CONJUGATION_TREE = new SegmentPartitionTreeConstructor<ConjugationTree<LineSegment>>(ConjugationTree::new, SegmentPartitionTree::visitConjugationTree);
	/**
	 * The partition tree used for store points for this segment partition tree.
	 */
	private final T partitions;
	/**
	 * A search function that can be used to traverse the used partition tree.
	 */
	private final VisitingFunction<T> partitionVisitor;
	/**
	 * The segments stored in this segment partition tree.
	 */
	private final List<LineSegment> segments = new CopyOnWriteArrayList<LineSegment>();
	/**
	 * Whether this segment partition tree is animated.
	 */
	private boolean animated = false;
	
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
	
	/**
	 * Checks if this segment partition tree is animated.
	 * @return True if this segment partition tree is animated.
	 */
	public boolean isAnimated(){
		return animated;
	}
	
	/**
	 * Sets whether this segment partition tree is animated.
	 * @param animated True if show animations for this tree.
	 */
	public void setAnimated(boolean animated){
		this.animated = animated;
	}
	
	/**
	 * Adds a new line segment to this segment partition tree.
	 * @param p1 The first point of the line segment to add.
	 * @param p2 The second point of the line segment to add.
	 */
	public void addSegment(Point2D p1, Point2D p2){
		addSegmentInternal(new LineSegment(p1, p2));
	}
	
	/**
	 * Adds a new line segment to this segment partition tree.
	 * @param line The line segment to add.
	 */
	public void addSegment(Line2D line){
		addSegmentInternal(new LineSegment(line));
	}
	
	/**
	 * Adds a new line segment to this segment partition tree.
	 * @param line The line segment to add.
	 */
	private void addSegmentInternal(LineSegment line){
		partitionVisitor.visitTree(partitions, line, false, PartitionTreeVisitor.terminal((node, seg)->{
			node.addData(seg);
			segments.add(seg);	
		}));
	}
	
	/**
	 * Checks if the line segment defined by the given points
	 * intersects any of the segments stored in this tree.
	 * @param p1 The first point of the line segment to check.
	 * @param p2 The second point of the line segment to check.
	 * @return True if the given line segment intersects a stored segment.
	 */
	public boolean intersects(Point2D p1, Point2D p2){
		return intersectsInternal(new LineSegment(p1, p2));
	}
	
	/**
	 * Checks if the line segment defined by the given points
	 * intersects any of the segments stored in this tree.
	 * @param line The line segment to check.
	 * @return True if the given line segment intersects a stored segment.
	 */
	public boolean intersects(Line2D line){
		return intersectsInternal(new LineSegment(line));
	}
	
	/**
	 * Checks if the line segment defined by the given points
	 * intersects any of the segments stored in this tree.
	 * @param line The line segment to check.
	 * @return True if the given line segment intersects a stored segment.
	 */
	private boolean intersectsInternal(LineSegment line){
		return !partitionVisitor.visitTree(partitions, line, true, PartitionTreeVisitor.all((node, seg)->{
			return !intersectsAny(node.getData(), line);
		}));
	}
	
	/**
	 * Returns a stream over all the nodes of the underlying partition tree.
	 * @return A stream over all partition tree nodes.
	 */
	public Stream<T> streamCells(){
		return partitions.streamCells();
	}
	
	@Override
	public void render(Graphics2D g){
		g.setStroke(Theme.POLY_STROKE);
		for(LineSegment seg : segments){
			g.setColor(seg.marked ? Color.RED : Color.BLACK);
			g.draw(seg);
		}	
		
		g.setStroke(Theme.BORDER_STROKE);
		partitions.render(g);
		
		g.setColor(Color.CYAN);
		g.setStroke(Theme.BORDER_STROKE);
		g.drawLine(0, 0, 0, Constants.PLAYFIELD_HEIGHT);
		g.drawLine(0, 0, Constants.PLAYFIELD_WIDTH, 0);
		g.drawLine(0, Constants.PLAYFIELD_HEIGHT, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
		g.drawLine(Constants.PLAYFIELD_WIDTH, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
	}
	
	/**
	 * Animates a search for intersections with the given line segment.
	 * @param a The first point of the line segment.
	 * @param b The second point of the line segment.
	 * @return The created animation object.
	 */
	public Animation showAnimation(Point2D a, Point2D b){
		Animation anim = new SearchAnimation(new LineSegment(a, b));
		setAnimation(anim);
		return anim;
	}
	
	/**
	 * Checks if the given line segment intersects any of the line segments
	 * in the given set of line segments. Overlapping end points are not
	 * reported as intersections.
	 * @param lines The set of lines to check for intersection with.
	 * @param line The line to check for whether it intersects any of
	 *        the lines in the given set of lines.
	 * @return True if an intersection was found with a line segment.
	 */
	private static final boolean intersectsAny(List<LineSegment> lines, LineSegment line){
		for(LineSegment test : lines){
			test = test.getOriginalSegment();
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
	
	/**
	 * Visitor function for kd-tree traversal.
	 * @param tree The kd-tree to traverse.
	 * @param line The query line.
	 * @param maxDepth The maximum search depth.
	 * @param ignoreInnerTerminals Whether to ignore inner terminals or not.
	 * @param visitor The visitor to report nodes to.
	 * @return True if the search concluded uninterrupted.
	 * @see VisitingFunction
	 * @see PartitionTreeVisitor
	 */
	private static final boolean visitKDTree(KDTree<LineSegment> tree, LineSegment line, int maxDepth, boolean ignoreInnerTerminals, PartitionTreeVisitor<KDTree<LineSegment>> visitor){
		if(maxDepth < 0 || line == null || ConvexUtil.approxEqual(line.getP1(), line.getP2())){
			return true;
		}
		
		if(tree.isLeafCell() || (!ignoreInnerTerminals && line.p1Clipped && line.p2Clipped)){
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
	
	/**
	 * Visitor function for conjugation tree traversal.
	 * @param tree The conjugation tree to traverse.
	 * @param line The query line.
	 * @param maxDepth The maximum search depth.
	 * @param ignoreInnerTerminals Whether to ignore inner terminals or not.
	 * @param visitor The visitor to report nodes to.
	 * @return True if the search concluded uninterrupted.
	 * @see VisitingFunction
	 * @see PartitionTreeVisitor
	 */
	private static final boolean visitConjugationTree(ConjugationTree<LineSegment> tree, LineSegment line, int maxDepth, boolean ignoreInnerTerminals, PartitionTreeVisitor<ConjugationTree<LineSegment>> visitor){
		if(maxDepth < 0 || ConvexUtil.approxEqual(line.getP1(), line.getP2())){
			return true;
		}
		
		if(tree.isLeafCell() || (!ignoreInnerTerminals && line.p1Clipped && line.p2Clipped)){
			return visitor.acceptTerminalNode(tree, line);
		}else{
			Line2D bisector = tree.getBisector();
			
			if(ConvexUtil.overlapsLine(line, bisector)){
				if(!ignoreInnerTerminals){
					return visitor.acceptTerminalNode(tree, line);
				}else{
					if(!visitor.acceptInnerNode(tree, line)){
						return false;
					}
					
					if(visitConjugationTree(tree.getLeftChild(), line, maxDepth - 1, ignoreInnerTerminals, visitor)){
						return visitConjugationTree(tree.getRightChild(), line, maxDepth - 1, ignoreInnerTerminals, visitor);
					}else{
						return false;
					}
				}
			}
			
			if(!visitor.acceptInnerNode(tree, line)){
				return false;
			}
			
			Point2D intercept = ConvexUtil.interceptClosed(bisector, line);
			
			if(intercept == null){
				if(bisector.relativeCCW(line.getP1()) == -1){
					return visitConjugationTree(tree.getLeftChild(), line, maxDepth - 1, ignoreInnerTerminals, visitor);
				}else{
					return visitConjugationTree(tree.getRightChild(), line, maxDepth - 1, ignoreInnerTerminals, visitor);
				}
			}else{
				if(visitConjugationTree(tree.getLeftChild(), line.deriveLine(-1, bisector, intercept), maxDepth - 1, ignoreInnerTerminals, visitor)){
					return visitConjugationTree(tree.getRightChild(), line.deriveLine(1, bisector, intercept), maxDepth - 1, ignoreInnerTerminals, visitor);
				}else{
					return false;
				}
			}
		}
	}
	
	private class SearchAnimation extends Animation implements BiConsumer<T, LineSegment>{
		private static final int DEPTH_DURATION = 250;
		private Map<Integer, List<T>> depthData = new HashMap<Integer, List<T>>();
		private int maxDepth;
		private long start;
		private int lastDepth = 0;
		
		private SearchAnimation(LineSegment query){
			partitionVisitor.visitTree(partitions, query, true, PartitionTreeVisitor.all(this));
			maxDepth = depthData.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
			start = System.currentTimeMillis();
		}
		
		@Override
		public void accept(T node, LineSegment seg){
			depthData.computeIfAbsent(node.getDepth(), d->new ArrayList<T>()).add(node);
		}

		@Override
		protected boolean render(Graphics2D g){
			int depth = (int)Math.floorDiv(System.currentTimeMillis() - start, DEPTH_DURATION);
			for(int i = lastDepth; i < Math.min(depth, maxDepth + 1); i++){
				for(T node : depthData.get(i)){
					node.setMarked(false);
					for(LineSegment seg : node.getData()){
						seg.marked = false;
					}
				}
			}
			
			if(depth <= maxDepth){
				if(lastDepth != depth){
					for(T node : depthData.get(depth)){
						node.setMarked(true);
						for(LineSegment seg : node.getData()){
							seg.marked = true;
						}
					}
					lastDepth = depth;
				}
			}
			
			SegmentPartitionTree.this.render(g);
			
			return depth <= maxDepth;
		}
	}
	
	/**
	 * Interface for a function that can be used to search a partition tree.
	 * @author Roan
	 * @param <T> The partition tree type.
	 */
	@FunctionalInterface
	private static abstract interface VisitingFunction<T extends PartitionTree<LineSegment, T>>{
		
		/**
		 * Visitor function for partition tree traversal. This function will search a partition tree
		 * with a given query line and report any traversed inner and terminal nodes. Two nodes are
		 * treated as terminal nodes: leaf cells and cells fully intersected by the query line.
		 * @param tree The partition tree to traverse.
		 * @param line The query line to search with.
		 * @param maxDepth The maximum search depth in the tree.
		 * @param ignoreInnerTerminals Whether to ignore inner terminals or not. When ignored the
		 *        search treats inner terminals as regular inner nodes and the search continues to
		 *        the child nodes of these inner terminal nodes.
		 * @param visitor The visitor to report traversed terminal and inner nodes to.
		 * @return True if the search concluded uninterrupted.
		 * @see VisitingFunction
		 * @see PartitionTreeVisitor
		 * @see #visitTree(PartitionTree, LineSegment, boolean, PartitionTreeVisitor)
		 */
		public abstract boolean visitTree(T tree, LineSegment line, int maxDepth, boolean ignoreInnerTerminals, PartitionTreeVisitor<T> visitor);
		
		/**
		 * Visitor function for partition tree traversal. This function will search a partition tree
		 * with a given query line and report any traversed inner and terminal nodes. Two nodes are
		 * treated as terminal nodes: leaf cells and cells fully intersected by the query line.
		 * @param tree The partition tree to traverse.
		 * @param line The query line to search with.
		 * @param ignoreInnerTerminals Whether to ignore inner terminals or not. When ignored the
		 *        search treats inner terminals as regular inner nodes and the search continues to
		 *        the child nodes of these inner terminal nodes.
		 * @param visitor The visitor to report traversed terminal and inner nodes to.
		 * @return True if the search concluded uninterrupted.
		 * @see VisitingFunction
		 * @see PartitionTreeVisitor
		 * @see #visitTree(PartitionTree, LineSegment, int, boolean, PartitionTreeVisitor)
		 */
		public default boolean visitTree(T tree, LineSegment line, boolean ignoreInnerTerminals, PartitionTreeVisitor<T> visitor){
			return visitTree(tree, line, Integer.MAX_VALUE, ignoreInnerTerminals, visitor);
		}
	}
	
	/**
	 * Interface for a function that nodes visited in a partition tree search are reported to.
	 * @author Roan
	 * @param <T> The partition tree type.
	 * @see VisitingFunction
	 */
	private static abstract interface PartitionTreeVisitor<T extends PartitionTree<LineSegment, T>>{
		
		/**
		 * Called when a partition tree search finds a terminal node.
		 * @param node The found terminal node.
		 * @param segment The segment of the query line within the terminal node.
		 * @return False to interrupt the search and return, true to continue
		 *         the search of the partition tree.
		 * @see VisitingFunction
		 */
		public abstract boolean acceptTerminalNode(T node, LineSegment segment);
		
		/**
		 * Called when a partition tree visits a new inner tree node.
		 * @param node The visited inner node.
		 * @param segment The segment of the query line within the node.
		 * @return False to interrupt the search and return, true to continue
		 *         the search of the partition tree.
		 * @see VisitingFunction
		 */
		public abstract boolean acceptInnerNode(T node, LineSegment segment);
		
		/**
		 * Constructs a special partition tree visitor that only receives terminal nodes.
		 * @param <T> The partition tree type.
		 * @param consumer The consumer to give found terminal nodes to.
		 * @return A partition tree visitor reporting only terminal nodes.
		 * @see VisitingFunction
		 */
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
		
		/**
		 * Constructs a special partition tree visitor that does not distinguish
		 * between inner and terminal nodes and reports both.
		 * @param <T> The partition tree type.
		 * @param fun The function to report found inner and terminal nodes to.
		 *        This function can return false to end the search early.
		 * @return A partition tree visitor reporting both inner and terminal nodes.
		 * @see VisitingFunction
		 */
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
		
		/**
		 * Constructs a special partition tree visitor that does not distinguish
		 * between inner and terminal nodes and reports both.
		 * @param <T> The partition tree type.
		 * @param consumer The consumer to give found inner and terminal nodes to.
		 * @return A partition tree visitor reporting both inner and terminal nodes.
		 * @see VisitingFunction
		 */
		public static <T extends PartitionTree<LineSegment, T>> PartitionTreeVisitor<T> all(BiConsumer<T, LineSegment> consumer){
			return all((node, seg)->{
				consumer.accept(node, seg);
				return true;
			});
		}
	}
	
	/**
	 * A class that can create new instances of a segment partition tree
	 * based on a specific partition tree type.
	 * @author Roan
	 * @param <T> The partition tree type.
	 */
	public static final class SegmentPartitionTreeConstructor<T extends PartitionTree<LineSegment, T>>{
		/**
		 * The partition tree constructor.
		 */
		private Function<List<Point2D>, T> ctor;
		/**
		 * A function that can traverse the partition tree type.
		 */
		private VisitingFunction<T> visitFun;
		
		/**
		 * Constructs a new segment partition tree constructor.
		 * @param ctor The partition tree constructor.
		 * @param visitFun The partition tree visiting function.
		 * @see VisitingFunction
		 * @see PartitionTree
		 */
		private SegmentPartitionTreeConstructor(Function<List<Point2D>, T> ctor, VisitingFunction<T> visitFun){
			this.ctor = ctor;
			this.visitFun = visitFun;
		}
		
		/**
		 * Constructs a new segment partition tree from the line segments making up
		 * the given set of convex objects. The objects are assumed to not have any overlap.
		 * @param objects The convex objects to initialise the segment tree with.
		 * @return The newly created segment partition tree.
		 */
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
		
		/**
		 * Constructs a new segment partition tree from the line segments in the
		 * given set. The lines are assumed to not have any overlap.
		 * @param lines The line segments to initialise the segment tree with.
		 * @return The newly created segment partition tree.
		 */
		public final SegmentPartitionTree<T> fromLines(List<Line2D> lines){
			SegmentPartitionTree<T> tree = fromPoints(lines.stream().flatMap(line->Stream.of(line.getP1(), line.getP2())).collect(Collectors.toList()));
			lines.forEach(line->tree.addSegment(line.getP1(), line.getP2()));
			return tree;
		}
		
		/**
		 * Constructs a new empty segment partition tree with the given set
		 * of points for the underlying partition tree. No line segments will
		 * be present in the segment tree initially.
		 * @param points The points to initialise the segment tree with.
		 * @return The newly created segment partition tree.
		 */
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
		/**
		 * The line segment this line segment was derived
		 * from or null if this line segment was not derived.
		 */
		private LineSegment original = null;
		/**
		 * Whether the first end point of this line was
		 * clipped, meaning the original line segment
		 * continues beyond the new first end point.
		 */
		private boolean p1Clipped = false;
		/**
		 * Whether the second end point of this line was
		 * clipped, meaning the original line segment
		 * continues beyond the new second end point.
		 */
		private boolean p2Clipped = false;
		/**
		 * Whether this segment is marked, used for animation.
		 */
		private boolean marked = false;

		/**
		 * Constructs a new line segment from the given line.
		 * @param line The line to initialise this line segment with.
		 */
		public LineSegment(Line2D line){
			this(line.getP1(), line.getP2());
		}
		
		/**
		 * Constructs a new line segment with the given end points. 
		 * @param p1 The first end point of the line.
		 * @param p2 The second end point of the line.
		 */
		public LineSegment(Point2D p1, Point2D p2){
			this.p1 = p1;
			this.p2 = p2;
		}
		
		/**
		 * Derives a new line from this line segment that is exactly the
		 * part of this line that lies within the given rectangle.
		 * @param bounds The rectangle to bound this line segment with.
		 * @return The part of this line segment within the given rectangle
		 *         or <code>null</code> if this line segment was not (partially)
		 *         contained within the given rectangle.
		 */
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
				line.original = getOriginalSegment();
				return line;
			}
		}
		
		/**
		 * Computes the intersection of this line segment with
		 * the given rectangle in the given direction.
		 * @param tl The top left point of the rectangle.
		 * @param tr The top right point of the rectangle.
		 * @param bl The bottom left point of the rectangle.
		 * @param br The bottom right point of the rectangle.
		 * @param out The outcode of the either line segment point
		 *        to determine the appropriate intersection. This
		 *        value cannot be 0
		 * @return The intersection of the line segment and the given
		 *         rectangle at the given exit point.
		 * @see Rectangle2D#outcode(Point2D)
		 */
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
				line.original = getOriginalSegment();
				return line;
			}else{
				LineSegment line = new LineSegment(p1, intersection);
				line.p2Clipped = true;
				line.p1Clipped = p1Clipped;
				line.original = getOriginalSegment();
				return line;
			}
		}
		
		/**
		 * Gets the original line segment this line
		 * segment was derived from. If this line segment
		 * was obtained though a series of derivations then
		 * the original segment is returned and not an
		 * intermediate derivative line.
		 * @return The line segment this line segment was
		 *         derived from or this line segment if this
		 *         line was not derived.
		 */
		private LineSegment getOriginalSegment(){
			return original == null ? this : original;
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
