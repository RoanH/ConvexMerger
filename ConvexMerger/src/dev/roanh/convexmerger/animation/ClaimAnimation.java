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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.ui.Theme;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Animation shown when claiming a single object.
 * @author Roan
 */
public class ClaimAnimation implements Animation{
	/**
	 * Number of milliseconds the animation plays for.
	 */
	private static final float DURATION = 250.0F;
	/**
	 * Millisecond time this animation started.
	 */
	private long start;
	/**
	 * The convex object that was claimed.
	 */
	private ConvexObject obj;
	/**
	 * The point that was clicked to claim the convex object.
	 */
	private Point2D loc;
	/**
	 * The largest distance to a vertex from the clicked point.
	 */
	private float dist = 0.0F;

	/**
	 * Constructs a new claim animation for the given object.
	 * @param selected The convex object that was claimed.
	 * @param location The point that was clicked to claim the object.
	 */
	public ClaimAnimation(ConvexObject selected, Point2D location){
		obj = selected;
		loc = location;
		start = System.currentTimeMillis();
		for(Point2D p : selected.getPoints()){
			dist = Math.max(dist, (float)location.distance(p));
		}
	}

	@Override
	public boolean run(Graphics2D g){
		long elapsed = System.currentTimeMillis() - start;

		if(elapsed < DURATION){
			g.setPaint(new RadialGradientPaint(
				loc,
				dist,
				new float[]{
					0.0F,
					Math.min(0.998F, (elapsed / DURATION) + 0.001F),
					Math.min(0.999F, 2.0F * (elapsed / DURATION) + 0.002F),
					1.0F
				},
				new Color[]{
					obj.getOwner().getTheme().getBody(),
					obj.getOwner().getTheme().getBody(),
					PlayerTheme.UNOWNED.getBody(),
					PlayerTheme.UNOWNED.getBody()
				}
			));
		}else{
			g.setColor(Theme.getPlayerBody(obj));
		}
		g.fill(obj.getShape());
		
		g.setStroke(Theme.POLY_STROKE);
		g.setColor(elapsed * 2.0F > DURATION ? Theme.getPlayerOutline(obj) : PlayerTheme.UNOWNED.getOutline());
		g.draw(obj.getShape());
		
		return elapsed <= DURATION;
	}
}
