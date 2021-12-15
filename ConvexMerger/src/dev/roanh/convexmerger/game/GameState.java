package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.Constants;

public class GameState{
	private List<ConvexObject> objects = new ArrayList<ConvexObject>();
	private List<Player> players = new ArrayList<Player>();
	private VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);
	
	public GameState(List<ConvexObject> objects, List<Player> players){
		this.objects = objects;
		this.players = players;
		objects.forEach(decomp::addObject);
		decomp.rebuild();
	}
	
	
	public ConvexObject getObject(double x, double y){
		//TODO remove when decomp done
		for(ConvexObject obj : objects){
			if(obj.contains(x, y)){
				return obj;
			}
		}
		return decomp.queryObject(x, y);
	}
	
	public List<ConvexObject> getObjects(){
		return objects;
	}
	
	public List<Line2D> getVerticalDecompLines(){
		return decomp.getDecompLines();
	}
}
