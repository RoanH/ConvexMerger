package dev.roanh.convexmerger.game;

import java.awt.Color;

public class HumanPlayer implements Player{

	@Override
	public Color getColor(){
		return Color.RED;//TODO just for testing
	}

	@Override
	public boolean isHuman(){
		return true;
	}
}
