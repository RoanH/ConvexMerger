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
package dev.roanh.convexmerger.player;

import java.util.Comparator;
import java.util.Optional;

import dev.roanh.convexmerger.game.ConvexObject;

/**
 * AI that focuses on maximising local area gain
 * starting from small objects.
 * @author Roan
 */
public class SmallPlayer extends LocalPlayer{

	/**
	 * Constructs a new small player (Shiro).
	 */
	public SmallPlayer(){
		super("Shiro");
	}
	
	@Override
	protected boolean claimNewObject(){
		Optional<ConvexObject> obj = state.stream().filter(ConvexObject::canClaim).sorted(Comparator.comparingDouble(ConvexObject::getArea)).filter(this::hasMergeFrom).findFirst();
		if(obj.isPresent()){
			target = state.claimObject(obj.get()).getResult();
			return true;
		}else{
			return super.claimNewObject();
		}
	}
}
