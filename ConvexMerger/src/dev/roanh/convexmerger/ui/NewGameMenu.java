package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_SIDE_TRIANGLE;

import java.awt.Graphics2D;
import java.awt.Paint;

public class NewGameMenu implements Menu{
	/**
	 * Maximum width used by the boxes.
	 */
	private static final int MAX_WIDTH = 1200;
	
	
	
	
	

	@Override
	public boolean render(Graphics2D g, int width, int height){
		renderMenuTitle(g, width, "New game");
		drawTitle(g, width);
		
		double size = Menu.getMaxWidth(width, 0.9D, MAX_WIDTH);
		Paint gradient = Theme.constructBorderGradient(null, width);
		g.translate((width - size) / 2.0D, GamePanel.TOP_SPACE + TOP_SIDE_TRIANGLE);
		
		//TODO magic
		double playersHeight = 200.0D;
		double optionsHeight = 200.0D;
		double startHeight = 100.0D;
		
		drawTitledBox(g, gradient, 0.0D, 0.0D, size, playersHeight, "Players");
		drawTitledBox(g, gradient, 0.0D, playersHeight + BOX_SPACING, size, optionsHeight, "Options");

		drawBox(g, size / 3.0D, playersHeight + optionsHeight + BOX_SPACING * 2, size / 3.0D, startHeight);
		
		
		
		
		
		
		
		return true;
	}

	
}