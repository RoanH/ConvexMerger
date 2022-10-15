package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class containing various utilities related
 * to convex objects and hulls. General assumptions
 * on the convex objects dealt with by this class are
 * documented at {@link #checkInvariants(List)}.
 * @author Roan
 */
public class ConvexUtil{

	/**
	 * Computes the convex hull of the given point set
	 * using the gift wrapping algorithm. The returned
	 * hull has the left most point as the first point
	 * and the winding order is counter-clockwise.
	 * @param points The points to compute the convex hull of.
	 * @return The convex hull for the given set of points.
	 * @see #checkInvariants(List)
	 */
	public static final List<Point2D> computeConvexHull(List<Point2D> points){
		List<Point2D> hull = new ArrayList<Point2D>();
		
		Point2D hullPoint = points.get(0);
		for(int i = 1; i < points.size(); i++){
			int val = Double.compare(points.get(i).getX(), hullPoint.getX());
			if(val < 0){
				hullPoint = points.get(i);
			}else if(val == 0){
				if(Double.compare(points.get(i).getY(), hullPoint.getY()) < 0){
					hullPoint = points.get(i);
				}
			}
		}
		
		do{
			hull.add(hullPoint);
			
			Point2D endpoint = points.get(0);
			for(Point2D point : points){
				if(endpoint.equals(hullPoint) || Line2D.relativeCCW(hullPoint.getX(), hullPoint.getY(), endpoint.getX(), endpoint.getY(), point.getX(), point.getY()) == 1){
					endpoint = point;
				}
			}
			
			hullPoint = endpoint;
		}while(!hull.get(0).equals(hullPoint));
		
		return hull;
	}
	
	/**
	 * Computes the two lines that would be required to
	 * combine the two given convex hulls into a single
	 * convex hull using the convex hull of both objects.
	 * It is required that the left most point of the
	 * combined hull is the first point in the given hull.
	 * @param first The first convex hull, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @param second The second convex hull, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @param hull The convex hull constructed by merging
	 *        the other two convex hulls, the first point
	 *        has to be bottom leftmost and be an exact object
	 *        reference to the first point of one of the given
	 *        objects, and the winding order of points has to
	 *        be counter-clockwise.
	 * @return The points for the two line segments that
	 *         would be required to complete the convex
	 *         hull of the two given convex hulls. The
	 *         first two points make up one of the line
	 *         segments and the other two the other.
	 * @see #computeMergeLines(List, List)
	 * @see #checkInvariants(List)
	 */
	public static final Point2D[] computeMergeLines(List<Point2D> first, List<Point2D> second, List<Point2D> hull){
		if(!first.contains(hull.get(0))){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}

		int idx = 0;
		while(!first.get(idx).equals(hull.get(0))){
			idx++;
		}

		//first merge line
		Point2D a = null;
		Point2D b = null;

		int hullIdx = 0;
		while(true){
			hullIdx++;
			idx = (idx + 1) % first.size();
			if(!first.get(idx).equals(hull.get(hullIdx))){
				a = hull.get((hullIdx == 0 ? hull.size() : hullIdx) - 1);
				b = hull.get(hullIdx);
				break;
			}
		}

		idx = 0;
		for(int i = 0; i < second.size(); i++){
			if(second.get(i).equals(b)){
				idx = i;
				break;
			}
		}

		//second merge line
		Point2D c = null;
		Point2D d = null;

		while(true){
			hullIdx = (hullIdx + 1) % hull.size();
			idx = (idx + 1) % second.size();
			if(!hull.get(hullIdx).equals(second.get(idx))){
				c = hull.get((hullIdx == 0 ? hull.size() : hullIdx) - 1);
				d = hull.get(hullIdx);
				break;
			}
		}

		return new Point2D[]{a, b, c, d};
	}
	
	/**
	 * Splits the given convex objects into segments that are
	 * either on the outside or on the inside of the convex
	 * object that is created when merging them.
	 * @param first The first convex object, should have the
	 *        bottom leftmost of the two objects. If not
	 *        the two objects will be swapped automatically.
	 *        The winding order of the object has to be counter-clockwise.
	 * @param second The second convex object, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @return The specific segments, index 0 has the part of the
	 *         first object that would be contained inside the
	 *         resulting hull, index 1 has the part of the first
	 *         object that would be part of the outside of the
	 *         resulting hull, index 2 has the part of the second
	 *         object that would be contained inside the resulting
	 *         hull, index 3 has the part of the second object that
	 *         would be part of the outside of the resulting hull.
	 * @see #computeMergeLines(List, List)
	 * @see #computeMergeBounds(List, List, Point2D[])
	 * @see #checkInvariants(List)
	 */
	public static final List<List<Point2D>> computeMergeBounds(List<Point2D> first, List<Point2D> second){
		return computeMergeBounds(first, second, computeMergeLines(first, second));
	}
	
	/**
	 * Splits the given convex objects into segments that are
	 * either on the outside or on the inside of the convex
	 * object that is created when merging them.
	 * @param first The first convex object, should have the
	 *        smallest x-coordinate of the two objects. If not
	 *        the two objects will be swapped automatically.
	 *        The winding order of the object has to be counter-clockwise.
	 * @param second The second convex object, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @param mergeLines The points describing the merge lines
	 *        that would be added to merge the two objects as
	 *        computed by {@link #computeMergeLines(List, List)}.
	 *        The points given here <b>must</b> be exact object
	 *        references corresponding to points in the given objects.
	 *        The first merge line has to be from the object with the
	 *        bottom leftmost to the other object and the second line
	 *        back from that object to the object with the bottom leftmost point.
	 * @return The specific segments, index 0 has the part of the
	 *         first object that would be contained inside the
	 *         resulting hull, index 1 has the part of the first
	 *         object that would be part of the outside of the
	 *         resulting hull, index 2 has the part of the second
	 *         object that would be contained inside the resulting
	 *         hull, index 3 has the part of the second object that
	 *         would be part of the outside of the resulting hull.
	 * @see #computeMergeLines(List, List)
	 * @see #computeMergeBounds(List, List)
	 * @see #checkInvariants(List)
	 */
	public static final List<List<Point2D>> computeMergeBounds(List<Point2D> first, List<Point2D> second, Point2D[] mergeLines){
		if(!first.contains(mergeLines[0])){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}
		
		List<List<Point2D>> left = computeMergeBounds(first, mergeLines[0], mergeLines[3]);
		List<List<Point2D>> right = computeMergeBounds(second, mergeLines[1], mergeLines[2]);
		
		return Arrays.asList(
			left.get(0),
			left.get(1),
			right.get(1),
			right.get(0)
		);
	}
	
	/**
	 * Splits the given hull into two parts, one part
	 * that is contained between the two points going
	 * from <code>a</code> to <code>b</code> and one
	 * part that is contained between the two points
	 * going from <code>b</code> to <code>a</code>.
	 * @param hull The hull to split.
	 * @param a The first split point.
	 * @param b The second split point.
	 * @return The specific segments with the part from
	 *         <code>a</code> to <code>b</code> at index
	 *         0 and the part from <code>b</code> to <code>
	 *         a</code> at index 1.
	 * @see #computeMergeBounds(List, List, Point2D[])
	 * @see #computeMergeBounds(List, List)
	 */
	public static final List<List<Point2D>> computeMergeBounds(List<Point2D> hull, Point2D a, Point2D b){
		List<Point2D> first = new ArrayList<Point2D>();
		List<Point2D> second = new ArrayList<Point2D>();
		
		//inner segment
		int idx = 0;
		while(true){
			if(hull.get(idx).equals(a)){
				first.add(hull.get(idx));
				break;
			}
			idx++;
		}
		
		//outer segment
		second.add(b);
		
		//extend both
		if(!a.equals(b)){
			do{
				idx = (idx + 1) % hull.size();
				first.add(hull.get(idx));
			}while(!hull.get(idx).equals(b));
			
			do{
				idx = (idx + 1) % hull.size();
				second.add(hull.get(idx));
			}while(!hull.get(idx).equals(a));
		}
		
		return Arrays.asList(first, second);
	}
	
	/**
	 * Function to check if 3 points are collinear.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param x3 The x coordinate of the third point.
	 * @param y3 The y coordinate of the third point.
	 * @return True if the given points are (close to) collinear.
	 */
	public static final boolean checkCollinear(int x1, int y1, int x2, int y2, int x3, int y3){
		return Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) == 0;
	}
	
	/**
	 * Function to check if 3 points are collinear.
	 * @param p1 The first point.
	 * @param p2 The second point.
	 * @param p3 The third point.
	 * @return True if the given points are (close to) collinear.
	 */
	public static final boolean checkCollinear(Point2D p1, Point2D p2, Point2D p3){
		return checkCollinear(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
	}
	
	/**
	 * Function to check if 3 points are collinear.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param x3 The x coordinate of the third point.
	 * @param y3 The y coordinate of the third point.
	 * @return True if the given points are (close to) collinear.
	 */
	public static final boolean checkCollinear(double x1, double y1, double x2, double y2, double x3, double y3){
		return Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) < 0.000006D;//account for FP rounding errors
	}
	
	/**
	 * Computes the two lines that would be required to
	 * combine the two given convex hulls into a single
	 * convex hull in linear time. The approach used for
	 * the computation is loosely based on an algorithm
	 * proposed in a paper by Godfried Toussaint.
	 * @param first The first convex hull, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @param second The second convex hull, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @return The points for the two line segments that
	 *         would be required to complete the convex
	 *         hull of the two given convex hulls. The
	 *         first two points make up in order the line
	 *         going from the object containing the bottom
	 *         leftmost point to the other object, and the
	 *         other two points make up in order the line
	 *         back from the other object to the object containing
	 *         the bottom leftmost point.
	 * @see #computeMergeLines(List, List, List)
	 * @see <a href="http://cgm.cs.mcgill.ca/~godfried/teaching/cg-projects/97/Plante/CompGeomProject-EPlante/algorithm.html">
	 *      Toussaint, Godfried T., "A simple linear algorithm for intersecting convex polygons", in The Visual Computer, vol. 1, 1985, pp. 118-123</a>
	 * @see #checkInvariants(List)
	 */
	public static final Point2D[] computeMergeLines(List<Point2D> first, List<Point2D> second){
		//ensure the first object has the bottom leftmost point
		int cmp = Double.compare(first.get(0).getX(), second.get(0).getX());
		if(cmp > 0 || (cmp == 0 && first.get(0).getY() >= second.get(0).getY())){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}
		
		Point2D[] lines = new Point2D[4];
		int ccw = Line2D.relativeCCW(
			first.get(0).getX(),
			first.get(0).getY(),
			first.get(0).getX(),
			first.get(0).getY() - 1.0D,//initial vertical position
			second.get(0).getX(),
			second.get(0).getY()
		);
		int nccw = ccw;
		int lidx = 0;
		int ridx = 0;

		do{
			Point2D lp1 = first.get(lidx % first.size());
			Point2D lp2 = first.get((lidx + 1) % first.size());
			Point2D rp1 = second.get(ridx % second.size());
			Point2D rp2 = second.get((ridx + 1) % second.size());

			double nla = angleFromVertical(lp1, lp2);
			double nra = angleFromVertical(rp1, rp2);
			
			//our angle needs to increase even if we pass the vertical at the end
			if(lidx >= first.size() - 1 && nla <= Math.PI){
				nla += 2.0D * Math.PI;
			}
			if(ridx >= second.size() - 1 && nra <= Math.PI){
				nra += 2.0D * Math.PI;
			}
			
			//compute relative calliper positions
			if(nla <= nra){
				lidx++;
				if(nla >= nra){
					ridx++;
				}
				
				//the first object provides the calliper line, the second only provides a point on its calliper
				nccw = Line2D.relativeCCW(
					lp1.getX(),
					lp1.getY(),
					lp2.getX(),
					lp2.getY(),
					rp1.getX(),
					rp1.getY()
				);
			}else{
				ridx++;

				//translate the calliper line to the other object
				nccw = Line2D.relativeCCW(
					lp1.getX(),
					lp1.getY(),
					rp2.getX() + lp1.getX() - rp1.getX(), 
					rp2.getY() + lp1.getY() - rp1.getY(),
					rp1.getX(),
					rp1.getY()
				);
			}
			
			//record merge line if the relative calliper order changed
			if(nccw != ccw && nccw != 0){
				ccw = nccw;
				
				//skip over collinear points if they exist
				if(lines[0] == null){
					lines[0] = checkCollinear(lp1, lp2, rp1) ? lp2 : lp1;
					lines[1] = checkCollinear(lp2, rp1, rp2) ? rp2 : rp1;
				}else{
					assert lines[2] == null : "More than 2 merge lines found";
					lines[2] = checkCollinear(rp1, rp2, lp1) ? rp2 : rp1;
					lines[3] = checkCollinear(rp2, lp1, lp2) ? lp2 : lp1;
					break;
				}
			}
		}while(lidx <= first.size() && ridx <= second.size());
		
		assert lines[0] != null && lines[1] != null && lines[2] != null && lines[3] != null : "Not enough merge lines found";
		return lines;
	}
	
	/**
	 * Computes the two lines that would be required to extend the
	 * given convex hull with the given point.
	 * @param points The first convex hull, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @param point The point to extend the hull with.
	 * @return The two resultant merge lines, both starting
	 *         from the convex object and ending at the point.
	 */
	public static final List<Line2D> computeSinglePointMergeLines(List<Point2D> points, Point2D point){
		int ccw = Line2D.relativeCCW(
			points.get(0).getX(),
			points.get(0).getY(),
			points.get(0).getX(),
			points.get(0).getY() - 1.0D,
			point.getX(),
			point.getY()
		);
		
		Point2D p1 = null;
		for(int i = 0; i <= points.size(); i++){
			int nccw = Line2D.relativeCCW(
				points.get(i % points.size()).getX(),
				points.get(i % points.size()).getY(),
				points.get((i + 1) % points.size()).getX(),
				points.get((i + 1) % points.size()).getY(),
				point.getX(),
				point.getY()
			);
			
			if(ccw != nccw){
				ccw = nccw;
				if(p1 == null){
					p1 = points.get(i % points.size());
				}else{
					return Arrays.asList(
						new Line2D.Double(p1, point),
						new Line2D.Double(points.get(i % points.size()), point)
					);
				}
			}
		}
		
		throw new IllegalStateException("Not enough merge lines found");
	}
	
	/**
	 * Merges the two given convex objects into a joint convex hull
	 * encompassing all points in both original objects. The output
	 * convex hull will not contain any collinear segments on its
	 * border, will wind counter-clockwise, and its first point
	 * will be bottom leftmost.
	 * @param first The first convex hull, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @param second The second convex hull, the first point
	 *        has to be bottom leftmost and the winding
	 *        order counter-clockwise.
	 * @param mergeLines The points describing the merge lines
	 *        that would be added to merge the two objects as
	 *        computed by {@link #computeMergeLines(List, List)}.
	 *        The points given here <b>must</b> be exact object
	 *        references corresponding to points in the given objects.
	 *        The first merge line has to be from the object with the
	 *        bottom leftmost to the other object and the second line
	 *        back from that object to the object with the bottom leftmost point.
	 * @return The computed joint convex hull.
	 * @see #computeMergeLines(List, List)
	 * @see #checkInvariants(List)
	 */
	public static final List<Point2D> mergeHulls(List<Point2D> first, List<Point2D> second, Point2D[] mergeLines){
		//ensure the first object has the bottom leftmost point
		if(second.contains(mergeLines[0])){
			List<Point2D> tmp = first;
			first = second;
			second = tmp;
		}
		
		List<Point2D> hull = new ArrayList<Point2D>();
		int lidx = 0;
		int ridx = 0;
		Point2D p;
		
		//first object till merge line
		do{
			p = first.get(lidx);
			lidx++;
			
			if(hull.size() >= 2 && checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p)){
				hull.remove(hull.size() - 1);
			}
			
			hull.add(p);
		}while(p != mergeLines[0]);
		
		//end of the first merge line
		if(hull.size() >= 2 && checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), mergeLines[1])){
			hull.remove(hull.size() - 1);
		}
		hull.add(mergeLines[1]);
		
		//skip to the end of the first merge line on the second object
		do{
			p = second.get(ridx);
			ridx++;
		}while(p != mergeLines[1]);
		if(ridx == second.size()){
			ridx = 0;
		}
		
		//add second object till second merge line
		while(p != mergeLines[2]){
			p = second.get(ridx);
			ridx = ridx == second.size() - 1 ? 0 : ridx + 1;
			
			if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p)){
				hull.remove(hull.size() - 1);
			}
			
			hull.add(p);
		}
		
		//end of the second merge line
		if(hull.get(0) != mergeLines[3]){
			if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), mergeLines[3])){
				hull.remove(hull.size() - 1);
			}
			hull.add(mergeLines[3]);
		}
		
		//skip to the end of the second merge line on the first object
		while(lidx < first.size() && first.get(lidx) != mergeLines[3]){
			lidx++;
		}
		
		//add remainder of the first object
		while(lidx < first.size()){
			p = first.get(lidx);
			lidx++;
			
			if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p)){
				hull.remove(hull.size() - 1);
			}
			
			hull.add(p);
		}

		//check for collinearity with respect to the start
		if(checkCollinear(hull.get(hull.size() - 2), hull.get(hull.size() - 1), hull.get(0))){
			hull.remove(hull.size() - 1);
		}

		return hull;
	}
	
	/**
	 * Checks if the convex object represented by the given set
	 * of points conforms to all the general invariants assumed
	 * by most algorithms in this class. More detailed information
	 * about any possible violations is printed to standard error.
	 * <p>
	 * The tested invariants are:
	 * <ol>
	 * <li>The object has an interior, meaning its area is non zero.</li>
	 * <li>No sequence of 3 points on the object's boundary are collinear.</li>
	 * <li>The first point of the object is the bottom leftmost point.</li>
	 * <li>The object is convex.</li>
	 * <li>The object winding order is counter-clockwise.</li>
	 * </ol>
	 * @param points The points that make up the convex object to test.
	 * @return True if all invariants hold, false otherwise.
	 */
	public static final boolean checkInvariants(List<Point2D> points){
		//check if it's an actual object
		if(points.size() < 3){
			System.err.println("The object has no interior");
			return false;
		}
		
		//no collinearity
		for(int i = 0; i < points.size(); i++){
			if(checkCollinear(points.get(i), points.get((i + 1) % points.size()), points.get((i + 2) % points.size()))){
				System.err.println("Points are colinear around indices: " + i + "-" + (i + 2));
				return false;
			}
		}
		
		//initial point is bottom left most
		Point2D min = points.get(0);
		for(int i = 1; i < points.size(); i++){
			int cmp = Double.compare(min.getX(), points.get(i).getX());
			if(cmp > 0 || (cmp == 0 && min.getY() >= points.get(i).getY())){
				System.err.println("Minimum point not minimal: p=" + points.get(i) + ", min=" + min);
				return false;
			}
		}
		
		//the object is convex
		for(int i = 0; i < points.size(); i++){
			Point2D a = points.get(i);
			Point2D b = points.get((i + 1) % points.size());
			Point2D c = points.get((i + 2) % points.size());
			if(Line2D.relativeCCW(a.getX(), a.getY(), c.getX(), c.getY(), b.getX(), b.getY()) == -1){
				System.err.println("Object is not convex (or winds clockwise) around indices: " + i + "-" + (i + 2));
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Computes the angle the given line makes with the 
	 * negative y-axis (going down along the y-axis). The
	 * angle is given as the number of radians traversed
	 * when rotating from the negative y-axis in counter
	 * -clockwise direction until the given line is found
	 * with the correct direction. For example the angle
	 * for the line <code>(0,0)-(10,0)</code> is 90 degrees, but the
	 * angle for the line <code>(0,0)-(-10,0)</code> is 270 degrees.
	 * @param line The line whose angle to the vertical compute.
	 * @return The angle of the vertical to the given line in radians,
	 *         the returned angle is always a positive number.
	 */
	public static final double angleFromVertical(Line2D line){
		return angleFromVertical(line.getX1(), line.getY1(), line.getX2(), line.getY2());
	}
	
	/**
	 * Computes the angle the given line makes with the 
	 * negative y-axis (going down along the y-axis). The
	 * angle is given as the number of radians traversed
	 * when rotating from the negative y-axis in counter
	 * -clockwise direction until the given line is found
	 * with the correct direction. For example the angle
	 * for the line <code>(0,0)-(10,0)</code> is 90 degrees, but the
	 * angle for the line <code>(0,0)-(-10,0)</code> is 270 degrees.
	 * @param p1 The start point of the given line.
	 * @param p2 The end point of the given line.
	 * @return The angle of the vertical to the given line in radians,
	 *         the returned angle is always a positive number.
	 */
	public static final double angleFromVertical(Point2D p1, Point2D p2){
		return angleFromVertical(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	
	/**
	 * Computes the angle the given line makes with the 
	 * negative y-axis (going down along the y-axis). The
	 * angle is given as the number of radians traversed
	 * when rotating from the negative y-axis in counter
	 * -clockwise direction until the given line is found
	 * with the correct direction. For example the angle
	 * for the line <code>(0,0)-(10,0)</code> is 90 degrees, but the
	 * angle for the line <code>(0,0)-(-10,0)</code> is 270 degrees.
	 * @param x1 The x-coordinate of the start point of the given line.
	 * @param y1 The y-coordinate of the start point of the given line.
	 * @param x2 The x-coordinate of the end point of the given line.
	 * @param y2 The y-coordinate of the end point of the given line.
	 * @return The angle of the vertical to the given line in radians,
	 *         the returned angle is always a positive number.
	 */
	public static final double angleFromVertical(double x1, double y1, double x2, double y2){
		double relative = Math.atan2(y2 - y1, x2 - x1) + 0.5D * Math.PI;
		return relative < 0.0D ? (relative + 2.0D * Math.PI) : relative;
	}
	
	/**
	 * Computes the relative angle in counter-clockwise direction between
	 * the two given directed line segments.
	 * @param a The first line segment (start).
	 * @param b The second line segment (end).
	 * @return The relative angle between the two line segments in radians,
	 *         the returned angle is always a positive number.
	 */
	public static final double angleBetweenLines(Line2D a, Line2D b){
		double relative = Math.atan2(b.getY2() - b.getY1(), b.getX2() - b.getX1()) - Math.atan2(a.getY2() - a.getY1(), a.getX2() - a.getX1());
		return relative < 0.0D ? (relative + 2.0D * Math.PI) : relative;
	}
}
