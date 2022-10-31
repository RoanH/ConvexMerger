package dev.roanh.convexmerger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import dev.roanh.convexmerger.game.ConjugationTree;
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
					point(150, 700)
				);
				
				
				
				ConjugationTree tree = new ConjugationTree(points);

				tree.render(g);
				
				g.setColor(Color.CYAN);
				for(Point2D p : points){
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
