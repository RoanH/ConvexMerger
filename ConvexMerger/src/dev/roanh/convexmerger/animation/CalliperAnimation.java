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
package dev.roanh.convexmerger.animation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.ConvexUtil;
import dev.roanh.convexmerger.ui.Theme;

/**
 * Animation to visualise the callipers used
 * when merging two convex objects.
 * @author Roan
 */
public class CalliperAnimation extends Animation{
	/**
	 * The number of milliseconds the animation runs for.
	 */
	private static final float DURATION = 5000.0F;
	/**
	 * The epoch millis start time of the animation.
	 */
	private long start;
//	/**
//	 * The convex object point set.
//	 */
//	private List<Point2D> points;
	/**
	 * Cached current index in the points list.
	 */
	private int indexFirst = 0;
	private int indexSecond = 0;
	private ConvexObject first;
	private ConvexObject second;
	private Line2D firstLine;
	private Line2D secondLine;
	private double firstAngle;
	private double secondAngle;
	
//	/**
//	 * Constructs a new calliper animation for the given object.
//	 * Note: this animation only renders the calliper and not the object.
//	 * @param obj The object to show a calliper for.
//	 */
	public CalliperAnimation(ConvexObject first, ConvexObject second){
		this.first = first;
		this.second = second;
		
		Point2D[] lines = ConvexUtil.computeMergeLines(first.getPoints(), second.getPoints());
		firstLine = new Line2D.Double(lines[0], lines[1]);
		secondLine = new Line2D.Double(lines[2], lines[3]);
		firstAngle = ConvexUtil.angleFromVertical(firstLine);
		secondAngle = ConvexUtil.angleFromVertical(secondLine);
		
		start = System.currentTimeMillis();
	}

	@Override
	protected boolean render(Graphics2D g){
		g.setStroke(Theme.BORDER_STROKE);
		long elapsed = System.currentTimeMillis() - start;
		double angle = (Math.PI * 2.0F * elapsed) / DURATION;
		
		first.render(g);
		second.render(g);
		
		g.setColor(Color.BLUE);
		if(firstAngle <= angle){
			g.draw(firstLine);
		}
		
		if(secondAngle <= angle){
			g.draw(secondLine);
		}
		
		indexFirst = drawCalliper(g, first.getPoints(), angle, indexFirst);
		indexSecond = drawCalliper(g, second.getPoints(), angle, indexSecond);
		
		return elapsed < DURATION;
	}
	
	private int drawCalliper(Graphics2D g, List<Point2D> points, double angle, int index){
		Point2D base = points.get(index % points.size());
		while(index < points.size()){
			if(ConvexUtil.angleFromVertical(points.get(index % points.size()), points.get((index + 1) % points.size())) >= angle){
				base = points.get(index % points.size());
				break;
			}
			index++;
		}
		
		g.setColor(Color.RED);
		angle += Math.PI * 0.5F;
		drawLine(
			g,
			base.getX(),
			base.getY(),
			base.getX() + Math.cos(angle),
			base.getY() + Math.sin(angle)
		);
		
		return index;
	}
	
	/**
	 * Draws an 'infinite' line segment through the two given points.
	 * @param g The graphics context to use.
	 * @param x1 The x-coordinate of the first point.
	 * @param y1 The y-coordinate of the first point.
	 * @param x2 The x-coordinate of the second point.
	 * @param y2 The y-coordinate of the second point.
	 */
	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2){
		if(x1 == x2){
			g.draw(new Line2D.Double(x1, -Constants.PLAYFIELD_HEIGHT, x2, Constants.PLAYFIELD_HEIGHT * 2.0D));
		}else{
			double coef = (y2 - y1) / (x2 - x1);
			double base = y1 - x1 * coef;
			g.draw(new Line2D.Double(-Constants.PLAYFIELD_WIDTH, base - coef * Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_WIDTH * 2.0D, base + coef * 2.0D * Constants.PLAYFIELD_WIDTH));
		}
	}
}
