package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

/**
 * Represents a client connection to a remote game server.
 * @author Roan
 */
public class ClientConnection extends Connection implements GameStateListener{
	/**
	 * The local player that wants to join.
	 */
	private Player self;
	/**
	 * The reason a server connection could not be established (if any).
	 */
	private RejectReason failReason;
	
	/**
	 * Constructs a new client connection from the given socket
	 * connection and with the given local player.
	 * @param socket The server connection.
	 * @param self The local player instance.
	 * @throws IOException When an IOException occurs.
	 */
	private ClientConnection(Socket socket, Player self) throws IOException{
		super(socket);
		this.self = self;
	}
	
	/**
	 * Checks if this client was connected to the server.
	 * @return True if this client was connected successfully.
	 */
	public boolean isConnected(){
		return failReason == null;
	}
	
	/**
	 * Gets the reason why the connection to the server failed (if any).
	 * @return The reason a server connection failed.
	 */
	public RejectReason getRejectReason(){
		return failReason;
	}
	
	/**
	 * Gets the gamestate for the remote game. This method blocks
	 * until the game is started and the game state sent.
	 * @return The remote game state.
	 * @throws IOException When an IOException occurs.
	 */
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
			}
		}
	}

	@Override
	public void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed){
		if(player.isLocal()){
			try{
				sendPacket(new PacketPlayerMove(player, source, target));
			}catch(IOException e){
				close();
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
			}
		}
	}
	
	@Override
	public void abort(){
		close();
	}

	/**
	 * Attempts to establish a new multiplayer connection
	 * to the given host and with the given local player.
	 * @param host The host to connect to.
	 * @param player The local player joining.
	 * @return The remote connection, possibly containing
	 *         a reason the connection failed.
	 * @throws IOException When an IOException occured.
	 */
	public static final ClientConnection connect(String host, Player player) throws IOException{
		ClientConnection con = new ClientConnection(new Socket(host, Constants.PORT), player);
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
