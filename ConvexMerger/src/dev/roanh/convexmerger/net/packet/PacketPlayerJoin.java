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
package dev.roanh.convexmerger.net.packet;

/**
 * Packet sent when a players wants to join a game.
 * @author Roan
 */
public class PacketPlayerJoin implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -6196877761115657750L;
	/**
	 * The name of the player that wants to join.
	 */
	private final String name;
	/**
	 * The version of the game the player that wants to join is running.
	 */
	private final String version;
	
	/**
	 * Constructs a new player join packet.
	 * @param name The name of the player that wants to join.
	 * @param version The version of the game the player is running.
	 */
	public PacketPlayerJoin(String name, String version){
		this.name = name;
		this.version = version;
	}
	
	/**
	 * Gets the name of the player that wants to join.
	 * @return The name of the player that wants to join.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the version of the game the player that wants to
	 * join is currently running.
	 * @return The game version the player runs.
	 */
	public String getVersion(){
		return version;
	}
	
	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN;
	}
}
