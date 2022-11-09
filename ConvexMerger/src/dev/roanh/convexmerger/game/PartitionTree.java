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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Abstract base class for partition tree implementations.
 * @author Roan
 * @param <T> The metadata type.
 * @param <S> The partition tree type.
 * @see KDTree
 * @see ConjugationTree
 */
public abstract class PartitionTree<T, S extends PartitionTree<T, S>>{
	/**
	 * The data stored in this cell.
	 */
	private List<T> data = new ArrayList<T>();
	/**
	 * Whether this partition tree is marked or not, used for animation.
	 */
	private boolean marked = false;
	
	/**
	 * Sets whether this partition tree is marked or not.
	 * This is used for animations.
	 * @param marked True to mark this tree.
	 */
	public void setMarked(boolean marked){
		this.marked = marked;
	}
	
	/**
	 * Adds an object to store at this tree node.
	 * @param obj The object to store.
	 */
	public void addData(T obj){
		data.add(obj);
	}
	
	/**
	 * Gets the data stored at this tree node.
	 * @return The data stored.
	 */
	public List<T> getData(){
		return data;
	}
	
	/**
	 * Renders this partition tree.
	 * @param g The graphics context to use.
	 */
	public void render(Graphics2D g){
		if(marked){
			g.setColor(new Color(255, 0, 0, 50));
			g.fill(getShape());
		}
	}
	
	/**
	 * Gets the height of the partition tree this
	 * cell is a part of. A value of 0 indicates
	 * that this tree only has a root node.
	 * @return The height of this partition tree.
	 */
	public int getHeight(){
		return streamLeafCells().mapToInt(S::getDepth).max().orElse(0);
	}
	
	/**
	 * Gets the depth of this cell in the partition tree.
	 * A value of 0 indicates that this cell is the root node.
	 * @return The depth of this partition tree node.
	 */
	public int getDepth(){
		S parent = getParent();
		return parent == null ? 0 : 1 + parent.getDepth();
	}
	
	/**
	 * Streams all the leaf cells in this tree.
	 * @return A stream of all partition tree leaf cells.
	 */
	public Stream<S> streamLeafCells(){
		if(isLeafCell()){
			return Stream.of(getSelf());
		}else{
			Stream<S> stream = Stream.empty();
			for(S child : getChildren()){
				stream = Stream.concat(stream, child.streamLeafCells());
			}
			return stream;
		}
	}
	
	/**
	 * Streams all the cells (both leaf and internal) in this tree.
	 * @return A stream of all partition tree cells.
	 */
	public Stream<S> streamCells(){
		Stream<S> stream = Stream.of(getSelf());
		for(S child : getChildren()){
			stream = Stream.concat(stream, child.streamCells());
		}
		return stream;
	}
	
	/**
	 * Checks if this tree node is a leaf cell.
	 * @return True if this tree node is a leaf cell.
	 */
	public abstract boolean isLeafCell();
	
	/**
	 * Gets the child nodes of this tree node.
	 * @return The direct child nodes of this node.
	 */
	public abstract List<S> getChildren();
	
	/**
	 * Gets the parent tree node of this node.
	 * @return The parent node of this node or
	 *         <code>null</code> if this is node
	 *         is the root node of the tree.
	 */
	public abstract S getParent();
	
	/**
	 * Gets the shape defining the boundary of this tree cell.
	 * @return The partition tree cell bounds.
	 */
	public abstract Shape getShape();
	
	/**
	 * Gets 'this' partition tree.
	 * @return This partition tree.
	 */
	public abstract S getSelf();
}
