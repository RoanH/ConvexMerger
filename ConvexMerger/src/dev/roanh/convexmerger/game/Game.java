package dev.roanh.convexmerger.game;

import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.player.Player;

public class Game{
	private List<Player> players = new ArrayList<Player>(4);
	
	
	
	public void addPlayer(Player player){
		players.add(player);
	}
	
	
	public int getPlayerCount(){
		return players.size();
	}
}
