package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;

import dev.roanh.convexmerger.animation.Animation;

public abstract class RenderableObject{
	/**
	 * The active animation for this object.
	 */
	private transient Animation animation = null;
	
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
	
	public abstract void render(Graphics2D g);
}
