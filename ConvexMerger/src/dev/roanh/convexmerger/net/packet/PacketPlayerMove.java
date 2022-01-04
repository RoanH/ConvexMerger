package dev.roanh.convexmerger.net.packet;

import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.net.PlayerProxy;

public class PacketPlayerMove implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -366297097116291189L;
	private PlayerProxy player;
	private MoveType type;
	private ConvexObject source;
	private ConvexObject target;
	private List<ConvexObject> absorbed;
	
	
	public PlayerProxy getPlayer(){
		return player;
	}
	
	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_MOVE;
	}
	
	public static enum MoveType{
		CLAIM,
		MERGE
	}
}
