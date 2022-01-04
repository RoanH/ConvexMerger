package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_SIDE_TRIANGLE;

import java.awt.Graphics2D;
import java.awt.Paint;

import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

public class NewGameMenu implements Menu{
	/**
	 * Maximum width used by the boxes.
	 */
	private static final int MAX_WIDTH = 1200;
	private PlayerPanel p1 = new PlayerPanel(PlayerTheme.P1);
	private PlayerPanel p2 = new PlayerPanel(PlayerTheme.P2);
	private PlayerPanel p3 = new PlayerPanel(PlayerTheme.P3);
	private PlayerPanel p4 = new PlayerPanel(PlayerTheme.P4);
	
	
	
	

	@Override
	public boolean render(Graphics2D g, int width, int height){
		renderMenuTitle(g, width, "New game");
		drawTitle(g, width);
		
		double size = Menu.getMaxWidth(width, 0.8D, MAX_WIDTH);
		Paint gradient = Theme.constructBorderGradient(null, width);
		double tx = (width - size) / 2.0D;
		double ty = GamePanel.TOP_SPACE + TOP_SIDE_TRIANGLE;
		
		//TODO magic
		double playersHeight = Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS * 2 + PlayerPanel.HEIGHT;
		double optionsHeight = 200.0D;
		double startHeight = 100.0D;
		
		drawTitledBox(g, gradient, tx, ty, size, playersHeight, "Players");
		drawTitledBox(g, gradient, tx, ty + playersHeight + BOX_SPACING, size, optionsHeight, "Options");

		g.setColor(Theme.MENU_BODY);
		drawBox(g, tx + (size / 3.0D), ty + playersHeight + optionsHeight + BOX_SPACING * 2, size / 3.0D, startHeight);
		
		double dx = (size - BOX_SPACING * 3.0D - PlayerPanel.WIDTH * 4.0D) / 2.0D;
		p1.render(g, tx + dx, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS);
		p2.render(g, tx + dx + PlayerPanel.WIDTH + BOX_SPACING, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS);
		p3.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 2.0D, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS);
		p4.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 3.0D, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS);
		
		return true;
	}

	private class PlayerPanel{
		private static final double WIDTH = 150.0D;
		private static final double HEIGHT = 62.0D;
		private PlayerTheme theme;
		
		private PlayerPanel(PlayerTheme theme){
			this.theme = theme;
		}
		
		private void render(Graphics2D g, double x, double y){
			g.setColor(Theme.LIGHTEN);
			drawBox(g, x, y, WIDTH, HEIGHT);
		}
		
	}
}