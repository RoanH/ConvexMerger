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

/**
 * Server responsible for hosting multiplayer games.
 * @author Roan
 */
public class InternalServer implements GameStateListener{
	/**
	 * Thread executing the main server logic.
	 */
	private ServerThread thread = new ServerThread();
	/**
	 * Listener to send server events to.
	 */
	private InternalServerListener handler;
	
	/**
	 * Constructs a new server with the given listener.
	 * @param handler The event listener.
	 */
	public InternalServer(InternalServerListener handler){
		this.handler = handler;
		thread.start();
	}
	
	/**
	 * Immediately shuts down this server and closes
	 * all the connections it had open.
	 */
	public void shutdown(){
		thread.shutdown();
		thread.close();
	}
	
	/**
	 * Gets the game constructor required to start
	 * a game with the given set of players and with
	 * the given playfield generator. After this
	 * is called no new connections will be accepted anymore.
	 * @param players The list of participating players.
	 * @param gen The playfield generator.
	 * @return The game state constructor for the game.
	 */
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
	public void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed){
		thread.broadCast(player, new PacketPlayerMove(player, source, target));
	}

	@Override
	public void end(){
		thread.broadCast(new PacketPlayerMove());
		thread.close();
	}
	
	@Override
	public void abort(){
		thread.close();
	}
	
	/**
	 * Main thread managing all connections and initially
	 * responsible for accepting joining players.
	 * @author Roan
	 */
	private class ServerThread extends Thread{
		/**
		 * Executor service used to handle incoming connections.
		 */
		private ExecutorService executor = Executors.newFixedThreadPool(4);
		/**
		 * Main server socket.
		 */
		private ServerSocket server;
		/**
		 * Map of active connections indexed by player ID.
		 */
		private Map<Integer, Connection> connections = new HashMap<Integer, Connection>();
		/**
		 * Whether the server is running and accepting new connections.
		 */
		private volatile boolean running = false;
		/**
		 * The ID to assign to the next valid player. ID 1 is reseved
		 * for the player hosting the game.
		 */
		private volatile int nextID = 2;
		
		/**
		 * Constructs a new server thread.
		 */
		private ServerThread(){
			this.setName("InternalServerThread");
			this.setDaemon(true);
		}
		
		/**
		 * Closes all connections held by this server thread.
		 */
		private void close(){
			for(Connection con : connections.values()){
				con.close();
			}
		}
		
		/**
		 * Broadcasts a packet to all connected players
		 * except for the given source player.
		 * @param source The player to exclude.
		 * @param packet The packet to send.
		 */
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
		
		/**
		 * Broadcasts a packet to all connected players.
		 * @param packet The packet to send.
		 */
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
		
		/**
		 * Shuts down the part of this server responsible
		 * for accepting incoming players. Connected players
		 * will still remain connected.
		 */
		private void shutdown(){
			running = false;
			try{
				if(server != null){
					server.close();
				}
			}catch(IOException e){
				//not very important, just reject connections
			}
		}
		
		/**
		 * Handles an incoming client connection.
		 * @param socket The client socket.
		 */
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
	
	/**
	 * Interface that receives server events.
	 * @author Roan
	 */
	public static abstract interface InternalServerListener{
		
		/**
		 * Called when a new player joined the server.
		 * @param player The player that joined.
		 */
		public abstract void handlePlayerJoin(Player player);
		
		/**
		 * Called when the server encountered a fatal exception
		 * while accepting incoming player connections.
		 * @param e The exception that occurred.
		 */
		public abstract void handleException(Exception e);
	}
}
