package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.Socket;

import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;
import dev.roanh.convexmerger.net.packet.PacketRegistry;

public class ClientConnecton extends Connection{

	
	
	
	
	
	
	
	
	protected ClientConnecton(Socket socket) throws IOException{
		super(socket);
		// TODO Auto-generated constructor stub
	}

	public PacketPlayerMove awaitMove() throws IOException{
		Packet packet;
		while((packet = readPacket()).getRegisteryType() != PacketRegistry.MOVE){
			//TODO handle
		}
		
		return (PacketPlayerMove)packet;
	}
}
