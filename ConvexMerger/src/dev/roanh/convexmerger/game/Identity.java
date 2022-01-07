package dev.roanh.convexmerger.game;

/**
 * Interface for objects that can be
 * identified via some unique ID.
 * @author Roan
 */
public abstract interface Identity{
	
	/**
	 * Gets the ID of this entity.
	 * @return The ID of this entity.
	 */
	public abstract int getID();

	/**
	 * Sets the ID of this entity.
	 * @param id The new ID of this
	 *        entity, must be unique.
	 */
	public abstract void setID(int id);
}
