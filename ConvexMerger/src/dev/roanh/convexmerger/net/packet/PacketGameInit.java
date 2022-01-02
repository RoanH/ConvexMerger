package dev.roanh.convexmerger.net.packet;

public class PacketGameInit implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3786951584716643604L;

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.INIT;
	}
}
