package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

public class HumanPlayer extends Player{

	public HumanPlayer(){
		//TODO color is temporary
		super(true, new Color(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255)));
	}
}
