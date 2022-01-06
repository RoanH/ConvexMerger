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
	public boolean executeMove(){
		if(state.stream().filter(ConvexObject::canClaim).findAny().isPresent() || stream().filter(this::hasMergeFrom).findAny().isPresent()){
			synchronized(state){
				try{
					state.wait();
				}catch(InterruptedException e){
					//cannot happen, we never interrupt threads
				}
			}
			return true;
		}else{
			return false;
		}
	}
}
