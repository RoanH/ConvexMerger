package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.Constants;

public class GameState{
	private List<ConvexObject> objects = new ArrayList<ConvexObject>();
	private List<Player> players = new ArrayList<Player>();
	private VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);
	private int activePlayer = 0;
	private ConvexObject selected = null;
	
	public GameState(List<ConvexObject> objects, List<Player> players){
		this.objects = objects;
		this.players = players;
		objects.forEach(decomp::addObject);
		decomp.rebuild();
	}
	
	public void claimObject(ConvexObject obj){
		System.out.println("Handle claim: " + obj);
		if(!obj.isOwned()){
			obj.setOwner(getActivePlayer());
			if(selected != null){
				selected.setSelected(false);
				if(mergeObjects(obj, selected)){
					endTurn();
				}else{
					//TODO show failed to merge retry
				}
			}else{
				endTurn();
			}
		}else if(getActivePlayer().equals(obj.getOwner())){
			if(selected == null){
				obj.setSelected(true);
				selected = obj;
			}else{
				if(obj.equals(selected)){
					obj.setSelected(false);
					selected = null;
				}else{
					selected.setSelected(false);
					if(mergeObjects(obj, selected)){
						endTurn();
					}else{
						//TODO show failed to merge retry
					}
				}
			}
		}else{
			//TODO show cannot claim opponent object
		}
	}
	
	private void endTurn(){
		selected = null;
		//TODO next
	}
	
	private boolean mergeObjects(ConvexObject first, ConvexObject second){
		//TODO
		
		return true;//merge success
	}
	
	public Player getActivePlayer(){
		return players.get(activePlayer);
	}
	
	public ConvexObject getObject(double x, double y){
		System.out.println("get");
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
