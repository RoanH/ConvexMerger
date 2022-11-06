package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;
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
	
	public abstract boolean isLeafCell();
	
	public abstract List<S> getChildren();
	
	public abstract S getParent();
	
	public int getHeight(){
		return streamLeafCells().mapToInt(S::getDepth).max().orElse(0);
	}
	
	//node depth, root = 0
	public int getDepth(){
		S parent = getParent();
		return parent == null ? 0 : 1 + parent.getDepth();
	}
	
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
	
	@SuppressWarnings("unchecked")
	public Stream<S> streamCells(){
		Stream<S> stream = (Stream<S>)Stream.of(this);
		for(S child : getChildren()){
			stream = (Stream<S>)Stream.concat(stream, child.streamCells());
		}
		return stream;
	}
}
