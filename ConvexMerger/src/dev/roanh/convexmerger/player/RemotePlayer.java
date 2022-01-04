package dev.roanh.convexmerger.player;

import java.io.IOException;

import dev.roanh.convexmerger.net.Connection;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove.MoveType;
import dev.roanh.convexmerger.net.packet.PacketRegistry;

public class RemotePlayer extends Player{
	private Connection con;

	public RemotePlayer(Connection con, boolean ai, String name){
		super(false, ai, name);
		this.con = con;
	}
	
	@Override
	public boolean executeMove(){
		try{
			System.out.println("Remote player move start: " + getName());
			Packet packet = con.readPacket();
			if(packet.getRegisteryType() != PacketRegistry.PLAYER_MOVE){
				//TODO err
			}
			
			PacketPlayerMove move = (PacketPlayerMove)packet;
			if(move.getType() == MoveType.END){
				con.close();
				return false;
			}
			
			if(move.getPlayer().getID() != getID()){
				//TODO not even the same player
				System.err.println("ERR: incorrect player; " + move.getPlayer().getID() + " / " + getID());
			}
			
			if(move.getType() == MoveType.CLAIM){
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getSource()).findFirst().get());
				return true;
			}else if(move.getType() == MoveType.MERGE){
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getSource()).findFirst().get());
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getTarget()).findFirst().get());
				return true;
			}
			
			
			
			//TODO
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return false;//TODO
	}
}
