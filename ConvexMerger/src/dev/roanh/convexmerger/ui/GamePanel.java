package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.Theme.CROWN_ICON_SIZE;
import static dev.roanh.convexmerger.ui.Theme.PLAYER_ICON_SIZE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ClaimResult;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.GameState.GameStateListener;
import dev.roanh.convexmerger.game.VerticalDecomposition;
import dev.roanh.convexmerger.player.Player;

/**
 * Main panel responsible for rendering the current game state.
 * @author Roan
 */
public final class GamePanel extends Screen implements GameStateListener{
	/**
	 * Number of pixels between the player icon and the text.
	 */
	private static final int ICON_TEXT_SPACING = 4;
	/**
	 * Number of pixels from the left text border to the player info.
	 */
	private static final int PLAYER_TEXT_OFFSET = 24;
	/**
	 * Message dialog width.
	 */
	private static final double DIALOG_WIDTH = 270.0D;
	/**
	 * Message dialog height.
	 */
	private static final double DIALOG_HEIGHT = 110.0D;
	/**
	 * Dialog yes/no button height.
	 */
	private static final double BUTTON_HEIGHT = 25.0D;
	/**
	 * The game state to visualise.
	 */
	private GameState state = null;
	/**
	 * True if the centroid of objects objects should be rendered.
	 */
	private boolean showCentroids = false;
	/**
	 * True if the vertical decomposition objects should be rendered/
	 */
	private boolean showDecomp = false;
	/**
	 * Currently showing feedback dialog.
	 */
	private MessageDialog activeDialog = null;
	/**
	 * Currently showing merge helper lines.
	 */
	private List<Line2D> helperLines = null;
	/**
	 * Result overlay.
	 */
	private ResultOverlay resultOverlay;
	/**
	 * Quit dialog yes button.
	 */
	private Path2D quitYes = new Path2D.Double();
	/**
	 * Quit dialog no button.
	 */
	private Path2D quitNo = new Path2D.Double();
	
	/**
	 * Constructs a new game panel with the given game context.
	 * @param context The game context.
	 * @param state The game state to visualise.
	 */
	protected GamePanel(ConvexMerger context, GameState state){
		super(context);
		resultOverlay = new ResultOverlay(state);
		this.showDecomp = state.getVerticalDecomposition().isAnimated();
		this.state = state;
		state.registerStateListener(this);
	}
	
	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		//render the game
		if(state != null){
			renderPlayfield(g, width, height);
		}
		
		//render UI shapes
		renderInterface(g, width, height);
		
		//dialog
		if(activeDialog != null){
			double offset = (width - DIALOG_WIDTH) / 2.0D;
			drawTitledBox(g, Theme.constructBorderGradient(state, width), offset, TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D, DIALOG_WIDTH, DIALOG_HEIGHT, activeDialog.getTitle());
			g.setColor(Theme.BOX_TEXT_COLOR);
			g.setFont(Theme.PRIDI_REGULAR_14);
			int end = fillText(g, (int)(offset + BOX_INSETS + BOX_TEXT_OFFSET), TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING * 2 + BOX_HEADER_HEIGHT + BOX_TEXT_OFFSET, (int)(DIALOG_WIDTH - BOX_INSETS * 2.0D - BOX_TEXT_OFFSET * 2.0D), (int)(DIALOG_HEIGHT - BOX_HEADER_HEIGHT - BOX_TEXT_OFFSET), activeDialog.getMessage());
			end += g.getFontMetrics().getHeight();
			g.setFont(Theme.PRIDI_MEDIUM_14);
			if(activeDialog == MessageDialog.QUIT){
				end -= SPACING;
				FontMetrics fm = g.getFontMetrics();
				quitYes = computeBox(offset + DIALOG_WIDTH / 9.0D, end, DIALOG_WIDTH / 3.0D, BUTTON_HEIGHT, 5.0D);
				g.setColor(quitYes.contains(mouseLoc) ? Theme.DOUBLE_LIGHTEN : Theme.LIGHTEN);
				g.fill(quitYes);
				g.setColor(Theme.DOUBLE_LIGHTEN);
				g.setStroke(Theme.BUTTON_STROKE);
				g.draw(quitYes);
				g.setColor(quitYes.contains(mouseLoc) ? Theme.REMOVE_BUTTON_HIGHLIGHT : Theme.ADD_COLOR);
				g.drawString("Yes", (float)(offset + DIALOG_WIDTH / 9.0D + (DIALOG_WIDTH / 3.0D - fm.stringWidth("Yes")) / 2.0D), (float)(end + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent()) / 2.0D));
				
				quitNo = computeBox(offset + (DIALOG_WIDTH * 5.0D) / 9.0D, end, DIALOG_WIDTH / 3.0D, BUTTON_HEIGHT, 5.0D);
				g.setColor(quitNo.contains(mouseLoc) ? Theme.DOUBLE_LIGHTEN : Theme.LIGHTEN);
				g.fill(quitNo);
				g.setColor(Theme.DOUBLE_LIGHTEN);
				g.setStroke(Theme.BUTTON_STROKE);
				g.draw(quitNo);
				g.setColor(quitNo.contains(mouseLoc) ? Theme.REMOVE_BUTTON_HIGHLIGHT : Theme.ADD_COLOR);
				g.drawString("No", (float)(offset + (DIALOG_WIDTH * 5.0D) / 9.0D + (DIALOG_WIDTH / 3.0D - fm.stringWidth("No")) / 2.0D), (float)(end + (BUTTON_HEIGHT + fm.getAscent() - fm.getDescent()) / 2.0D));
			}else{
				g.setColor(Theme.BOX_SECONDARY_COLOR);
				g.drawString("(Click anywhere to dismiss)", (width - g.getFontMetrics().stringWidth("(Click anywhere to dismiss)")) / 2.0F, end);
			}
		}

		//render results
		if(resultOverlay != null){
			resultOverlay.render(g, width, height, mouseLoc);
		}
	}
	
	/**
	 * Renders the user interface with the given graphics.
	 * @param g The graphics context to use.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	private void renderInterface(Graphics2D g, int width, int height){
		renderMainInterface(g, width, height, state);
		
		if(state == null){
			return;
		}
		
		//render action hint
		g.setColor(state.isFinished() ? Theme.CROWN_COLOR : state.getActivePlayer().getTheme().getTextColor());
		renderMenuTitle(g, width, state.isFinished() ? "Game Finished" : (state.isSelectingSecond() ? "Merge with an object" : "Select an object"));
		
		//render player data
		List<Player> players = state.getPlayers();
		double max = players.stream().mapToDouble(Player::getArea).max().getAsDouble();
		for(int i = 0; i < players.size(); i++){
			Player player = players.get(i);
			int x = ((i * width) / players.size());
			g.setClip(x, 0, width / players.size(), TOP_SPACE);
			
			//offset
			g.setFont(Theme.PRIDI_MEDIUM_24);
			FontMetrics fm = g.getFontMetrics();
			int y = (TOP_SPACE - fm.getHeight() - CROWN_ICON_SIZE) / 2;
			x += PLAYER_TEXT_OFFSET;
			
			//player icon and name
			g.drawImage(player.isAI() ? player.getTheme().getIconAI() : player.getTheme().getIconHuman(), x, y, null);
			g.setColor(player.getTheme().getTextColor());
			g.drawString(player.getName(), x + PLAYER_ICON_SIZE + ICON_TEXT_SPACING, y + PLAYER_ICON_SIZE / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
			
			//new offset
			y += fm.getHeight();
			g.setFont(Theme.PRIDI_REGULAR_18);
			fm = g.getFontMetrics();
			
			//crown icon
			if(Double.compare(player.getArea(), max) >= 0){
				g.drawImage(Theme.CROWN_ICON, x + (PLAYER_ICON_SIZE - CROWN_ICON_SIZE) / 2, y, null);
				g.setColor(Theme.SCORE_COLOR_LEAD);
			}else{
				g.setColor(Theme.SCORE_COLOR);
			}
			
			//player score
			AffineTransform transform = g.getTransform();
			g.translate(x + PLAYER_ICON_SIZE + ICON_TEXT_SPACING, y + CROWN_ICON_SIZE / 2.0D + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D);
			player.getScoreAnimation().run(g);
			g.setTransform(transform);
		}
		g.setClip(null);
	}
	
	/**
	 * Renders the playfield with the given graphics.
	 * @param g The graphics context to use.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	private void renderPlayfield(Graphics2D g, int width, int height){
		AffineTransform transform = g.getTransform();
		g.translate(SIDE_OFFSET, TOP_SPACE + TOP_OFFSET);
		double sx = (double)(width - 2 * SIDE_OFFSET) / (double)Constants.PLAYFIELD_WIDTH;
		double sy = (double)(height - TOP_SPACE - TOP_OFFSET - BOTTOM_OFFSET) / (double)Constants.PLAYFIELD_HEIGHT;
		if(sx < sy){
			g.scale(sx, sx);
		}else{
			g.translate((width - Constants.PLAYFIELD_WIDTH * sy - 2 * SIDE_OFFSET) / 2.0D, 0.0D);
			g.scale(sy, sy);
		}
		
		for(ConvexObject obj : state.getObjects()){
			if(obj.hasAnimation()){
				obj.runAnimation(g);
			}else{
				obj.render(g);
			}
			
			if(showCentroids){
				g.setColor(Color.BLACK);
				Point2D c = obj.getCentroid();
				g.fill(new Ellipse2D.Double(c.getX() - 5, c.getY() - 5, 10, 10));	
			}
		}
		
		if(showDecomp){
			VerticalDecomposition decomp = state.getVerticalDecomposition();
			synchronized(decomp){
				g.setStroke(Theme.POLY_STROKE);
				g.setColor(Color.BLACK);
				decomp.getLines().forEach(g::draw);
				Line2D last = decomp.getLastLine();
				if(last != null){
					g.setColor(Color.BLUE);
					g.draw(last);
				}
				
				g.setColor(new Color(0, 255, 255, 50));
				g.fillRect(0, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
				
				g.setColor(Color.WHITE);
				g.setStroke(Theme.BORDER_STROKE);
				decomp.getDecompLines().forEach(g::draw);
			}
		}
		
		if(helperLines != null){
			g.setStroke(Theme.HELPER_STROKE);
			g.setColor(state.getActivePlayer().getTheme().getOutline());
			helperLines.forEach(g::draw);
		}
		
		g.setTransform(transform);
		g.setClip(null);
		
		if(activeDialog != null){
			g.setColor(Theme.OVERLAY_BACKGROUND);
			g.fillRect(0, 0, width, height);
		}
	}
	
	/**
	 * Translates the given point from window coordinate space
	 * to game coordinate space.
	 * @param x The x coordinate of the point to translate.
	 * @param y The y coordinate of the point to translate.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 * @return The point translated to game space.
	 */
	private Point2D translateToGameSpace(double x, double y, int width, int height){
		double sx = (double)(width - 2 * SIDE_OFFSET) / (double)Constants.PLAYFIELD_WIDTH;
		double sy = (double)(height - TOP_SPACE - TOP_OFFSET - BOTTOM_OFFSET) / (double)Constants.PLAYFIELD_HEIGHT;
		if(sx < sy){
			return new Point2D.Double(
				(x - SIDE_OFFSET) / sx,
				(y - TOP_SPACE - TOP_OFFSET) / sx
			);
		}else{
			return new Point2D.Double(
				(x - ((width - Constants.PLAYFIELD_WIDTH * sy - 2 * SIDE_OFFSET) / 2.0D) - SIDE_OFFSET) / sy,
				(y - TOP_SPACE - TOP_OFFSET) / sy
			);
		}
	}
	
	@Override
	protected void handleLeftButtonClick(){
		if(state.isFinished()){
			this.getContext().abortGame();
			switchScene(new MainMenu(this.getContext()));
		}else{
			activeDialog = MessageDialog.QUIT;
		}
	}
	
	@Override
	public void handleMousePress(Point2D point, int width, int height){
		if(!resultOverlay.isEnabled() && activeDialog == null){
			Point2D loc = translateToGameSpace(point.getX(), point.getY(), width, height);
			if(!state.isSelectingSecond()){
				ConvexObject obj = state.getObject(loc);
				if(obj != null && obj.isOwnedBy(state.getActivePlayer())){
					state.claimObject(obj, loc);
				}
			}else if(state.getSelectedObject().equals(state.getObject(loc))){
				state.clearSelection();
			}
		}
	}

	@Override
	public void handleMouseRelease(Point2D point, int width, int height){
		super.handleMouseRelease(point, width, height);
		
		if(resultOverlay.isEnabled()){
			if(resultOverlay.intersectsMenuButton(point)){
				this.switchScene(new MainMenu(getContext()));
			}
			return;
		}
		
		if(activeDialog != null){
			if(activeDialog == MessageDialog.QUIT){
				if(quitNo.contains(point)){
					activeDialog = null;
				}else if(quitYes.contains(point)){
					this.getContext().abortGame();
					switchScene(new MainMenu(this.getContext()));
				}
			}else{
				activeDialog = null;
			}
			return;
		}
		
		if(state.ready() && state.getActivePlayer().requireInput() && !state.isFinished()){
			Point2D loc = translateToGameSpace(point.getX(), point.getY(), width, height);
			ConvexObject obj = state.getObject(loc);
			if(obj != null){
				if(obj.canClaim() || (state.isSelectingSecond() && !obj.equals(state.getSelectedObject()))){
					ClaimResult result = state.claimObject(obj, loc);
					activeDialog = result.getMessage();
					helperLines = null;
					if(result.hasResult()){
						synchronized(state){
							state.notify();
						}
					}
				}else if(!obj.isOwnedBy(state.getActivePlayer())){
					state.clearSelection();
					helperLines = null;
					activeDialog = MessageDialog.ALREADY_OWNED;
				}
			}
		}else{
			activeDialog = state.isFinished() ? MessageDialog.GAME_END : (state.ready() ? MessageDialog.NO_TURN : MessageDialog.NOT_READY);
		}
	}
	
	@Override
	public void handleMouseDrag(Point2D loc, int width, int height){
		handleMouseMove(loc, width, height);
	}

	@Override
	public void handleMouseMove(Point2D loc, int width, int height){
		super.handleMouseMove(loc, width, height);
		if(state.getActivePlayer().requireInput() && state.isSelectingSecond()){
			helperLines = state.getHelperLines(translateToGameSpace(loc.getX(), loc.getY(), width, height));
		}
	}

	@Override
	public void handleKeyReleased(KeyEvent e){
		if(e.isControlDown()){
			if(e.getKeyCode() == KeyEvent.VK_R && resultOverlay != null){
				activeDialog = null;
				resultOverlay.setEnabled(!resultOverlay.isEnabled());
				state.clearSelection();
				helperLines = null;
			}else if(e.getKeyCode() == KeyEvent.VK_C){
				showCentroids = !showCentroids;
			}else if(e.getKeyCode() == KeyEvent.VK_D){
				showDecomp = !showDecomp;
				state.getVerticalDecomposition().setAnimated(showDecomp);
			}
		}
	}

	@Override
	protected boolean isLeftButtonEnabled(){
		return !resultOverlay.isEnabled();
	}

	@Override
	protected boolean isRightButtonEnabled(){
		return !resultOverlay.isEnabled();
	}

	@Override
	protected String getLeftButtonText(){
		return "Menu";
	}

	@Override
	protected String getRightButtonText(){
		return "Info";
	}

	@Override
	protected void handleRightButtonClick(){
		activeDialog = null;
		this.switchScene(new InfoMenu(this.getContext(), state, this));
	}

	@Override
	public void claim(Player player, ConvexObject obj){
	}

	@Override
	public void merge(Player player, ConvexObject source, ConvexObject target, ConvexObject result, List<ConvexObject> absorbed){
	}

	@Override
	public void end(){
		activeDialog = null;
		resultOverlay.setEnabled(true);
	}

	@Override
	public void abort(){
	}
}