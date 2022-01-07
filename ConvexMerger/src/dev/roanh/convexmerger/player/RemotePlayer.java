package dev.roanh.convexmerger.player;

import java.io.IOException;

import dev.roanh.convexmerger.net.Connection;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove.MoveType;
import dev.roanh.convexmerger.net.packet.PacketRegistry;

public class RemotePlayer extends GreedyPlayer{
	private Connection con;
	private boolean lost = false;
	
	public RemotePlayer(Connection con, boolean ai, String name){
		super(false, ai, name);
		this.con = con;
	}
	
	public boolean isLost(){
		return lost;
	}
	
	private boolean fallback() throws InterruptedException{
		con.close();
		lost = true;
		if(!getName().endsWith("[Lost]")){
			this.setName(this.getName() + "[Lost]");
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
			
			if(move.getPlayer().isLost() && !getName().endsWith("[Lost]")){
				this.setName(this.getName() + "[Lost]");
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
