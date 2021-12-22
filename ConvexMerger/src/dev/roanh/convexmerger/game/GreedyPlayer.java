package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public class GreedyPlayer extends Player{

	protected GreedyPlayer(String name){
		super(false, "Isla", Color.WHITE);
	}
	
	public void executeMove(GameState state){
		List<ConvexObject> owned = state.stream().filter(this::owns).collect(Collectors.toList());
		
		//find the single largest object
		ConvexObject max = null;
		for(ConvexObject obj : state.getObjects()){
			if(!obj.isOwned()){
				if(max == null || obj.getArea() > max.getArea()){
					max = obj;
				}
			}
		}
		
		//merge any of our owned objects with something else to get the largest area
		if(!owned.isEmpty()){
			ConvexObject first = null;
			ConvexObject second = null;
			double increase = 0.0D;
			
			for(ConvexObject obj : owned){
				for(ConvexObject other : state.getObjects()){
					if(!other.isOwnedBy(this)){
						ConvexObject combined = obj.merge(state, other);
						if(combined != null){
							if(first == null){
								first = obj;
								second = other;
							}else{
								double area = combined.getArea();
								
								if(other.isOwnedBy(this)){
									area -= other.getArea();
								}
								if(obj.isOwnedBy(this)){
									area -= obj.getArea();
								}
								
								if(area > increase){
									increase = area;
									first = obj;
									second = other;
								}
							}
						}
					}
				}
			}
			
			if(increase >= max.getArea()){
				state.claimObject(first);
				state.claimObject(second);
			}
		}
		
		//claiming the largest object is best
		state.claimObject(max);
	}
}
