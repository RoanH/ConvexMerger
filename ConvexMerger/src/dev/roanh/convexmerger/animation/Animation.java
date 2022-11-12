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

/**
 * Class to represent ongoing animations.
 * @author Roan
 */
public abstract class Animation{
	/**
	 * An 'animation' that renders nothing effectively
	 * hiding the object it is applied to.
	 */
	public static final Animation EMPTY = new Animation(){

		@Override
		protected boolean render(Graphics2D g){
			return true;
		}
	};
	/**
	 * When true indicates that the animation has no frames remaining.
	 */
	private volatile boolean finished;

	/**
	 * Renders the next frame of the animation.
	 * @param g The graphics instance to use.
	 * @return True if the animation has frames
	 *         remaining, false if it finished.
	 */
	public final boolean run(Graphics2D g){
		if(!finished){
			finished = !render(g);
			if(finished){
				synchronized(this){
					notifyAll();
				}
			}else{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Blocks the current thread until this animation has finished.
	 * @throws InterruptedException When the current thread is interrupted.
	 */
	public synchronized void waitFor() throws InterruptedException{
		while(!finished){
			wait();
		}
	}
	
	/**
	 * Renders the next frame of this animation.
	 * @param g The graphics context to use.
	 * @return True if the animation has frames
	 *         remaining, false if it finished.
	 */
	protected abstract boolean render(Graphics2D g);
}
