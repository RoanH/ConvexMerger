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
package dev.roanh.convexmerger;

/**
 * Class containing various game constants.
 * @author Roan
 */
public final class Constants{
	/**
	 * Current game version.
	 */
	public static final String VERSION = "v1.2";//don't forget build.gradle
	/**
	 * Game and window title.
	 */
	public static final String TITLE = "ConvexMerger";
	/**
	 * The playfield height (from 0 to {@value #PLAYFIELD_WIDTH}).
	 */
	public static final int PLAYFIELD_WIDTH = 1600;
	/**
	 * The playfield height (from 0 to {@value #PLAYFIELD_HEIGHT}).
	 */
	public static final int PLAYFIELD_HEIGHT = 900;
	/**
	 * Minimum frame size scaling factor
	 */
	public static final int MIN_SIZE = 70;
	/**
	 * Initial frame size scaling factor
	 */
	public static final int INIT_SIZE = 80;
	/**
	 * Number of milliseconds per animation frame (60FPS).
	 */
	public static final long ANIMATION_RATE = 33;
	/**
	 * Minimum number of milliseconds any turn should take.
	 */
	public static final long MIN_TURN_TIME = 650;
	/**
	 * Multiplayer server port.
	 */
	public static final int PORT = 11111;
	/**
	 * The number of milliseconds an AI has to wait before making a move.
	 */
	public static final long AI_WAIT_TIME = 400;
}
