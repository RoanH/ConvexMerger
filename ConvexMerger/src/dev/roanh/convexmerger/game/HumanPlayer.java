package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(){
		//TODO color is temporary
		super(true, "Player " + (ID++), new Color(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255)));
	}
}
