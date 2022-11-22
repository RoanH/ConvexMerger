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
package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.roanh.convexmerger.game.SegmentPartitionTree.LineSegment;
import dev.roanh.convexmerger.game.SegmentPartitionTree.SegmentPartitionTreeConstructor;

public class SegmentPartitionTreeTest{
	private static final List<Point2D> testPoints = Arrays.asList(
		new Point2D.Double(100.0D, 300.0D),
		new Point2D.Double(200.0D, 200.0D),
		new Point2D.Double(400.0D, 400.0D),
		new Point2D.Double(600.0D, 600.0D),
		new Point2D.Double(800.0D, 800.0D),
		new Point2D.Double(1000.0D, 800.0D),
		new Point2D.Double(900.0D, 700.0D)
	);
	
	@Test
	public void intersectTestKD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(5));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void noIntersectTestKD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(4), testPoints.get(5));
		assertFalse(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void edgeIntersectTestKD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(3), testPoints.get(5));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void edgeCaseSeed0KD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromObjects(
			new PlayfieldGenerator("3Y657EW3LVKQ9LHX178Z").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(133.692809343338D, 690.875816822052D, 107.05947849314808D, 518.3026268080998D)));
		assertFalse(tree.intersects(new Line2D.Double(238.82418653529285D, 617.9104715780545D, 249.7712426185608D, 668.1307184100151D)));
	}
	
	@Test
	public void simpleIntersectionTestKD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(6));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(3)));
	}
	
	@Test
	public void overlapIsNotIntersectionTestKD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(6));
		assertFalse(tree.intersects(testPoints.get(0), testPoints.get(3)));
	}
	
	@Test
	public void edgeCaseSeed1KD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromObjects(
			new PlayfieldGenerator("3Y64QTK1WI14ZQ79GFMW").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(444.31914618102513D, 194.85131929075814D, 628.4039710577933D, 331.7503860849795D)));
		assertFalse(tree.intersects(new Line2D.Double(514.6784789976089D, 402.33862253612847D, 350.20149757949315D, 306.22387013590435D)));
	}
	
	@Test
	public void edgeCaseSeed2KD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromObjects(
			new PlayfieldGenerator("3Y64YQ018986HXCC9DZ3").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(193.6013062596321D, 640.6928106546402D, 323.98344983777235D, 485.96471945056874D)));
		assertFalse(tree.intersects(new Line2D.Double(465.15992274007033D, 562.0431520701404D, 257.9150328040123D, 667.3594777584076D)));
	}
	
	@Test
	public void edgeCaseSeed3KD(){
		SegmentPartitionTree<KDTree<LineSegment>> tree = SegmentPartitionTree.TYPE_KD_TREE.fromObjects(
			new PlayfieldGenerator("3Y64YQ00FZCAU04RTVFN").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(816.1023556884412D, 693.7616905532058D, 1105.7172609057357D, 768.9144205150844D)));
	}
	
	@Test
	public void intersectTestConj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(5));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void noIntersectTestConj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(4), testPoints.get(5));
		assertFalse(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void edgeIntersectTestConj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(3), testPoints.get(5));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(4)));
	}
	
	@Test
	public void edgeCaseSeed0Conj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(
			new PlayfieldGenerator("3Y657EW3LVKQ9LHX178Z").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(133.692809343338D, 690.875816822052D, 107.05947849314808D, 518.3026268080998D)));
		assertFalse(tree.intersects(new Line2D.Double(238.82418653529285D, 617.9104715780545D, 249.7712426185608D, 668.1307184100151D)));
	}
	
	@Test
	public void simpleIntersectionTestConj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(6));
		assertTrue(tree.intersects(testPoints.get(2), testPoints.get(3)));
	}
	
	@Test
	public void overlapIsNotIntersectionTestConj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromPoints(testPoints);
		tree.addSegment(testPoints.get(0), testPoints.get(6));
		assertFalse(tree.intersects(testPoints.get(0), testPoints.get(3)));
	}
	
	@Test
	public void edgeCaseSeed1Conj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(
			new PlayfieldGenerator("3Y64QTK1WI14ZQ79GFMW").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(444.31914618102513D, 194.85131929075814D, 628.4039710577933D, 331.7503860849795D)));
		assertFalse(tree.intersects(new Line2D.Double(514.6784789976089D, 402.33862253612847D, 350.20149757949315D, 306.22387013590435D)));
	}
	
	@Test
	public void edgeCaseSeed2Conj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(
			new PlayfieldGenerator("3Y64YQ018986HXCC9DZ3").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(193.6013062596321D, 640.6928106546402D, 323.98344983777235D, 485.96471945056874D)));
		assertFalse(tree.intersects(new Line2D.Double(465.15992274007033D, 562.0431520701404D, 257.9150328040123D, 667.3594777584076D)));
	}
	
	@Test
	public void edgeCaseSeed3Conj(){
		SegmentPartitionTree<ConjugationTree<LineSegment>> tree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(
			new PlayfieldGenerator("3Y64YQ00FZCAU04RTVFN").generatePlayfield()
		);
		assertTrue(tree.intersects(new Line2D.Double(816.1023556884412D, 693.7616905532058D, 1105.7172609057357D, 768.9144205150844D)));
	}
	
	@Test
	public void randomTestKD(){
		testAll(SegmentPartitionTree.TYPE_KD_TREE, new PlayfieldGenerator());
	}
	
	@Test
	public void randomTestConj(){
		testAll(SegmentPartitionTree.TYPE_CONJUGATION_TREE, new PlayfieldGenerator());
	}
	
	private void testAll(SegmentPartitionTreeConstructor<?> ctor, PlayfieldGenerator gen){
		List<ConvexObject> objects = gen.generatePlayfield();
		SegmentPartitionTree<?> tree = ctor.fromObjects(objects);
		for(int i = 0; i < objects.size(); i++){
			objects.get(i).setID(i + 1);
		}
		
		for(ConvexObject obj1 : objects){
			for(ConvexObject obj2 : objects){
				if(obj1 != obj2){
					Point2D[] merge = ConvexUtil.computeMergeLines(obj1.getPoints(), obj2.getPoints());
					
					ConvexObject intersected = objects.stream().filter(obj->obj != obj1 && obj != obj2).filter(obj->obj.intersects(merge[0], merge[1])).findAny().orElse(null);
					assertEquals(
						intersected != null,
						tree.intersects(merge[0], merge[1]),
						"obj " + obj1.getID() + " to " + obj2.getID() + " with seed " + gen.getSeed()
						+ " and line " + merge[0] + " to " + merge[1] + (intersected != null ? " intersects " + intersected.getID() : "")
					);
					
					intersected = objects.stream().filter(obj->obj != obj1 && obj != obj2).filter(obj->obj.intersects(merge[2], merge[3])).findAny().orElse(null);
					assertEquals(
						intersected != null,
						tree.intersects(merge[2], merge[3]),
						"obj " + obj1.getID() + " to " + obj2.getID() + " with seed " + gen.getSeed()
						+ " and line " + merge[2] + " to " + merge[3] + (intersected != null ? " intersects " + intersected.getID() : "")
					);
				}
			}
		}
	}
}
