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

import java.io.IOException;

import dev.roanh.convexmerger.net.Connection;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove.MoveType;
import dev.roanh.convexmerger.net.packet.PacketRegistry;

/**
 * Proxy representative for a player playing on a remote system.
 * If the remote connection is lost the {@link GreedyPlayer} AI takes over.
 * @author Roan
 * @see GreedyPlayer
 */
public class RemotePlayer extends GreedyPlayer{
	/**
	 * The remote player connection.
	 */
	private Connection con;
	/**
	 * True if the connection to the remote player was lost.
	 */
	private boolean lost = false;
	
	/**
	 * Constructs a new remote player with the given
	 * player connection, AI status and name.
	 * @param con The connection to the remote player.
	 * @param ai Whether the remote player is an AI.
	 * @param name The name of the remote player.
	 */
	public RemotePlayer(Connection con, boolean ai, String name){
		super(false, ai, name);
		this.con = con;
	}
	
	/**
	 * Checks if the connection to the remtoe player was lost.
	 * @return True if the remote connection was lost.
	 */
	public boolean isLost(){
		return lost;
	}
	
	/**
	 * Executes a fallback AI move if the player connection was lost.
	 * @return True if a move was executed, false if no moves
	 *         are left in the game (signals game end).
	 * @throws InterruptedException When the player was
	 *         interrupted (signals that the game was aborted).
	 */
	private boolean fallback() throws InterruptedException{
		con.close();
		lost = true;
		if(!getName().endsWith(" [Lost]")){
			this.setName(this.getName() + " [Lost]");
		}
		return super.executeMove();
	}
	
	@Override
	public boolean executeMove() throws InterruptedException{
		if(lost){
			return super.executeMove();
		}
		
		try{
			Packet packet = con.readPacket();
			if(packet == null){
				return fallback();
			}
			
			if(packet.getRegisteryType() != PacketRegistry.PLAYER_MOVE){
				if(packet.getRegisteryType() == PacketRegistry.GAME_END){
					return false;
				}else{
					return fallback();
				}
			}
			
			PacketPlayerMove move = (PacketPlayerMove)packet;
			if(move.getType() == MoveType.END){
				con.close();
				return false;
			}
			
			if(move.getPlayer().getID() != getID()){
				return fallback();
			}
			
			if(move.getPlayer().isLost() && !getName().endsWith(" [Lost]")){
				this.setName(this.getName() + " [Lost]");
			}
			
			if(move.getType() == MoveType.CLAIM){
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getSource()).findFirst().get());
				return true;
			}else if(move.getType() == MoveType.MERGE){
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getSource()).findFirst().get());
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getTarget()).findFirst().get());
				return true;
			}
		}catch(IOException e){
			return fallback();
		}
		
		return false;
	}
}
