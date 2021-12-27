package dev.roanh.convexmerger.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.ConvexUtil;
import dev.roanh.convexmerger.ui.Theme;

public class MergeAnimation extends ClaimAnimation{
	private static final float LINE_DURATION = 250.0F;
	private boolean unclaimed;
	private ConvexObject owned;
	private ConvexObject target;
	private Point[] mergeLines;
	
	private long start;
	
	private Path2D firstOuter;
	private Path2D firstInner;
	private Path2D secondOuter;
	private Path2D secondInner;
	
	public MergeAnimation(ConvexObject owned, ConvexObject target, ConvexObject result, List<ConvexObject> contained){
		super(target, target.getCentroid());
		unclaimed = !target.isOwned();
		target.setOwner(owned.getOwner());
		this.owned = owned;
		this.target = target;
		
		//TODO handle contained
		
		mergeLines = ConvexUtil.computeMergeLines(owned.getPoints(), target.getPoints(), result.getPoints());
		List<List<Point>> mergeBounds = ConvexUtil.computeMergeBounds(owned.getPoints(), target.getPoints(), mergeLines);
		firstInner = createPath(mergeBounds.get(0));
		firstOuter = createPath(mergeBounds.get(1));
		secondInner = createPath(mergeBounds.get(2));
		secondOuter = createPath(mergeBounds.get(3));
		
		
		
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
		float factor = Math.min(1.0F, elapsed / LINE_DURATION);
		
		//owned.render(g);
		//target.render(g);
		
		g.setColor(Theme.getPlayerBody(owned));
		g.fill(owned.getShape());
		g.fill(target.getShape());
		
		g.setColor(Theme.getPlayerOutline(owned));
		g.setStroke(Theme.POLY_STROKE);
		g.draw(new Line2D.Double(mergeLines[0],	interpolate(mergeLines[0], mergeLines[1], factor)));
		g.draw(new Line2D.Double(mergeLines[2],	interpolate(mergeLines[2], mergeLines[3], factor)));
		
		Composite composite = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F - factor));
		g.draw(firstInner);
		g.draw(secondInner);
		
		g.setComposite(composite);
		
		g.draw(firstOuter);
		g.draw(secondOuter);
		
		
		
		
		
		// TODO Auto-generated method stub
		return true;//elapsed <= LINE_DURATION;
	}
	
	private <T extends Point2D> Path2D createPath(List<T> points){
		Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, points.size());
		path.moveTo(points.get(0).getX(), points.get(0).getY());
		for(int i = 1; i < points.size(); i++){
			path.lineTo(points.get(i).getX(), points.get(i).getY());
		}
		return path;
	}

	private Point2D interpolate(Point2D source, Point2D target, float fraction){
		return new Point2D.Double(
			clamp(source.getX(), target.getX(), source.getX() + (target.getX() - source.getX()) * fraction),//TODO clamp redundant?
			clamp(source.getY(), target.getY(), source.getY() + (target.getY() - source.getY()) * fraction)
		);
	}
	
	private double clamp(double a, double b, double val){
		return Math.max(Math.min(a, b), Math.min(Math.max(a, b), val));
	}
}
