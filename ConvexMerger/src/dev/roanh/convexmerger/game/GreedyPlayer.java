package dev.roanh.convexmerger.game;

import java.util.List;
import java.util.stream.Collectors;

import dev.roanh.convexmerger.game.Theme.PlayerTheme;

/**
 * Simple AI that follows the greedy
 * strategy of maximising area again
 * in every turn.
 * @author Roan
 */
public class GreedyPlayer extends Player{

	protected GreedyPlayer(PlayerTheme theme){
		super(false, "Isla", theme);
	}
	
	public void executeMove(GameState state){
		System.out.println("START AI TURN");
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
					if(!other.isOwned() || (other.isOwnedBy(this) && !other.equals(obj))){
						ConvexObject combined = obj.merge(state, other);
						if(combined != null){
							double area = combined.getArea();
							
							if(other.isOwnedBy(this)){
								area -= other.getArea();
							}
							if(obj.isOwnedBy(this)){
								area -= obj.getArea();
							}
							
							for(ConvexObject check : state.getObjects()){
								if(!obj.equals(other) && !obj.equals(obj) && combined.contains(check)){
									if(check.isOwnedBy(this)){
										area -= check.getArea();
									}else if(check.isOwned()){
										//stealing is good
										area += check.getArea();
									}
								}
							}
							
							if(first == null || area > increase){
								increase = area;
								first = obj;
								second = other;
							}
						}
					}
				}
			}
			
			if(max == null || increase >= max.getArea()){
				if(first != null){
					state.claimObject(first);
					state.claimObject(second);
				}
				return;
			}
		}
		
		//claiming the largest object is best
		if(max != null){
			state.claimObject(max);
		}
	}
}
