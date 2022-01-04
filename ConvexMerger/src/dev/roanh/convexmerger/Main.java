package dev.roanh.convexmerger;

import dev.roanh.convexmerger.ui.ConvexMerger;
import dev.roanh.util.Util;

public class Main{

	public static void main(String[] args){
		Util.installUI();
		
		ConvexMerger game = new ConvexMerger();
//		game.hostMultiplayerGame();
		
		game.initialiseGame();
		game.showGame();
		
		
//		ConvexMerger game2 = new ConvexMerger();
//		game2.joinMultiplayerGame();
		
		//game2.showGame();
	}
}
