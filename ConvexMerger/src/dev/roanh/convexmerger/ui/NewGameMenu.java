package dev.roanh.convexmerger.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.player.AIRegistry;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

public class NewGameMenu extends Screen{
	/**
	 * Maximum width used by the boxes.
	 */
	private static final int MAX_WIDTH = 1200;
	private PlayerPanel p1 = new PlayerPanel(PlayerTheme.P1);
	private PlayerPanel p2 = new PlayerPanel(PlayerTheme.P2);
	private PlayerPanel p3 = new PlayerPanel(PlayerTheme.P3);
	private PlayerPanel p4 = new PlayerPanel(PlayerTheme.P4);
	private Path2D start = new Path2D.Double();
	
	public NewGameMenu(ConvexMerger context){
		super(context);
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		
		g.setColor(Theme.CROWN_COLOR);
		renderMenuTitle(g, width, "New game");
		drawTitle(g, width);
		
		double size = Screen.getMaxWidth(width, 0.8D, MAX_WIDTH);
		Paint gradient = Theme.constructBorderGradient(null, width);
		double tx = (width - size) / 2.0D;
		double ty = GamePanel.TOP_SPACE + TOP_SIDE_TRIANGLE;
		
		//TODO magic
		double playersHeight = Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS * 2 + PlayerPanel.HEIGHT;
		double optionsHeight = 200.0D;
		double startHeight = 100.0D;
		
		drawTitledBox(g, gradient, tx, ty, size, playersHeight, "Players");
		drawTitledBox(g, gradient, tx, ty + playersHeight + BOX_SPACING, size, optionsHeight, "Options");

		g.setColor(Theme.MENU_BODY);
		start = computeBox(tx + (size / 3.0D), ty + playersHeight + optionsHeight + BOX_SPACING * 2, size / 3.0D, startHeight, BOX_INSETS);
		g.fill(start);
		
		double dx = (size - BOX_SPACING * 3.0D - PlayerPanel.WIDTH * 4.0D) / 2.0D;
		p1.render(g, tx + dx, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
		p2.render(g, tx + dx + PlayerPanel.WIDTH + BOX_SPACING, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
		p3.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 2.0D, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
		p4.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 3.0D, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
	}

	@Override
	public void handleMouseClick(Point2D loc, int width, int height){
		super.handleMouseClick(loc, width, height);
		p1.handleMouseClick(loc);
		p2.handleMouseClick(loc);
		p3.handleMouseClick(loc);
		p4.handleMouseClick(loc);
	}

	@Override
	public void handleKeyPressed(KeyEvent event){
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

	@Override
	protected boolean isLeftButtonEnabled(){
		return true;
	}

	@Override
	protected boolean isRightButtonEnabled(){
		return true;//TODO disable
	}

	@Override
	protected String getLeftButtonText(){
		return "Back";
	}

	@Override
	protected String getRightButtonText(){
		return "StartTMP";
	}

	@Override
	protected void handleLeftButtonClick(){
		//TODO handle back
	}

	@Override
	protected void handleRightButtonClick(){
		//TODO move to not the right button
		List<Player> players = new ArrayList<Player>();
		p1.getPlayer().ifPresent(players::add);
		p2.getPlayer().ifPresent(players::add);
		p3.getPlayer().ifPresent(players::add);
		p4.getPlayer().ifPresent(players::add);

		//TODO
		List<ConvexObject> objects = new PlayfieldGenerator().generatePlayfield();

		this.getContext().initialiseGame(new GameState(objects, players));
	}
	
	private class ButtonAssembly{
		
		
		private void render(Graphics2D g, double x, double y, Point2D mouseLoc){
			
		}
		
	}

	/**
	 * Panel to configure a new AI or human player.
	 * @author Roan
	 */
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
		private TextField name = null;
		private ComboBox<AIRegistry> ai = null;
		private Path2D remove = new Path2D.Double();
		private Image icon;
		
		private PlayerPanel(PlayerTheme theme){
			this.theme = theme;
		}
		
		private Optional<Player> getPlayer(){
			if(name != null){
				return Optional.of(new HumanPlayer(name.getText()));
			}else if(ai != null){
				return Optional.of(ai.getValue().createInstance());
			}else{
				return Optional.empty();
			}
		}
		
		private void render(Graphics2D g, double x, double y, Point2D mouseLoc){
			g.setColor(Theme.LIGHTEN);
			drawBox(g, x, y, WIDTH, HEIGHT);
			
			if(name == null && ai == null){
				renderAddButtons(g, x, y, mouseLoc);
			}else{
				y += (HEIGHT - CONTENT_HEIGHT * 2.0D - SPACING) / 2.0D;
				x += (WIDTH - CONTENT_WIDTH) / 2.0D;
				g.drawImage(icon, (int)x, (int)y, null);
				renderRemoveButton(g, x, y + CONTENT_HEIGHT + SPACING, mouseLoc);
				if(name != null){
					name.render(g, x + Theme.PLAYER_ICON_SIZE_SMALL + SPACING, y, CONTENT_WIDTH - Theme.PLAYER_ICON_SIZE_SMALL - SPACING, CONTENT_HEIGHT);
				}else if(ai != null){
					ai.render(g, x + Theme.PLAYER_ICON_SIZE_SMALL + SPACING, y, CONTENT_WIDTH - Theme.PLAYER_ICON_SIZE_SMALL - SPACING, CONTENT_HEIGHT, mouseLoc);
				}
			}
		}
		
		private void handleMouseClick(Point2D loc){
			if(name != null){
				name.handleMouseClick(loc);
			}
			
			if(name == null && ai == null){
				if(addPlayer.contains(loc)){
					icon = theme.getSmallIconHuman();
					name = new TextField(theme.getBaseOutline());
				}else if(addAI.contains(loc)){
					icon = theme.getSmallIconAI();
					ai = new ComboBox<AIRegistry>(AIRegistry.values(), AIRegistry::getName, theme.getBaseOutline());
				}
			}else{
				if(remove.contains(loc) && !(ai != null && ai.hasFocus())){
					name = null;
					ai = null;
				}
			}
			
			if(ai != null){
				ai.handleMouseClick(loc);
			}
		}
		
		private void renderRemoveButton(Graphics2D g, double x, double y, Point2D mouseLoc){
			remove = computeBox(x, y, CONTENT_WIDTH, CONTENT_HEIGHT, 5.0D);
			
			g.setFont(Theme.PRIDI_REGULAR_14);
			
			FontMetrics fm = g.getFontMetrics();
			double offset = (CONTENT_WIDTH - fm.stringWidth("Remove") -Theme.REMOVE_ICON_SIZE - SPACING) / 2.0D;
			
			g.setColor(Theme.DOUBLE_LIGHTEN);
			if(remove.contains(mouseLoc) && !(ai != null && ai.hasFocus())){
				g.fill(remove);
				g.drawImage(Theme.REMOVE_ICON_HIGHLIGHT, (int)(x + offset), (int)(y + (CONTENT_HEIGHT + 1.0D - Theme.REMOVE_ICON_SIZE) / 2.0D), null);
				g.setColor(Theme.REMOVE_BUTTON_HIGHLIGHT);
			}else{
				g.setStroke(Theme.BUTTON_STROKE);
				g.draw(remove);
				g.drawImage(Theme.REMOVE_ICON, (int)(x + offset), (int)(y + (CONTENT_HEIGHT + 1.0D - Theme.REMOVE_ICON_SIZE) / 2.0D), null);
				g.setColor(Theme.ADD_COLOR);
			}
			g.drawString("Remove", (float)(x + offset + Theme.REMOVE_ICON_SIZE + SPACING), (float)(y + CONTENT_HEIGHT - 1.0D - (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
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