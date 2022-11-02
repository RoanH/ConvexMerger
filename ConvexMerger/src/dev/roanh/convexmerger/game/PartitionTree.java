package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
	
	public abstract boolean isLeafCell();
	
	public abstract List<S> getChildren();
	
	public abstract S getParent();
	
	//public abstract boolean queryLine(Line2D line, Consumer<S> callback);
	
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
