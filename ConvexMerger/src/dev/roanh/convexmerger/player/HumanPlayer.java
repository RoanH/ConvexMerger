package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(GameState game, PlayerTheme theme){
		//TODO color is temporary
		super(game, true, "Player " + (ID++), theme);
	}

	@Override
	public boolean executeMove(){
		// TODO Auto-generated method stub
		return false;
	}
}
