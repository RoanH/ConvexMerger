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
package dev.roanh.convexmerger.net;

import java.io.Serializable;
import java.util.Objects;

import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

/**
 * Proxy representative of players across a connection.
 * @author Roan
 */
public class PlayerProxy implements Serializable{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1220844320624428578L;
	/**
	 * The ID of the player this proxy represents.
	 */
	private int id;
	/**
	 * The name of the player this proxy represents.
	 */
	private String name;
	/**
	 * Whether the player this proxy represents is an AI.
	 */
	private boolean ai;
	/**
	 * Whether the connection to the player this proxy represents was lost.
	 */
	private boolean lost;
	
	/**
	 * Constructs a new player proxy from the given player.
	 * @param player The player to construct a proxy for.
	 */
	public PlayerProxy(Player player){
		id = player.getID();
		name = player.getName();
		ai = player.isAI();
		if(!player.isLocal() && player instanceof RemotePlayer){
			lost = ((RemotePlayer)player).isLost();
		}else{
			lost = false;
		}
	}
	
	/**
	 * Checks if the connection to the player was lost.
	 * @return True if the connection to the player was lost.
	 */
	public boolean isLost(){
		return lost;
	}
	
	/**
	 * Gets the ID of the player.
	 * @return The ID of the player.
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Gets the name of the player.
	 * @return The name of the player.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Checks if the player is an AI.
	 * @return True if the player is an AI.
	 */
	public boolean isAI(){
		return ai;
	}
	
	@Override
	public boolean equals(Object other){
		return other instanceof PlayerProxy ? ((PlayerProxy)other).id == id : false;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
}
