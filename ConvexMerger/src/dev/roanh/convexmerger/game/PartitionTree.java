package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class PartitionTree<T, S extends PartitionTree<T, ?>>{
	/**
	 * The data stored in this cell.
	 */
	private List<T> data = new ArrayList<T>();
	protected boolean marked = false;//anim
	
	public void setMarked(boolean marked){
		this.marked = marked;
	}
	
	public void addData(T obj){
		data.add(obj);
	}
	
	public List<T> getData(){
		return data;
	}
	
	public abstract void render(Graphics2D g);
	
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
	@SuppressWarnings("unchecked")
	public Stream<S> streamLeafCells(){
		if(isLeafCell()){
			return Stream.of((S)this);
		}else{
			Stream<S> stream = Stream.empty();
			for(S child : getChildren()){
				stream = (Stream<S>)Stream.concat(stream, child.streamLeafCells());
			}
			return stream;
		}
	}
	
	/**
	 * Streams all the cells (both leaf and internal) in this tree.
	 * @return A stream of all partition tree cells.
	 */
	@SuppressWarnings("unchecked")
	public Stream<S> streamCells(){
		Stream<S> stream = (Stream<S>)Stream.of(this);
		for(S child : getChildren()){
			stream = (Stream<S>)Stream.concat(stream, child.streamCells());
		}
		return stream;
	}
}
