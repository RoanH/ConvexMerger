package dev.roanh.convexmerger.net.packet;

public class PacketPlayerJoin implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -6196877761115657750L;
	private final String name;
	
	public PacketPlayerJoin(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN;
	}
}
