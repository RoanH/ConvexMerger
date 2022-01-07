package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.GameState.GameStateListener;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketGameEnd;
import dev.roanh.convexmerger.net.packet.PacketGameInit;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoin;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinAccept;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinReject;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinReject.RejectReason;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;
import dev.roanh.convexmerger.net.packet.PacketRegistry;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

public class ClientConnection extends Connection implements GameStateListener{
	private Player self;
	private Consumer<Exception> disconnectHandler;
	private RejectReason failReason;
	
	private ClientConnection(Socket socket, Player self) throws IOException{
		super(socket);
		this.self = self;
	}
	
	public boolean isConnected(){
		return failReason == null;
	}
	
	public RejectReason getRejectReason(){
		return failReason;
	}
	
	public void setDisconnectHandler(Consumer<Exception> handler){
		disconnectHandler = handler;
	}
	
	public GameState getGameState() throws IOException{
		Packet recv = readPacket();
		
		if(recv.getRegisteryType() != PacketRegistry.GAME_INIT){
			return null;
		}
		
		PacketGameInit data = (PacketGameInit)recv;
		
		List<Player> players = new ArrayList<Player>(4);
		for(PlayerProxy player : data.getPlayers()){
			if(player.getID() == self.getID()){
				players.add(self);
			}else{
				RemotePlayer remote = new RemotePlayer(this, player.isAI(), player.getName());
				remote.setID(player.getID());
				players.add(remote);
			}
		}
		
		GameState state = new GameState(data.getObjects(), data.getSeed(), players);
		state.registerStateListener(this);
		return state;
	}

	@Override
	public void claim(Player player, ConvexObject obj){
		if(player.isLocal()){
			try{
				sendPacket(new PacketPlayerMove(player, obj));
			}catch(IOException e){
				close();
				disconnectHandler.accept(e);
			}
		}
	}

	@Override
	public void merge(Player player, ConvexObject source, ConvexObject target){
		if(player.isLocal()){
			try{
				sendPacket(new PacketPlayerMove(player, source, target));
			}catch(IOException e){
				close();
				disconnectHandler.accept(e);
			}
		}
	}

	@Override
	public void end(){
		if(!isClosed()){
			try{
				sendPacket(new PacketGameEnd());
			}catch(IOException e){
				close();
				disconnectHandler.accept(e);
			}
		}
	}

	public static final ClientConnection connect(String host, Player player) throws IOException{
		ClientConnection con = new ClientConnection(new Socket(host, InternalServer.PORT), player);
		con.sendPacket(new PacketPlayerJoin(player.getName(), Constants.VERSION));
		
		Packet recv = con.readPacket();
		if(recv.getRegisteryType() != PacketRegistry.PLAYER_JOIN_ACCEPT){
			if(recv.getRegisteryType() == PacketRegistry.PLAYER_JOIN_REJECT){
				con.failReason = ((PacketPlayerJoinReject)recv).getReason();
			}else{
				con.failReason = RejectReason.UNKNOWN;
			}
			con.close();
			return con;
		}
		
		player.setID(((PacketPlayerJoinAccept)recv).getID());
		return con;
	}
}
