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
	
	protected MergeOption findBestMergeFrom(ConvexObject obj){
		ConvexObject first = null;
		ConvexObject second = null;
		double increase = 0.0D;
		
		for(ConvexObject other : state.getObjects()){
			if(!other.isOwned() || (other.isOwnedBy(this) && !other.equals(obj))){
				ConvexObject combined = obj.merge(state, other);
				if(combined != null){
					double area = combined.getArea();
					
					if(other.isOwnedBy(this)){
						area -= other.getArea();
					}
					if(obj.isOwnedBy(this)){
						area -= obj.getArea();
					}
					
					for(ConvexObject check : state.getObjects()){
						if(!obj.equals(other) && !obj.equals(obj) && combined.contains(check)){
							if(check.isOwnedBy(this)){
								area -= check.getArea();
							}else if(check.isOwned()){
								//stealing is good
								area += check.getArea();
							}
						}
					}
					
					if(first == null || area > increase){
						increase = area;
						first = obj;
						second = other;
					}
				}
			}
		}
		
		return first == null ? null : new MergeOption(first, second, increase);
	}
		
	@Override
	public String toString(){
		return "Player[name=\"" + name + "\",human=" + human + ",area=" + area + "]";
	}
	
	protected class MergeOption{
		private ConvexObject first = null;
		private ConvexObject second = null;
		private double increase = 0.0D;
		
		private MergeOption(ConvexObject first, ConvexObject second, double increase){
			this.first = first;
			this.second = second;
			this.increase = increase;
		}
		
		public double getIncrease(){
			return increase;
		}
		
		public void execute(){
			state.claimObject(first);
			state.claimObject(second);
		}
	}
}
