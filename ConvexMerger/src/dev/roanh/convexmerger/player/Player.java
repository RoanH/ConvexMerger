package dev.roanh.convexmerger.player;

import dev.roanh.convexmerger.animation.ScoreAnimation;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

public abstract class Player{
	protected GameState state;
	private ScoreAnimation scoreAnimation = new ScoreAnimation(this);
	private String name;
	private boolean human;
	private PlayerTheme theme;
	private double area;

	protected Player(boolean human, String name){
		this.human = human;
		this.name = name;
	}
	
	public void init(GameState game, PlayerTheme theme){
		this.theme = theme;
		state = game;
	}
	
	public abstract boolean executeMove();

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
	
	protected ConvexObject findLargestUnownedObject(){
		ConvexObject max = null;
		for(ConvexObject obj : state.getObjects()){
			if(!obj.isOwned()){
				if(max == null || obj.getArea() > max.getArea()){
					max = obj;
				}
			}
		}
		return max;
	}
		
	@Override
	public String toString(){
		return "Player[name=\"" + name + "\",human=" + human + ",area=" + area + "]";
	}
}
