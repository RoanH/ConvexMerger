package dev.roanh.convexmerger;

import dev.roanh.convexmerger.game.ConvexMerger;
import dev.roanh.util.Util;

public class Main{

	public static void main(String[] args){
		Util.installUI();
		ConvexMerger game = new ConvexMerger();
		game.initialiseGame();
		game.showGame();
		//TODO ...
	}
}
