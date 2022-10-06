package dev.roanh.convexmerger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import dev.roanh.convexmerger.game.KDTree;
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
		game.switchScene(new Screen(game){
			private KDTree tree;
			
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
				tree = new KDTree(points);
				
				g.setColor(Color.GRAY);
				for(Point2D point : points){
					g.fill(new Ellipse2D.Double(point.getX() - 5.0D, point.getY() - 5.0D, 10.0D, 10.0D));	
				}
				
				tree.render(g);
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
		});
		game.showGame();
	}
}
