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

import dev.roanh.convexmerger.game.ClaimResult;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.ui.GamePanel;

/**
 * Represents a player controlled by a local human.
 * @author Roan
 */
public class HumanPlayer extends Player{
	/**
	 * The panel visualising the game this player is playing in.
	 */
	private GamePanel game;
	/**
	 * The objected selected by the player as the next object to claim.
	 * @see #clickPoint
	 */
	private volatile ConvexObject nextClaim = null;
	/**
	 * The point clicked to claim the next object to claim.
	 * @see #nextClaim
	 */
	private volatile Point2D clickPoint = new Point2D.Double();

	/**
	 * Constructs a new human player with the given name.
	 * @param name The name of the player.
	 */
	public HumanPlayer(String name){
		super(true, false, name);
	}
	
	/**
	 * Sets the game panel visualising the game this player is playing in.
	 * @param game The game panel for this player.
	 */
	public void setGamePanel(GamePanel game){
		this.game = game;
	}
	
	/**
	 * Checks if this player currently requires input via the UI.
	 * @return True if this player requires UI interaction.
	 */
	public boolean requireInput(){
		return nextClaim == null;
	}
	
	/**
	 * Handles a new object to try to claim for this player.
	 * @param claimed The object that was attempted to be claimed.
	 * @param location The point clicked to claim the given object.
	 */
	public synchronized void handleClaim(ConvexObject claimed, Point2D location){
		nextClaim = claimed;
		clickPoint = location;
		notify();
	}

	@Override
	public synchronized boolean executeMove() throws InterruptedException{
		if(!state.stream().filter(ConvexObject::canClaim).findAny().isPresent() && !stream().filter(this::hasMergeFrom).findAny().isPresent()){
			return false;
		}
		
		ClaimResult result;
		do{
			while(nextClaim == null){
				wait();
			}
			
			result = state.claimObject(nextClaim, clickPoint);
			game.setMessage(result.getMessage());
			nextClaim = null;
		}while(!result.hasResult());
		
		return true;
	}
}
