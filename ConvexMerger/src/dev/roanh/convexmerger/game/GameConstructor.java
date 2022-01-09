package dev.roanh.convexmerger.game;

/**
 * A simple interface used to execute the longer
 * calculations required to start a game on the
 * main game thread.
 * @author Roan
 */
public abstract interface GameConstructor{

	/**
	 * Constructs the game state for the configured game.
	 * @return The game state.
	 */
	public abstract GameState create();
}
