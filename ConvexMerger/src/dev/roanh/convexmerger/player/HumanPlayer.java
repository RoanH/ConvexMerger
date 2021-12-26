package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.game.ConvexObject;

public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(){//TODO pass name
		super(true, "Player " + (ID++));
	}

	@Override
	public boolean executeMove(){
		return state.stream().filter(ConvexObject::canClaim).findAny().isPresent() || stream().filter(this::hasMergeFrom).findAny().isPresent();
	}
}
