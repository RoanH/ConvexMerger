package dev.roanh.convexmerger.net.packet;

public class PacketPlayerJoinReject implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5372738175706062480L;
	private final int reason;
	
	public PacketPlayerJoinReject(RejectReason reason){
		this.reason = reason.id;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN_REJECT;
	}
	
	public RejectReason getReason(){
		for(RejectReason msg : RejectReason.values()){
			if(msg.id == reason){
				return msg;
			}
		}
		return RejectReason.UNKNOWN;
	}
	
	public static enum RejectReason{
		UNKNOWN(0, "Unknown reason"),
		FULL(1, "Game is already full."),
		VERSION_MISMATCH(2, "Host game version does not match client version.");

		private final int id;
		private final String msg;
		
		private RejectReason(int id, String msg){
			this.id = id;
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
