/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.player;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import dev.roanh.convexmerger.animation.ScoreAnimation;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.Identity;
import dev.roanh.convexmerger.net.PlayerProxy;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Abstract base class for player instances.
 * @author Roan
 */
public abstract class Player implements Identity{
	/**
	 * The ID of this player.
	 */
	private int id;
	/**
	 * The game this player is associated with.
	 */
	protected GameState state;
	/**
	 * The game stats for this player.
	 */
	protected PlayerStats stats;
	/**
	 * The score animation for this player.
	 */
	private ScoreAnimation scoreAnimation = new ScoreAnimation(this);
	/**
	 * The name for this player.
	 */
	private String name;
	/**
	 * If this player is an AI or not.
	 */
	private boolean ai;
	/**
	 * The theme for this player.
	 */
	private PlayerTheme theme;
	/**
	 * The total area claimed by this player.
	 */
	private double area;
	/**
	 * Whether this player is local or acting as remote proxy.
	 */
	private boolean local;

	/**
	 * Constructs a new player instance.
	 * @param local Whether this player is local or acting as remote proxy.
	 * @param ai Whether this player is an AI.
	 * @param name The name of this player.
	 */
	protected Player(boolean local, boolean ai, String name){
		this.ai = ai;
		this.name = name;
		this.local = local;
		stats = new PlayerStats();
	}
	
	/**
	 * Initialises this player with the given game state and theme.
	 * @param game The game this player is now a part of.
	 * @param theme The color theme for this player.
	 */
	public void init(GameState game, PlayerTheme theme){
		this.theme = theme;
		state = game;
	}
	
	/**
	 * Executes a move for this player.
	 * @return True if the move was executed successfully,
	 *         false if no possible move was found (game end).
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 */
	public abstract boolean executeMove() throws InterruptedException;
	
	/**
	 * Checks if this player is executing locally or
	 * acting as a remote proxy.
	 * @return True if this player is local.
	 */
	public boolean isLocal(){
		return local;
	}
	
	/**
	 * Gets the color theme for this player.
	 * @return The color theme for this player.
	 */
	public PlayerTheme getTheme(){
		return theme;
	}

	/**
	 * Checks if this player is an AI.
	 * @return True if this player is an AI.
	 */
	public boolean isAI(){
		return ai;
	}
	
	/**
	 * Adds some claimed area to this player.
	 * @param area The area that was claimed.
	 */
	public void addArea(double area){
		this.area += area;
	}
	
	/**
	 * Removes some claimed area from this player.
	 * @param area The area to remove.
	 */
	public void removeArea(double area){
		this.area -= area;
	}
	
	/**
	 * Gets the total area claimed by this player.
	 * @return The total claimed area for this player.
	 */
	public double getArea(){
		return area;
	}
	
	/**
	 * Gets the name of this player.
	 * @return The name of this player.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Checks if this player owns the given object.
	 * @param obj The object to check.
	 * @return True if this player owns the given object.
	 */
	public boolean owns(ConvexObject obj){
		return obj.isOwnedBy(this);
	}
	
	/**
	 * Getst the animation rendering the score for this player.
	 * @return The score rendering animation for this player.
	 */
	public ScoreAnimation getScoreAnimation(){
		return scoreAnimation;
	}
	
	/**
	 * Streams all the objects owned by this player.
	 * @return A stream of all the objects owned by this player.
	 */
	public Stream<ConvexObject> stream(){
		return state.stream().filter(this::owns);
	}
	
	/**
	 * Finds the largest unowned object left in the
	 * current game state.
	 * @return The largest unowned object or <code>
	 *         null</code> if there are no unowned
	 *         object left in the game.
	 */
	protected ConvexObject findLargestUnownedObject(){
		return findLargestUnownedObject(Double::compare);
	}
	
	/**
	 * Finds the largest unowned object left in the
	 * current game state according to the given comparator.
	 * @param comparator The comparator to use to compare object sizes.
	 * @return The largest unowned object or <code>
	 *         null</code> if there are no unowned
	 *         object left in the game.
	 */
	protected ConvexObject findLargestUnownedObject(Comparator<Double> comparator){
		ConvexObject max = null;
		for(ConvexObject obj : state.getObjects()){
			if(!obj.isOwned()){
				if(max == null || comparator.compare(obj.getArea(), max.getArea()) > 0){
					max = obj;
				}
			}
		}
		return max;
	}
	
	/**
	 * Sets the name of this player.
	 * @param name The new player name.
	 */
	protected void setName(String name){
		this.name = name;
	}
	
	/**
	 * Checks if there exists a possible valid merge from the
	 * given object to any other object. The given object does
	 * not need to be owned by this player.
	 * @param obj The object to check merges for.
	 * @return True if a merge exists from the given object.
	 */
	protected boolean hasMergeFrom(ConvexObject obj){
		for(ConvexObject other : state.getObjects()){
			if((!other.isOwned() || other.isOwnedBy(this)) && !other.equals(obj)){
				ConvexObject combined = obj.merge(state, other);
				if(combined != null){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Finds the best possible merge (largest relative area gain)
	 * from the given object for this player.
	 * @param obj The object to check from.
	 * @return The best possible merge option or <code>null</code>
	 *         if there are no possible merges left in the game.
	 */
	protected MergeOption findBestMergeFrom(ConvexObject obj){
		ConvexObject first = null;
		ConvexObject second = null;
		double increase = 0.0D;
		
		for(ConvexObject other : state.getObjects()){
			if((!other.isOwned() || other.isOwnedBy(this)) && !other.equals(obj)){
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
						if(!obj.equals(check) && !other.equals(check) && combined.contains(check)){
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
	
	/**
	 * Gets a proxy for this player.
	 * @return The proxy for this player.
	 */
	public PlayerProxy getProxy(){
		return new PlayerProxy(this);
	}
	
	/**
	 * Gets the game stats for this player.
	 * @return The game stats for this player.
	 */
	public PlayerStats getStats(){
		return stats;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object other){
		return other instanceof Player ? ((Player)other).id == id : false;
	}
		
	@Override
	public String toString(){
		return "Player[name=\"" + name + "\",ai=" + ai + ",area=" + area + "]";
	}

	@Override
	public int getID(){
		return id;
	}

	@Override
	public void setID(int id){
		this.id = id;
	}
	
	/**
	 * Class describing a possible merge that a player could perform.
	 * @author Roan
	 */
	protected class MergeOption{
		/**
		 * The source object in the merge.
		 */
		private final ConvexObject first;
		/**
		 * The target object in the merge.
		 */
		private final ConvexObject second;
		/**
		 * The relative area gain for this merge.
		 */
		private final double increase;
		
		/**
		 * Constructs a new merge option with the
		 * given objects and area gain.
		 * @param first The source object in the merge.
		 * @param second The target object in the merge.
		 * @param increase The relative area gain for this merge.
		 */
		private MergeOption(ConvexObject first, ConvexObject second, double increase){
			this.first = first;
			this.second = second;
			this.increase = increase;
		}
		
		/**
		 * Gets the relative area gain for this merge. This
		 * means stolen area counts for more than unowned area.
		 * @return The relative area gain for this merge.
		 */
		public double getIncrease(){
			return increase;
		}
		
		/**
		 * Executes this merge by making it concrete in the game state.
		 * @return The object that resulted from the merge.
		 * @throws InterruptedException When the player was
		 *         interrupted while making its move. Signalling
		 *         that the game was aborted.
		 */
		public ConvexObject execute() throws InterruptedException{
			state.claimObject(first);
			return state.claimObject(second).getResult();
		}
	}
	
	/**
	 * Class holding various game related player statistics.
	 * @author Roan
	 */
	public class PlayerStats{
		/**
		 * Number of objects directly claimed by this player.
		 */
		private int claims;
		/**
		 * Number of merges performed by this player.
		 */
		private int merges;
		/**
		 * Number of objects absorbed in merges by this player.
		 */
		private int absorbed;
		/**
		 * Total time spent across all turns.
		 */
		private long totalTurnTime;
		/**
		 * Total number of player turns.
		 */
		private int turns;
		/**
		 * Player score at the end of each turn.
		 */
		private List<Double> scoreHistory = new CopyOnWriteArrayList<Double>();
		
		/**
		 * Increments the total number of objects claimed by one.
		 */
		public void addClaim(){
			claims++;
		}
		
		/**
		 * Increments the total number of merges by one.
		 */
		public void addMerge(){
			merges++;
		}
		
		/**
		 * Increments the total number of objects absorbed
		 * in merges by the given number.
		 * @param n The number of absorbed objects to add.
		 */
		public void addAbsorbed(int n){
			absorbed += n;
		}
		
		/**
		 * Adds the time a player turn took.
		 * @param time The turn time.
		 */
		public void addTurnTime(long time){
			totalTurnTime += time;
			turns++;
			scoreHistory.add(area);
		}

		/**
		 * Gets the total number of objects claimed by this player.
		 * @return The total number of objects claimed by this player.
		 */
		public int getClaims(){
			return claims;
		}

		/**
		 * Gets the total number of merges performed by this player.
		 * @return The total number of merges performed.
		 */
		public int getMerges(){
			return merges;
		}

		/**
		 * Gets the total number of unrelated objects absorbed by merges.
		 * @return The total number of absorbed/stolen objects.
		 */
		public int getAbsorbed(){
			return absorbed;
		}

		/**
		 * Gets the average number of milliseconds per turn for this player.
		 * @return The average number of milliseconds per turn.
		 */
		public long getAverageTurnTime(){
			return turns == 0 ? 0L : totalTurnTime / turns;
		}
		
		/**
		 * Gets the score per turn for this player.
		 * @return The score history for this player.
		 */
		public List<Double> getScoreHistory(){
			return scoreHistory;
		}
		
		/**
		 * Gets the total number of turns for this player.
		 * @return The total number of turns for this player.
		 */
		public int getTurns(){
			return turns;
		}
	}
}
