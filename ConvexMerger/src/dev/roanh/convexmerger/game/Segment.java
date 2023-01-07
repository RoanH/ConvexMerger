package dev.roanh.convexmerger.game;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

/**
 * A line segment instance with equality based
 * on its end points.
 * @author Roan
 */
public class Segment extends Line2D{
	/**
	 * First end point of the line.
	 */
	protected Point2D p1;
	/**
	 * Second end point of the line.
	 */
	protected Point2D p2;
	
	/**
	 * Constructs a new line segment with the given end points. 
	 * @param p1 The first end point of the line.
	 * @param p2 The second end point of the line.
	 */
	public Segment(Point2D p1, Point2D p2){
		this.p1 = p1;
		this.p2 = p2;
	}
	
	@Override
	public Rectangle2D getBounds2D(){
		return new Rectangle2D.Double(
			Math.min(p1.getX(), p2.getX()),
			Math.min(p1.getY(), p2.getY()),
			Math.abs(p1.getX() - p2.getX()),
			Math.abs(p1.getY() - p2.getY())
		);
	}

	@Override
	public double getX1(){
		return p1.getX();
	}

	@Override
	public double getY1(){
		return p1.getY();
	}

	@Override
	public Point2D getP1(){
		return p1;
	}

	@Override
	public double getX2(){
		return p2.getX();
	}

	@Override
	public double getY2(){
		return p2.getY();
	}

	@Override
	public Point2D getP2(){
		return p2;
	}

	@Override
	public void setLine(double x1, double y1, double x2, double y2){
		throw new IllegalStateException("Unsupported operation");
	}

	@Override
	public int hashCode(){
		return Objects.hash(p1, p2);
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof Segment){
			Segment line = (Segment)other;
			return (line.p1 == p1 && line.p2 == p2) || (line.p1 == p2 && line.p2 == p1);
		}else{
			return false;
		}
	}
	
	@Override
	public String toString(){
		return "Segment[p1=(" + p1.getX() + "," + p1.getY() + "),p2=(" + p2.getX() + "," + p2.getY() + ")]";
	}
}
