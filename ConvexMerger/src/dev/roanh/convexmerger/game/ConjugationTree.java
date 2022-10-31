package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.roanh.convexmerger.Constants;

public class ConjugationTree{
	private ConjugationTree parent;
	private Line2D bisector;
	private List<Point2D> on = new ArrayList<Point2D>(2);
	private ConjugationTree left;
	private ConjugationTree right;
	
	public ConjugationTree(List<Point2D> points){
		//only root bisector finding requires O(n log n) time
		points.sort(Comparator.comparingDouble(Point2D::getX));
		int idx = points.size() / 2;
		
		double mid = points.get(idx).getX();
		bisector = new Line2D.Double(mid, 0.0D, mid, Constants.PLAYFIELD_HEIGHT);
		
		//by assumption we have few points on the bisector, but for other applications this could be false
		List<Point2D> leftPoints = new ArrayList<Point2D>(idx);
		List<Point2D> rightPoints = new ArrayList<Point2D>(idx);
		on.add(points.get(idx));
		for(int i = idx - 1; i >= 0; i--){
			Point2D p = points.get(i);
			if(p.getX() >= mid){
				on.add(p);
			}else{
				leftPoints.add(p);
			}
		}
		for(int i = idx + 1; i < points.size(); i++){
			Point2D p = points.get(i);
			if(p.getX() <= mid){
				on.add(p);
			}else{
				rightPoints.add(p);
			}
		}
		
		System.out.println("pconj: " + leftPoints + " / " + rightPoints);
		
		//construct children
		ConjugateData data = computeConjugate(leftPoints, rightPoints, bisector);
		System.out.println("pdata: " + data.conjugate + " / " + data.leftOn.size() + " / " + data.rightOn.size());
		left = new ConjugationTree(this, leftPoints, data.leftOn, data.conjugate);
		right = new ConjugationTree(this, rightPoints, data.rightOn, data.conjugate);
	}
	
	private ConjugationTree(ConjugationTree parent, List<Point2D> points, List<Point2D> on, Line2D bisector){
		this.parent = parent;
		this.bisector = bisector;
		this.on = on;
		
		List<Point2D> leftPoints = new ArrayList<Point2D>(points.size() / 2);
		List<Point2D> rightPoints = new ArrayList<Point2D>(points.size() / 2);
		for(Point2D p : points){
			if(!on.contains(p)){
				int rel = bisector.relativeCCW(p);
				switch(rel){
				case -1:
					leftPoints.add(p);
					break;
				case 1:
					rightPoints.add(p);
					break;
				case 0:
				default:
					assert false : "Impossible CCW";
				}
			}
		}
		
		System.out.println("conj: " + leftPoints.size() + " / " + rightPoints.size());

		//construct children
		if(!(leftPoints.isEmpty() || rightPoints.isEmpty())){//TODO this is wrong
			ConjugateData data = computeConjugate(leftPoints, rightPoints, bisector);
			left = new ConjugationTree(this, leftPoints, data.leftOn, data.conjugate);
			right = new ConjugationTree(this, rightPoints, data.rightOn, data.conjugate);
		}
	}
	
	public void render(Graphics2D g){
		g.setColor(new Color[]{
			Color.WHITE,
			Color.CYAN,
			new Color(0, 150, 150),
			Color.BLUE,
		}[depth()]);
		g.draw(clipLine(extendLine(bisector), on.get(0)));
		
		g.setColor(Color.RED);
		for(Point2D p : on){
			g.fill(new Ellipse2D.Double(p.getX() - 5, p.getY() - 5, 10, 10));
		}
		
		if(parent == null || parent.parent == null || true){
			if(left != null){
				left.render(g);
			}
			if(right != null){
				right.render(g);
			}
		}
	}
	
	private Line2D clipLine(Line2D line, Point2D on){
		if(parent == null || parent.parent == null){
			return line;
		}else{
			ConjugationTree node = parent.parent;
			while(node != null){
				Point2D intercept = intercept(line.getP1(), line.getP2(), node.bisector.getP1(), node.bisector.getP2());
				if(intercept != null){
					int onCCW = node.bisector.relativeCCW(on);
					if(onCCW == node.bisector.relativeCCW(line.getP1())){
						line = new Line2D.Double(line.getP1(), intercept);
					}else{//p2
						line = new Line2D.Double(intercept, line.getP2());
					}
				}
				
				node = node.parent;
			}
			return line;
		}
	}
	
	//extend to structure bounds
	private Line2D extendLine(Line2D line){
		if(line.getX1() == line.getX2()){
			return new Line2D.Double(line.getX1(), 0.0D, line.getX2(), Constants.PLAYFIELD_HEIGHT);
		}else{
			double coef = (line.getY2() - line.getY1()) / (line.getX2() - line.getX1());
			double base = line.getY1() - line.getX1() * coef;
			
			//w = base + coef * x
			//w - base = coef * x
			//(w - base) / coef = x
			double max = clamp(0.0D, Constants.PLAYFIELD_WIDTH, (Constants.PLAYFIELD_HEIGHT - base) / coef);
			double min = clamp(0.0D, Constants.PLAYFIELD_WIDTH, -base / coef);
//			exit = Constants.PLAYFIELD_WIDTH;
//			System.out.println(exit);
			
			//y = base + coef * x
			
//			System.out.println(min + " / " + max);
			
			return new Line2D.Double(min, base + coef * min, max, base + coef * max);
		}
	}
	
	//node depth, root = 0
	public int depth(){
		return parent == null ? 0 : 1 + parent.depth();
	}
	
	private static final ConjugateData computeConjugate(List<Point2D> left, List<Point2D> right, Line2D bisector){
		//TODO this is a naive temporary solution
		
		ConjugateData data = new ConjugateData();
		for(Point2D p1 : left){
			for(Point2D p2 : right){
				assert data.leftOn.isEmpty() && data.rightOn.isEmpty() : "Start not empty";
				Line2D conj = new Line2D.Double(p1, p2);
				
				int val = 0;
				for(Point2D lp : left){
					if(lp != p1){
						int rel = conj.relativeCCW(lp);
						val += rel;
						if(rel == 0){
							data.leftOn.add(lp);
						}
					}
				}
				
				if(Math.abs(val) > 1){
					data.leftOn.clear();
					continue;
				}
				
				val = 0;
				for(Point2D rp : right){
					if(p2 != rp){
						int rel = conj.relativeCCW(rp);
						val += rel;
						if(rel == 0){
							data.rightOn.add(rp);
						}
					}
				}
				
				if(Math.abs(val) <= 1){
					data.conjugate = conj;
					data.leftOn.add(p1);
					data.rightOn.add(p2);
					return data;
				}else{
					data.leftOn.clear();
					data.rightOn.clear();
				}
			}
		}
		
		//TODO handle empty left/right cases
//		assert false : "Not implemented : " + left.size() + " / " + right.size();
		System.out.println("end");
		data.conjugate = new Line2D.Double(10, 10, 20, 20);
		return data;
	}
	
	private void drawLine(Graphics2D g, Line2D line){
		drawLine(g, line.getX1(), line.getY1(), line.getX2(), line.getY2());
	}
	
	//TODO util
	/**
	 * Computes the intersection point of the two given closed line segments.
	 * @param a The first point of the first line segment.
	 * @param b The second point of the first line segment.
	 * @param c The first point of the second line segment.
	 * @param d The second point of the second line segment.
	 * @return The intersection point, or <code>null</code>
	 *         if the given line segments do not intersect.
	 */
	private Point2D intercept(Point2D a, Point2D b, Point2D c, Point2D d){
		double det = (a.getX() - b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() - d.getX());
		Point2D p = new Point2D.Double(
			((a.getX() * b.getY() - a.getY() * b.getX()) * (c.getX() - d.getX()) - (a.getX() - b.getX()) * (c.getX() * d.getY() - c.getY() * d.getX())) / det,
			((a.getX() * b.getY() - a.getY() * b.getX()) * (c.getY() - d.getY()) - (a.getY() - b.getY()) * (c.getX() * d.getY() - c.getY() * d.getX())) / det
		);
		return (onLine(p, a, b) && onLine(p, c, d)) ? p : null;
	}
	
	//TODO util
	/**
	 * Checks if the given point <code>p</code> is
	 * on the closed line segment between <code>a
	 * </code> and <code>b</code>. The given point is
	 * assumed to be on the infinite line segment
	 * <code>a</code> and <code>b</code>
	 * @param p The point to check.
	 * @param a The first point of the line segment.
	 * @param b The second point of the line segment.
	 * @return True if the given point is on the given line segment.
	 */
	private boolean onLine(Point2D p, Point2D a, Point2D b){
		return Math.min(a.getX(), b.getX()) - 0.00005D <= p.getX() && p.getX() <= Math.max(a.getX(), b.getX()) + 0.00005D && Math.min(a.getY(), b.getY()) - 0.00005D <= p.getY() && p.getY() <= Math.max(a.getY(), b.getY()) + 0.00005D; 
	}
	
	//TODO move to util
	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2){
		if(x1 == x2){
			g.draw(new Line2D.Double(x1, 0.0D, x2, Constants.PLAYFIELD_HEIGHT));
		}else{
			double coef = (y2 - y1) / (x2 - x1);
			double base = y1 - x1 * coef;
			g.draw(new Line2D.Double(0.0D, base, Constants.PLAYFIELD_WIDTH, base + coef * Constants.PLAYFIELD_WIDTH));
		}
	}
	
	//TODO util
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
	
	private static class ConjugateData{
		private Line2D conjugate;
		private List<Point2D> leftOn = new ArrayList<Point2D>(2);
		private List<Point2D> rightOn = new ArrayList<Point2D>(2);
	}
}
