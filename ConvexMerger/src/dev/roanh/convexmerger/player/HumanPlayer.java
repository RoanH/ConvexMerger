package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.game.ConvexObject;

/**
 * Represents a player controlled by a local human.
 * @author Roan
 */
public class HumanPlayer extends Player{

	/**
	 * Constructs a new human player with the given name.
	 * @param name The name of the player.
	 */
	public HumanPlayer(String name){
		super(true, false, name);
	}

	@Override
	public boolean executeMove() throws InterruptedException{
		synchronized(state){
			if(state.stream().filter(ConvexObject::canClaim).findAny().isPresent() || stream().filter(this::hasMergeFrom).findAny().isPresent()){
				state.wait();
				return true;
			}else{
				return false;
			}
		}
	}
}
