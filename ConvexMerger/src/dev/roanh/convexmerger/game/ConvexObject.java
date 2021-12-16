package dev.roanh.convexmerger.game;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class ConvexObject{
	private List<Point> points;
	private Path2D shape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
	private Player owner = null;
	private boolean selected = false;
	
	public ConvexObject(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
		this(new int[]{x1, y1, x2, y2, x3, y3, x4, y4});
	}
	
	public ConvexObject(int x1, int y1, int x2, int y2, int x3, int y3){
		this(new int[]{x1, y1, x2, y2, x3, y3});
	}
	
	private ConvexObject(int[] data){
		points = new ArrayList<Point>(4);
		points.add(new Point(data[0], data[1]));
		shape.moveTo(data[0], data[1]);
		for(int i = 2; i < data.length; i += 2){
			points.add(new Point(data[i], data[i + 1]));
			shape.lineTo(data[i], data[i + 1]);
		}
		shape.closePath();
	}
	
	public ConvexObject(List<Point> data){
		points = data;
		shape.moveTo(data.get(0).x, data.get(0).y);
		for(int i = 1; i < data.size(); i++){
			shape.lineTo(data.get(i).x, data.get(i).y);
		}
		shape.closePath();
	}
	
	/**
	 * Gets a closed path representing the shape of
	 * this convex object. The shape is guaranteed
	 * to be convex.
	 * @return The shape of this convex object.
	 */
	public Path2D getShape(){
		return shape;
	}
	
	public List<Point> getPoints(){
		return points;
	}
	
	public Player getOwner(){
		return owner;
	}
	
	public boolean isOwned(){
		return owner != null;
	}
	
	public void setOwner(Player player){
		owner = player;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public boolean contains(double x, double y){
		return shape.contains(x, y);
	}
}
