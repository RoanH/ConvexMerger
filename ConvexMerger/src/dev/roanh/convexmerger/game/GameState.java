package dev.roanh.convexmerger.game;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
	
	public GameState(List<ConvexObject> objects, List<Player> players){
		this.objects = new CopyOnWriteArrayList<ConvexObject>(objects);
		this.players = Collections.unmodifiableList(players);
		objects.forEach(decomp::addObject);
		for(int i = 0; i < players.size(); i++){
			players.get(i).init(this, PlayerTheme.get(i + 1));
		}
		decomp.rebuild();
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
			
			Iterator<ConvexObject> iterator = objects.iterator();
			List<ConvexObject> contained = new ArrayList<ConvexObject>();
			while(iterator.hasNext()){
				ConvexObject obj = iterator.next();
				if(merged.contains(obj)){
					iterator.remove();
					decomp.removeObject(obj);
					contained.add(obj);
					if(obj.isOwned()){
						obj.getOwner().removeArea(obj.getArea());
					}
				}
			}
			
			objects.add(merged);
			decomp.addObject(merged);
			player.addArea(merged.getArea());
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
		System.out.println("pt: " + p);
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
	
	public List<Line2D> getHelperLines(int x, int y){
		if(selected != null){
			List<Point> points = new ArrayList<Point>();
			points.addAll(selected.getPoints());
			
			Point p = new Point(x, y);
			if(points.contains(p) || selected.contains(p.getX(), p.getY())){
				return null;
			}
			points.add(p);
			
			List<Point> hull = ConvexUtil.computeConvexHull(points);
			for(int i = 0; i < hull.size(); i++){
				if(hull.get(i).equals(p)){
					Point prev = hull.get((i == 0 ? hull.size() : i) - 1);
					Point next = hull.get((i + 1) % hull.size());
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
		ended = !getActivePlayer().executeMove();
	}
	
	public boolean isFinished(){
		return ended;
	}
}
