package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.Game;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.GameState.GameStateListener;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketGameFull;
import dev.roanh.convexmerger.net.packet.PacketGameInit;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoin;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinAccept;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;
import dev.roanh.convexmerger.net.packet.PacketRegistry;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

public class InternalServer implements GameStateListener{
	public static final int PORT = 11111;
	private Game game;
	private ServerThread thread = new ServerThread();
	private Consumer<Player> playerHandler;
	
	public InternalServer(Player self, PlayfieldGenerator gen, Consumer<Player> handler){
		playerHandler = handler;
		game = new Game(gen);
		game.addPlayer(self);
		thread.start();
	}
	
	public int getPlayerCount(){
		return game.getPlayerCount();
	}
	
	public GameState startGame(){
		GameState state = null;
		try{
			thread.shutdown();
		
			state = game.toGameState();
			thread.broadCast(new PacketGameInit(state.getObjects(), state.getPlayers()));
		
			//TODO
		
			
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		state.registerStateListener(this);
		return state;
	}
	
	public void setNewPlayerHandler(Consumer<Player> handler){
		playerHandler = handler;
	}

	@Override
	public void claim(Player player, ConvexObject obj){
		try{
			System.out.println("send claim for: " + player.getName());
			thread.broadCast(player, new PacketPlayerMove(player, obj.getID()));
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void merge(Player player, ConvexObject source, ConvexObject target){
		try{
			System.out.println("send merge for: " + player.getName());
			thread.broadCast(player, new PacketPlayerMove(player, source.getID(), target.getID()));
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void end(){
		try{
			thread.broadCast(new PacketPlayerMove());
			thread.close();
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class ServerThread extends Thread{
		private ServerSocket server;
		private Map<Integer, Connection> connections = new HashMap<Integer, Connection>();
		private volatile boolean running = false;
		
		private ServerThread(){
			this.setName("InternalServerThread");
			this.setDaemon(true);
		}
		
		private void close(){
			for(Connection con : connections.values()){
				con.close();
			}
		}
		
		private void broadCast(Player source, Packet packet) throws IOException{
			for(Entry<Integer, Connection> entry : connections.entrySet()){
				if(entry.getKey() != source.getID()){
					entry.getValue().sendPacket(packet);
				}
			}
		}
		
		private void broadCast(Packet packet) throws IOException{
			for(Connection con : connections.values()){
				con.sendPacket(packet);
			}
		}
		
		private void shutdown() throws IOException{
			running = false;
			server.close();
		}
		
		private void handleClient(Socket socket) throws IOException{
			Connection con = new Connection(socket);
			Packet packet = con.readPacket();
			
			if(packet.getRegisteryType() != PacketRegistry.PLAYER_JOIN){
				//bad client
				socket.close();
			}
			
			synchronized(game){
				if(game.getPlayerCount() < 4){
					Player player = new RemotePlayer(con, false, ((PacketPlayerJoin)packet).getName());
					con.sendPacket(new PacketPlayerJoinAccept(game.addPlayer(player)));
					connections.put(player.getID(), con);
					if(playerHandler != null){
						playerHandler.accept(player);
					}
				}else{
					con.sendPacket(new PacketGameFull());
				}
			}
		}
		
		@Override
		public void run(){
			try{
				running = true;
				System.out.println("Server started");
				server = new ServerSocket(PORT);
				while(true){//TODO ?
					handleClient(server.accept());
				}
			}catch(IOException e){
				if(running){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
