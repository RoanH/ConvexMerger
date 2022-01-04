package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

import dev.roanh.convexmerger.game.Game;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketGameFull;
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
		System.out.println("cl");
		game = new Game(gen);
		game.addPlayer(self);
		thread.start();
		System.out.println("started");
	}
	
	public int getPlayerCount(){
		return game.getPlayerCount();
	}
	
	public GameState startGame(){
		try{
			thread.server.close();
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//TODO
		
		return game.toGameState();
	}
	
	public void setNewPlayerHandler(Consumer<Player> handler){
		playerHandler = handler;
	}
	
	
	private class ServerThread extends Thread{
		private ServerSocket server;
		private List<RemotePlayer> connections;
		
		private ServerThread(){
			this.setName("InternalServerThread");
			this.setDaemon(true);
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
				System.out.println("Server started");
				server = new ServerSocket(PORT);
				while(true){//TODO ?
					handleClient(server.accept());
				}
			}catch(IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
