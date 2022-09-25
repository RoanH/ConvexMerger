package dev.roanh.convexmerger.animation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.ConvexUtil;

/**
 * Animation to visualise the callipers used
 * when merging two convex objects.
 * @author Roan
 */
public class CalliperAnimation implements Animation{
	/**
	 * The number of milliseconds the animation runs for.
	 */
	private static final float DURATION = 10000.0F;
	/**
	 * The epoch millis start time of the animation.
	 */
	private long start;
	/**
	 * The convex object point set.
	 */
	private List<Point2D> points;
	/**
	 * Cached current index in the points list.
	 */
	private int index = 0;
	
	/**
	 * Constructs a new calliper animation for the given object.
	 * Note: this animation only renders the calliper and not the object.
	 * @param obj The object to show a calliper for.
	 */
	public CalliperAnimation(ConvexObject obj){
		points = obj.getPoints();
		start = System.currentTimeMillis();
	}

	@Override
	public boolean run(Graphics2D g){
		long elapsed = System.currentTimeMillis() - start;
		float angle = (float)((Math.PI * 2.0F * elapsed) / DURATION);
		
		Point2D base = points.get(index % points.size());
		while(index < points.size()){
			if(ConvexUtil.angleFromVertical(points.get(index % points.size()), points.get((index + 1) % points.size())) >= angle){
				base = points.get(index % points.size());
				break;
			}
			index++;
		}
		
		g.setStroke(new BasicStroke(1.0F));
		g.setColor(Color.RED);
		angle += Math.PI * 0.5F;
		drawLine(
			g,
			base.getX(),
			base.getY(),
			base.getX() + Math.cos(angle),
			base.getY() + Math.sin(angle)
		);
		
		return elapsed < DURATION;
	}
	
	/**
	 * Draws an 'infinite' line segment through the two given points.
	 * @param g The graphics context to use.
	 * @param x1 The x-coordinate of the first point.
	 * @param y1 The y-coordinate of the first point.
	 * @param x2 The x-coordinate of the second point.
	 * @param y2 The y-coordinate of the second point.
	 */
	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2){
		double coef = x1 == x2 ? 0.0D : (y2 - y1) / (x2 - x1);
		double base = y1 - x1 * coef;
		g.draw(new Line2D.Double(0.0D, base, Constants.PLAYFIELD_WIDTH, base + coef * Constants.PLAYFIELD_WIDTH));
	}
}
