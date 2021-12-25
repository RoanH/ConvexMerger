package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(PlayerTheme theme){
		//TODO color is temporary
		super(true, "Player " + (ID++), theme);
	}
}
