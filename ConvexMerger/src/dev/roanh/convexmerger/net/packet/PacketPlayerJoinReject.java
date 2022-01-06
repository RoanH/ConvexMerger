package dev.roanh.convexmerger.net.packet;

/**
 * Packet sent when a player is not accepted into a game.
 * @author Roan
 */
public class PacketPlayerJoinReject implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5372738175706062480L;
	/**
	 * They ID of the reject reason.
	 */
	private final int reason;
	
	/**
	 * Constructs a new join reject packet with the given reason.
	 * @param reason The reason for rejection.
	 */
	public PacketPlayerJoinReject(RejectReason reason){
		this.reason = reason.id;
	}
	
	/**
	 * Gets the reason the player was not allowed to join.
	 * @return The rejection reason.
	 */
	public RejectReason getReason(){
		for(RejectReason msg : RejectReason.values()){
			if(msg.id == reason){
				return msg;
			}
		}
		return RejectReason.UNKNOWN;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.PLAYER_JOIN_REJECT;
	}
	
	/**
	 * Enum of join reject reasons.
	 * @author Roan
	 */
	public static enum RejectReason{
		/**
		 * The exact reason is not known.
		 */
		UNKNOWN(0, "Unknown reason"),
		/**
		 * The game was already full.
		 */
		FULL(1, "Game is already full."),
		/**
		 * The server version does not match the client version.
		 */
		VERSION_MISMATCH(2, "Host game version does not match client version.");

		/**
		 * Numeric ID of this reason.
		 */
		private final int id;
		/**
		 * The help text for this reason.
		 */
		private final String msg;
		
		/**
		 * Constructs a new reject reason with the given ID and text.
		 * @param id The reason ID.
		 * @param msg The reason help text.
		 */
		private RejectReason(int id, String msg){
			this.id = id;
			this.msg = msg;
		}
		
		/**
		 * Gets the help text message for this reject reason.
		 * @return The help text message.
		 */
		public String getMessage(){
			return msg;
		}
		
		@Override
		public String toString(){
			return msg;
		}
	}
}
