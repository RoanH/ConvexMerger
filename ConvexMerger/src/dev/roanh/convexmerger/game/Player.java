package dev.roanh.convexmerger.game;

import java.awt.Color;

public abstract class Player{
	private boolean human;
	private Color color;

	protected Player(boolean human, Color color){
		this.human = human;
		this.color = color;
	}

	public Color getColor(){
		return color;
	}

	public boolean isHuman(){
		return human;
	}
}
