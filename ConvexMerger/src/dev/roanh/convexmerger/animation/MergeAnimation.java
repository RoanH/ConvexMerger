package dev.roanh.convexmerger.animation;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.ConvexUtil;
import dev.roanh.convexmerger.ui.Theme;

public class MergeAnimation extends ClaimAnimation{
	private static final float LINE_DURATION = 1000.0F;
	private boolean unclaimed;
	private ConvexObject owned;
	private ConvexObject target;
	private Point[] mergeLines;
	
	private long start;
	
	public MergeAnimation(ConvexObject owned, ConvexObject target, ConvexObject result, List<ConvexObject> contained){
		super(target, target.getCentroid());
		unclaimed = !target.isOwned();
		target.setOwner(owned.getOwner());
		this.owned = owned;
		this.target = target;
		
		//TODO handle contained
		
		mergeLines = ConvexUtil.computeMergeLines(owned.getPoints(), target.getPoints(), result.getPoints());
	}

	@Override
	public boolean run(Graphics2D g){
		if(unclaimed){
			owned.render(g);
			if(!super.run(g)){
				unclaimed = false;
				start = System.currentTimeMillis();
			}
			return true;
		}
		
		long elapsed = System.currentTimeMillis() - start;
		
		owned.render(g);
		target.render(g);

		g.setStroke(Theme.POLY_STROKE);
		g.draw(new Line2D.Double(mergeLines[0],	interpolate(mergeLines[0], mergeLines[1], elapsed / LINE_DURATION)));
		g.draw(new Line2D.Double(mergeLines[2],	interpolate(mergeLines[2], mergeLines[3], elapsed / LINE_DURATION)));
		
		
		
		
		// TODO Auto-generated method stub
		return elapsed <= LINE_DURATION;
	}

	private Point2D interpolate(Point2D source, Point2D target, float fraction){
		return new Point2D.Double(
			clamp(source.getX(), target.getX(), source.getX() + (target.getX() - source.getX()) * fraction),
			clamp(source.getY(), target.getY(), source.getY() + (target.getY() - source.getY()) * fraction)
		);
	}
	
	private double clamp(double a, double b, double val){
		return Math.max(Math.min(a, b), Math.min(Math.max(a, b), val));
	}
}
