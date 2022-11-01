package dev.roanh.convexmerger.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import dev.roanh.convexmerger.Constants;

public class ConjugationTree<T> extends PartitionTree<T, ConjugationTree<T>>{
	private ConjugationTree<T> parent;
	private Line2D bisector;
	private List<Point2D> on = new ArrayList<Point2D>(2);
	private ConjugationTree<T> left;
	private ConjugationTree<T> right;
	
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
				Point2D intercept = ConvexUtil.intercept(line.getP1(), line.getP2(), node.bisector.getP1(), node.bisector.getP2());
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
			double max = ConvexUtil.clamp(0.0D, Constants.PLAYFIELD_WIDTH, (Constants.PLAYFIELD_HEIGHT - base) / coef);
			double min = ConvexUtil.clamp(0.0D, Constants.PLAYFIELD_WIDTH, -base / coef);
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

	@Override
	public Stream<ConjugationTree<T>> streamLeafCells(){
		return isLeafCell() ? Stream.of(this) : Stream.concat(left.streamLeafCells(), right.streamLeafCells());
	}

	@Override
	public boolean isLeafCell(){
		return left == null && right == null;
	}
	
	@Override
	public List<ConjugationTree<T>> getChildren(){
		return Arrays.asList(left, right);
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
	
	//TODO move to util?
	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2){
		if(x1 == x2){
			g.draw(new Line2D.Double(x1, 0.0D, x2, Constants.PLAYFIELD_HEIGHT));
		}else{
			double coef = (y2 - y1) / (x2 - x1);
			double base = y1 - x1 * coef;
			g.draw(new Line2D.Double(0.0D, base, Constants.PLAYFIELD_WIDTH, base + coef * Constants.PLAYFIELD_WIDTH));
		}
	}
	
	private static class ConjugateData{
		private Line2D conjugate;
		private List<Point2D> leftOn = new ArrayList<Point2D>(2);
		private List<Point2D> rightOn = new ArrayList<Point2D>(2);
	}
}
