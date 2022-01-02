package dev.roanh.convexmerger.net.packet;

public class PacketGameFull implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5372738175706062480L;

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.GAME_FULL;
	}
}
