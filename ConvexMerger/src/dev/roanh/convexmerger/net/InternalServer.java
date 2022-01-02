package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

import dev.roanh.convexmerger.game.Game;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketGameFull;
import dev.roanh.convexmerger.net.packet.PacketPlayerInit;
import dev.roanh.convexmerger.net.packet.PacketRegistry;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

public class InternalServer{
	private static final int PORT = 11111;
	private Game game = new Game();
	private ServerThread thread = new ServerThread();
	
	public InternalServer(PlayfieldGenerator gen){
		
	}
	
	
	
	public Game startGame(){
		
		
		
		//TODO
		
		return game;
	}
	
	
	private class ServerThread extends Thread{
		private ServerSocket server;
		private List<RemotePlayer> connections;
		
		private ServerThread(){
			this.setName("InternalServerThread");
			this.setDaemon(true);
		}
		
		private void handleClient(Socket socket) throws IOException{
			ClientConnecton con = new ClientConnecton(socket);
			Packet packet = con.readPacket();
			
			if(packet.getRegisteryType() != PacketRegistry.PLAYER_INIT){
				//bad client
				socket.close();
			}
			
			synchronized(game){
				if(game.getPlayerCount() < 4){
					PacketPlayerInit info = (PacketPlayerInit)packet;
					game.addPlayer(new RemotePlayer(con, info.isHuman(), info.getName()));
				}else{
					con.sendPacket(new PacketGameFull());
				}
			}
		}
		
		@Override
		public void run(){
			try{
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
