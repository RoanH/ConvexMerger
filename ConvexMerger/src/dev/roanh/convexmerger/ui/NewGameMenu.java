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
import java.util.Locale;
import java.util.Optional;
import java.util.function.IntConsumer;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.game.PlayfieldGenerator.GeneratorProgressListener;
import dev.roanh.convexmerger.player.AIRegistry;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.TextField.FocusLossListener;
import dev.roanh.convexmerger.ui.TextField.TextChangeListener;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Menu used to configure and start a new game.
 * @author Roan
 */
public class NewGameMenu extends Screen implements GeneratorProgressListener, TextChangeListener, FocusLossListener{
	/**
	 * Maximum width used by the boxes.
	 */
	private static final int MAX_WIDTH = 1200;
	/**
	 * Height of the start button.
	 */
	private static final double START_HEIGHT = 100.0D;
	/**
	 * Raw minimum range values associated with the object size buttons.
	 */
	private static final int[] RANGE_VALUES_MIN = new int[]{10, 0, 50};
	/**
	 * Raw maximum range values associated with the object size buttons.
	 */
	private static final int[] RANGE_VALUES_MAX = new int[]{20, 100, 100};
	/**
	 * Raw coverage values associated with the density buttons.
	 */
	private static final int[] COVERAGE_VALUES = new int[]{30, 70, 114};
	/**
	 * Raw scaling values associated with the spacing buttons.
	 */
	private static final int[] SCALING_VALUES = new int[]{240, 200, 100};
	/**
	 * Configuration for player 1.
	 */
	protected PlayerPanel p1 = new PlayerPanel(PlayerTheme.P1);
	/**
	 * Configuration for player 2.
	 */
	protected PlayerPanel p2 = new PlayerPanel(PlayerTheme.P2);
	/**
	 * Configuration for player 3.
	 */
	protected PlayerPanel p3 = new PlayerPanel(PlayerTheme.P3);
	/**
	 * Configuration for player 4.
	 */
	protected PlayerPanel p4 = new PlayerPanel(PlayerTheme.P4);
	/**
	 * Bounds of the start button.
	 */
	private Path2D start = new Path2D.Double();
	/**
	 * Configuration for the playfield object size.
	 */
	private ButtonAssembly size = new ButtonAssembly(1, "Object Size", "Small", "Medium", "Large");
	/**
	 * Configuration for the playfield density.
	 */
	private ButtonAssembly density = new ButtonAssembly(1, "Density", "Low", "Medium", "High");
	/**
	 * Configuration for the playfield spacing.
	 */
	private ButtonAssembly spacing = new ButtonAssembly(1, "Spacing", "Small", "Medium", "Large");
	/**
	 * Playfield generation progress (0~1).
	 */
	private double progress = 0.0D;
	/**
	 * Whether the start button was clicked.
	 */
	private boolean started = false;
	/**
	 * Whether to show the vertical decomposition
	 * from the start of the game.
	 */
	protected boolean showDecomp = false;
	/**
	 * Text field showing the game seed.
	 */
	private TextField seed = new TextField(PlayerTheme.P1.getBaseOutline());
	/**
	 * The playfield generator being configured.
	 */
	private PlayfieldGenerator gen = new PlayfieldGenerator();
	
	/**
	 * Constructs a new new game menu with the given game context.
	 * @param context The game context.
	 */
	public NewGameMenu(ConvexMerger context){
		super(context);
		
		seed.setText(gen.getSeed());
		seed.setForegroundColor(PlayerTheme.P3.getBaseOutline());
		seed.setChangeListener(this);
		seed.setFocusListener(this);
		seed.setCentred(true);
		
		gen.setProgressListener(this);
		size.setChangeListener(i->{
			gen.setRange(RANGE_VALUES_MIN[i], RANGE_VALUES_MAX[i]);
			seed.setForegroundColor(PlayerTheme.P3.getBaseOutline());
		});
		density.setChangeListener(i->{
			gen.setCoverage(COVERAGE_VALUES[i]);
			seed.setForegroundColor(PlayerTheme.P3.getBaseOutline());
		});
		spacing.setChangeListener(i->{
			gen.setScaling(SCALING_VALUES[i]);
			seed.setForegroundColor(PlayerTheme.P3.getBaseOutline());
		});
	}
	
	/**
	 * Resets this menu keeping the selected options
	 * but resetting that the game was started.
	 */
	public void reset(){
		started = false;
		progress = 0.0D;
	}
	
	/**
	 * Checks if enough players are configured to start (at least 1).
	 * @return True if enough players have been configured to start.
	 */
	protected boolean canStart(){
		return p1.hasPlayer() || p2.hasPlayer() || p3.hasPlayer() || p4.hasPlayer();
	}
	
	/**
	 * Gets the status message to display on the start button.
	 * @return The status message to display.
	 */
	protected String getButtonMessage(){
		if(started){
			return String.format("Generating game: %.1f%%", progress * 100.0D);
		}else if(!canStart()){
			return "At least one player required";
		}else{
			return "Will show vertical decomposition construction";
		}
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		renderMenuTitle(g, width, getMenuTitle());
		drawTitle(g, width);
		
		double size = Screen.getMaxWidth(width, 0.8D, MAX_WIDTH);
		Paint gradient = Theme.constructBorderGradient(null, width);
		double tx = (width - size) / 2.0D;
		double ty = Screen.TOP_SPACE + TOP_SIDE_TRIANGLE;
		
		double playersHeight = Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS * 2.0D + PlayerPanel.HEIGHT;
		FontMetrics fm = g.getFontMetrics(Theme.PRIDI_REGULAR_16);
		double optionsHeight = Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS + fm.getAscent() * 2.0D - fm.getDescent() * 2.0D + BOX_SPACING * 3.0D + PlayerPanel.CONTENT_HEIGHT + ButtonAssembly.BUTTON_HEIGHT + g.getFontMetrics(Theme.PRIDI_REGULAR_14).getAscent();
		
		drawTitledBox(g, gradient, tx, ty, size, playersHeight, "Players");
		drawTitledBox(g, gradient, tx, ty + playersHeight + BOX_SPACING, size, optionsHeight, "Options");

		start = drawButton(g, "Start Game", tx + (size / 3.0D), ty + playersHeight + optionsHeight + BOX_SPACING * 2.0D, (size / 3.0D), START_HEIGHT, canStart() ? mouseLoc : null);
		if(!canStart() || started || showDecomp){
			int offset = g.getFontMetrics(Theme.PRIDI_REGULAR_18).getHeight();
			g.setFont(Theme.PRIDI_REGULAR_12);
			fm = g.getFontMetrics();
			g.setColor(Theme.ADD_COLOR);
			String text = getButtonMessage();
			g.drawString(text, (float)(tx + (size / 3.0D) + SPACING + ((size / 3.0D) - fm.stringWidth(text)) / 2.0D), (float)(ty + playersHeight + optionsHeight + BOX_SPACING * 2.0D + START_HEIGHT + offset - (START_HEIGHT - fm.getAscent() + fm.getDescent() + fm.getLeading()) / 2.0D));
		}
		
		double dx = (size - BOX_SPACING * 3.0D - PlayerPanel.WIDTH * 4.0D) / 2.0D;
		ty += Screen.BOX_HEADER_HEIGHT + Screen.BOX_INSETS;
		p1.render(g, tx + dx, ty, mouseLoc);
		p2.render(g, tx + dx + PlayerPanel.WIDTH + BOX_SPACING, ty, mouseLoc);
		p3.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 2.0D, ty, mouseLoc);
		p4.render(g, tx + dx + (PlayerPanel.WIDTH + BOX_SPACING) * 3.0D, ty, mouseLoc);
		
		ty += playersHeight + BOX_SPACING;
		this.size.render(g, tx, ty, size / 3.0D, mouseLoc);
		density.render(g, tx + (size / 3.0D), ty, size / 3.0D, mouseLoc);
		spacing.render(g, tx + ((size * 2.0D) / 3.0D), ty, size / 3.0D, mouseLoc);
	
		g.setFont(Theme.PRIDI_REGULAR_16);
		fm = g.getFontMetrics();
		ty += spacing.getHeight(g) + BOX_SPACING + fm.getAscent() - fm.getDescent();
		g.drawString("Seed", (float)(tx + (size - fm.stringWidth("Seed")) / 2.0D), (float)ty);
		seed.render(g, tx + (size - spacing.getWidth()) / 2.0D, ty + BOX_SPACING, spacing.getWidth(), PlayerPanel.CONTENT_HEIGHT);
	}
	
	/**
	 * Gets the title of this menu.
	 * @return The title of this menu.
	 */
	protected String getMenuTitle(){
		return "New Game";
	}
	
	/**
	 * Handles the start of the actual game given the
	 * player and playfield generator configuration.
	 * @param players The participating players.
	 * @param gen The playfield generator to use.
	 */
	protected void handleStart(List<Player> players, PlayfieldGenerator gen){
		this.getContext().initialiseGame(()->{
			GameState state = new GameState(gen, players);
			state.getVerticalDecomposition().setAnimated(showDecomp);
			return state;
		});
	}
	
	/**
	 * Finds the index of the element in the array
	 * closest to the given value.
	 * @param values The list of elements to compare to.
	 * @param value The target value.
	 * @return The index of the element in the given values
	 *         array closest to the given value.
	 */
	private int findClosest(int[] values, int value){
		int idx = Math.abs(values[0] - value) > Math.abs(values[1] - value) ? 1 : 0;
		return Math.abs(values[idx] - value) > Math.abs(values[2] - value) ? 2 : idx;
	}

	@Override
	public void onFocusLost(){
		if(!PlayfieldGenerator.isValidSeed(seed.getText().trim().toUpperCase(Locale.ROOT))){
			seed.setText(gen.getSeed());
			seed.setForegroundColor(PlayerTheme.P3.getBaseOutline());
		}
	}

	@Override
	public void onTextChange(String text){
		text = text.trim().toUpperCase(Locale.ROOT);
		seed.setText(text);
		
		if(PlayfieldGenerator.isValidSeed(text)){
			seed.setForegroundColor(PlayerTheme.P3.getBaseOutline());
			gen.setSeed(text);
			
			density.setSelected(findClosest(COVERAGE_VALUES, gen.getCoverage()));
			spacing.setSelected(findClosest(SCALING_VALUES, gen.getScaling()));
			
			int[] rdiff = new int[RANGE_VALUES_MAX.length];
			for(int i = 0; i < rdiff.length; i++){
				rdiff[i] = Math.abs(RANGE_VALUES_MIN[i] - gen.getRangeMin()) + Math.abs(RANGE_VALUES_MAX[i] - gen.getRangeMax());
			}
			size.setSelected(findClosest(rdiff, 0));
		}else{
			seed.setForegroundColor(Theme.ERROR_COLOR);
		}
	}
	
	@Override
	public void handleKeyReleased(KeyEvent e){
		if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_D){
			showDecomp = !showDecomp;
		}
	}

	@Override
	public void handleMouseRelease(Point2D loc, int width, int height){
		super.handleMouseRelease(loc, width, height);
		
		if(!started){
			p1.handleMouseClick(loc);
			p2.handleMouseClick(loc);
			p3.handleMouseClick(loc);
			p4.handleMouseClick(loc);
			size.handleMouseClick(loc);
			density.handleMouseClick(loc);
			spacing.handleMouseClick(loc);
			seed.handleMouseClick(loc);
			
			if(canStart() && start.contains(loc)){
				List<Player> players = new ArrayList<Player>();
				p1.getPlayer().ifPresent(players::add);
				p2.getPlayer().ifPresent(players::add);
				p3.getPlayer().ifPresent(players::add);
				p4.getPlayer().ifPresent(players::add);

				handleStart(players, gen);
				started = true;
			}
		}
	}

	@Override
	public void handleKeyPressed(KeyEvent event){
		if(!started){
			if(p1.name != null){
				p1.name.handleKeyEvent(event);
			}else if(p2.name != null){
				p2.name.handleKeyEvent(event);
			}else if(p2.name != null){
				p3.name.handleKeyEvent(event);
			}else if(p4.name != null){
				p4.name.handleKeyEvent(event);
			}
			seed.handleKeyEvent(event);
		}
	}

	@Override
	public void update(double progress){
		this.progress = progress;
	}

	@Override
	protected boolean isLeftButtonEnabled(){
		return !started;
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
		switchScene(new MainMenu(this.getContext()));
	}

	@Override
	protected void handleRightButtonClick(){
	}
	
	/**
	 * Assembly of three mutually exclusive buttons.
	 * @author Roan
	 */
	private class ButtonAssembly{
		/**
		 * Width of the individual buttons.
		 */
		private static final double BUTTON_WIDTH = 100.0D;
		/**
		 * Height of the individual buttons.
		 */
		private static final double BUTTON_HEIGHT = 30.0D;
		/**
		 * Title of this assembly.
		 */
		private String title;
		/**
		 * Button display values.
		 */
		private String[] values;
		/**
		 * The bounds for the left button.
		 */
		private Path2D left;
		/**
		 * The bounds for the middle button.
		 */
		private Path2D middle;
		/**
		 * The bounds for the right button.
		 */
		private Path2D right;
		/**
		 * The index of the currently selected button (0~2).
		 */
		private int selected;
		/**
		 * Listener called when the selected button changes.
		 */
		private IntConsumer listener = i->{};
		
		/**
		 * Constructs a new button assembly with the given initially
		 * selected button, assembly title and button values.
		 * @param selected The initially selected button.
		 * @param title The assembly title.
		 * @param values The button display values.
		 */
		public ButtonAssembly(int selected, String title, String... values){
			this.selected = selected;
			this.title = title;
			this.values = values;
		}
		
		/**
		 * Sets the listener called when the selected button changes.
		 * @param listener The new change listener.
		 */
		public void setChangeListener(IntConsumer listener){
			this.listener = listener;
		}
		
		/**
		 * Gets the height of this button assembly.
		 * @param g The graphics context to use.
		 * @return The height of this button assembly.
		 */
		public double getHeight(Graphics2D g){
			FontMetrics fm = g.getFontMetrics(Theme.PRIDI_REGULAR_16);
			return fm.getAscent() - fm.getDescent() + BOX_SPACING + BUTTON_HEIGHT;
		}
		
		/**
		 * Gets the width of this button assembly.
		 * @return The width of this button assembly.
		 */
		public double getWidth(){
			return BUTTON_WIDTH * 3.0D - BOX_INSETS * 4.0D;
		}
		
		/**
		 * Sets the selected button for this assembly.
		 * @param selected The selected button (0~2).
		 */
		public void setSelected(int selected){
			this.selected = selected;
		}
		
		/**
		 * Gets the bounds of the selected button.
		 * @return The bounds of the selected button.
		 */
		private Path2D getSelectedPath(){
			return selected == 0 ? left : (selected == 1 ? middle : right);
		}
		
		/**
		 * Checks if the given point is on the left button.
		 * @param point The point to check.
		 * @return True if the given point is on the left button.
		 */
		private boolean intersectsLeft(Point2D point){
			Path2D sel = getSelectedPath();
			return sel == left ? sel.contains(point) : (left.contains(point) && !middle.contains(point));
		}

		/**
		 * Checks if the given point is on the middle button.
		 * @param point The point to check.
		 * @return True if the given point is on the middle button.
		 */
		private boolean intersectsMiddle(Point2D point){
			Path2D sel = getSelectedPath();
			return sel == middle ? sel.contains(point) : (middle.contains(point) && !right.contains(point) && !sel.contains(point));
		}

		/**
		 * Checks if the given point is on the right button.
		 * @param point The point to check.
		 * @return True if the given point is on the right button.
		 */
		private boolean intersectsRight(Point2D point){
			Path2D sel = getSelectedPath();
			return sel == right ? sel.contains(point) : (right.contains(point) && !sel.contains(point));
		}
		
		/**
		 * Renders this button assembly at the given location.
		 * @param g The graphics context to use.
		 * @param x The x coordinate of the top left corner.
		 * @param y The y coordinate of the top left corner.
		 * @param w The width of the assembly.
		 * @param mouseLoc The current cursor location.
		 */
		private void render(Graphics2D g, double x, double y, double w, Point2D mouseLoc){
			g.setFont(Theme.PRIDI_REGULAR_16);
			g.setColor(Theme.ADD_COLOR_HIGHLIGHT);
			FontMetrics fm = g.getFontMetrics();
			y += fm.getAscent() - fm.getDescent();
			g.drawString(title, (float)(x + (w - fm.stringWidth(title)) / 2.0D), (float)y);
			
			x += (w - getWidth()) / 2.0D;
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
			
			g.setFont(Theme.PRIDI_REGULAR_14);
			fm = g.getFontMetrics();
			g.setColor(Theme.ADD_COLOR_HIGHLIGHT);
			
			g.drawString(values[0], (float)(x - BUTTON_WIDTH * 1.5D + BOX_INSETS * 4.0D - fm.stringWidth(values[0]) / 2.0D), (float)(y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
			g.drawString(values[1], (float)(x - BUTTON_WIDTH * 0.5D + BOX_INSETS * 2.0D - fm.stringWidth(values[1]) / 2.0D), (float)(y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
			g.drawString(values[2], (float)(x + (BUTTON_WIDTH - fm.stringWidth(values[2])) / 2.0D), (float)(y + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
		}
		
		/**
		 * Handles a mouse click on this button assembly.
		 * @param loc The clicked location.
		 */
		private void handleMouseClick(Point2D loc){
			int old = selected;
			if(intersectsLeft(loc)){
				selected = 0;
			}else if(intersectsMiddle(loc)){
				selected = 1;
			}else if(intersectsRight(loc)){
				selected = 2;
			}
			
			if(old != selected){
				listener.accept(selected);
				seed.setText(gen.getSeed());
			}
		}
	}

	/**
	 * Panel to configure a new AI or human player.
	 * @author Roan
	 */
	protected class PlayerPanel{
		/**
		 * The width of the panels.
		 */
		private static final double WIDTH = 150.0D;
		/**
		 * The height of the panels.
		 */
		private static final double HEIGHT = 62.0D;
		/**
		 * Border inset from the outside of the button.
		 */
		private static final double BUTTON_INSET = 4.0D;
		/**
		 * Width of the panel content.
		 */
		protected static final double CONTENT_WIDTH = 134.0D;
		/**
		 * Height of the panel content.
		 */
		protected static final double CONTENT_HEIGHT = 21.0D;
		/**
		 * Bounds of the add (human) player button.
		 */
		private Path2D addPlayer = null;
		/**
		 * Bounds of the add AI button.
		 */
		private Path2D addAI = null;
		/**
		 * Panel theme.
		 */
		private PlayerTheme theme;
		/**
		 * Text field used to configure a human player.
		 */
		private TextField name = null;
		/**
		 * Combo box used to configure an AI.
		 */
		private ComboBox<AIRegistry> ai = null;
		/**
		 * Bounds of the remove button.
		 */
		private Path2D remove = new Path2D.Double();
		/**
		 * Current panel icon.
		 */
		private Image icon;
		
		/**
		 * Constructs a new player panel with the given theme.
		 * @param theme The panel theme.
		 */
		protected PlayerPanel(PlayerTheme theme){
			this.theme = theme;
		}
		
		/**
		 * Checks if this panel has a configured player.
		 * @return True if this panel has a configured player.
		 */
		protected boolean hasPlayer(){
			return ai != null || name != null;
		}
		
		/**
		 * Gets the configured player for this panel if it exists.
		 * @return The configured player for this panel if present.
		 */
		protected Optional<Player> getPlayer(){
			if(name != null){
				return Optional.of(new HumanPlayer(name.getText()));
			}else if(ai != null){
				return Optional.of(ai.getValue().createInstance());
			}else{
				return Optional.empty();
			}
		}
		
		/**
		 * Renders this panel at the given location.
		 * @param g The graphics context to use.
		 * @param x The x coordinate of the top left corner.
		 * @param y The y coordinate of the top left corner.
		 * @param mouseLoc The current cursor location.
		 */
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
		
		/**
		 * Sets the player currently being configured to a human
		 * and activates the corresponding UI elements.
		 * @param name The player name to use or <code>null</code>
		 *        to use the default name.
		 */
		protected void setHuman(String name){
			icon = theme.getSmallIconHuman();
			
			if(name == null && p1.name == null && p2.name == null && p3.name == null && p4.name == null){
				name = System.getProperty("user.name");
			}
			
			if(name == null){
				if(p1 == this){
					name = "Player 1";
				}else if(p2 == this){
					name = "Player 2";
				}else if(p3 == this){
					name = "Player 3";
				}else if(p4 == this){
					name = "Player 4";
				}
			}
			
			this.name = new TextField(theme.getBaseOutline());
			this.name.setText(name);
		}
		
		/**
		 * Handles a mouse click on this component.
		 * @param loc The location that was clicked.
		 */
		protected void handleMouseClick(Point2D loc){
			if(name != null){
				name.handleMouseClick(loc);
			}
			
			if(name == null && ai == null){
				if(addPlayer.contains(loc)){
					setHuman(null);
				}else if(addAI.contains(loc)){
					icon = theme.getSmallIconAI();
					ai = new ComboBox<AIRegistry>(AIRegistry.values(), AIRegistry::getName, theme.getBaseOutline());
				}
			}else{
				if(remove != null && remove.contains(loc) && !(ai != null && ai.hasFocus())){
					name = null;
					ai = null;
				}
			}
			
			if(ai != null){
				ai.handleMouseClick(loc);
			}
		}
		
		/**
		 * Renders the remove button at the given location.
		 * @param g The graphics context to use.
		 * @param x The x coordinate of the top left corner.
		 * @param y The y coordinate of the top left corner.
		 * @param mouseLoc The current cursor location.
		 */
		protected void renderRemoveButton(Graphics2D g, double x, double y, Point2D mouseLoc){
			remove = computeBox(x, y, CONTENT_WIDTH, CONTENT_HEIGHT, 5.0D);
			
			g.setFont(Theme.PRIDI_REGULAR_14);
			
			FontMetrics fm = g.getFontMetrics();
			double offset = (CONTENT_WIDTH - fm.stringWidth("Remove") - Theme.REMOVE_ICON_SIZE - SPACING) / 2.0D;
			
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
		
		/**
		 * Renders the add player/AI buttons at the given location.
		 * @param g The graphics context to use.
		 * @param x The x coordinate of the top left corner.
		 * @param y The y coordinate of the top left corner.
		 * @param mouseLoc The current cursor location.
		 */
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