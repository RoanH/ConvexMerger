package dev.roanh.convexmerger.net.packet;

import java.io.Serializable;

public abstract interface Packet extends Serializable{

	public abstract PacketRegistry getRegisteryType();
}
