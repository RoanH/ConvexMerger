package dev.roanh.convexmerger.net.packet;

/**
 * Packet sent when a players wants to join a game.
 * @author Roan
 */
public class PacketPlayerJoin implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -6196877761115657750L;
	/**
	 * The name of the player that wants to join.
	 */
	private final String name;
	/**
	 * The version of the game the player that wants to join is running.
	 */
	private final String version;
	
	/**
	 * Constructs a new player join packet.
	 * @param name The name of the player that wants to join.
	 * @param version The version of the game the player is running.
	 */
	public PacketPlayerJoin(String name, String version){
		this.name = name;
		this.version = version;
	}
	
	/**
	 * Gets the name of the player that wants to join.
	 * @return The name of the player that wants to join.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the version of the game the player that wants to
	 * join is currently running.
	 * @return The game version the player runs.
	 */
	public String getVersion(){
		return version;
	}
	
	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN;
	}
}
