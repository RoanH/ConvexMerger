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

import java.util.function.Supplier;

/**
 * Registry of AI players.
 * @author Roan
 */
public enum AIRegistry{
	/**
	 * Isla, a greedy AI that maximises relative area gain.
	 */
	ISLA("Isla", GreedyPlayer::new),
	/**
	 * Elaina, a greedy AI that maximises the relative area
	 * gain in a specific part of the map.
	 */
	ELAINA("Elaina", LocalPlayer::new),
	/**
	 * Shiro, a greedy AI that maximises the relative area
	 * gain in a specific part of the map from a small object.
	 */
	SHIRO("Shiro", SmallPlayer::new);
	
	/**
	 * The name of this AI.
	 */
	private String name;
	/**
	 * The construct for this AI.
	 */
	private Supplier<Player> ctor;
	
	/**
	 * Constructs a new registry entry.
	 * @param name The name of the AI.
	 * @param ctor The construct of the AI.
	 */
	private AIRegistry(String name, Supplier<Player> ctor){
		this.name = name;
		this.ctor = ctor;
	}
	
	/**
	 * Creates a new instance of this AI.
	 * @return A new AI instance.
	 */
	public Player createInstance(){
		return ctor.get();
	}
	
	/**
	 * Gets the name of this AI.
	 * @return The name of this AI.
	 */
	public String getName(){
		return name;
	}
}
