package dev.roanh.convexmerger.net.packet;

/**
 * Enum of all packet types.
 * @author Roan
 */
public enum PacketRegistry{
	/**
	 * Sent when a game is started.
	 */
	GAME_INIT,
	/**
	 * Sent when a player wants to join a game.
	 */
	PLAYER_JOIN,
	/**
	 * Sent when a player is accepted into a game.
	 */
	PLAYER_JOIN_ACCEPT,
	/**
	 * Sent when a player make a move.
	 */
	PLAYER_MOVE,
	/**
	 * Sent when a player is not allowed to join a game.
	 */
	PLAYER_JOIN_REJECT,
	/**
	 * Sent when the game ends.
	 */
	GAME_END;
}
