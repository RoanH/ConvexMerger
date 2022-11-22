/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	QUIT("Quit", "    Are you sure you want to quit?"),
	/**
	 * Shown when the game state is not ready to handle the next player action.
	 */
	NOT_READY("Game Not Ready", "The game is not ready yet to handle the next turn, please wait a bit.");
	
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
