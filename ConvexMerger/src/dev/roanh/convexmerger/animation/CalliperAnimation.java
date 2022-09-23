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
	private static final float DURATION = 10000.0F;
	private long start;
	private List<Point2D> points;
	private int index = 0;
	
	public CalliperAnimation(ConvexObject obj){
		points = obj.getPoints();
		start = System.currentTimeMillis();
	}

	//TODO not showing the end properly yet
	@Override
	public boolean run(Graphics2D g){
		long elapsed = System.currentTimeMillis() - start;
		float angle = (float)((Math.PI * 2.0F * elapsed) / DURATION);
		
		Point2D base = points.get(index);
		while(index < points.size() - 1){
			if(ConvexUtil.angleFromVertical(points.get(index), points.get((index + 1) % points.size())) >= angle){
				base = points.get(index);
				break;
			}
			index++;
		}
		
		//TODO customisation
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
	
	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2){
		double coef = x1 == x2 ? 0.0D : (y2 - y1) / (x2 - x1);
		double base = y1 - x1 * coef;
		g.draw(new Line2D.Double(0.0D, base, Constants.PLAYFIELD_WIDTH, base + coef * Constants.PLAYFIELD_WIDTH));
	}
}
