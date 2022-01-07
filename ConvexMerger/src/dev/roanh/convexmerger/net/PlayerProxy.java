package dev.roanh.convexmerger.net;

import java.io.Serializable;
import java.util.Objects;

import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.RemotePlayer;

/**
 * Proxy representative of players across a connection.
 * @author Roan
 */
public class PlayerProxy implements Serializable{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1220844320624428578L;
	/**
	 * The ID of the player this proxy represents.
	 */
	private int id;
	/**
	 * The name of the player this proxy represents.
	 */
	private String name;
	/**
	 * Whether the player this proxy represents is an AI.
	 */
	private boolean ai;
	/**
	 * Whether the connection to the player this proxy represents was lost.
	 */
	private boolean lost;
	
	/**
	 * Constructs a new player proxy from the given player.
	 * @param player The player to construct a proxy for.
	 */
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
	
	/**
	 * Checks if the connection to the player was lost.
	 * @return True if the connection to the player was lost.
	 */
	public boolean isLost(){
		return lost;
	}
	
	/**
	 * Gets the ID of the player.
	 * @return The ID of the player.
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Gets the name of the player.
	 * @return The name of the player.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Checks if the player is an AI.
	 * @return True if the player is an AI.
	 */
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
