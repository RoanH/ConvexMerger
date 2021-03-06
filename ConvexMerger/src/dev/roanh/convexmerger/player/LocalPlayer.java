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

	/**
	 * Constructs a new local player (Elaina).
	 */
	public LocalPlayer(){
		this("Elaina");
	}
	
	/**
	 * Constructs a new local player with the given name.
	 * @param name The player name.
	 */
	protected LocalPlayer(String name){
		super(true, true, name);
	}
	
	@Override
	public boolean executeMove() throws InterruptedException{
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
	
	/**
	 * Selects a new object to claim and start maximising area from.
	 * @return True if a new object was found, false otherwise (no move left).
	 */
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
