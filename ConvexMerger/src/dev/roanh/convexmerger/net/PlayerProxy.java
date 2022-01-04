package dev.roanh.convexmerger.net;

import java.io.Serializable;
import java.util.Objects;

import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

public class PlayerProxy implements Serializable{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1220844320624428578L;
	private int id;
	private String name;
	private boolean ai;
	private boolean lost;
	
	public PlayerProxy(Player player){
		id = player.getID();
		name = player.getName();
		ai = player.isAI();
		if(!player.isLocal() && player instanceof RemotePlayer){
			lost = ((RemotePlayer)player).isLost();
		}else{
			lost = false;
		}
	}
	
	public boolean isLost(){
		return lost;
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
