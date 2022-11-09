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
		while(!turnEnd){
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
