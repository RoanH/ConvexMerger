package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.SegmentPartitionTree.LineSegment;
import dev.roanh.convexmerger.ui.Theme;

public class ConjugationTree<T> extends PartitionTree<T, ConjugationTree<T>>{
	private ConjugationTree<T> parent;
	private Line2D bisector;
	private List<Point2D> on = new ArrayList<Point2D>(2);
	private ConjugationTree<T> left;
	private ConjugationTree<T> right;
	private List<Point2D> hull;
	private Path2D shape;
	
	public ConjugationTree(List<Point2D> points){
		//only root bisector finding requires O(n log n) time
		points.sort(Comparator.comparingDouble(Point2D::getX));
		int idx = points.size() / 2;
		
		double mid = points.get(idx).getX();
		bisector = new Line2D.Double(mid, 0.0D, mid, Constants.PLAYFIELD_HEIGHT);
		hull = Arrays.asList(
			new Point2D.Double(0.0D, 0.0D),
			new Point2D.Double(Constants.PLAYFIELD_WIDTH, 0.0D),
			new Point2D.Double(Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT),
			new Point2D.Double(0.0D, Constants.PLAYFIELD_HEIGHT)
		);
		
		//by assumption we have few points on the bisector, but for other applications this could be false
		List<Point2D> leftPoints = new ArrayList<Point2D>(idx);
		List<Point2D> rightPoints = new ArrayList<Point2D>(idx);
		on.add(points.get(idx));
		for(int i = idx - 1; i >= 0; i--){
			Point2D p = points.get(i);
			if(p.getX() >= mid){
				on.add(p);
			}else{
				leftPoints.add(p);
			}
		}
		for(int i = idx + 1; i < points.size(); i++){
			Point2D p = points.get(i);
			if(p.getX() <= mid){
				on.add(p);
			}else{
				rightPoints.add(p);
			}
		}
		
		//construct children
		ConjugateData data = computeConjugate(leftPoints, rightPoints, this);
		data.conjugate = extendLine(data.conjugate);
		List<List<Point2D>> hulls = ConvexUtil.splitHull(hull, bisector);
		left = new ConjugationTree<T>(this, leftPoints, data.leftOn, data.conjugate, hulls.get(0));
		right = new ConjugationTree<T>(this, rightPoints, data.rightOn, data.conjugate, hulls.get(1));
		
		constructShape();
	}
	
	private ConjugationTree(ConjugationTree<T> parent, List<Point2D> points, Point2D on, Line2D bisector, List<Point2D> hull){
		this.parent = parent;
		this.bisector = bisector;
		if(on != null){
			this.on.add(on);
		}
		this.hull = hull;
		
		List<Point2D> leftPoints = new ArrayList<Point2D>(points.size() / 2);
		List<Point2D> rightPoints = new ArrayList<Point2D>(points.size() / 2);
		for(Point2D p : points){
			if(on != p){
				int rel = bisector.relativeCCW(p);
				switch(rel){
				case -1:
					leftPoints.add(p);
					break;
				case 1:
					rightPoints.add(p);
					break;
				case 0:
					this.on.add(p);
					break;
				default:
					assert false : "Impossible CCW";
				}
			}
		}
		
		//construct children
		if(!leftPoints.isEmpty() || !rightPoints.isEmpty()){
			ConjugateData data = computeConjugate(leftPoints, rightPoints, this);
			data.conjugate = clipLine(parent, extendLine(data.conjugate), data.leftOn == null ? data.rightOn : data.leftOn);
			List<List<Point2D>> hulls = ConvexUtil.splitHull(hull, bisector);
			left = new ConjugationTree<T>(this, leftPoints, data.leftOn, data.conjugate, hulls.get(0));
			right = new ConjugationTree<T>(this, rightPoints, data.rightOn, data.conjugate, hulls.get(1));
		}else if(bisector != null){
			List<List<Point2D>> hulls = ConvexUtil.splitHull(hull, bisector);
			left = new ConjugationTree<T>(this, leftPoints, null, null, hulls.get(0));
			right = new ConjugationTree<T>(this, rightPoints, null, null, hulls.get(1));
		}
		
		constructShape();
	}
	
	/**
	 * Constructs the shape object for the bounds of this object.
	 */
	private void constructShape(){
		shape = new Path2D.Double(Path2D.WIND_NON_ZERO, hull.size());
		shape.moveTo(hull.get(0).getX(), hull.get(0).getY());
		for(int i = 1; i < hull.size(); i++){
			shape.lineTo(hull.get(i).getX(), hull.get(i).getY());
		}
		shape.closePath();
	}
	
	//on points
	public List<Point2D> getPoints(){
		return on;
	}
	
	public Line2D getBisector(){
		return bisector;
	}
	
	public ConjugationTree<T> getLeftChild(){
		return left;
	}
	
	public ConjugationTree<T> getRightChild(){
		return right;
	}
	
	public Point2D getCentroid(){
		//TODO after decomp merge
		return null;
	}
	
	@Override
	public void render(Graphics2D g){
		if(marked){
			g.setColor(new Color(255, 0, 0, 50));
			g.fill(shape);
		}
		
		//TODO no
		for(Object obj : getData()){
			LineSegment s = (LineSegment)obj;
			g.setStroke(Theme.POLY_STROKE);
			g.setColor(s.marked ? Color.RED : Color.BLACK);
			g.draw(s);
		}
		
		if(!isLeafCell()){
			int c = Math.max(0, 255 - getDepth() * 25);
			g.setColor(new Color(0, c, c));
			g.setStroke(Theme.BORDER_STROKE);
			g.draw(bisector);
			
			g.setColor(Color.RED);
			for(Point2D p : on){
				g.fill(new Ellipse2D.Double(p.getX() - 5, p.getY() - 5, 10, 10));
			}
			
			if(left != null){
				left.render(g);
			}
			if(right != null){
				right.render(g);
			}
		}
	}
	
	@Override
	public Shape getShape(){
		return shape;
	}
	
	@Override
	public ConjugationTree<T> getParent(){
		return parent;
	}

	@Override
	public boolean isLeafCell(){
		return bisector == null;
	}
	
	@Override
	public List<ConjugationTree<T>> getChildren(){
		return isLeafCell() ? Collections.emptyList() : Arrays.asList(left, right);
	}

	//given node + ancestors
	private static Line2D clipLine(ConjugationTree<?> node, Line2D line, Point2D on){
		while(node != null){
			Point2D intercept = ConvexUtil.interceptClosed(line.getP1(), line.getP2(), node.bisector.getP1(), node.bisector.getP2());
			if(intercept != null){
				int onCCW = node.bisector.relativeCCW(on);
				if(onCCW == node.bisector.relativeCCW(line.getP1())){
					line = new Line2D.Double(line.getP1(), intercept);
				}else{//p2
					line = new Line2D.Double(intercept, line.getP2());
				}
			}

			node = node.parent;
		}
		return line;
	}

	/**
	 * Extends the given closed line segment to the geometric bounds of this
	 * partition tree as defined by {@link Constants#PLAYFIELD_HEIGHT} and
	 * {@link Constants#PLAYFIELD_WIDTH}. The result being a new closed line
	 * segment ending on the bounds of the partition tree.
	 * @param line The line to extend.
	 * @return The line segment extended to the structure bounds.
	 */
	private static Line2D extendLine(Line2D line){
		if(line.getX1() == line.getX2()){
			return new Line2D.Double(line.getX1(), 0.0D, line.getX2(), Constants.PLAYFIELD_HEIGHT);
		}else{
			double coef = (line.getY2() - line.getY1()) / (line.getX2() - line.getX1());
			double base = line.getY1() - line.getX1() * coef;
			double max = ConvexUtil.clamp(0.0D, Constants.PLAYFIELD_WIDTH, (Constants.PLAYFIELD_HEIGHT - base) / coef);
			double min = ConvexUtil.clamp(0.0D, Constants.PLAYFIELD_WIDTH, -base / coef);
			return new Line2D.Double(min, base + coef * min, max, base + coef * max);
		}
	}

	/**
	 * Computes a conjugate line for the given point sets and with
	 * the given parent conjugation tree node. All the points in the
	 * left set are left (CCW -1) of the bisector of the given parent
	 * tree node and all the points in the right set are right (CCW 1)
	 * of the bisector of the parent node. The left and right sets also
	 * have approximately the same size. The conjugate line computed by
	 * this subroutine will split the given left and right point sets
	 * in two subsets of approximately equal size and the conjugate line
	 * will also intersect the bisector of the given parent node. In addition,
	 * the conjugate line will pass through at least one point from the
	 * left point set and at least one point from the right point set.
	 * These points together with the computed conjugate line will be
	 * returned from this subroutine. If either of the given point sets
	 * is empty then the conjugate can be any line that splits the other
	 * point set and intersects the parent node bisector.
	 * @param left The left point set to split.
	 * @param right The right point set to split.
	 * @param parent The parent conjugation tree node.
	 * @return The computed conjugate line and its supporting points.
	 */
	private static final ConjugateData computeConjugate(List<Point2D> left, List<Point2D> right, ConjugationTree<?> parent){
		//TODO this is a naive temporary solution, @emu have fun
		
		ConjugateData data = new ConjugateData();
		for(Point2D p1 : left){
			for(Point2D p2 : right){
				Line2D conj = new Line2D.Double(p1, p2);
				
				int val = 0;
				for(Point2D lp : left){
					if(lp != p1){
						val += conj.relativeCCW(lp);
					}
				}
				
				if(Math.abs(val) > 1){
					continue;
				}
				
				val = 0;
				for(Point2D rp : right){
					if(p2 != rp){
						val += conj.relativeCCW(rp);
					}
				}
				
				if(Math.abs(val) <= 1){
					data.conjugate = conj;
					data.leftOn = p1;
					data.rightOn = p2;
					return data;
				}
			}
		}
		
		//handle empty leaf cells
		if(left.isEmpty()){
			data.rightOn = right.get(0);
			data.conjugate = new Line2D.Double(parent.on.get(0), right.get(0));
		}else{
			data.leftOn = left.get(0);
			data.conjugate = new Line2D.Double(parent.on.get(0), left.get(0));
		}

		return data;
	}
	
	/**
	 * Class holding data about a single conjugate line.
	 * @author Roan
	 */
	private static class ConjugateData{
		/**
		 * The conjugate line.
		 */
		private Line2D conjugate;
		/**
		 * The point from the left point set
		 * defining the conjugate line, possibly
		 * null if the left point set was empty.
		 */
		private Point2D leftOn;
		/**
		 * The point from the right point set
		 * defining the conjugate line, possibly
		 * null if the left point set was empty.
		 */
		private Point2D rightOn;
	}
}
