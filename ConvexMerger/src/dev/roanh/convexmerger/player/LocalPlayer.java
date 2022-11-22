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
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 */
	protected boolean claimNewObject() throws InterruptedException{
		target = findLargestUnownedObject();
		if(target == null){
			return false;
		}else{
			target = state.claimObject(target).getResult();
			return true;
		}
	}
}
