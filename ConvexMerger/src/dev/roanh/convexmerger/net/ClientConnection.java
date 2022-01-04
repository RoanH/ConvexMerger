package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.Socket;

import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoin;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinAccept;
import dev.roanh.convexmerger.net.packet.PacketRegistry;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.Player;

public class ClientConnection extends Connection{

	
	
	
	
	
	
	
	
	
	
	private ClientConnection(Socket socket) throws IOException{
		super(socket);
		// TODO Auto-generated constructor stub
	}

	public static final ClientConnection connect(String host, Player player) throws IOException{
		ClientConnection con = new ClientConnection(new Socket(host, InternalServer.PORT));
		con.sendPacket(new PacketPlayerJoin(player.getName()));
		
		Packet recv = con.readPacket();
		if(recv.getRegisteryType() != PacketRegistry.PLAYER_JOIN_ACCEPT){
			return null;
		}
		
		player.setID(((PacketPlayerJoinAccept)recv).getID());
		return con;
	}
}
