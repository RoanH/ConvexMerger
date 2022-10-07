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
import dev.roanh.convexmerger.ui.Theme;

/**
 * Implementation of a kd-tree for k = 2.
 * @author Roan
 * @param <T> The per cell item data type.
 */
public class KDTree<T>{
	private KDTree<T> parent = null;
	private KDTree<T> low = null;
	private KDTree<T> high = null;
	private Point2D point = null;
	private boolean xAxis;
	private Rectangle2D bounds = null;
	private List<T> data = null;
	
	public KDTree(List<Point2D> points){
		this(null, points, false);
	}
	
	private KDTree(KDTree<T> parent, List<Point2D> points, boolean xAxis){
		this.parent = parent;
		this.xAxis = xAxis;
		if(points != null && !points.isEmpty()){
			points.sort(Comparator.comparing(xAxis ? Point2D::getX : Point2D::getY));
			this.point = points.get(points.size() / 2);
			
			if(points.size() > 1){
				low = new KDTree<T>(this, new ArrayList<Point2D>(points.subList(0, points.size() / 2)), !xAxis);
			}else{
				low = new KDTree<T>(this, null, !xAxis);
			}
			
			if(points.size() > 2){
				high = new KDTree<T>(this, new ArrayList<Point2D>(points.subList(points.size() / 2 + 1, points.size())), !xAxis);
			}else{
				high = new KDTree<T>(this, null, !xAxis);
			}
		}else{
			data = new ArrayList<T>();
		}
	}
	
	public void addData(T line){
		data.add(line);
	}
	
	public boolean isLeafCell(){
		return point == null;
	}
	
	public KDTree<T> getLowNode(){
		return low;
	}
	
	public KDTree<T> getHighNode(){
		return high;
	}
	
	public boolean contains(Line2D line){
		Rectangle2D bounds = getBounds();
		int pos1 = bounds.outcode(line.getP1());
		int pos2 = bounds.outcode(line.getP2());
		
		//delegate when no boundary points are involved
		if(pos1 != 0 && pos2 != 0){
			return bounds.intersectsLine(line);
		}
		
		//for a non degenerate line an intersection exists if both points are internal
		if(pos1 == 0 && pos2 == 0){
			return true;
		}
		
		//make p1 internal
		Point2D p1 = line.getP1();
		if(pos1 != 0){
			pos2 = pos1;
			pos1 = 0;
			p1 = line.getP2();
		}
		
		//check for lines moving away from each boundary
		//note: lower bounds are inherited and thus more accurate, exact even unless collinearity is involved
		return (p1.getX() - bounds.getMinX() > 0.0000001D || (pos2 & Rectangle2D.OUT_LEFT) == 0) 
		    && (bounds.getMaxX() - p1.getX() > 0.0000006D || (pos2 & Rectangle2D.OUT_RIGHT) == 0)
		    && (p1.getY() - bounds.getMinY() > 0.0000001D || (pos2 & Rectangle2D.OUT_TOP) == 0)
		    && (bounds.getMaxY() - p1.getY() > 0.0000006D || (pos2 & Rectangle2D.OUT_BOTTOM) == 0);
	}
	
	public List<T> getData(){
		return data;
	}
	
	public Rectangle2D getBounds(){
		if(bounds == null){
			if(parent == null){
				bounds = new Rectangle2D.Double(0.0D, 0.0D, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			}else if(this == parent.low){
				Rectangle2D parentBounds = parent.getBounds();
				if(xAxis){
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parentBounds.getMinY(),
						parentBounds.getWidth(),
						parent.point.getY() - parentBounds.getMinY()
					);
				}else{
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parentBounds.getMinY(),
						parent.point.getX() - parentBounds.getMinX(),
						parentBounds.getHeight()
					);
				}
			}else if(this == parent.high){
				Rectangle2D parentBounds = parent.getBounds();
				if(xAxis){
					bounds = new Rectangle2D.Double(
						parentBounds.getMinX(),
						parent.point.getY(),
						parentBounds.getWidth(),
						parentBounds.getMaxY() - parent.point.getY()
					);
				}else{
					bounds = new Rectangle2D.Double(
						parent.point.getX(),
						parentBounds.getMinY(),
						parentBounds.getMaxX() - parent.point.getX(),
						parentBounds.getHeight()
					);
				}
			}
		}
		
		return bounds;
	}
	
	public void render(Graphics2D g){
		Rectangle2D bounds = getBounds();
		if(isLeafCell()){
			g.setColor(data.isEmpty() ? new Color(0, 255, 255, 50) : new Color(255, 0, 0, 50));
			g.fill(bounds);
			
			g.setColor(Color.WHITE);
			String num = String.valueOf(data.size());
			g.drawString(
				num,
				(float)(bounds.getMinX() + (bounds.getWidth() - g.getFontMetrics().stringWidth(num)) / 2.0F),
				(float)(bounds.getMinY() + (bounds.getHeight() + g.getFontMetrics().getAscent()) / 2.0F)
			);
		}else{
			g.setColor(Color.WHITE);
			g.setStroke(Theme.BORDER_STROKE);
			if(xAxis){
				g.draw(new Line2D.Double(point.getX(), bounds.getMinY(), point.getX(), bounds.getMaxY()));
			}else{
				g.draw(new Line2D.Double(bounds.getMinX(), point.getY(), bounds.getMaxX(), point.getY()));
			}
			
			low.render(g);
			high.render(g);
			
			g.setColor(Color.BLUE);
			g.fill(new Ellipse2D.Double(point.getX() - 2.5D, point.getY() - 2.5D, 5.0D, 5.0D));
		}
	}
}
