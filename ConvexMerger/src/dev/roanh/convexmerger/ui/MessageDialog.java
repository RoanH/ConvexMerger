package dev.roanh.convexmerger.ui;

import java.util.Arrays;
import java.util.List;

/**
 * Enum of feedback messages shown to the player.
 * @author Roan
 */
public enum MessageDialog{
	/**
	 * Shown when the player tries to claim an object that was
	 * previously already claimed by a different player.
	 */
	ALREADY_OWNED("Already Claimed", "This object was already claimed by another player."),
	/**
	 * Shown when a merge attempted by the player intersects
	 * other objects on its boundary.
	 */
	MERGE_INTERSECTS("Invalid Merge", "Your merge intersects other objects on its boundary."),
	/**
	 * Shown when the player tries to perform a move when it's not their turn.
	 */
	NO_TURN("Not Your Turn", "Please wait for the other player(s) to finish their turn."),
	/**
	 * Shown when the player tries to perform a move after the game ended.
	 */
	GAME_END("Game Ended", "This game has ended, please start a new game."),
	/**
	 * Shown when the player presses the menu button.
	 */
	QUIT("Quit", "    Are you sure you want to quit?");
	
	/**
	 * The title for this dialog.
	 */
	private final String title;
	/**
	 * The message for this dialog.
	 */
	private final List<String> message;
	
	/**
	 * Constructs a new message dialog with the given
	 * title and feedback message.
	 * @param title The dialog title.
	 * @param message The feedback message.
	 */
	private MessageDialog(String title, String message){
		this.title = title;
		this.message = Arrays.asList(message.split(" "));
	}
	
	/**
	 * Gets the title for this dialog.
	 * @return The title for this dialog.
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Gets the feedback message for this dialog.
	 * @return The feedback message for this dialog
	 *         as a list of words.
	 */
	public List<String> getMessage(){
		return message;
	}
}
