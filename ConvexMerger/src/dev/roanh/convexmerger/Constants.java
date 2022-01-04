package dev.roanh.convexmerger;

import java.awt.geom.Rectangle2D;

/**
 * Class containing various game constants.
 * @author Roan
 */
public final class Constants{
	/**
	 * Game and window title.
	 */
	public static final String TITLE = "Convex Merger";
	/**
	 * The playfield height (from 0 to {@value #PLAYFIELD_WIDTH}).
	 */
	public static final int PLAYFIELD_WIDTH = 1600;
	/**
	 * The playfield height (from 0 to {@value #PLAYFIELD_HEIGHT}).
	 */
	public static final int PLAYFIELD_HEIGHT = 900;
	/**
	 * The bounding box the vertical decomposition can use that
	 * ensures that all convex objects are fully contained.
	 */
	public static final Rectangle2D DECOMP_BOUNDS = new Rectangle2D.Double(-1.0D, -1.0D, PLAYFIELD_WIDTH + 1.0D, PLAYFIELD_HEIGHT + 1.0D);
	/**
	 * Minimum frame size scaling factor
	 */
	public static final int MIN_SIZE = 60;
	/**
	 * Initial frame size scaling factor
	 */
	public static final int INIT_SIZE = 80;
	/**
	 * Number of milliseconds per animation frame (60FPS).
	 */
	public static final long ANIMATION_RATE = 33;
	/**
	 * Current game version.
	 */
	public static final String VERSION = "v1.0";//don't forget build.gradle
}
