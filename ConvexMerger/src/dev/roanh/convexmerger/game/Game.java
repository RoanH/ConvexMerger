package dev.roanh.convexmerger.game;

import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.player.Player;

public class Game{
	private List<Player> players = new ArrayList<Player>(4);
	private String seed = null;//TODO
	private List<ConvexObject> objects;//TODO
	
	public Game(PlayfieldGenerator gen){
		objects = gen.generatePlayfield();
		System.out.println("finish gen");
	}
	
	public int addPlayer(Player player){
		players.add(player);
		find: for(int i = 1; i <= 4; i++){
			for(Player p : players){
				if(p.getID() == i){
					continue find;
				}
			}
			
			player.setID(i);
			break;
		}
		return player.getID();
	}
	
	public int getPlayerCount(){
		return players.size();
	}
	
	public GameState toGameState(){
		for(int i = 0; i < objects.size(); i++){
			objects.get(i).setID(i + 1);
		}
		
		return new GameState(objects, players);
	}
}
