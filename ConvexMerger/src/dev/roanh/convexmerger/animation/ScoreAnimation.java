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

import java.awt.Graphics2D;

import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme;

/**
 * Animation that shows the player score increasing or decreasing.
 * @author Roan
 */
public class ScoreAnimation extends Animation{
	/**
	 * Number of score points to add each millisecond.
	 */
	private static final int SCORE_PER_MS = 51;
	/**
	 * Player whose score to display.
	 */
	private Player player;
	/**
	 * Current area claimed by the player as
	 * shown by the animation.
	 */
	private double area;
	/**
	 * Timestamp the last animation frame was rendered.
	 */
	private long last = -1L;
	
	/**
	 * Constructs a new score animation for the given player.
	 * @param player The player whose score to animate.
	 */
	public ScoreAnimation(Player player){
		this.player = player;
		area = player.getArea();
	}

	@Override
	protected boolean render(Graphics2D g){
		if(last == -1L){
			last = System.currentTimeMillis();
		}
		
		long time = System.currentTimeMillis();
		if(area <= player.getArea()){
			area = Math.min(player.getArea(), area + (time - last) * SCORE_PER_MS);
		}else{
			area = Math.max(player.getArea(), area - (time - last) * SCORE_PER_MS);
		}
		
		g.drawString(Theme.formatScore(area), 0, 0);
		
		if(Double.compare(area, player.getArea()) != 0){
			last = time;
			return true;
		}else{
			last = -1L;
			return false;
		}
	}
}
