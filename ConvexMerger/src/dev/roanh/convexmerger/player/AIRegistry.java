package dev.roanh.convexmerger.player;

import java.util.function.Supplier;

public enum AIRegistry{
	ISLA("Isla", GreedyPlayer::new),
	ELAINA("Elaina", LocalPlayer::new),
	SHIRO("Shiro", SmallPlayer::new);
	
	private String name;
	private Supplier<Player> ctor;
	
	private AIRegistry(String name, Supplier<Player> ctor){
		this.name = name;
		this.ctor = ctor;
	}
	
	public Player createInstance(){
		return ctor.get();
	}
	
	public String getName(){
		return name;
	}
}
