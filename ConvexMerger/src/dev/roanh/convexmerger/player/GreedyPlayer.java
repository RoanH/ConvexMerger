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
import java.util.stream.Collectors;

import dev.roanh.convexmerger.game.ConvexObject;

/**
 * Simple AI that follows the greedy
 * strategy of maximising relative
 * area gain in every turn.
 * @author Roan
 */
public class GreedyPlayer extends Player{

	/**
	 * Constructs a new greedy player (Isla).
	 */
	public GreedyPlayer(){
		super(true, true, "Isla");
	}
	
	/**
	 * Constructs a new greedy player with the given parameters.
	 * @param local Whether this AI is executing locally or acting as a remote proxy.
	 * @param ai Whether this player is an AI or not.
	 * @param name The name of this player.
	 */
	protected GreedyPlayer(boolean local, boolean ai, String name){
		super(local, ai, name);
	}
	
	@Override
	public boolean executeMove() throws InterruptedException{
		List<ConvexObject> owned = stream().collect(Collectors.toList());
		
		//find the single largest object
		ConvexObject max = findLargestUnownedObject();
		
		//merge any of our owned objects with something else to get the largest area
		MergeOption bestMerge = null;

		for(ConvexObject obj : owned){
			MergeOption option = findBestMergeFrom(obj);
			if(option != null && (bestMerge == null || option.getIncrease() > bestMerge.getIncrease())){
				bestMerge = option;
			}
		}

		if(bestMerge != null && (max == null || bestMerge.getIncrease() > max.getArea())){
			bestMerge.execute();
			return true;
		}
		
		//claiming the largest object is best
		if(max != null){
			state.claimObject(max);
			return true;
		}else{
			return false;
		}
	}
}
