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
	protected ConvexObject target = null;

	public LocalPlayer(){
		super(false, "Elaina");
	}
	
	protected LocalPlayer(String name){
		super(false, name);
	}
	
	@Override
	public boolean executeMove(){
		if(target == null){
			return claimNewObject();
		}
		
		MergeOption merge = findBestMergeFrom(target);
		if(merge != null){
			target = merge.execute();
			return true;
		}

		return claimNewObject();
	}
	
	protected boolean claimNewObject(){
		target = findLargestUnownedObject();
		if(target == null){
			return false;
		}else{
			target = state.claimObject(target).getResult();
			return true;
		}
	}
}
