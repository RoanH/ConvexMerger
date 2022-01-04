package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketGameInit;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoin;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinAccept;
import dev.roanh.convexmerger.net.packet.PacketRegistry;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

public class ClientConnection extends RemoteConnecton{

	
	
	
	
	
	
	
	
	
	
	private ClientConnection(Socket socket) throws IOException{
		super(socket);
		// TODO Auto-generated constructor stub
	}
	
	public GameState getGameState() throws IOException{
		Packet recv = readPacket();
		
		if(recv.getRegisteryType() != PacketRegistry.GAME_INIT){
			return null;
		}
		
		PacketGameInit data = (PacketGameInit)recv;
		
		List<Player> players = new ArrayList<Player>(4);
		for(PlayerProxy player : data.getPlayers()){
			RemotePlayer remote = new RemotePlayer(this, player.isAI(), player.getName());
			remote.setID(player.getID());
			players.add(remote);
		}
		
		return new GameState(data.getObjects(), players);
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
