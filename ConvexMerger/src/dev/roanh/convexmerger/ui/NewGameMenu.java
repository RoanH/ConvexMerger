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
	private static final double SPACING = 4.0D;
	private PlayerPanel p1 = new PlayerPanel(PlayerTheme.P1);
	private PlayerPanel p2 = new PlayerPanel(PlayerTheme.P2);
	private PlayerPanel p3 = new PlayerPanel(PlayerTheme.P3);
	private PlayerPanel p4 = new PlayerPanel(PlayerTheme.P4);
	private Path2D start = new Path2D.Double();
	private ButtonAssembly size = new ButtonAssembly(1, "Object Size", "Small", "Medium", "Large");
	private ButtonAssembly density = new ButtonAssembly(2, "Density", "Low", "Medium", "High");
	private ButtonAssembly spacing = new ButtonAssembly(0, "Spacing", "Small", "Medium", "Large");
	
	public NewGameMenu(ConvexMerger context){
		super(context);
	}
	
	private boolean canStart(){
		return p1.hasPlayer() || p2.hasPlayer() || p3.hasPlayer() || p4.hasPlayer();
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
		double optionsHeight = 100.0D;
		double startHeight = 100.0D;
		
		drawTitledBox(g, gradient, tx, ty, size, playersHeight, "Players");
		drawTitledBox(g, gradient, tx, ty + playersHeight + BOX_SPACING, size, optionsHeight, "Options");

		g.setColor(Theme.MENU_BODY);
		start = computeBox(tx + (size / 3.0D), ty + playersHeight + optionsHeight + BOX_SPACING * 2.0D, size / 3.0D, startHeight, BOX_INSETS);
		g.fill(start);
		g.setFont(Theme.PRIDI_REGULAR_18);
		g.setColor(Theme.DOUBLE_LIGHTEN);
		Path2D border = computeBox(tx + (size / 3.0D) + SPACING, ty + playersHeight + optionsHeight + BOX_SPACING * 2.0D + SPACING, size / 3.0D - SPACING * 2.0D, startHeight - SPACING * 2.0D, BOX_INSETS);
		g.draw(border);
		if(canStart() && start.contains(mouseLoc)){
			g.fill(border);
		}
		g.setColor(Theme.ADD_COLOR_HIGHLIGHT);
		FontMetrics fm = g.getFontMetrics();
		g.drawString("Start Game", (float)(tx + (size / 3.0D) + SPACING + ((size / 3.0D) - fm.stringWidth("Start Game")) / 2.0D), (float)(ty + playersHeight + optionsHeight + BOX_SPACING * 2.0D + startHeight - (startHeight - fm.getAscent() + fm.getDescent() + fm.getLeading()) / 2.0D));
		if(!canStart()){
			int offset = fm.getHeight();
			g.setFont(Theme.PRIDI_REGULAR_12);
			fm = g.getFontMetrics();
			g.setColor(Theme.ADD_COLOR);
			g.drawString("At least one player required", (float)(tx + (size / 3.0D) + SPACING + ((size / 3.0D) - fm.stringWidth("At least one player required")) / 2.0D), (float)(ty + playersHeight + optionsHeight + BOX_SPACING * 2.0D + startHeight + offset - (startHeight - fm.getAscent() + fm.getDescent() + fm.getLeading()) / 2.0D));
		}
		
		double dx = (size - BOX_SPACING * 3.0D - PlayerPanel.WIDTH * 4.0D) / 2.0D;
		p1.render(g, tx + dx, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
		p2.render(g, tx + dx + PlayerPanel.WIDTH + BOX_SPACING, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
		p3.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 2.0D, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
		p4.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 3.0D, ty + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, mouseLoc);
		
		this.size.render(g, tx, ty + playersHeight + BOX_SPACING + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, size / 3.0D, optionsHeight - Screen.BOX_HEADER_HEIGHT - Screen.BOX_INSETS, mouseLoc);
		density.render(g, tx + (size / 3.0D), ty + playersHeight + BOX_SPACING + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, size / 3.0D, optionsHeight - Screen.BOX_HEADER_HEIGHT - Screen.BOX_INSETS, mouseLoc);
		spacing.render(g, tx + ((size * 2.0D) / 3.0D), ty + playersHeight + BOX_SPACING + Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS, size / 3.0D, optionsHeight - Screen.BOX_HEADER_HEIGHT - Screen.BOX_INSETS, mouseLoc);
	}

	@Override
	public void handleMouseClick(Point2D loc, int width, int height){
		super.handleMouseClick(loc, width, height);
		p1.handleMouseClick(loc);
		p2.handleMouseClick(loc);
		p3.handleMouseClick(loc);
		p4.handleMouseClick(loc);
		size.handleMouseClick(loc);
		density.handleMouseClick(loc);
		spacing.handleMouseClick(loc);
		
		if(start.contains(loc)){
			List<Player> players = new ArrayList<Player>();
			p1.getPlayer().ifPresent(players::add);
			p2.getPlayer().ifPresent(players::add);
			p3.getPlayer().ifPresent(players::add);
			p4.getPlayer().ifPresent(players::add);

			//TODO
			PlayfieldGenerator gen = new PlayfieldGenerator();

			this.getContext().initialiseGame(new GameState(gen, players));
		}
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
		return false;
	}

	@Override
	protected String getLeftButtonText(){
		return "Back";
	}

	@Override
	protected String getRightButtonText(){
		return null;
	}

	@Override
	protected void handleLeftButtonClick(){
		//TODO handle back
	}

	@Override
	protected void handleRightButtonClick(){
	}
	
	private class ButtonAssembly{
		private static final double BUTTON_WIDTH = 80.0D;
		private static final double BUTTON_HEIGHT = 30.0D;
		private String title;
		private String[] values;
		private Path2D left;
		private Path2D middle;
		private Path2D right;
		private int selected;
		
		public ButtonAssembly(int selected, String title, String... values){
			this.selected = selected;
			this.title = title;
			this.values = values;
		}
		
		private Path2D getSelectedPath(){
			return selected == 0 ? left : (selected == 1 ? middle : right);
		}
		
		private boolean intersectsLeft(Point2D point){
			Path2D sel = getSelectedPath();
			return sel == left ? sel.contains(point) : (left.contains(point) && !middle.contains(point));
		}
		
		private boolean intersectsMiddle(Point2D point){
			Path2D sel = getSelectedPath();
			return sel == middle ? sel.contains(point) : (middle.contains(point) && !right.contains(point) && !sel.contains(point));
		}
		
		private boolean intersectsRight(Point2D point){
			Path2D sel = getSelectedPath();
			return sel == right ? sel.contains(point) : (right.contains(point) && !sel.contains(point));
		}
		
		private void render(Graphics2D g, double x, double y, double w, double h, Point2D mouseLoc){
			g.setFont(Theme.PRIDI_REGULAR_16);
			g.setColor(Theme.ADD_COLOR_HIGHLIGHT);
			FontMetrics fm = g.getFontMetrics();
			y += fm.getAscent() - fm.getDescent();
			g.drawString(title, (float)(x + (w - fm.stringWidth(title)) / 2.0D), (float)y);
			
			x += (w - BUTTON_WIDTH * 3.0D + BOX_INSETS * 4.0D) / 2.0D;
			y += BOX_SPACING;
			
			left = computeBox(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, BOX_INSETS);
			x += BUTTON_WIDTH - BOX_INSETS * 2;
			middle = computeBox(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, BOX_INSETS);
			x += BUTTON_WIDTH - BOX_INSETS * 2;
			right = computeBox(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, BOX_INSETS);
			
			g.setStroke(Theme.BORDER_STROKE);
			
			Path2D sel = getSelectedPath();
			
			if(left != sel){
				g.setColor(intersectsLeft(mouseLoc) ? Theme.DOUBLE_LIGHTEN : Theme.LIGHTEN);
				g.fill(left);
				g.setColor(Theme.DOUBLE_LIGHTEN);
				g.draw(left);
			}
			
			if(middle != sel){
				g.setColor(intersectsMiddle(mouseLoc) ? Theme.DOUBLE_LIGHTEN : Theme.LIGHTEN);
				g.fill(middle);
				g.setColor(Theme.DOUBLE_LIGHTEN);
				g.draw(middle);
			}
			
			if(right != sel){
				g.setColor(intersectsRight(mouseLoc) ? Theme.DOUBLE_LIGHTEN : Theme.LIGHTEN);
				g.fill(right);
				g.setColor(Theme.DOUBLE_LIGHTEN);
				g.draw(right);
			}
			
			g.setColor(Theme.BUTTON_SELECT);
			g.fill(sel);
			g.setColor(Theme.DOUBLE_LIGHTEN);
			g.draw(sel);
			
			g.setFont(Theme.PRIDI_REGULAR_12);
			fm = g.getFontMetrics();
			g.setColor(Theme.ADD_COLOR_HIGHLIGHT);
			
			g.drawString(values[0], (float)(x - BUTTON_WIDTH * 1.5D + BOX_INSETS * 4.0D - fm.stringWidth(values[0]) / 2.0D), (float)(y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
			g.drawString(values[1], (float)(x - BUTTON_WIDTH * 0.5D + BOX_INSETS * 2.0D - fm.stringWidth(values[1]) / 2.0D), (float)(y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
			g.drawString(values[2], (float)(x + (BUTTON_WIDTH - fm.stringWidth(values[2])) / 2.0D), (float)(y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
		}
		
		private void handleMouseClick(Point2D loc){
			if(intersectsLeft(loc)){
				selected = 0;
			}else if(intersectsMiddle(loc)){
				selected = 1;
			}else if(intersectsRight(loc)){
				selected = 2;
			}
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
		
		private boolean hasPlayer(){
			return ai != null || name != null;
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