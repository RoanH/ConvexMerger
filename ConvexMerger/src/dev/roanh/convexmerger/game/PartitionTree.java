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
	
	public void addData(T obj){
		data.add(obj);
	}
	
	public List<T> getData(){
		return data;
	}
	
	public abstract void render(Graphics2D g);
	
	public abstract Stream<S> streamLeafCells();
	
	public abstract boolean isLeafCell();
	
	public abstract List<S> getChildren();
}
