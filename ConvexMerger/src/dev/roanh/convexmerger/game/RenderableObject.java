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
package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;

import dev.roanh.convexmerger.animation.Animation;

/**
 * Represents an object that can be rendered and animated.
 * @author Roan
 * @see Animation
 */
public abstract class RenderableObject{
	/**
	 * The active animation for this object.
	 */
	private transient Animation animation = null;
	
	/**
	 * Gets the animation for this object.
	 * @return The animation for this object.
	 */
	public Animation getAnimation(){
		return animation;
	}
	
	/**
	 * Checks if this convex object has an active animation.
	 * @return True if this convex object has an active animation.
	 */
	public boolean hasAnimation(){
		return animation != null;
	}
	
	/**
	 * Sets the active animation for this convex object.
	 * @param animation The new active animation.
	 */
	public void setAnimation(Animation animation){
		this.animation = animation;
	}
	
	/**
	 * Renders the animation for this convex object
	 * using the given graphics instance.
	 * @param g The graphics instance to use.
	 * @return True if the animation still has frames
	 *         remaining, false otherwise.
	 */
	public boolean runAnimation(Graphics2D g){
		if(animation.run(g)){
			return true;
		}else{
			animation = null;
			return false;
		}
	}
	
	/**
	 * Renders this object or runs its animation
	 * if it has one set.
	 * @param g The graphics context to use.
	 */
	public void renderOrAnimate(Graphics2D g){
		if(hasAnimation()){
			runAnimation(g);
		}else{
			render(g);
		}
	}
	
	/**
	 * Renders this object.
	 * @param g The graphics context to use.
	 */
	public abstract void render(Graphics2D g);
}
