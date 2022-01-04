package dev.roanh.convexmerger.net.packet;

import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.net.PlayerProxy;
import dev.roanh.convexmerger.player.Player;

public class PacketGameInit implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3786951584716643604L;
	private List<ConvexObject> objects;
	private List<PlayerProxy> players = new ArrayList<PlayerProxy>(4);
	
	public PacketGameInit(List<ConvexObject> objects, List<Player> players){
		this.objects = objects;
		players.forEach(player->this.players.add(player.getProxy()));
	}
	
	public List<ConvexObject> getObjects(){
		return objects;
	}
	
	public List<PlayerProxy> getPlayers(){
		return players;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.GAME_INIT;
	}
}
