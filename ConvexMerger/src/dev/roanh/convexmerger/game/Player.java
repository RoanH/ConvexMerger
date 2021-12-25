package dev.roanh.convexmerger.game;

import dev.roanh.convexmerger.game.Theme.PlayerTheme;

public abstract class Player{
	private ScoreAnimation scoreAnimation = new ScoreAnimation(this);
	private String name;
	private boolean human;
	private PlayerTheme theme;
	private double area;

	protected Player(boolean human, String name, PlayerTheme theme){
		this.human = human;
		this.theme = theme;
		this.name = name;
	}

	public PlayerTheme getTheme(){
		return theme;
	}

	public boolean isHuman(){
		return human;
	}
	
	public boolean isAI(){
		return !human;
	}
	
	public void addArea(double area){
		this.area += area;
	}
	
	public void removeArea(double area){
		this.area -= area;
	}
	
	public double getArea(){
		return area;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean owns(ConvexObject obj){
		return obj.isOwnedBy(this);
	}
	
	public ScoreAnimation getScoreAnimation(){
		return scoreAnimation;
	}
	
	@Override
	public String toString(){
		return "Player[name=\"" + name + "\",human=" + human + ",area=" + area + "]";
	}
}
