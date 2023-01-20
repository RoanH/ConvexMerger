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

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.PlayfieldGenerator;

public class ConjugationTreeTest{
	private static final List<Point2D> testPoints = Arrays.asList(
		new Point2D.Double(100.0D, 100.0D),
		new Point2D.Double(200.0D, 300.0D),
		new Point2D.Double(300.0D, 400.0D),
		new Point2D.Double(400.0D, 200.0D),
		new Point2D.Double(500.0D, 500.0D),
		new Point2D.Double(600.0D, 600.0D),
		new Point2D.Double(150.0D, 700.0D),
		new Point2D.Double(800.0D, 200.0D),
		new Point2D.Double(750.0D, 450.0D),
		new Point2D.Double(300.0D, 800.0D),
		new Point2D.Double(1000.0D, 300.0D),
		new Point2D.Double(550.0D, 550.0D),
		new Point2D.Double(990.0D, 440.0D),
		new Point2D.Double(1100.0D, 480.0D)
	);
	
	@Test
	public void constructionPoints(){
		ConjugationTree<Void> tree = new ConjugationTree<Void>(testPoints);
		List<ConjugationTree<Void>> leaves = tree.streamLeafCells().collect(Collectors.toList());

		assertEquals(31L, tree.streamCells().count());
		assertEquals(16, leaves.size());
		leaves.forEach(leaf->assertEquals(4, leaf.getDepth()));
		
		testTree(tree);
	}
	
	@Test
	@Timeout(1)
	public void colinTest(){
		SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(Arrays.asList(
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(0.0D, 0.0D),
				new Point2D.Double(0.0D, 10.0D),
				new Point2D.Double(10.0D, 10.0D),
				new Point2D.Double(10.0D, 0.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT),
				new Point2D.Double(Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT - 10.0D),
				new Point2D.Double(Constants.PLAYFIELD_WIDTH - 10.0D, Constants.PLAYFIELD_HEIGHT - 10.0D),
				new Point2D.Double(Constants.PLAYFIELD_WIDTH - 10.0D, Constants.PLAYFIELD_HEIGHT)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(Constants.PLAYFIELD_WIDTH, 0.0D),
				new Point2D.Double(Constants.PLAYFIELD_WIDTH, 10.0D),
				new Point2D.Double(Constants.PLAYFIELD_WIDTH - 10.0D, 10.0D),
				new Point2D.Double(Constants.PLAYFIELD_WIDTH - 10.0D, 0.0D)
			))),
			new ConvexObject(ConvexUtil.computeConvexHull(Arrays.asList(
				new Point2D.Double(0.0D, Constants.PLAYFIELD_HEIGHT),
				new Point2D.Double(0.0D, Constants.PLAYFIELD_HEIGHT - 10.0D),
				new Point2D.Double(10.0D, Constants.PLAYFIELD_HEIGHT - 10.0D),
				new Point2D.Double(10.0D, Constants.PLAYFIELD_HEIGHT)
			)))
		));
	}
	
	@Test
	@Timeout(1)
	public void conjugationComputationTest0(){
		testConstructionSeed("3ZGRJD43F20COCERMV59");
	}
	
	@Test
	@Timeout(1)
	public void conjugationComputationTest1(){
		testConstructionSeed("3ZGRJD42GZYECMFRN0NQ");
	}

	@Test
	@Timeout(1)
	public void conjugationComputationTest2(){
		testConstructionSeed("3ZGRJD4163DXEYWINF8G");
	}
	
	@Test
	@Timeout(1)
	public void conjugationComputationTest3(){
		testConstructionSeed("3ZGRJD42I9EX87S8Y04P");
	}
	
	@Test
	@Timeout(1)
	public void conjugationComputationTest4(){
		testConstructionSeed("3ZGRJD41OQ741AD949KW");
	}
	
	@Test
	@Timeout(1)
	public void conjugationComputationTest5(){
		testConstructionSeed("3ZGRJD43QZ02Q4C61DYX");
	}
	
	@Test
	@Timeout(1)
	public void conjugationComputationTest6(){
		testConstructionSeed("3ZGRJD433ZR2AVHZ7Y7O");
	}
	
	@Test
	@Timeout(1)
	public void conjugationComputationTest7(){
		testConstructionSeed("3ZGRJD40WD57FXXT815Q");
	}
	
	private void testConstructionSeed(String seed){
		testTree(new ConjugationTree<Void>(new PlayfieldGenerator(seed).generatePlayfield().stream().flatMap(obj->{
			return obj.getPoints().stream();
		}).collect(Collectors.toList())));
	}
	
	private void testTree(ConjugationTree<?> tree){
		List<ConjugationTree<?>> leaves = tree.streamLeafCells().collect(Collectors.toList());
		
		for(ConjugationTree<?> leaf : leaves){
			//all leaves have no bisector and thus no points
			assertEquals(0, leaf.getPoints().size());
			assertNull(leaf.getBisector());
		}
		
		//all internal nodes have exactly one point
		Deque<ConjugationTree<?>> nodes = new LinkedList<ConjugationTree<?>>();
		while(!nodes.isEmpty()){
			ConjugationTree<?> node = nodes.pop();
			if(!node.isLeafCell()){
				assertEquals(1, node.getPoints().size());
			}
		}
		
		//assert that all bisectors are also conjugates
		tree.streamCells().forEach(cell->{
			if(cell.getDepth() > 0 && !cell.isLeafCell()){
				assertNotNull(ConvexUtil.interceptClosed(cell.getBisector(), cell.getParent().getBisector()));
			}
		});
		
		//the root is at depth 0
		assertEquals(0, tree.getDepth());
	}
}