package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import dev.roanh.convexmerger.game.Theme.PlayerTheme;

public class SelectAnimation implements Animation{
	private static final float DURATION = 5000.0F;
	private long start;
	private ConvexObject obj;
	private Point2D loc;
	private float dist = 0.0F;

	public SelectAnimation(ConvexObject selected, Point2D location){
		obj = selected;
		loc = location;
		start = System.currentTimeMillis() + 1L;
		for(Point p : selected.getPoints()){
			dist = Math.max(dist, (float)location.distance(p));
		}
	}

	@Override
	public boolean run(Graphics2D g){
		long elapsed = System.currentTimeMillis() - start;
		
		if(elapsed < 2.0F * DURATION){
			System.out.println("ANIMATION RUN: " + (elapsed / DURATION));

			g.setPaint(new RadialGradientPaint(
				loc,
				dist,
				new float[]{
					0.0F,
					(elapsed % DURATION) / DURATION,
					1.0F
				},
				new Color[]{
					obj.getOwner().getTheme().getBody(),
					elapsed > DURATION ? obj.getOwner().getTheme().getBody() : PlayerTheme.UNOWNED.getBody(),
					PlayerTheme.UNOWNED.getBody()
				}
			));
		}else{
			g.setColor(Theme.getPlayerBody(obj));
		}
		g.fill(obj.getShape());
		
		g.setStroke(Theme.POLY_STROKE);
		g.setColor(Theme.getPlayerOutline(obj));
		g.draw(obj.getShape());
		
		return false;
	}
}
