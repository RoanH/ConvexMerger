package dev.roanh.convexmerger.player;

import java.io.IOException;

import dev.roanh.convexmerger.net.ClientConnecton;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;

public class RemotePlayer extends Player{
	private ClientConnecton con;

	public RemotePlayer(ClientConnecton con, boolean human, String name){
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
