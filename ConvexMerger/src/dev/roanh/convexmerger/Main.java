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
				
//				PlayfieldGenerator gen = new PlayfieldGenerator();
//				tree = new ConjugationTree<Void>(gen.generatePlayfield().stream().flatMap(obj->obj.getPoints().stream()).collect(Collectors.toList()));

				LineSegment query = new LineSegment(new Point2D.Double(1000, 600), new Point2D.Double(800, 100));
				
				
				g.translate(200, 50);
				tree.render(g);
				
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
				g.draw(query);
				
				System.out.println("====");
				SegmentPartitionTree.conjugationTreeVisitor(tree, query);
				
				g.setColor(Color.CYAN);
				for(Point2D p : new Point2D[]{point(1100, 480), point(800, 200)}){
					g.fill(new Ellipse2D.Double(p.getX() - 3, p.getY() - 3, 6, 6));
				}
			}
			
			@Override
			protected boolean isRightButtonEnabled(){
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected boolean isLeftButtonEnabled(){
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected void handleRightButtonClick(){
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void handleLeftButtonClick(){
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected String getRightButtonText(){
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected String getLeftButtonText(){
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	private static final Point2D point(double x, double y){
		return new Point2D.Double(x, y);
	}
}
