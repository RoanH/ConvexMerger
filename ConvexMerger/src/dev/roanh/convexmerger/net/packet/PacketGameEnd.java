package dev.roanh.convexmerger.net.packet;

/**
 * Packet sent when the game ends.
 * @author Roan
 */
public class PacketGameEnd implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5196039665681435523L;

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.GAME_END;
	}
}
