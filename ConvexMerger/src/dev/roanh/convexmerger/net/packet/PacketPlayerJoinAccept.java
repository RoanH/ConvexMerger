package dev.roanh.convexmerger.net.packet;

/**
 * Packet sent when a player is accepted into a game.
 * @author Roan
 */
public class PacketPlayerJoinAccept implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5956530603458320833L;
	/**
	 * The assigned player ID.
	 */
	private final int id;
	
	/**
	 * Constructs a new join accept packet with the given
	 * assigned ID for the joining player.
	 * @param id The ID for the player.
	 */
	public PacketPlayerJoinAccept(int id){
		this.id = id;
	}
	
	/**
	 * Gets the ID assigned to the joining player.
	 * @return The player ID.
	 */
	public int getID(){
		return id;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN_ACCEPT;
	}
}
