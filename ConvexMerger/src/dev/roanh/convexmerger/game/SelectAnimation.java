package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class SelectAnimation implements Animation{
	private static final long DURATION = 1000;
	private ConvexObject obj;
	private Point2D loc;

	public SelectAnimation(ConvexObject selected, Point2D location){
		obj = selected;
		loc = location;
	}

	@Override
	public boolean run(Graphics2D g){
		// TODO Auto-generated method stub
		
		
		
		
		return false;
	}
}
