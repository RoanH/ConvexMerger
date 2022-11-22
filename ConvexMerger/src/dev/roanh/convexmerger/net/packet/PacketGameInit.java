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

import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.net.PlayerProxy;
import dev.roanh.convexmerger.player.Player;

/**
 * Packet sent when the game starts.
 * @author Roan
 */
public class PacketGameInit implements Packet{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3786951584716643604L;
	/**
	 * The convex objects for this game.
	 */
	private final List<ConvexObject> objects;
	/**
	 * The game seed.
	 */
	private final String seed;
	/**
	 * The players participating in this game.
	 */
	private final List<PlayerProxy> players = new ArrayList<PlayerProxy>(4);
	
	/**
	 * Constructs a new game init packet with the given objects and players.
	 * @param objects The game objects.
	 * @param seed The game seed.
	 * @param players The participating players.
	 */
	public PacketGameInit(List<ConvexObject> objects, String seed, List<Player> players){
		this.objects = objects;
		this.seed = seed;
		players.forEach(player->this.players.add(player.getProxy()));
	}
	
	/**
	 * Gets the game seed.
	 * @return The game seed.
	 */
	public String getSeed(){
		return seed;
	}
	
	/**
	 * Gets the objects for this game.
	 * @return The game objects.
	 */
	public List<ConvexObject> getObjects(){
		return objects;
	}
	
	/**
	 * Gets the players for this game.
	 * @return The participating players.
	 */
	public List<PlayerProxy> getPlayers(){
		return players;
	}

	@Override
	public PacketRegistry getRegisteryType(){
		return PacketRegistry.GAME_INIT;
	}
}
