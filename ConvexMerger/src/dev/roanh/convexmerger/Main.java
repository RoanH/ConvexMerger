package dev.roanh.convexmerger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
//		game.switchScene(getTestScreen(game));
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
				
//				points = Arrays.asList(
//					new Point2D.Double(100.0D, 300.0D),
//					new Point2D.Double(200.0D, 200.0D),
//					new Point2D.Double(400.0D, 400.0D),
//					new Point2D.Double(600.0D, 600.0D),
//					new Point2D.Double(800.0D, 800.0D),
//					new Point2D.Double(1000.0D, 800.0D),
//					new Point2D.Double(900.0D, 700.0D)
//				);
				
				
				ConjugationTree<LineSegment> tree = new ConjugationTree<LineSegment>(points);
				
//				PlayfieldGenerator gen = new PlayfieldGenerator("3Y64YRJ35TVADL3BXU2V");
//				tree = new ConjugationTree<LineSegment>(gen.generatePlayfield().stream().flatMap(obj->obj.getPoints().stream()).collect(Collectors.toList()));

//				LineSegment query = new LineSegment(new Point2D.Double(1000, 600), new Point2D.Double(300, 100));
				
//				System.out.println("====");
//				SegmentPartitionTree.conjugationTreeVisitor(tree, query);
				
				SegmentPartitionTree<?> stree = SegmentPartitionTree.TYPE_KD_TREE.fromPoints(points);
				stree.addSegment(new Point2D.Double(100.0D, 300.0D), new Point2D.Double(900.0D, 700.0D));
				
//				SegmentPartitionTree<?> stree = SegmentPartitionTree.TYPE_KD_TREE.fromObjects(
//					new PlayfieldGenerator("3Y64QTK1WI14ZQ79GFMW").generatePlayfield()
//				);
				
				g.translate(200, 50);
				stree.render(g);
				
				g.setColor(Color.MAGENTA);
				g.drawLine(0, 0, 0, Constants.PLAYFIELD_HEIGHT);
				g.drawLine(0, 0, Constants.PLAYFIELD_WIDTH, 0);
				g.drawLine(0, Constants.PLAYFIELD_HEIGHT, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
				g.drawLine(Constants.PLAYFIELD_WIDTH, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
				
//				g.setColor(Color.CYAN);
//				for(Point2D p : points){
//					g.fill(new Ellipse2D.Double(p.getX() - 3, p.getY() - 3, 6, 6));
//				}
				
				g.setColor(Color.RED);
//				g.draw(query);
				g.draw(new Line2D.Double(444.31914618102513D, 194.85131929075814D, 628.4039710577933D, 331.7503860849795D));
				g.setColor(Color.GREEN);
				g.draw(new Line2D.Double(514.6784789976089D, 402.33862253612847D, 350.20149757949315D, 306.22387013590435D));
				
				g.setColor(Color.CYAN);
				for(Point2D p : new Point2D[]{point(1100, 480), point(800, 200)}){
					g.fill(new Ellipse2D.Double(p.getX() - 3, p.getY() - 3, 6, 6));
				}
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
