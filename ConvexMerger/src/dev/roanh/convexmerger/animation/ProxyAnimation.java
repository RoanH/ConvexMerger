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
package dev.roanh.convexmerger.animation;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.roanh.convexmerger.game.RenderableObject;

public class ProxyAnimation extends Animation{
	private List<RenderableObject> objects;
	
	public ProxyAnimation(RenderableObject... objs){
		objects = Arrays.asList(objs);
	}
	
	public ProxyAnimation(RenderableObject obj1, RenderableObject obj2, List<? extends RenderableObject> other){
		objects = new ArrayList<RenderableObject>(2 + other.size());
		objects.add(obj1);
		objects.add(obj2);
		objects.addAll(other);
	}

	@Override
	protected boolean render(Graphics2D g){
		for(RenderableObject obj : objects){
			obj.renderOrAnimate(g);
		}

		return true;
	}
}
