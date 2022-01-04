package dev.roanh.convexmerger.player;

import java.io.IOException;

import dev.roanh.convexmerger.net.RemoteConnecton;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;

public class RemotePlayer extends Player{
	private RemoteConnecton con;

	public RemotePlayer(RemoteConnecton con, boolean ai, String name){
		super(false, ai, name);
		this.con = con;
	}

	@Override
	public boolean executeMove(){
		try{
			System.out.println("Remote player move start: " + getName());
			PacketPlayerMove move = con.awaitMove();
			System.out.println("move received");
			
			if(move.getPlayer().getID() != getID()){
				//TODO not even the same player
				System.err.println("ERR: incorrect player; " + move.getPlayer().getID() + " / " + getID());
			}
			
			switch(move.getType()){
			case CLAIM:
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getSource()).findFirst().get());
				return true;
			case MERGE:
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getSource()).findFirst().get());
				state.claimObject(state.stream().filter(obj->obj.getID() == move.getTarget()).findFirst().get());
				return true;
			default:
				//TODO really bad
				break;
			}
			
			
			//TODO
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return false;//TODO
	}
}
