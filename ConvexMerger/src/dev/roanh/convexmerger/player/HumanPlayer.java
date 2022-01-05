package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.game.ConvexObject;

/**
 * Represents a player controlled by a local human.
 * @author Roan
 */
public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(){//TODO pass name
		super(true, false, "Player " + (ID++));
	}

	@Override
	public boolean executeMove(){
		if(state.stream().filter(ConvexObject::canClaim).findAny().isPresent() || stream().filter(this::hasMergeFrom).findAny().isPresent()){
			synchronized(state){
				try{
					state.wait();
				}catch(InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return true;
		}else{
			return false;
		}
	}
}
