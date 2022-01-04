package dev.roanh.convexmerger.net.packet;

public class PacketPlayerJoinAccept implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5956530603458320833L;
	private int id;
	
	public PacketPlayerJoinAccept(int id){
		this.id = id;
	}
	
	public int getID(){
		return id;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN_ACCEPT;
	}
}
