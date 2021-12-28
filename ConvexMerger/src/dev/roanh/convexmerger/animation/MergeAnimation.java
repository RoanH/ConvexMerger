package dev.roanh.convexmerger.animation;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.ConvexUtil;
import dev.roanh.convexmerger.ui.Theme;

/**
 * Animation that plays when two objects are merged
 * into a single object (along with contained objects).
 * @author Roan
 */
public class MergeAnimation extends ClaimAnimation{
	/**
	 * Number of milliseconds the merge line drawing
	 * phase lasts for.
	 */
	private static final float LINE_DURATION = 250.0F;
	/**
	 * Number of milliseconds the body merge drawing
	 * phase lasts for.
	 */
	private static final float FLOW_DURATION = 400.0F;
	/**
	 * Whether or not the target object was unclaimed before.
	 */
	private boolean unclaimed;
	/**
	 * The already owned object in the merge.
	 */
	private ConvexObject owned;
	/**
	 * The target object of the merge (possibly unowned)
	 */
	private ConvexObject target;
	/**
	 * The merge lines used to construct the result object.
	 */
	private Point2D[] mergeLines;
	/**
	 * A list of objects contained within the merge area.
	 */
	private List<ConvexObject> contained;
	/**
	 * Time in milliseconds when the animation started.
	 */
	private long start;
	/**
	 * Path for the outer (not internal after the merge)
	 * hull segment of the first (leftmost) object
	 */
	private Path2D firstOuter;
	/**
	 * Path for the inner (internal after the merge)
	 * hull segment of the first (leftmost) object
	 */
	private Path2D firstInner;
	/**
	 * Path for the outer (not internal after the merge)
	 * hull segment of the second object
	 */
	private Path2D secondOuter;
	/**
	 * Path for the inner (internal after the merge)
	 * hull segment of the second object
	 */
	private Path2D secondInner;
	/**
	 * Points defining the inner hull segment of the first object.
	 * @see #firstInner
	 */
	private List<Point2D> firstInnerData;
	/**
	 * Points defining the inner hull segment of the second object.
	 * @see #secondInner
	 */
	private List<Point2D> secondInnerData;
		
	/**
	 * Constructs a new merge animation with the given event data.
	 * @param owned The owned object the merge was started from.
	 * @param target The target object of the merge.
	 * @param result The result of the merge.
	 * @param contained The objects contained within the merge area.
	 */
	public MergeAnimation(ConvexObject owned, ConvexObject target, ConvexObject result, List<ConvexObject> contained){
		super(target, target.getCentroid());
		unclaimed = !target.isOwned();
		target.setOwner(owned.getOwner());
		this.owned = owned;
		this.target = target;
		this.contained = contained;
		
		mergeLines = ConvexUtil.computeMergeLines(owned.getPoints(), target.getPoints(), result.getPoints());
		List<List<Point2D>> mergeBounds = ConvexUtil.computeMergeBounds(owned.getPoints(), target.getPoints(), mergeLines);
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
			for(ConvexObject obj : contained){
				obj.render(g);
			}
			
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
		g.fill(target.getShape());
		
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
			float flowFactor = Math.min(1.0F, (elapsed - LINE_DURATION) / FLOW_DURATION);
			
			//draw flow polygons
			g.setColor(Theme.getPlayerBody(owned));
			g.fill(computeFlowPath(firstInnerData, mergeLines[0], mergeLines[1], mergeLines[3], mergeLines[2], flowFactor));
			g.fill(computeFlowPath(secondInnerData, mergeLines[2], mergeLines[3], mergeLines[1], mergeLines[0], flowFactor));
			
			//prevent seem lines
			g.setColor(Theme.getPlayerBody(owned));
			g.setStroke(Theme.POLY_STROKE);
			g.draw(new Line2D.Double(mergeLines[0],	mergeLines[3]));
			g.draw(new Line2D.Double(mergeLines[1],	mergeLines[2]));
		}
		
		g.setColor(Theme.getPlayerOutline(owned));
		g.setStroke(Theme.POLY_STROKE);
		g.draw(new Line2D.Double(mergeLines[0],	interpolate(mergeLines[0], mergeLines[1], factor)));
		g.draw(new Line2D.Double(mergeLines[2],	interpolate(mergeLines[2], mergeLines[3], factor)));
		
		g.draw(firstOuter);
		g.draw(secondOuter);
		
		return elapsed <= LINE_DURATION + FLOW_DURATION;
	}
	
	/**
	 * Computes the flow path showing the merge progress, this
	 * path slowly moves to fill the entire merge area (the
	 * quadrilateral defined by the merge lines).
	 * @param data The data for the inner hull segment moving
	 *        towards the other end of the merge area.
	 * @param firstStart The first point of the line segment
	 *        defining the first merge line starting from the
	 *        current side of the area.
	 * @param firstEnd The second point of the line segment
	 *        defining the first merge line starting from the
	 *        current side of the area.
	 * @param secondStart The first point of the line segment
	 *        defining the second merge line starting from the
	 *        current side of the area.
	 * @param secondEnd The second point of the line segment
	 *        defining the second merge line starting from the
	 *        current side of the area.
	 * @param flowFactor The progress made so far in filling the
	 *        merge area, this value is between 0 and 1.
	 * @return The current flow path.
	 */
	private Path2D computeFlowPath(List<Point2D> data, Point2D firstStart, Point2D firstEnd, Point2D secondStart, Point2D secondEnd, float flowFactor){
		Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, data.size() + 2);
		Point2D firstSlope = computeSlope(firstStart, firstEnd, flowFactor);
		Point2D secondSlope = computeSlope(secondStart, secondEnd, flowFactor);
		Point2D slope = new Point2D.Double((firstSlope.getX() + secondSlope.getX()) / 2.0D, (firstSlope.getY() + secondSlope.getY()) / 2.0D);
		
		path.moveTo(secondStart.getX(), secondStart.getY());
		path.lineTo(firstStart.getX(), firstStart.getY());
		path.lineTo(data.get(0).getX() + firstSlope.getX(), data.get(0).getY() + firstSlope.getY());
		for(int i = 1; i < data.size() - 1; i++){
			clipAdd(
				path,
				data.get(i),
				slope,
				firstEnd,
				secondEnd,
				data.get(0),
				firstSlope,
				data.get(data.size() - 1),
				secondSlope
			);
		}
		path.lineTo(data.get(data.size() - 1).getX() + secondSlope.getX(), data.get(data.size() - 1).getY() + secondSlope.getY());
		path.closePath();
		
		return path;
	}

	/**
	 * Adds the given slope to the given point and then adds the
	 * resulting point to the given path. Unless the result would
	 * go outside of the merge area (the quadrilateral defined by
	 * the merge lines). If this is the case the result is clipped
	 * to be on the boundary of the merge area.
	 * @param path The path to append to.
	 * @param p The point to add the slope to.
	 * @param slope The slope to add.
	 * @param a The first point of the line segment defining the
	 *        merge line on the other side of the merge area.
	 * @param b The second point of the line segment defining the
	 *        merge line on the other side of the merge area.
	 * @param firstBase The starting location of the first merge line.
	 * @param firstSlope The slope denoting the progress along the first merge line.
	 * @param secondBase The starting location of the second merge line.
	 * @param secondSlope The slope denoting the progress along the second merge line.
	 */
	private void clipAdd(Path2D path, Point2D p, Point2D slope, Point2D a, Point2D b, Point2D firstBase, Point2D firstSlope, Point2D secondBase, Point2D secondSlope){
		Point2D target = new Point2D.Double(p.getX() + slope.getX(), p.getY() + slope.getY());
		
		Point2D inter = intercept(p, target, a, b);
		if(inter != null){
			path.lineTo(inter.getX(), inter.getY());
			return;
		}
		
		inter = intercept(p, target, mergeLines[0], mergeLines[1]);
		if(inter != null){
			if(Math.abs(inter.getX() - firstBase.getX()) > Math.abs(firstSlope.getX()) || Math.abs(inter.getY() - firstBase.getY()) > Math.abs(firstSlope.getY())){
				path.lineTo(inter.getX(), inter.getY());
			}
			return;
		}
			
		inter = intercept(p, target, mergeLines[2], mergeLines[3]);
		if(inter != null){
			if(Math.abs(inter.getX() - secondBase.getX()) > Math.abs(secondSlope.getX()) || Math.abs(inter.getY() - secondBase.getY()) > Math.abs(secondSlope.getY())){
				path.lineTo(inter.getX(), inter.getY());
			}
			return;
		}
		
		path.lineTo(target.getX(), target.getY());
	}
	
	/**
	 * Computes the intersection point of the two given closed line segments.
	 * @param a The first point of the first line segment.
	 * @param b The second point of the first line segment.
	 * @param c The first point of the second line segment.
	 * @param d The second point of the second line segment.
	 * @return The intersection point, or <code>null</code> if
	 *         the given line segments do not intersect.
	 */
	private Point2D intercept(Point2D a, Point2D b, Point2D c, Point2D d){
		double det = (a.getX() - b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() - d.getX());
		Point2D p = new Point2D.Double(
			((a.getX() * b.getY() - a.getY() * b.getX()) * (c.getX() - d.getX()) - (a.getX() - b.getX()) * (c.getX() * d.getY() - c.getY() * d.getX())) / det,
			((a.getX() * b.getY() - a.getY() * b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() * d.getY() - c.getY() * d.getX())) / det
		);
		return (onLine(p, a, b) && onLine(p, c , d)) ? p : null;
	}
	
	/**
	 * Checks if the given point <code>p</code> is
	 * on the closed line segment between <code>a
	 * </code> and <code>b</code>.
	 * @param p The point to check.
	 * @param a The first point of the line segment.
	 * @param b The second point of the line segment.
	 * @return True if the given point is on the given line segment.
	 */
	private boolean onLine(Point2D p, Point2D a, Point2D b){
		return Math.min(a.getX(), b.getX()) <= p.getX() && p.getX() <= Math.max(a.getX(), b.getX()) && Math.min(a.getY(), b.getY()) <= p.getY() && p.getY() <= Math.max(a.getY(), b.getY()); 
	}
	
	/**
	 * Creates a path from the given list of points.
	 * @param <T> The point data type.
	 * @param points The list of points.
	 * @return The constructed path.
	 */
	private <T extends Point2D> Path2D createPath(List<T> points){
		Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, points.size());
		path.moveTo(points.get(0).getX(), points.get(0).getY());
		for(int i = 1; i < points.size(); i++){
			path.lineTo(points.get(i).getX(), points.get(i).getY());
		}
		return path;
	}

	/**
	 * Interpolates between the given two points starting
	 * from the source and ending at the given fraction to
	 * the target point.
	 * @param source The source point.
	 * @param target The target point.
	 * @param fraction The fraction to cover.
	 * @return The interpolated point.
	 */
	private Point2D interpolate(Point2D source, Point2D target, float fraction){
		Point2D slope = computeSlope(source, target, fraction);
		return new Point2D.Double(
			clamp(source.getX(), target.getX(), source.getX() + slope.getX()),
			clamp(source.getY(), target.getY(), source.getY() + slope.getY())
		);
	}
	
	/**
	 * Computes the 'slope' that needs to be added
	 * to the given source point to get to the given
	 * fraction in the direction of the given target.
	 * @param source The source point.
	 * @param target The target point.
	 * @param fraction The fraction to cover.
	 * @return The slope or delta required.
	 */
	private Point2D computeSlope(Point2D source, Point2D target, float fraction){
		return new Point2D.Double(
			(target.getX() - source.getX()) * fraction,
			(target.getY() - source.getY()) * fraction
		);
	}
	
	/**
	 * Clamps the given value to be between the given bounds.
	 * @param a The first bound value.
	 * @param b The second bound value.
	 * @param val The value to clamp.
	 * @return The clamped value.
	 */
	private double clamp(double a, double b, double val){
		return Math.max(Math.min(a, b), Math.min(Math.max(a, b), val));
	}
}
