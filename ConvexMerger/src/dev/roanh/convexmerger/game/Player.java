package dev.roanh.convexmerger.game;

import java.awt.Color;

public abstract class Player{
	private String name;
	private boolean human;
	private Color color;
	private double area;

	protected Player(boolean human, String name, Color color){
		this.human = human;
		this.color = color;
		this.name = name;
	}

	public Color getColor(){
		return color;
	}

	public boolean isHuman(){
		return human;
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
	
	@Override
	public String toString(){
		return "Player[name=\"" + name + "\",human=" + human + ",area=" + area + "]";
	}
}
