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

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.net.PlayerProxy;
import dev.roanh.convexmerger.player.Player;

/**
 * Packet sent when a player finishes their move.
 * @author Roan
 */
public class PacketPlayerMove implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -366297097116291189L;
	/**
	 * The player that made the move.
	 */
	private final PlayerProxy player;
	/**
	 * The type of move.
	 */
	private final MoveType type;
	/**
	 * The source object involved (the ID of the claimed
	 * object or the ID of the object a merge started from).
	 */
	private final int source;
	/**
	 * The ID of the target object of a merge move.
	 */
	private final int target;
	
	/**
	 * Constructs a new player move no more remaining moves
	 * being available to the player.
	 */
	public PacketPlayerMove(){
		this(null, null, null, MoveType.END);
	}
	
	/**
	 * Constructs a new player move packet for a claim with the given data.
	 * @param player The player that made the move.
	 * @param claimed The object that was claimed.
	 */
	public PacketPlayerMove(Player player, ConvexObject claimed){
		this(player, claimed, null, MoveType.CLAIM);
	}
	
	/**
	 * Constructs a new player move packet for a merge with the given data.
	 * @param player The player that made the move.
	 * @param source The source object the merge started from.
	 * @param target The merge target object.
	 */
	public PacketPlayerMove(Player player, ConvexObject source, ConvexObject target){
		this(player, source, target, MoveType.MERGE);
	}
	
	/**
	 * Constructs a new player move packet with the given data.
	 * @param player The player that made the move.
	 * @param source The source object involved (claimed object
	 *        or object a merge started from).
	 * @param target The merge target object.
	 * @param type The type of the move.
	 */
	private PacketPlayerMove(Player player, ConvexObject source, ConvexObject target, MoveType type){
		this.player = player == null ? null : player.getProxy();
		this.source = source == null ? -1 : source.getID();
		this.target = target == null ? -1 : target.getID();
		this.type = type;
	}
	
	/**
	 * Gets the player that made this move.
	 * @return The player that made this move.
	 */
	public PlayerProxy getPlayer(){
		return player;
	}
	
	/**
	 * Gets the type of move that was made by the player.
	 * @return The type of move.
	 */
	public MoveType getType(){
		return type;
	}
	
	/**
	 * Gets the ID of the source object in this move. This
	 * is either the ID of the object that was claimed or
	 * the ID of the object a merge was started from.
	 * @return The source object ID or -1.
	 */
	public int getSource(){
		return source;
	}
	
	/**
	 * Gets the ID of the target object in this move. This
	 * is the ID of the target object in a merge move.
	 * @return The target object ID or -1.
	 */
	public int getTarget(){
		return target;
	}
	
	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_MOVE;
	}
	
	/**
	 * Player move types.
	 * @author Roan
	 */
	public static enum MoveType{
		/**
		 * Claiming a new convex object.
		 */
		CLAIM,
		/**
		 * Merging two convex objects.
		 */
		MERGE,
		/**
		 * No more moves remain for the player.
		 */
		END
	}
}
