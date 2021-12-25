package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.game.ConvexObject;

/**
 * AI that focuses on maximising local area gain.
 * @author Roan
 */
public class LocalPlayer extends Player{
	/**
	 * Object currently being worked on.
	 */
	private ConvexObject target = null;

	public LocalPlayer(){
		super(false, "Elaina");
	}
	
	@Override
	public boolean executeMove(){
		if(target == null){
			return claimLargestUnowned();
		}
		
		MergeOption merge = findBestMergeFrom(target);
		if(merge != null){
			target = merge.execute();
			return true;
		}

		return claimLargestUnowned();
	}
	
	private boolean claimLargestUnowned(){
		target = findLargestUnownedObject();
		if(target == null){
			return false;
		}else{
			target = state.claimObject(target).getResult();
			return true;
		}
	}
}
