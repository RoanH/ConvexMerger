package dev.roanh.convexmerger.net.packet;

public class PacketPlayerInit implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -6196877761115657750L;
	private final String name;
	private final boolean human;
	
	public PacketPlayerInit(boolean human, String name){
		this.name = name;
		this.human = human;
	}
	
	public boolean isHuman(){
		return human;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_INIT;
	}
}
