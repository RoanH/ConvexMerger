package dev.roanh.convexmerger.net.packet;

import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.net.PlayerProxy;
import dev.roanh.convexmerger.player.Player;

/**
 * Packet sent when the game starts.
 * @author Roan
 */
public class PacketGameInit implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3786951584716643604L;
	/**
	 * The convex objects for this game.
	 */
	private final List<ConvexObject> objects;
	/**
	 * The game seed.
	 */
	private final String seed;
	/**
	 * The players participating in this game.
	 */
	private final List<PlayerProxy> players = new ArrayList<PlayerProxy>(4);
	
	/**
	 * Constructs a new game init packet with the given objects and players.
	 * @param objects The game objects.
	 * @param seed The game seed.
	 * @param players The participating players.
	 */
	public PacketGameInit(List<ConvexObject> objects, String seed, List<Player> players){
		this.objects = objects;
		this.seed = seed;
		players.forEach(player->this.players.add(player.getProxy()));
	}
	
	/**
	 * Gets the game seed.
	 * @return The game seed.
	 */
	public String getSeed(){
		return seed;
	}
	
	/**
	 * Gets the objects for this game.
	 * @return The game objects.
	 */
	public List<ConvexObject> getObjects(){
		return objects;
	}
	
	/**
	 * Gets the players for this game.
	 * @return The participating players.
	 */
	public List<PlayerProxy> getPlayers(){
		return players;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.GAME_INIT;
	}
}
