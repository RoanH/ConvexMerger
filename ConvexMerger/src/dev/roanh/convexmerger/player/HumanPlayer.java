/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.player;

import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.game.ClaimResult;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState.GameStateListener;
import dev.roanh.convexmerger.ui.GamePanel;

/**
 * Represents a player controlled by a local human.
 * @author Roan
 */
public class HumanPlayer extends Player implements GameStateListener{
	/**
	 * Boolean indicating the end of a player turn.
	 */
	@Deprecated
	private volatile boolean turnEnd;
	private GamePanel game;
	
	private volatile ConvexObject nextClaim = null;
	private volatile Point2D clickPoint = new Point2D.Double();

	/**
	 * Constructs a new human player with the given name.
	 * @param name The name of the player.
	 */
	public HumanPlayer(String name){
		super(true, false, name);
	}
	
	public void setGamePanel(GamePanel game){
		this.game = game;
	}

	@Override
	public synchronized boolean executeMove() throws InterruptedException{
		synchronized(state){
			if(!state.stream().filter(ConvexObject::canClaim).findAny().isPresent() && !stream().filter(this::hasMergeFrom).findAny().isPresent()){
				return false;
			}
		}
		
//		turnEnd = false;
//		while(!turnEnd){
//			wait();
//		}
		
		while(true){
			while(nextClaim == null){
				wait();
			}
			
			ClaimResult result = state.claimObject(nextClaim, clickPoint);
			game.setMessage(result.getMessage());
			
			//TODO set message
			if(result.hasResult()){
				break;
			}else{
				nextClaim = null;
			}
		}
		
		nextClaim = null;
		return true;
	}
	
	@Override
	public boolean requireInput(){
		return nextClaim == null;
	}
	
	//TODO somehow pass message -- consumer?
	public synchronized void handleClaim(ConvexObject claimed, Point2D location){
		nextClaim = claimed;
		clickPoint = location;
		notify();
	}

	@Override
	public synchronized void claim(Player player, ConvexObject obj){
		turnEnd = true;
//		notify();
	}

	@Override
	public synchronized void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed){
		
		turnEnd = true;
//		notify();
	}

	@Override
	public void end(){
	}

	@Override
	public void abort(){
	}
}
