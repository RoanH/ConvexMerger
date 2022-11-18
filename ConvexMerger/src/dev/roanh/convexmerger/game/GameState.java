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
package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.animation.Animation;
import dev.roanh.convexmerger.animation.ClaimAnimation;
import dev.roanh.convexmerger.animation.MergeAnimation;
import dev.roanh.convexmerger.game.SegmentPartitionTree.LineSegment;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.MessageDialog;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Class managing the main game state, data,
 * players and general control flow.
 * @author Roan
 */
public class GameState{
	/**
	 * The convex objects in this game.
	 */
	private List<ConvexObject> objects;
	/**
	 * The players playing in this game.
	 */
	private List<Player> players = new ArrayList<Player>();
	/**
	 * The vertical decomposition for the game state.
	 */
	private VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);
	/**
	 * The index of the player whose turn it is.
	 */
	private int activePlayer = 0;
	/**
	 * The convex object currently selected by the player,
	 * or <code>null</code> if there is no selected object.
	 */
	private ConvexObject selected = null;
	/**
	 * Whether the game has ended or not.
	 */
	private boolean ended = false;
	/**
	 * Millisecond time the game started.
	 */
	private final long gameStart;
	/**
	 * Millisecond time the game ended or -1.
	 */
	private long gameEnd = -1L;
	/**
	 * The number of individual player turns so far in this game.
	 */
	private int turns = 0;
	/**
	 * Listeners subscribed for gamestate events.
	 */
	private List<GameStateListener> listeners = new ArrayList<GameStateListener>();
	/**
	 * The seed of the generator that generated this game's playfield.
	 */
	private String seed;
	/**
	 * The conjugation tree based segment intersection tree.
	 */
	private SegmentPartitionTree<ConjugationTree<LineSegment>> segmentTreeConj;
	/**
	 * The kd-tree based segment intersection tree.
	 */
	private SegmentPartitionTree<KDTree<LineSegment>> segmentTreeKD;

	/**
	 * Constructs a new game state with the given playfield generator and
	 * list of participating players. The game timer will be started immediately.
	 * @param generator The generator to use to generate the playfield.
	 * @param players The list of participating players.
	 */
	public GameState(PlayfieldGenerator generator, List<Player> players){
		this(generator.generatePlayfield(), generator.getSeed(), players);
	}
	
	/**
	 * Constructs a new game state with the given playfield objects, seed and
	 * list of participating players. The game timer will be started immediately.
	 * @param objects The playfield objects for the game.
	 * @param seed The seed of the playfield generator.
	 * @param players The list of participating players.
	 */
	public GameState(List<ConvexObject> objects, String seed, List<Player> players){
		this.objects = new ArrayList<ConvexObject>(objects);
		this.players = Collections.unmodifiableList(players);
		this.seed = seed;
		for(int i = 0; i < objects.size(); i++){
			ConvexObject obj = objects.get(i);
			obj.setID(i + 1);
			decomp.addObject(obj);
		}
		for(int i = 0; i < players.size(); i++){
			Player player = players.get(i);
			player.init(this, PlayerTheme.get(i + 1));
			player.setID(i + 1);
			if(player instanceof GameStateListener){
				listeners.add((GameStateListener)player);
			}
		}
		segmentTreeConj = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(objects);
		segmentTreeKD = SegmentPartitionTree.TYPE_KD_TREE.fromObjects(objects);
		gameStart = System.currentTimeMillis();
	}
	
	/**
	 * Gets the conjugation tree based segment intersection tree for this game state.
	 * @return The conjugation tree based segment intersection tree.
	 */
	public SegmentPartitionTree<ConjugationTree<LineSegment>> getSegmentTreeConj(){
		return segmentTreeConj;
	}
	
	/**
	 * Gets the kd-tree based segment intersection tree for this game state.
	 * @return The kd-tree based segment intersection tree.
	 */
	public SegmentPartitionTree<KDTree<LineSegment>> getSegmentTreeKD(){
		return segmentTreeKD;
	}
	
	/**
	 * Initialises the game state running tasks that
	 * need to run on the main game thread.
	 * @throws InterruptedException When the game was aborted.
	 */
	public void init() throws InterruptedException{
		decomp.rebuild();
	}
	
	/**
	 * Checks if the game state is ready to handle the next turn.
	 * @return True if the game state is ready for the next turn.
	 */
	public boolean ready(){
		return !decomp.needsRebuild();
	}
	
	/**
	 * Gets the seed of the playfield generator that
	 * was used to generate this game.
	 * @return The game seed.
	 */
	public String getSeed(){
		return seed;
	}
	
	/**
	 * Registers a listener to receive game status updates.
	 * @param listener The listener to register.
	 */
	public void registerStateListener(GameStateListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Attempts to claim the given convex object for the active player.
	 * @param obj The object to claim.
	 * @return The result of the attempted claim.
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 * @see #getActivePlayer()
	 */
	public ClaimResult claimObject(ConvexObject obj) throws InterruptedException{
		return claimObject(obj, obj.getCentroid());
	}
	
	/**
	 * Attempts to claim the given convex object for the active player.
	 * @param obj The object to claim.
	 * @param location The point within the object that was clicked to claim it.
	 * @return The result of the attempted claim.
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 * @see #getActivePlayer()
	 */
	public ClaimResult claimObject(ConvexObject obj, Point2D location) throws InterruptedException{
		if(!obj.isOwned()){
			if(selected != null){
				ConvexObject merged = mergeObjects(selected, obj);
				if(merged != null){
					endTurn();
					return ClaimResult.of(merged);
				}else{
					selected = null;
					return ClaimResult.of(MessageDialog.MERGE_INTERSECTS);
				}
			}else{
				Player player = getActivePlayer();
				player.addArea(obj.getArea());
				synchronized(objects){
					obj.setOwner(player);
					obj.setAnimation(new ClaimAnimation(obj, location));
				}
				player.getStats().addClaim();
				listeners.forEach(l->l.claim(player, obj));
				endTurn();
				return ClaimResult.of(obj);
			}
		}else if(getActivePlayer().equals(obj.getOwner())){
			if(selected == null){
				selected = obj;
				return ClaimResult.EMPTY;
			}else{
				if(obj.equals(selected)){
					selected = null;
					return ClaimResult.EMPTY;
				}else{
					ConvexObject merged = mergeObjects(obj, selected);
					if(merged != null){
						endTurn();
						return ClaimResult.of(merged);
					}else{
						selected = null;
						return ClaimResult.of(MessageDialog.MERGE_INTERSECTS);
					}
				}
			}
		}else{
			if(selected != null){
				selected = null;
			}
			return ClaimResult.of(MessageDialog.ALREADY_OWNED);
		}
	}
	
	/**
	 * Ends the turn for the active player moving on to the next player.
	 * @see #getActivePlayer()
	 */
	private void endTurn(){
		selected = null;
		activePlayer = (activePlayer + 1) % players.size();
	}
	
	/**
	 * Attempts to merge the given two convex objects.
	 * @param first The first object to merge (already owned).
	 * @param second The second object to merge (could be unowned).
	 * @return The convex object that is the result of merging the
	 *         two given objects, or <code>null</code> if the merge
	 *         was not possible.
	 * @throws InterruptedException When the player was
	 *         interrupted while making its move. Signalling
	 *         that the game was aborted.
	 */
	private ConvexObject mergeObjects(ConvexObject first, ConvexObject second) throws InterruptedException{
		ConvexObject merged = first.merge(this, second, true);
		if(merged != null){
			Player player = first.getOwner();
			
			List<ConvexObject> contained = new ArrayList<ConvexObject>();
			int maxID = Math.max(first.getID(), second.getID());
			synchronized(objects){
				objects.remove(first);
				objects.remove(second);
				decomp.removeObject(first);
				decomp.removeObject(second);
				
				for(ConvexObject obj : objects){
					maxID = Math.max(maxID, obj.getID());
					if(merged.contains(obj)){
						decomp.removeObject(obj);
						contained.add(obj);
						if(obj.isOwned()){
							obj.getOwner().removeArea(obj.getArea());
						}
					}
				}
				objects.removeAll(contained);
				
				objects.add(merged);
				decomp.addObject(merged);
				
				player.removeArea(first.getArea());
				player.removeArea(second.getArea());
				player.addArea(merged.getArea());
				player.getStats().addMerge();
				player.getStats().addAbsorbed(contained.size());
				
				merged.setOwner(player);
				merged.setID(maxID + 1);
				merged.setAnimation(Animation.EMPTY);
			}
			
			for(GameStateListener listener : listeners){
				listener.merge(player, first, second, merged, contained);
			}
			merged.setAnimation(new MergeAnimation(first, second, merged, contained));
			
			return merged;
		}else{
			return null;
		}
	}
	
	/**
	 * Gets the active players whose turn it is currently.
	 * @return The active player.
	 */
	public Player getActivePlayer(){
		return players.get(activePlayer);
	}
	
	/**
	 * Gets the object located at the given coordinates.
	 * @param p The coordinates to get the object at.
	 * @return The object at the given coordinates.
	 */
	public ConvexObject getObject(Point2D p){
		return getObject(p.getX(), p.getY());
	}
	
	/**
	 * Gets the object located at the given coordinates.
	 * @param x The x coordinate to look at.
	 * @param y The y coordinate to look at.
	 * @return The object at the given coordinates.
	 */
	public ConvexObject getObject(double x, double y){
		return decomp.queryObject(x, y);
	}
	
	/**
	 * Gets all the objects in this game.
	 * @return All the objects in this game.
	 */
	public List<ConvexObject> getObjects(){
		return objects;
	}
	
	/**
	 * Gets the vertical decomposition for the game state.
	 * @return The vertical decomposition.
	 */
	public VerticalDecomposition getVerticalDecomposition(){
		return decomp;
	}
	
	/**
	 * Gets the helper lines to be drawn from the currently
	 * selected first object to the given point.
	 * @param p The point to draw the helper lines to.
	 * @return The helper lines from the currently selected
	 *         object to the given point, or <code>null</code>
	 *         if there is no object selected currently.
	 */
	public List<Line2D> getHelperLines(Point2D p){
		if(selected == null || selected.contains(p.getX(), p.getY())){
			return null;
		}else{
			return ConvexUtil.computeSinglePointMergeLines(selected.getPoints(), p);
		}
	}
	
	/**
	 * True if the active player is currently selecting
	 * a second object to merge with.
	 * @return True if the active player is selecting
	 *         a second object for a merge.
	 */
	public boolean isSelectingSecond(){
		return selected != null;
	}
	
	/**
	 * Streams all the objects in this game.
	 * @return A stream of all the objects in this game.
	 */
	public Stream<ConvexObject> stream(){
		return objects.stream();
	}
	
	/**
	 * Gets a list of all the players in this game.
	 * @return All the players in this game.
	 */
	public List<Player> getPlayers(){
		return players;
	}
	
	/**
	 * Gets the total number of players in this game.
	 * @return The total number of players.
	 */
	public int getPlayerCount(){
		return players.size();
	}
	
	/**
	 * Executes the turn for the active player.
	 * @throws InterruptedException When the current
	 *         thread is interrupted while the player
	 *         is executing its move. This signals that
	 *         the game was aborted.
	 * @see #getActivePlayer()
	 */
	public void executePlayerTurn() throws InterruptedException{
		ended = !getActivePlayer().executeMove();
		if(ended){
			turns--;
			gameEnd = System.currentTimeMillis();
			listeners.forEach(GameStateListener::end);
		}
		turns++;
		if(decomp.needsRebuild()){
			decomp.rebuild();
		}
	}
	
	/**
	 * Checks if the game is finished.
	 * @return True if the game is finished.
	 */
	public boolean isFinished(){
		return ended;
	}
	
	/**
	 * Gets the total number of milliseconds this game took
	 * or is taking up to this point in time.
	 * @return The total game time in milliseconds.
	 */
	public long getGameTime(){
		return ended ? (gameEnd - gameStart) : (System.currentTimeMillis() - gameStart);
	}
	
	/**
	 * Gets the number of rounds in this game. In a single round
	 * each players gets a single turn. Note that the last round
	 * can be a partial round where not every player got a turn.
	 * @return The number of rounds.
	 */
	public int getRounds(){
		int rounds = Math.floorDiv(turns, players.size());
		return rounds * players.size() == turns ? rounds : (rounds + 1);
	}
	
	/**
	 * Sets the object selected by the current player. This is the
	 * object the player intends to start a merge from. Note that
	 * it is generally safer to use {@link #claimObject(ConvexObject)}
	 * to set this value as this method does not perform any checks
	 * to ensure it is actually valid for the player to select the
	 * provided object.
	 * @param obj The objected to set as selected.
	 * @see #claimObject(ConvexObject)
	 * @see #claimObject(ConvexObject, Point2D)
	 */
	public void setSelectedObject(ConvexObject obj){
		selected = obj;
	}

	/**
	 * Clears the object selection for the active player,
	 * meaning they are no longer selecting a second object for a merge.
	 */
	public void clearSelection(){
		selected = null;
	}
	
	/**
	 * Gets the (first) object selected by the player.
	 * @return The currently selected object to start
	 *         a merge from or <code>null</code>.
	 * @see #isSelectingSecond()
	 */
	public ConvexObject getSelectedObject(){
		return selected;
	}

	/**
	 * Signals that this game was forcefully terminated
	 * before it was supposed to finish.
	 */
	public void abort(){
		listeners.forEach(GameStateListener::abort);
	}
	
	/**
	 * Interface that receives game state updates.
	 * @author Roan
	 */
	public static abstract interface GameStateListener{
		
		/**
		 * Called when a player claims a new object.
		 * @param player The player that made the claim.
		 * @param obj The object that was claimed.
		 */
		public abstract void claim(Player player, ConvexObject obj);
		
		/**
		 * Called when a player performs a merge.
		 * @param player The player that performed the merge.
		 * @param source The object the merge was started from.
		 * @param target The target object of the merge.
		 * @param result The object resulting from the merge.
		 * @param absorbed The objects absorbed in the merge.
		 * @throws InterruptedException When the player was
		 *         interrupted while making its move. Signalling
		 *         that the game was aborted.
		 */
		public abstract void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed) throws InterruptedException;
		
		/**
		 * Called when the game ends.
		 */
		public abstract void end();
		
		/**
		 * Called when the game is aborted (forcefully terminated).
		 */
		public abstract void abort();
	}
}
