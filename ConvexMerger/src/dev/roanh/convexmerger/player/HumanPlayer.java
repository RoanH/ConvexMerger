package dev.roanh.convexmerger.player;

import java.util.Objects;

public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(){//TODO pass name
		super(true, "Player " + (ID++));
	}

	@Override
	public boolean executeMove(){
		return findLargestUnownedObject() != null || stream().map(this::findBestMergeFrom).anyMatch(Objects::nonNull);
	}
}
