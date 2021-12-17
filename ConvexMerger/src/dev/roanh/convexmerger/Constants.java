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
	 * Initial frame size scaling factor
	 */
	public static final int INIT_SIZE = 80;
}
