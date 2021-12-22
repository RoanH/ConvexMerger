package dev.roanh.convexmerger.game;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import dev.roanh.convexmerger.Constants;

public class GameState{
	private List<ConvexObject> objects = new ArrayList<ConvexObject>();
	private List<Player> players = new ArrayList<Player>();
	private VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);
	private int activePlayer = 0;
	private ConvexObject selected = null;
	
	public GameState(List<ConvexObject> objects, List<Player> players){
		this.objects = objects;
		this.players = players;
		objects.forEach(decomp::addObject);
		decomp.rebuild();
	}
	
	public MessageDialog claimObject(ConvexObject obj){
		System.out.println("Handle claim: " + obj + " / " + getActivePlayer() + " / " + obj.getOwner());
		if(!obj.isOwned()){
			Player player = getActivePlayer();
			obj.setOwner(player);
			player.addArea(obj.getArea());
			if(selected != null){
				selected.setSelected(false);
				if(mergeObjects(obj, selected)){
					endTurn();
				}else{
					return MessageDialog.MERGE_INTERSECTS;
				}
			}else{
				endTurn();
			}
		}else if(getActivePlayer().equals(obj.getOwner())){
			if(selected == null){
				obj.setSelected(true);
				selected = obj;
			}else{
				if(obj.equals(selected)){
					obj.setSelected(false);
					selected = null;
				}else{
					selected.setSelected(false);
					if(mergeObjects(obj, selected)){
						endTurn();
					}else{
						return MessageDialog.MERGE_INTERSECTS;
					}
				}
			}
		}else{
			return MessageDialog.ALREADY_OWNED;
		}
		return null;
	}
	
	private void endTurn(){
		selected = null;
		activePlayer = (activePlayer + 1) % players.size();
		//TODO next
		
		players.forEach(System.out::println);
		
		if(objects.stream().allMatch(ConvexObject::isOwned)){
			//TODO verify no merges are possible
			System.out.println("soft game end (no more unowned objects)");
		}
	}
	
	/**
	 * Attempts to merge the given two convex objects.
	 * @param first The first object to merge.
	 * @param second The second object to merge.
	 * @return True if the merge was valid and did not
	 *         have any other convex objects on its boundary.
	 */
	private boolean mergeObjects(ConvexObject first, ConvexObject second){
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
			while(iterator.hasNext()){
				ConvexObject obj = iterator.next();
				if(merged.contains(obj)){
					iterator.remove();
					decomp.removeObject(obj);
					player.removeArea(obj.getArea());
				}
			}
			objects.add(merged);
			decomp.addObject(merged);
			player.addArea(merged.getArea());
			return true;
		}else{
			return false;
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
}
