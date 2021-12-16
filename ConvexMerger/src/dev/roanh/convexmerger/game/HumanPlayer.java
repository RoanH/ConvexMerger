package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

public class HumanPlayer implements Player{
	//TODO temp
	private Color color = new Color(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255));

	@Override
	public Color getColor(){
		return color;//TODO just for testing
	}

	@Override
	public boolean isHuman(){
		return true;
	}
}
