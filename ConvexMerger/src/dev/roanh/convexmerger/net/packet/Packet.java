package dev.roanh.convexmerger.net.packet;

import java.io.Serializable;

/**
 * Interface for packets that get sent across the network.
 * @author Roan
 */
public abstract interface Packet extends Serializable{

	/**
	 * The registry type of this packet.
	 * @return The type of this packet.
	 */
	public abstract PacketRegistry getRegisteryType();
}
