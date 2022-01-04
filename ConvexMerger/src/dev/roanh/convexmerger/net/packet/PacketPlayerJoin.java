package dev.roanh.convexmerger.net.packet;

public class PacketPlayerJoin implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -6196877761115657750L;
	private final String name;
	private final String version;
	
	public PacketPlayerJoin(String name, String version){
		this.name = name;
		this.version = version;
	}
	
	public String getName(){
		return name;
	}
	
	public String getVersion(){
		return version;
	}
	
	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN;
	}
}
