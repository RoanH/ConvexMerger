package dev.roanh.convexmerger.player;

import java.io.IOException;

import dev.roanh.convexmerger.net.RemoteConnecton;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;

public class RemotePlayer extends Player{
	private RemoteConnecton con;

	public RemotePlayer(RemoteConnecton con, boolean human, String name){
		super(human, name);
		this.con = con;
	}

	@Override
	public boolean executeMove(){
		try{
			PacketPlayerMove move = con.awaitMove();
			
			
			
			//TODO
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return false;//TODO
	}
}
