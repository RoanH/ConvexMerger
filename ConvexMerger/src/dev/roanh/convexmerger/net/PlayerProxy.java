package dev.roanh.convexmerger.net;

import java.io.Serializable;
import java.util.Objects;

import dev.roanh.convexmerger.player.Player;

public class PlayerProxy implements Serializable{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1220844320624428578L;
	private int id;
	private String name;
	private boolean ai;
	
	public PlayerProxy(Player player){
		id = player.getID();
		name = player.getName();
		ai = player.isAI();
	}
	
	public int getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isAI(){
		return ai;
	}
	
	@Override
	public boolean equals(Object other){
		return other instanceof PlayerProxy ? ((PlayerProxy)other).id == id : false;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
}
