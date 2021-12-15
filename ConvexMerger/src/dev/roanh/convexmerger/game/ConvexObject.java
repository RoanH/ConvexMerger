package dev.roanh.convexmerger.game;

import java.awt.geom.Path2D;

public class ConvexObject{
	private Path2D shape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
	private Player owner = null;
	private boolean selected = false;
	
	public ConvexObject(int x1, int y1, int x2, int y2, int x3, int y3){
		shape.moveTo(x1, y1);
		shape.lineTo(x2, y2);
		shape.lineTo(x3, y3);
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
