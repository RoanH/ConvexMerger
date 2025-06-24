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
package dev.roanh.convexmerger.game;

import java.util.List;

import dev.roanh.convexmerger.player.Player;

/**
 * Interface that receives game state updates.
 * @author Roan
 */
public abstract interface GameStateListener{
	
	/**
	 * Called when a player claims a new object.
	 * @param player The player that made the claim.
	 * @param obj The object that was claimed.
	 */
	public abstract void claim(Player player, ConvexObject obj);
	
	/**
	 * Called when a player performs a merge.
	 * @param player The player that performed the merge.
	 * @param source The object the merge was started from.
	 * @param target The target object of the merge.
	 * @param result The object resulting from the merge.
	 * @param absorbed The objects absorbed in the merge.
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 */
	public abstract void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed) throws InterruptedException;
	
	/**
	 * Called when the game ends.
	 */
	public abstract void end();
	
	/**
	 * Called when the game is aborted (forcefully terminated).
	 */
	public abstract void abort();
}