/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.util;

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
