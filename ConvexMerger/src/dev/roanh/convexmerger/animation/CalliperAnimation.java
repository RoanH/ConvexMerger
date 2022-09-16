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

public class CalliperAnimation implements Animation{
	private static final float DURATION = 10000.0F;
	private long start;
	private List<Point2D> points;
	
	
	
	
	public CalliperAnimation(ConvexObject obj){
		points = obj.getPoints();
		start = System.currentTimeMillis();
	
	
	
	}
	
	
	
	

	@Override
	public boolean run(Graphics2D g){
		long elapsed = System.currentTimeMillis() - start;
		float angle = (float)((float)((Math.PI * 2.0F * elapsed) / DURATION) % (2.0F * Math.PI));
		
		System.out.println("angle: " + angle);
		
		Point2D base = points.get(0);
		for(int i = 0; i < points.size(); i++){
			if(ConvexUtil.angleFromVertical(points.get(i), points.get((i + 1) % points.size())) >= angle){
				base = points.get(i);
				break;
			}
		}
		
		
		angle += Math.PI * 0.5F;
		System.out.println("render");
		
		
		g.setStroke(new BasicStroke(1.0F));
		g.setColor(Color.RED);
		drawLine(
			g,
			base.getX(), base.getY(),
			base.getX() + Math.cos(angle),
			base.getY() + Math.sin(angle)
			
			);
		
		
		
		// TODO Auto-generated method stub
		return true;
	}
	
	
	
	private void drawLine(Graphics2D g, Point2D a, Point2D b){
		drawLine(g, a.getX(), a.getY(), b.getX(), b.getY());
	}
	
	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2){
		double coef = (y2 - y1) / (x2 - x1);
		double base = y1 - x1 * coef;
		g.draw(new Line2D.Double(0.0D, base, Constants.PLAYFIELD_WIDTH, base + coef * Constants.PLAYFIELD_WIDTH));
	}
}
