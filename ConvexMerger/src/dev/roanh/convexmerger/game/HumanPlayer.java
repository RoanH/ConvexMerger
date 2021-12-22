package dev.roanh.convexmerger.game;

import dev.roanh.convexmerger.game.Theme.PlayerTheme;

public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(PlayerTheme theme){
		//TODO color is temporary
		super(true, "Player " + (ID++), theme);
	}
}
