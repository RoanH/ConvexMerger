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
package dev.roanh.convexmerger.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class KDTreeTest{
	private static final List<Point2D> testPoints = Arrays.asList(
		new Point2D.Double(400.0D, 400.0D),
		new Point2D.Double(200.0D, 200.0D),
		new Point2D.Double(100.0D, 300.0D)
	);

	@Test
	public void simpleConstruction(){
		KDTree<Void> tree = new KDTree<Void>(new ArrayList<Point2D>(testPoints));
		
		assertEquals(testPoints.get(1), tree.getPoint());
		assertEquals(testPoints.get(2), tree.getLowNode().getPoint());
		assertEquals(testPoints.get(0), tree.getHighNode().getPoint());
		
		tree.streamLeafCells().forEach(cell->{
			assertTrue(cell.getData().isEmpty());
		});
	}
}
