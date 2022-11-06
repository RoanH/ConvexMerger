package dev.roanh.convexmerger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import dev.roanh.convexmerger.game.ConjugationTree;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.game.SegmentPartitionTree;
import dev.roanh.convexmerger.game.SegmentPartitionTree.LineSegment;
import dev.roanh.convexmerger.ui.ConvexMerger;
import dev.roanh.convexmerger.ui.Screen;
import dev.roanh.util.Util;

/**
 * Main entry point for the application.
 * @author Roan
 */
public class Main{

	/**
	 * Main subroutine that starts the game.
	 * @param args No valid command line arguments.
	 */
	public static void main(String[] args){
		Util.installUI();
		
		ConvexMerger game = new ConvexMerger();
		game.switchScene(getTestScreen(game));
		game.showGame();
	}

	private static Screen getTestScreen(ConvexMerger cm){
		return new Screen(cm){
			
			@Override
			protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
				List<Point2D> points = Arrays.asList(
					point(100, 100),
					point(200, 300),
					point(300, 400),
					point(400, 200),
					point(500, 500),
					point(600, 600),
					point(150, 700),
					point(800, 200),
					point(750, 450),
					point(300, 800),
					point(1000, 300),
					point(550, 550),
					point(990, 440),
					point(1100, 480)
				);
				
				ConjugationTree<LineSegment> tree = new ConjugationTree<LineSegment>(points);
				
//				PlayfieldGenerator gen = new PlayfieldGenerator("3Y64YRJ35TVADL3BXU2V");
//				tree = new ConjugationTree<LineSegment>(gen.generatePlayfield().stream().flatMap(obj->obj.getPoints().stream()).collect(Collectors.toList()));

//				SegmentPartitionTree<?> stree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(points);
//				stree.addSegment(new Point2D.Double(100.0D, 300.0D), new Point2D.Double(900.0D, 700.0D));
				
				System.out.println("===");
				SegmentPartitionTree<?> stree = SegmentPartitionTree.TYPE_CONJUGATION_TREE.fromObjects(
					new PlayfieldGenerator("3Y64YQ00FZCAU04RTVFN").generatePlayfield()
				);
				stree.intersects(new Line2D.Double(816.1023556884412D, 693.7616905532058D, 1105.7172609057357D, 768.9144205150844D));
				
				g.translate(200, 50);
				stree.render(g);
				
				g.setColor(Color.RED);
				g.draw(new Line2D.Double(816.1023556884412D, 693.7616905532058D, 1105.7172609057357D, 768.9144205150844D));
				
				g.setColor(Color.MAGENTA);
				g.drawLine(0, 0, 0, Constants.PLAYFIELD_HEIGHT);
				g.drawLine(0, 0, Constants.PLAYFIELD_WIDTH, 0);
				g.drawLine(0, Constants.PLAYFIELD_HEIGHT, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
				g.drawLine(Constants.PLAYFIELD_WIDTH, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			}
			
			@Override
			protected boolean isRightButtonEnabled(){
				return false;
			}
			
			@Override
			protected boolean isLeftButtonEnabled(){
				return false;
			}
			
			@Override
			protected void handleRightButtonClick(){
			}
			
			@Override
			protected void handleLeftButtonClick(){
			}
			
			@Override
			protected String getRightButtonText(){
				return null;
			}
			
			@Override
			protected String getLeftButtonText(){
				return null;
			}
		};
	}
	
	private static final Point2D point(double x, double y){
		return new Point2D.Double(x, y);
	}
}
