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
package dev.roanh.convexmerger.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.ui.Theme;

/**
 * Implementation of a kd-tree for k = 2.
 * @author Roan
 * @param <T> The per cell item data type.
 */
public class KDTree<T> extends PartitionTree<T, KDTree<T>>{
	/**
	 * The parent kd-tree this kd-tree is a cell in. Will
	 * be <code>null</code> if this is the root node of the tree.
	 */
	private KDTree<T> parent = null;
	/**
	 * The low child of this kd-tree. This is the cell
	 * containing the points lower than the point for this
	 * KD tree. Will be <code>null</code> if this is a leaf cell.
	 */
	private KDTree<T> low = null;
	/**
	 * The high child of this kd-tree. This is the cell
	 * containing the points higher than the point for this
	 * kd-tree. Will be <code>null</code> if this is a leaf cell.
	 */
	private KDTree<T> high = null;
	/**
	 * The point defining the line splitting this kd-tree
	 * cell into a low and high cell. Will be <code>null</code>
	 * if this is a leaf cell.
	 */
	private Point2D point = null;
	/**
	 * The way this kd-tree node is split into a low and
	 * high cell. If true the splitting line is vertical
	 * and otherwise the splitting line is horizontal.
	 */
	private boolean xAxis;
	/**
	 * The axis aligned bounding rectangle defining the
	 * bounds of this kd-tree cell.
	 */
	private Rectangle2D bounds = null;
	
	/**
	 * Constructs a new kd-tree for the given point set.
	 * @param points The points to build a kd-tree from.
	 */
	public KDTree(List<Point2D> points){
		this(null, points, true);
	}
	
	/**
	 * Constructs a new child kd-tree node with the given parent
	 * node, point set to build a sub tree with and axis to split on.
	 * @param parent The parent node of this kd-tree node.
	 * @param points The points to build a kd-subtree from.
	 * @param xAxis True if this cell should be split based on the
	 *        X coordinate of points, false to split on the Y coordinate.
	 */
	private KDTree(KDTree<T> parent, List<Point2D> points, boolean xAxis){
		this.parent = parent;
		this.xAxis = xAxis;
		if(points != null && !points.isEmpty()){
			points.sort(Comparator.comparing(xAxis ? Point2D::getX : Point2D::getY));
			this.point = points.get(points.size() / 2);
			
			if(points.size() > 1){
				low = new KDTree<T>(this, new ArrayList<Point2D>(points.subList(0, points.size() / 2)), !xAxis);
			}else{
				low = new KDTree<T>(this, null, !xAxis);
			}
			
			if(points.size() > 2){
				high = new KDTree<T>(this, new ArrayList<Point2D>(points.subList(points.size() / 2 + 1, points.size())), !xAxis);
			}else{
				high = new KDTree<T>(this, null, !xAxis);
			}
		}
	}
	
	/**
	 * Gets the point defining the line dividing this kd-tree
	 * node into two child nodes.
	 * @return The point dividing this tree node.
	 */
	public Point2D getPoint(){
		return point;
	}
	
	/**
	 * Gets the child node containing the low value
	 * points stored in the children of this node.
	 * @return The low value child node.
	 */
	public KDTree<T> getLowNode(){
		return low;
	}
	
	/**
	 * Gets the child node containing the high value
	 * points stored in the children of this node.
	 * @return The high value child node.
	 */
	public KDTree<T> getHighNode(){
		return high;
	}

	/**
	 * Checks if the given line intersects this kd-tree node.
	 * @param line The line to check for intersection.
	 * @return True if the given line intersects this kd-tree node.
	 */
	public boolean intersects(Line2D line){
		return getBounds().intersectsLine(line);
	}
	
	/**
	 * Gets the bounding rectangle of this kd-tree node.
	 * @return The bounding rectangle of this kd-tree node.
	 */
	public Rectangle2D getBounds(){
		if(bounds == null){
			if(parent == null){
				bounds = new Rectangle2D.Double(0.0D, 0.0D, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			}else if(this == parent.low){
				Rectangle2D parentBounds = parent.getBounds();
				if(xAxis){
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parentBounds.getMinY(),
						parentBounds.getWidth(),
						parent.point.getY() - parentBounds.getMinY()
					);
				}else{
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parentBounds.getMinY(),
						parent.point.getX() - parentBounds.getMinX(),
						parentBounds.getHeight()
					);
				}
			}else if(this == parent.high){
				Rectangle2D parentBounds = parent.getBounds();
				if(xAxis){
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parent.point.getY(),
						parentBounds.getWidth(),
						parentBounds.getMaxY() - parent.point.getY()
					);
				}else{
					bounds = new Rectangle2D.Double(
						parent.point.getX(),
						parentBounds.getMinY(),
						parentBounds.getMaxX() - parent.point.getX(),
						parentBounds.getHeight()
					);
				}
			}
		}
		
		return bounds;
	}
	
	@Override
	public void render(Graphics2D g){
		super.render(g);
		
		if(!isLeafCell()){
			low.render(g);
			high.render(g);
			
			Rectangle2D bounds = getBounds();
			int c = Math.max(0, 255 - getDepth() * 25);
			g.setColor(new Color(0, c, c));
			g.setStroke(Theme.BORDER_STROKE);
			if(xAxis){
				g.draw(new Line2D.Double(point.getX(), bounds.getMinY(), point.getX(), bounds.getMaxY()));
			}else{
				g.draw(new Line2D.Double(bounds.getMinX(), point.getY(), bounds.getMaxX(), point.getY()));
			}
			
			g.setColor(Color.BLUE);
			g.fill(new Ellipse2D.Double(point.getX() - 2.5D, point.getY() - 2.5D, 5.0D, 5.0D));
		}
	}
	
	@Override
	public Shape getShape(){
		return getBounds();
	}
	
	@Override
	public boolean isLeafCell(){
		return point == null;
	}
	
	@Override
	public List<KDTree<T>> getChildren(){
		return isLeafCell() ? Collections.emptyList() : Arrays.asList(low, high);
	}
	
	@Override
	public KDTree<T> getParent(){
		return parent;
	}
	
	@Override
	public KDTree<T> getSelf(){
		return this;
	}
}
