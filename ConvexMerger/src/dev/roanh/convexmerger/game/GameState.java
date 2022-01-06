package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.animation.ClaimAnimation;
import dev.roanh.convexmerger.animation.MergeAnimation;
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
	private boolean ended = false;
	private final long gameStart;
	private long gameEnd = -1L;
	private int turns = 0;
	private List<GameStateListener> listeners = new ArrayList<GameStateListener>();
	
	//TODO pass generator instead of objects also alt ctor for net play
	public GameState(List<ConvexObject> objects, List<Player> players){
		this.objects = new CopyOnWriteArrayList<ConvexObject>(objects);
		this.players = Collections.unmodifiableList(players);
		for(int i = 0; i < objects.size(); i++){
			ConvexObject obj = objects.get(i);
			obj.setID(i + 1);
			decomp.addObject(obj);
		}
		for(int i = 0; i < players.size(); i++){
			Player player = players.get(i);
			player.init(this, PlayerTheme.get(i + 1));
			player.setID(i + 1);
		}
		decomp.rebuild();
		gameStart = System.currentTimeMillis();
	}
	
	public void registerStateListener(GameStateListener listener){
		listeners.add(listener);
	}
	
	public ClaimResult claimObject(ConvexObject obj){
		return claimObject(obj, obj.getCentroid());
	}
	
	public ClaimResult claimObject(ConvexObject obj, Point2D location){
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
				obj.setOwner(player);
				player.addArea(obj.getArea());
				obj.setAnimation(new ClaimAnimation(obj, location));
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
	 */
	private ConvexObject mergeObjects(ConvexObject first, ConvexObject second){
		ConvexObject merged = first.merge(this, second);
		if(merged != null){
			Player player = first.getOwner();
			merged.setOwner(player);
			objects.remove(first);
			objects.remove(second);
			decomp.removeObject(first);
			decomp.removeObject(second);
			player.removeArea(first.getArea());
			player.removeArea(second.getArea());
			
			List<ConvexObject> contained = new ArrayList<ConvexObject>();
			int maxID = 0;
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
			player.addArea(merged.getArea());
			player.getStats().addMerge();
			player.getStats().addAbsorbed(contained.size());
			
			merged.setID(maxID + 1);
			listeners.forEach(l->l.merge(player, first, second));
			merged.setAnimation(new MergeAnimation(first, second, merged, contained));
			
			return merged;
		}else{
			return null;
		}
	}
	
	public Player getActivePlayer(){
		return players.get(activePlayer);
	}
	
	public ConvexObject getObject(Point2D p){
		return getObject(p.getX(), p.getY());
	}
	
	public ConvexObject getObject(double x, double y){
		//TODO remove when decomp done
		for(ConvexObject obj : objects){
			if(obj.contains(x, y)){
				return obj;
			}
		}
		return decomp.queryObject(x, y);
	}
	
	public List<ConvexObject> getObjects(){
		return objects;
	}
	
	public List<Line2D> getVerticalDecompLines(){
		return decomp.getDecompLines();
	}
	
	public List<Line2D> getHelperLines(Point2D p){
		if(selected != null){
			List<Point2D> points = new ArrayList<Point2D>();
			points.addAll(selected.getPoints());
			
			if(points.contains(p) || selected.contains(p.getX(), p.getY())){
				return null;
			}
			points.add(p);
			
			List<Point2D> hull = ConvexUtil.computeConvexHull(points);
			for(int i = 0; i < hull.size(); i++){
				if(hull.get(i).equals(p)){
					Point2D prev = hull.get((i == 0 ? hull.size() : i) - 1);
					Point2D next = hull.get((i + 1) % hull.size());
					return Arrays.asList(
						new Line2D.Double(p.getX(), p.getY(), prev.getX(), prev.getY()),
						new Line2D.Double(p.getX(), p.getY(), next.getX(), next.getY())
					);
				}
			}
		}
		return null;
	}
	
	public boolean isSelectingSecond(){
		return selected != null;
	}
	
	public Stream<ConvexObject> stream(){
		return objects.stream();
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public int getPlayerCount(){
		return players.size();
	}
	
	public void executePlayerTurn(){
		if(ended = !getActivePlayer().executeMove()){
			turns--;
			gameEnd = System.currentTimeMillis();
			listeners.forEach(GameStateListener::end);
		}
		turns++;
	}
	
	public boolean isFinished(){
		return ended;
	}
	
	public long getGameTime(){
		return ended ? (gameEnd - gameStart) : (System.currentTimeMillis() - gameStart);
	}
	
	public int getRounds(){
		return Math.floorDiv(turns, players.size());
	}

	public void clearSelection(){
		selected = null;
	}
	
	public static abstract interface GameStateListener{
		
		public abstract void claim(Player player, ConvexObject obj);
		
		public abstract void merge(Player player, ConvexObject source, ConvexObject target);
		
		public abstract void end();
	}
}
