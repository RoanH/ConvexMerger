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
import dev.roanh.convexmerger.ui.Theme;

/**
 * Implementation of a conjugation tree inspired by the
 * description in a paper by Herbert Edelsbrunner and Emo Welzl.
 * @author Roan
 * @param <T> The metadata storage type.
 * @see <a href="https://doi.org/10.1016/0020-0190(86)90088-8">Edelsbrunner, Herbert and Welzl, Emo, 
 *      "Halfplanar range search in linear space and O(n^0.695) query time", in Information Processing
 *      Letters, vol. 23, 1986, pp. 289-293</a>
 */
public class ConjugationTree<T> extends PartitionTree<T, ConjugationTree<T>>{
	/**
	 * The parent node of this tree node.
	 */
	private ConjugationTree<T> parent;
	/**
	 * The bisector of this tree node (a conjugate of the parent bisector).
	 */
	private Line2D bisector;
	/**
	 * The points in this cell on the bisector line.
	 */
	private List<Point2D> on = new ArrayList<Point2D>(2);
	/**
	 * The left child node of this node (containing CCW -1 points).
	 */
	private ConjugationTree<T> left;
	/**
	 * The right child node of this node (containing CCW 1 points).
	 */
	private ConjugationTree<T> right;
	/**
	 * The convex hull defining the bounds of this tree cell.
	 */
	private List<Point2D> hull;
	/**
	 * The shape defining the bounds of this cell as constructed
	 * from the convex hull points in {@link #hull}.
	 */
	private Path2D shape;
	
	/**
	 * Constructs a new conjugation tree storing the given point set.
	 * @param points The point set to store.
	 */
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
	
	/**
	 * Constructs a new child conjugation tree node with the given parent node,
	 * point set, conjugate bisector line and convex hull.
	 * @param parent The parent node for this conjugation tree node.
	 * @param points The points stored at or below this tree node.
	 * @param on The points on the bisector for this tree node.
	 * @param bisector The bisector for this tree node (conjugate of the parent bisector).
	 * @param hull The convex hull for this tree cell.
	 */
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
	
	/**
	 * Gets all the points located on the bisector for this tree cell.
	 * @return The points on the bisector of this tree node.
	 */
	public List<Point2D> getPoints(){
		return on;
	}
	
	/**
	 * Gets the bisector line for this tree node. This
	 * line splits this tree cell in two halves with
	 * both about the same number of points.
	 * @return The bisector for this tree cell.
	 */
	public Line2D getBisector(){
		return bisector;
	}
	
	/**
	 * Gets the left child node of this tree node (CCW -1).
	 * @return The left child node of <code>null</code>
	 *         if this node is a leaf.
	 */
	public ConjugationTree<T> getLeftChild(){
		return left;
	}
	
	/**
	 * Gets the right child node of this tree node (CCW 1).
	 * @return The right child node of <code>null</code>
	 *         if this node is a leaf.
	 */
	public ConjugationTree<T> getRightChild(){
		return right;
	}
	
	/**
	 * Gets the centroid of this conjugation tree node.
	 * @return The centroid.
	 */
	public Point2D getCentroid(){
		return ConvexUtil.computeCentroid(hull);
	}
	
	@Override
	public void render(Graphics2D g){
		super.render(g);
		
		if(!isLeafCell()){
			left.render(g);
			right.render(g);
			
			int c = Math.max(0, 255 - getDepth() * 25);
			g.setColor(new Color(0, c, c));
			g.setStroke(Theme.BORDER_STROKE);
			g.draw(bisector);
			
			g.setColor(Color.BLUE);
			for(Point2D p : on){
				g.fill(new Ellipse2D.Double(p.getX() - 2.5D, p.getY() - 2.5D, 5.0D, 5.0D));
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
	
	@Override
	public ConjugationTree<T> getSelf(){
		return this;
	}

	/**
	 * Clips the given line segment to be fully contained within the bounds
	 * of the given conjugation tree node. The given point is assumed to lie
	 * on the given line segment and within the given tree node.
	 * @param node The tree node to contain the line within.
	 * @param line The line to clip.
	 * @param on A point on the given line and within the tree node.
	 * @return The clipped line segment.
	 */
	private static final Line2D clipLine(ConjugationTree<?> node, Line2D line, Point2D on){
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
	private static final Line2D extendLine(Line2D line){
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