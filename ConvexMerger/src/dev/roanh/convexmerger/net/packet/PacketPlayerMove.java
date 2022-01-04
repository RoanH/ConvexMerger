package dev.roanh.convexmerger.net.packet;

import dev.roanh.convexmerger.net.PlayerProxy;
import dev.roanh.convexmerger.player.Player;

public class PacketPlayerMove implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -366297097116291189L;
	private PlayerProxy player;
	private MoveType type;
	private int source;
	private int target;
	
	public PacketPlayerMove(Player player, int claimed){
		this.player = player.getProxy();
		source = claimed;
		type = MoveType.CLAIM;
	}
	
	public PacketPlayerMove(Player player, int source, int target){
		this.player = player.getProxy();
		this.source = source;
		this.target = target;
		type = MoveType.MERGE;
	}
	
	public PlayerProxy getPlayer(){
		return player;
	}
	
	public MoveType getType(){
		return type;
	}
	
	public int getSource(){
		return source;
	}
	
	public int getTarget(){
		return target;
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
