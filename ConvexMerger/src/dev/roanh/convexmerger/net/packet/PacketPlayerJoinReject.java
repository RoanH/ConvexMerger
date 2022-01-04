package dev.roanh.convexmerger.net.packet;

public class PacketPlayerJoinReject implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5372738175706062480L;
	private final RejectReason reason;
	
	public PacketPlayerJoinReject(RejectReason reason){
		this.reason = reason;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN_REJECT;
	}
	
	public RejectReason getReason(){
		return reason;
	}
	
	public static enum RejectReason{
		FULL("Game is already full."),
		VERSION_MISMATCH("Host game version does not match client version.");
		
		private final String msg;
		
		private RejectReason(String msg){
			this.msg = msg;
		}
		
		public String getMessage(){
			return msg;
		}
		
		@Override
		public String toString(){
			return msg;
		}
	}
}
