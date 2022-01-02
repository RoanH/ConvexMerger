package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import dev.roanh.convexmerger.game.Game;

public class InternalServer{
	private static final int PORT = 11111;
	private Game game;
	
	
	
	
	
	public void run(){
		
		
	}
	
	
	
	
	
	
	private class ServerThread extends Thread{
		
		private ServerThread(){
			this.setName("InternalServerThread");
			this.setDaemon(true);
		}
		
		private void handleClient(Socket con){
			
		}
		
		@Override
		public void run(){
			try{
				ServerSocket server = new ServerSocket(PORT);
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
