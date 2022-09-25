package dev.roanh.convexmerger;

import dev.roanh.convexmerger.game.ConvexUtil.TestScreen;
import dev.roanh.convexmerger.ui.ConvexMerger;
import dev.roanh.util.Util;

/**
 * Main entry point for the application.
 * @author Roan
 */
public class Main{

	/**
	 * Main subroutine that starts the game.
	 * @param args No valid command line arguments.
	 */
	public static void main(String[] args){
		Util.installUI();
		
		ConvexMerger game = new ConvexMerger();
		game.showGame();
		
		game.switchScene(new TestScreen(game));
	}
}
