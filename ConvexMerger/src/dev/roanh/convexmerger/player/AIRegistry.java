package dev.roanh.convexmerger.player;

import java.util.function.Supplier;

/**
 * Registry of AI players.
 * @author Roan
 */
public enum AIRegistry{
	/**
	 * Isla, a greedy AI that maximises relative area gain.
	 */
	ISLA("Isla", GreedyPlayer::new),
	/**
	 * Elaina, a greedy AI that maximises the relative area
	 * gain in a specific part of the map.
	 */
	ELAINA("Elaina", LocalPlayer::new),
	/**
	 * Shiro, a greedy AI that maximises the relative area
	 * gain in a specific part of the map from a small object.
	 */
	SHIRO("Shiro", SmallPlayer::new);
	
	/**
	 * The name of this AI.
	 */
	private String name;
	/**
	 * The construct for this AI.
	 */
	private Supplier<Player> ctor;
	
	/**
	 * Constructs a new registry entry.
	 * @param name The name of the AI.
	 * @param ctor The construct of the AI.
	 */
	private AIRegistry(String name, Supplier<Player> ctor){
		this.name = name;
		this.ctor = ctor;
	}
	
	/**
	 * Creates a new instance of this AI.
	 * @return A new AI instance.
	 */
	public Player createInstance(){
		return ctor.get();
	}
	
	/**
	 * Gets the name of this AI.
	 * @return The name of this AI.
	 */
	public String getName(){
		return name;
	}
}
