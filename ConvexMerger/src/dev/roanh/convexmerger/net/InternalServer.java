package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.roanh.convexmerger.game.Game;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketGameFull;
import dev.roanh.convexmerger.net.packet.PacketGameInit;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoin;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinAccept;
import dev.roanh.convexmerger.net.packet.PacketRegistry;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

public class InternalServer{
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
			Packet start = new PacketGameInit(state.getObjects(), state.getPlayers());
			for(RemoteConnecton con : thread.connections){
				con.sendPacket(start);
			}
		
			//TODO
		
			
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return state;
	}
	
	public void setNewPlayerHandler(Consumer<Player> handler){
		playerHandler = handler;
	}
	
	private class ServerThread extends Thread{
		private ServerSocket server;
		private List<RemoteConnecton> connections = new ArrayList<RemoteConnecton>(4);
		private volatile boolean running = false;
		
		private ServerThread(){
			this.setName("InternalServerThread");
			this.setDaemon(true);
		}
		
		private void shutdown() throws IOException{
			running = false;
			server.close();
		}
		
		private void handleClient(Socket socket) throws IOException{
			RemoteConnecton con = new RemoteConnecton(socket);
			Packet packet = con.readPacket();
			
			if(packet.getRegisteryType() != PacketRegistry.PLAYER_JOIN){
				//bad client
				socket.close();
			}
			
			synchronized(game){
				if(game.getPlayerCount() < 4){
					Player player = new RemotePlayer(con, false, ((PacketPlayerJoin)packet).getName());
					con.sendPacket(new PacketPlayerJoinAccept(game.addPlayer(player)));
					connections.add(con);
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
