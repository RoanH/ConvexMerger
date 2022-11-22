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
 * Packet sent when a player is accepted into a game.
 * @author Roan
 */
public class PacketPlayerJoinAccept implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5956530603458320833L;
	/**
	 * The assigned player ID.
	 */
	private final int id;
	
	/**
	 * Constructs a new join accept packet with the given
	 * assigned ID for the joining player.
	 * @param id The ID for the player.
	 */
	public PacketPlayerJoinAccept(int id){
		this.id = id;
	}
	
	/**
	 * Gets the ID assigned to the joining player.
	 * @return The player ID.
	 */
	public int getID(){
		return id;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN_ACCEPT;
	}
}
