package dev.roanh.convexmerger.player;

import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState.GameStateListener;

/**
 * Represents a player controlled by a local human.
 * @author Roan
 */
public class HumanPlayer extends Player implements GameStateListener{
	/**
	 * Boolean indicating the end of a player turn.
	 */
	private volatile boolean turnEnd;

	/**
	 * Constructs a new human player with the given name.
	 * @param name The name of the player.
	 */
	public HumanPlayer(String name){
		super(true, false, name);
	}

	@Override
	public synchronized boolean executeMove() throws InterruptedException{
		synchronized(state){
			if(!state.stream().filter(ConvexObject::canClaim).findAny().isPresent() && !stream().filter(this::hasMergeFrom).findAny().isPresent()){
				return false;
			}
		}
		
		turnEnd = false;
		while(turnEnd){
			wait();
		}
		
		return true;
	}

	@Override
	public synchronized void claim(Player player, ConvexObject obj){
		turnEnd = true;
		notify();
	}

	@Override
	public synchronized void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed){
		turnEnd = true;
		notify();
	}

	@Override
	public void end(){
	}

	@Override
	public void abort(){
	}
}
