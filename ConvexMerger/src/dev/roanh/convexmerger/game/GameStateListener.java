package dev.roanh.convexmerger.game;

import java.util.List;

import dev.roanh.convexmerger.player.Player;

/**
 * Interface that receives game state updates.
 * @author Roan
 */
public abstract interface GameStateListener{
	
	/**
	 * Called when a player claims a new object.
	 * @param player The player that made the claim.
	 * @param obj The object that was claimed.
	 */
	public abstract void claim(Player player, ConvexObject obj);
	
	/**
	 * Called when a player performs a merge.
	 * @param player The player that performed the merge.
	 * @param source The object the merge was started from.
	 * @param target The target object of the merge.
	 * @param result The object resulting from the merge.
	 * @param absorbed The objects absorbed in the merge.
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 */
	public abstract void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed) throws InterruptedException;
	
	/**
	 * Called when the game ends.
	 */
	public abstract void end();
	
	/**
	 * Called when the game is aborted (forcefully terminated).
	 */
	public abstract void abort();
}