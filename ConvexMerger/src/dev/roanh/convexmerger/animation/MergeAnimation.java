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
	private static final float LINE_DURATION = 2500.0F * 2;
	private boolean unclaimed;
	private ConvexObject owned;
	private ConvexObject target;
	private Point[] mergeLines;
	
	private List<ConvexObject> contained;
	
	private long start;
	
	private Path2D firstOuter;
	private Path2D firstInner;
	private Path2D secondOuter;
	private Path2D secondInner;
	
	private List<Point> firstInnerData;
	private List<Point> secondInnerData;
	
	public MergeAnimation(ConvexObject owned, ConvexObject target, ConvexObject result, List<ConvexObject> contained){
		super(target, target.getCentroid());
		unclaimed = !target.isOwned();
		target.setOwner(owned.getOwner());
		this.owned = owned;
		this.target = target;
		this.contained = contained;
		
		mergeLines = ConvexUtil.computeMergeLines(owned.getPoints(), target.getPoints(), result.getPoints());
		List<List<Point>> mergeBounds = ConvexUtil.computeMergeBounds(owned.getPoints(), target.getPoints(), mergeLines);
		firstInner = createPath(mergeBounds.get(0));
		firstOuter = createPath(mergeBounds.get(1));
		secondInner = createPath(mergeBounds.get(2));
		secondOuter = createPath(mergeBounds.get(3));
		firstInnerData = mergeBounds.get(0);
		secondInnerData = mergeBounds.get(2);
		start = System.currentTimeMillis();
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
		float factor = Math.min(2.0F, elapsed / LINE_DURATION);
		
		g.setColor(Theme.getPlayerBody(owned));
		g.fill(owned.getShape());
		//g.fill(target.getShape());
		
		Composite composite = g.getComposite();
		g.setStroke(Theme.POLY_STROKE);
		g.setColor(Theme.getPlayerOutline(owned));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.0F, 1.0F - factor)));
		g.draw(firstInner);
		g.draw(secondInner);
		for(ConvexObject obj : contained){
			obj.render(g);
		}
		g.setComposite(composite);
		
		if(factor >= 1.0F){
			g.setColor(Theme.getPlayerBody(owned));
			
//			Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
//			path.moveTo(mergeLines[3].getX(), mergeLines[3].getY());
//			path.lineTo(mergeLines[0].getX(), mergeLines[0].getY());
//			Point2D side = interpolate(mergeLines[0], mergeLines[1], factor - 1.0F);
//			path.lineTo(side.getX(), side.getY());
//			path.closePath();
//			g.fill(path);
//			
//			path = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
//			path.moveTo(mergeLines[1].getX(), mergeLines[1].getY());
//			path.lineTo(mergeLines[2].getX(), mergeLines[2].getY());
//			side = interpolate(mergeLines[2], mergeLines[3], factor - 1.0F);
//			path.lineTo(side.getX(), side.getY());
//			path.closePath();
//			g.fill(path);
			
			//path.transform(AffineTransform.)
			
			Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO);
			//path.moveTo(x, y)
			
			Point2D firstSlope = computeSlope(mergeLines[0], mergeLines[1], factor - 1.0F);
			Point2D secondSlope = computeSlope(mergeLines[3], mergeLines[2], factor - 1.0F);
			Point2D slope = new Point2D.Double((firstSlope.getX() + secondSlope.getX()) / 2.0D, (firstSlope.getY() + secondSlope.getY()) / 2.0D);
			
			path.moveTo(mergeLines[3].getX(), mergeLines[3].getY());
			path.lineTo(mergeLines[0].getX(), mergeLines[0].getY());
			path.lineTo(firstInnerData.get(0).getX() + firstSlope.getX(), firstInnerData.get(0).getY() + firstSlope.getY());
			for(int i = 1; i < firstInnerData.size() - 1; i++){
				Point p = firstInnerData.get(i);
				//path.lineTo(p.getX() + slope.getX(), p.getY() + slope.getY());
				clipAdd(path, p, slope, mergeLines[1], mergeLines[2]);
			}
			path.lineTo(firstInnerData.get(firstInnerData.size() - 1).getX() + secondSlope.getX(), firstInnerData.get(firstInnerData.size() - 1).getY() + secondSlope.getY());
			path.closePath();
			g.fill(path);
			
			
			
			
		}
		
		g.setColor(Color.RED);//Theme.getPlayerOutline(owned));
		g.setStroke(Theme.POLY_STROKE);
		g.draw(new Line2D.Double(mergeLines[0],	interpolate(mergeLines[0], mergeLines[1], factor)));
		g.draw(new Line2D.Double(mergeLines[2],	interpolate(mergeLines[2], mergeLines[3], factor)));
		g.draw(new Line2D.Double(mergeLines[0],	mergeLines[3]));
		g.setColor(Color.GREEN);
		g.draw(new Line2D.Double(mergeLines[1],	mergeLines[2]));
		
		g.draw(firstOuter);
		g.draw(secondOuter);
		
		return elapsed <= 2 * LINE_DURATION;
	}
	
	//private Path2D extendToLine()
	
	private void clipAdd(Path2D path, Point2D p, Point2D slope, Point2D a, Point2D b){
		Point2D inter = intercept(p, new Point2D.Double(p.getX() + slope.getX(), p.getY() + slope.getY()), a, b);
		if(inter == null){
			path.lineTo(p.getX() + slope.getX(), p.getY() + slope.getY());
		}else{
			path.lineTo(inter.getX(), inter.getY());
		}
	}
	
	private Point2D intercept(Point2D a, Point2D b, Point2D c, Point2D d){
		double det = (a.getX() - b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() - d.getX());
		Point2D p = new Point2D.Double(
			((a.getX() * b.getY() - a.getY() * b.getX()) * (c.getX() - d.getX()) - (a.getX() - b.getX()) * (c.getX() * d.getY() - c.getY() * d.getX())) / det,
			((a.getX() * b.getY() - a.getY() * b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() * d.getY() - c.getY() * d.getX())) / det
		);
		System.out.println(p);
		return (onLine(p, a, b) && onLine(p, c , d)) ? p : null;
	}
	
	private boolean onLine(Point2D p, Point2D a, Point2D b){
		return Math.min(a.getX(), b.getX()) <= p.getX() && p.getX() <= Math.max(a.getX(), b.getX()) && Math.min(a.getY(), b.getY()) <= p.getY() && p.getY() <= Math.max(a.getY(), b.getY()); 
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
		Point2D slope = computeSlope(source, target, fraction);
		return new Point2D.Double(
			clamp(source.getX(), target.getX(), source.getX() + slope.getX()),
			clamp(source.getY(), target.getY(), source.getY() + slope.getY())
		);
	}
	
	private Point2D computeSlope(Point2D source, Point2D target, float fraction){
		return new Point2D.Double(
			(target.getX() - source.getX()) * fraction,
			(target.getY() - source.getY()) * fraction
		);
	}
	
	private double clamp(double a, double b, double val){
		return Math.max(Math.min(a, b), Math.min(Math.max(a, b), val));
	}
}
