package dev.roanh.convexmerger.game;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

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
		ConjugationTree<Void> tree = new ConjugationTree<>(testPoints);
		
		List<ConjugationTree<Void>> leaves = tree.streamLeafCells().collect(Collectors.toList());
		assertEquals(8, leaves.size());
		
		boolean empty = false;
		for(ConjugationTree<Void> leaf : leaves){
			//all leaves have one point except one that has none
			assertTrue(leaf.getPoints().size() <= 1);
			if(empty){
				assertEquals(1, leaf.getPoints().size());
			}
			empty |= leaf.getPoints().isEmpty();
			
			//all leaves are at depth 3
			assertEquals(3, leaf.depth());
		}
		
		//all internal nodes have exactly one point
		Deque<ConjugationTree<Void>> nodes = new LinkedList<ConjugationTree<Void>>();
		while(!nodes.isEmpty()){
			ConjugationTree<Void> node = nodes.pop();
			if(!node.isLeafCell()){
				assertEquals(1, node.getPoints().size());
			}
		}
		
		//the root is at depth 0
		assertEquals(0, tree.depth());
	}
	
	@Test
	public void constructionConjugates(){
		ConjugationTree<Void> tree = new ConjugationTree<Void>(testPoints);

		assertEquals(15L, tree.streamCells().count());
		
		//assert that all bisectors are also conjugates
		tree.streamCells().forEach(cell->{
			if(cell.depth() > 0){
				System.out.println(cell.depth());
				assertNotNull(ConvexUtil.interceptClosed(cell.getBisector(), cell.getParent().getBisector()));
			}
		});
	}
}
