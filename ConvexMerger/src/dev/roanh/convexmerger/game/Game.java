package dev.roanh.convexmerger.game;

import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.player.Player;

public class Game{
	private List<Player> players = new ArrayList<Player>(4);
	private String seed = null;//TODO
	private List<ConvexObject> objects;//TODO
	
	
	
	public void addPlayer(Player player){
		players.add(player);
	}
	
	public int getPlayerCount(){
		return players.size();
	}
	
	public GameState toGameState(){
		for(int i = 0; i < players.size(); i++){
			players.get(i).setID(i + 1);
		}
		
		for(int i = 0; i < objects.size(); i++){
			objects.get(i).setID(i + 1);
		}
		
		return new GameState(objects, players);
	}
}
