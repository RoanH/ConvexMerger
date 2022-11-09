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
package dev.roanh.convexmerger.net.packet;

/**
 * Enum of all packet types.
 * @author Roan
 */
public enum PacketRegistry{
	/**
	 * Sent when a game is started.
	 */
	GAME_INIT,
	/**
	 * Sent when a player wants to join a game.
	 */
	PLAYER_JOIN,
	/**
	 * Sent when a player is accepted into a game.
	 */
	PLAYER_JOIN_ACCEPT,
	/**
	 * Sent when a player make a move.
	 */
	PLAYER_MOVE,
	/**
	 * Sent when a player is not allowed to join a game.
	 */
	PLAYER_JOIN_REJECT,
	/**
	 * Sent when the game ends.
	 */
	GAME_END;
}
