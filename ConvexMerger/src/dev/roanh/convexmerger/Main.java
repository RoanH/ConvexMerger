package dev.roanh.convexmerger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import dev.roanh.convexmerger.game.SegmentPartitionTree;
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
		Screen test = new Screen(game){
			
			@Override
			protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
				List<Point2D> points = Arrays.asList(
					new Point2D.Double(100.0D, 300.0D),
					new Point2D.Double(200.0D, 200.0D),
					new Point2D.Double(400.0D, 400.0D),
					new Point2D.Double(600.0D, 600.0D),
					new Point2D.Double(800.0D, 800.0D),
					new Point2D.Double(1000.0D, 800.0D),
					new Point2D.Double(900.0D, 700.0D)
				);
				SegmentPartitionTree tree = SegmentPartitionTree.fromPoints(points);
				Line2D line = new Line2D.Double(points.get(0), points.get(5));
				tree.addSegment(line);
				
				Line2D query = new Line2D.Double(points.get(2), points.get(4));
				
				g.setColor(Color.GRAY);
				for(Point2D point : points){
					g.fill(new Ellipse2D.Double(point.getX() - 5.0D, point.getY() - 5.0D, 10.0D, 10.0D));	
				}
				g.draw(line);
				g.setColor(Color.MAGENTA);
				g.draw(query);
				
				System.out.println(tree.intersects(query));
				
				tree.render(g);
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
		game.switchScene(test);
		game.showGame();
	}
}
