package dev.roanh.convexmerger.game;

import dev.roanh.convexmerger.ui.MessageDialog;

/**
 * Object returned to describe the outcome of an attempted claim.
 * @author Roan
 */
public class ClaimResult{
	/**
	 * Result returned when the claim did not affect the game state.
	 * This claim result has no message and no result.
	 */
	public static final ClaimResult EMPTY = new ClaimResult();
	/**
	 * Message returned for invalid moves.
	 */
	private MessageDialog message = null;
	/**
	 * The newly claimed or created convex object
	 * as a result of the claim that was made.
	 */
	private ConvexObject result = null;
	
	/**
	 * Prevent direct instantiation.
	 */
	private ClaimResult(){
	}
	
	/**
	 * Constructs a new claim result with the
	 * given feedback message.
	 * @param msg The feedback message.
	 */
	private ClaimResult(MessageDialog msg){
		message = msg;
	}
	
	/**
	 * Constructs a new claim result with the
	 * given claim result.
	 * @param obj The newly claimed or created object.
	 */
	private ClaimResult(ConvexObject obj){
		result = obj;
	}
	
	/**
	 * Checks if this result has a feedback message.
	 * @return True if this result has a feedback message.
	 */
	public boolean hasMessage(){
		return message != null;
	}
	
	/**
	 * Gets the feedback message for this result.
	 * @return The feedback message or <code>null</code>
	 *         if there is no feedback message.
	 * @see #hasMessage()
	 */
	public MessageDialog getMessage(){
		return message;
	}
	
	/**
	 * Checks if this claim result has a game state result.
	 * @return True if this result has a game state result.
	 */
	public boolean hasResult(){
		return result != null;
	}
	
	/**
	 * Gets the game state result for this claim result.
	 * This is the convex object that was claimed or created.
	 * @return The game state result.
	 */
	public ConvexObject getResult(){
		return result;
	}
	
	/**
	 * Constructs a new claim result with the
	 * given feedback message.
	 * @param msg The feedback message.
	 * @return The newly constructed claim result.
	 */
	public static final ClaimResult of(MessageDialog msg){
		return new ClaimResult(msg);
	}
	
	/**
	 * Constructs a new claim result with the
	 * given claim result.
	 * @param obj The newly claimed or created object.
	 * @return The newly constructed claim result.
	 */
	public static final ClaimResult of(ConvexObject obj){
		return new ClaimResult(obj);
	}
}
