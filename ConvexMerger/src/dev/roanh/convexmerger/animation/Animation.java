package dev.roanh.convexmerger.animation;

import java.awt.Graphics2D;

/**
 * Interface to represent ongoing animations.
 * @author Roan
 */
public abstract interface Animation{

	/**
	 * Renders the next frame of the animation.
	 * @param g The graphics instance to use.
	 * @return True if the animation has frames
	 *         remaining, false if it finished.
	 */
	public abstract boolean run(Graphics2D g);
}
