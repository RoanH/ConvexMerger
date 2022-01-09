package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameConstructor;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.GameState.GameStateListener;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.net.packet.Packet;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinReject;
import dev.roanh.convexmerger.net.packet.PacketGameInit;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoin;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinAccept;
import dev.roanh.convexmerger.net.packet.PacketPlayerMove;
import dev.roanh.convexmerger.net.packet.PacketRegistry;
import dev.roanh.convexmerger.net.packet.PacketPlayerJoinReject.RejectReason;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

public class InternalServer implements GameStateListener{
	private ServerThread thread = new ServerThread();
	private InternalServerListener handler;
	
	public InternalServer(InternalServerListener handler){
		this.handler = handler;
		thread.start();
	}
	
	public void shutdown(){
		thread.shutdown();
		thread.close();
	}
	
	public GameConstructor startGame(List<Player> players, PlayfieldGenerator gen){
		thread.shutdown();

		return ()->{
			GameState state = new GameState(gen, players);
			thread.broadCast(new PacketGameInit(state.getObjects(), state.getSeed(), state.getPlayers()));
			state.registerStateListener(this);
			return state;
		};
	}
	
	@Override
	public void claim(Player player, ConvexObject obj){
		thread.broadCast(player, new PacketPlayerMove(player, obj));
	}

	@Override
	public void merge(Player player, ConvexObject source, ConvexObject target){
		thread.broadCast(player, new PacketPlayerMove(player, source, target));
	}

	@Override
	public void end(){
		thread.broadCast(new PacketPlayerMove());
		thread.close();
	}
	
	private class ServerThread extends Thread{
		private ExecutorService executor = Executors.newFixedThreadPool(4);
		private ServerSocket server;
		private Map<Integer, Connection> connections = new HashMap<Integer, Connection>();
		private volatile boolean running = false;
		private volatile int nextID = 2;//id 1 for host
		
		private ServerThread(){
			this.setName("InternalServerThread");
			this.setDaemon(true);
		}
		
		private void close(){
			for(Connection con : connections.values()){
				con.close();
			}
		}
		
		private void broadCast(Player source, Packet packet){
			for(Entry<Integer, Connection> entry : connections.entrySet()){
				if(entry.getKey() != source.getID() && !entry.getValue().isClosed()){
					try{
						entry.getValue().sendPacket(packet);
					}catch(IOException e){
						//will be detected on read
						entry.getValue().close();
					}
				}
			}
		}
		
		private void broadCast(Packet packet){
			for(Connection con : connections.values()){
				if(!con.isClosed()){
					try{
						con.sendPacket(packet);
					}catch(IOException e){
						//will be detected on read
						con.close();
					}
				}
			}
		}
		
		private void shutdown(){
			running = false;
			try{
				server.close();
			}catch(IOException e){
				//not very important, just reject connections
			}
		}
		
		private void handleClient(Socket socket){
			try{
				Connection con = new Connection(socket);
				Packet packet = con.readPacket();
				
				if(packet == null || packet.getRegisteryType() != PacketRegistry.PLAYER_JOIN){
					//bad client
					con.close();
				}
				
				PacketPlayerJoin data = (PacketPlayerJoin)packet;
				if(!data.getVersion().equals(Constants.VERSION)){
					con.sendPacket(new PacketPlayerJoinReject(RejectReason.VERSION_MISMATCH));
					con.close();
					return;
				}
				
				synchronized(handler){
					if(nextID <= 4 && running){
						Player player = new RemotePlayer(con, false, data.getName());
						player.setID(nextID++);
						con.sendPacket(new PacketPlayerJoinAccept(player.getID()));
						connections.put(player.getID(), con);
						handler.handlePlayerJoin(player);
					}else{
						con.sendPacket(new PacketPlayerJoinReject(RejectReason.FULL));
					}
				}
			}catch(Exception e){
				try{
					socket.close();
				}catch(IOException e1){
					//not really relevant, client is bad
				}
			}
		}
		
		@Override
		public void run(){
			try{
				running = true;
				server = new ServerSocket(Constants.PORT);
				while(running){
					final Socket s = server.accept();
					executor.submit(()->handleClient(s));
				}
			}catch(IOException e){
				if(running){
					handler.handleException(e);
				}
			}
		}
	}
	
	public static abstract interface InternalServerListener{
		
		public abstract void handlePlayerJoin(Player player);
		
		public abstract void handleException(Exception e);
	}
}
