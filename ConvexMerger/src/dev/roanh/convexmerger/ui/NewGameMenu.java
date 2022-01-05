package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_SIDE_TRIANGLE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

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
	public boolean render(Graphics2D g, int width, int height, Point2D mouseLoc){
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
		p1.render(g, tx + dx, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS, mouseLoc);
		p2.render(g, tx + dx + PlayerPanel.WIDTH + BOX_SPACING, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS, mouseLoc);
		p3.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 2.0D, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS, mouseLoc);
		p4.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 3.0D, ty + Menu.BOX_HEADER_HEIGHT + Menu.BOX_INSETS, mouseLoc);
		
		return true;
	}

	@Override
	public void handleMouseClick(Point2D loc){
		if(p1.name != null){
			p1.name.handleMouseClick(loc);
		}
		if(p2.name != null){
			p2.name.handleMouseClick(loc);
		}
		if(p3.name != null){
			p3.name.handleMouseClick(loc);
		}
		if(p4.name != null){
			p4.name.handleMouseClick(loc);
		}
	}

	@Override
	public void handleKeyTyped(KeyEvent event){
		if(p1.name != null && p1.name.hasFocus()){
			p1.name.handleKeyEvent(event);
		}else if(p2.name != null && p2.name.hasFocus()){
			p2.name.handleKeyEvent(event);
		}else if(p2.name != null && p3.name.hasFocus()){
			p3.name.handleKeyEvent(event);
		}else if(p4.name != null && p4.name.hasFocus()){
			p4.name.handleKeyEvent(event);
		}
	}

	private class PlayerPanel{
		private static final double WIDTH = 150.0D;
		private static final double HEIGHT = 62.0D;
		private static final double BUTTON_INSET = 4.0D;
		private static final double CONTENT_WIDTH = 134.0D;
		private static final double CONTENT_HEIGHT = 21.0D;
		private static final double SPACING = 4.0D;
		private Path2D addPlayer = null;
		private Path2D addAI = null;
		private PlayerTheme theme;
		private TextField name;//TODO = null;
		private ComboBox ai = null;
		private Path2D remove = null;
		
		private PlayerPanel(PlayerTheme theme){
			this.theme = theme;
			name = new TextField(theme.getBaseOutline());
		}
		
		private void render(Graphics2D g, double x, double y, Point2D mouseLoc){
			g.setColor(Theme.LIGHTEN);
			drawBox(g, x, y, WIDTH, HEIGHT);
			
//			renderAddButtons(g, x, y, mouseLoc);
			renderAddPlayer(g, x, y, mouseLoc);
		}
		
		
		
		
		private void renderAddPlayer(Graphics2D g, double x, double y, Point2D mouseLoc){
			y += (HEIGHT - CONTENT_HEIGHT * 2.0D - SPACING) / 2.0D;
			x += (WIDTH - CONTENT_WIDTH) / 2.0D;
			g.drawImage(theme.getSmallIconHuman(), (int)x, (int)y, null);
			name.render(g, x + Theme.PLAYER_ICON_SIZE_SMALL + SPACING, y, CONTENT_WIDTH - Theme.PLAYER_ICON_SIZE_SMALL - SPACING, CONTENT_HEIGHT);
			renderRemoveButton(g, x, y + CONTENT_HEIGHT + SPACING, mouseLoc);
		}
		
		private void renderRemoveButton(Graphics2D g, double x, double y, Point2D mouseLoc){
			Path2D box = computeBox(g, x, y, CONTENT_WIDTH, CONTENT_HEIGHT, 5.0D);
			
			g.setColor(Theme.DOUBLE_LIGHTEN);
			if(box.contains(mouseLoc)){
				g.fill(box);
			}else{
				g.setStroke(Theme.BUTTON_STROKE);
				g.draw(box);
			}
		}
		
		private void renderAddButtons(Graphics2D g, double x, double y, Point2D mouseLoc){
			double width = (WIDTH - 1.0D - 3.0D * BUTTON_INSET) / 2.0D;
			
			addPlayer = new Path2D.Double(Path2D.WIND_NON_ZERO, 6);
			addPlayer.moveTo(x + BUTTON_INSET, y + BOX_INSETS + BUTTON_INSET);
			addPlayer.lineTo(x + BUTTON_INSET + BOX_INSETS, y + BUTTON_INSET);
			addPlayer.lineTo(x + BUTTON_INSET + width, y + BUTTON_INSET);
			addPlayer.lineTo(x + BUTTON_INSET + width, y + HEIGHT - BUTTON_INSET - 1);
			addPlayer.lineTo(x + BUTTON_INSET + BOX_INSETS, y + HEIGHT - BUTTON_INSET - 1);
			addPlayer.lineTo(x + BUTTON_INSET, y + HEIGHT - BUTTON_INSET - BOX_INSETS - 1);
			addPlayer.closePath();
			
			g.setColor(Theme.DOUBLE_LIGHTEN);
			if(addPlayer.contains(mouseLoc)){
				g.fill(addPlayer);
				g.drawImage(Theme.PLAYER_ADD_HIGHLIGHT, (int)(x + BUTTON_INSET + (width - Theme.ADD_ICON_SIZE) / 2.0D), (int)(y + BUTTON_INSET + (HEIGHT - Theme.ADD_ICON_SIZE - BUTTON_INSET * 2.0D) / 2.0D), null);
			}else{
				g.drawImage(Theme.PLAYER_ADD, (int)(x + BUTTON_INSET + (width - Theme.ADD_ICON_SIZE) / 2.0D), (int)(y + BUTTON_INSET + (HEIGHT - Theme.ADD_ICON_SIZE - BUTTON_INSET * 2.0D) / 2.0D), null);
			}
			
			x += width + BUTTON_INSET * 2.0D;
			addAI = new Path2D.Double(Path2D.WIND_NON_ZERO, 6);
			addAI.moveTo(x, y + BUTTON_INSET);
			addAI.lineTo(x + width - BOX_INSETS, y + BUTTON_INSET);
			addAI.lineTo(x + width, y + BUTTON_INSET + BOX_INSETS);
			addAI.lineTo(x + width, y + HEIGHT - BUTTON_INSET - BOX_INSETS - 1);
			addAI.lineTo(x + width - BOX_INSETS, y + HEIGHT - BUTTON_INSET - 1);
			addAI.lineTo(x, y + HEIGHT - BUTTON_INSET - 1);
			addAI.closePath();
			
			if(addAI.contains(mouseLoc)){
				g.fill(addAI);
				g.drawImage(Theme.AI_ADD_HIGHLIGHT, (int)(x + (width - Theme.ADD_ICON_SIZE) / 2.0D), (int)(y + BUTTON_INSET + (HEIGHT - Theme.ADD_ICON_SIZE - BUTTON_INSET * 2.0D) / 2.0D), null);
			}else{
				g.drawImage(Theme.AI_ADD, (int)(x + (width - Theme.ADD_ICON_SIZE) / 2.0D), (int)(y + BUTTON_INSET + (HEIGHT - Theme.ADD_ICON_SIZE - BUTTON_INSET * 2.0D) / 2.0D), null);
			}
			
			g.setStroke(Theme.BUTTON_STROKE);
			g.draw(addPlayer);
			g.draw(addAI);
		}
	}
}