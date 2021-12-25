package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.game.ConvexObject;

public class LocalPlayer extends Player{
	private ConvexObject target = null;

	public LocalPlayer(){
		super(false, "Elaina");
	}
	
	@Override
	public boolean executeMove(){
		if(target == null){
			return claimLargestUnowned();
		}
		
		
		
		
		
		
		
		
		
		
		return false;
	}
	
	private boolean claimLargestUnowned(){
		target = findLargestUnownedObject();
		if(target == null){
			return false;
		}else{
			state.claimObject(target);
			return true;
		}
	}
}
