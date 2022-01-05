package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.Theme.CROWN_ICON_SIZE;
import static dev.roanh.convexmerger.ui.Theme.PLAYER_ICON_SIZE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ClaimResult;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.player.Player;

/**
 * Main panel responsibly for rendering the current game state.
 * @author Roan
 */
public final class GamePanel extends Menu implements MouseListener, MouseMotionListener, KeyListener{
	/**
	 * Executor service used to run animations.
	 */
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 5749409248962652951L;
	/**
	 * Height of the top score display part of the game panel.
	 */
	public static final int TOP_SPACE = 100;
	/**
	 * Offset from the side to the playfield rectangle.
	 */
	public static final int SIDE_OFFSET = 20 + 1;
	/**
	 * Offset from the score display part of the game panel to the playfield rectangle.
	 */
	public static final int TOP_OFFSET = 30 + 1;
	/**
	 * Offset from the bottom to the playfield rectangle.
	 */
	public static final int BOTTOM_OFFSET = 50 + 1;
	/**
	 * Width of the middle text area attached to the top part.
	 */
	public static final int TOP_MIDDLE_WIDTH = 200;
	/**
	 * Text offset from the bottom in the top middle text area.
	 */
	public static final int TOP_MIDDLE_TEXT_OFFSET = 2;
	/**
	 * Dimensions of the triangles on the left and right side of the top part.
	 */
	public static final int TOP_SIDE_TRIANGLE = 50;
	/**
	 * Height of the middle text area attached to the top part.
	 */
	private static final int TOP_MIDDLE_OFFSET = 30;
	/**
	 * Height of the buttons in the bottom left and right.
	 */
	private static final int BUTTON_HEIGHT = 50;
	/**
	 * Width of the buttons in the bottom left and right.
	 */
	private static final int BUTTON_WIDTH = 150;
	/**
	 * Number of pixels between the player icon and the text.
	 */
	private static final int ICON_TEXT_SPACING = 4;
	/**
	 * Number of pixels from the left text border to the player info.
	 */
	private static final int PLAYER_TEXT_OFFSET = 24;
	/**
	 * The game state to visualise.
	 */
	private GameState state = null;
	/**
	 * True if the centroid of objects objects should be rendered.
	 */
	private boolean showCentroids = false;
	/**
	 * Currently showing feedback dialog.
	 */
	private MessageDialog activeDialog = null;
	/**
	 * Currently showing merge helper lines.
	 */
	private List<Line2D> helperLines = null;
	/**
	 * Whether an animation is actively running.
	 */
	private boolean animationRunning;
	/**
	 * Result overlay.
	 */
	private ResultOverlay resultOverlay;
	
	/**
	 * Constructs a new game panel.
	 */
	protected GamePanel(){
		this.setFocusable(true);
	}
	
	/**
	 * Sets the game state to display in this game panel.
	 * @param state The game state to display.
	 */
	public void setGameState(GameState state){
		if(this.state == null){
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.addKeyListener(this);
		}
		resultOverlay = new ResultOverlay(state);
		this.state = state;
	}
	
	/**
	 * Enables and shows the result overlay.
	 */
	public void showResults(){
		resultOverlay.setEnabled(true);
		repaint();
	}
	
	/**
	 * Renders the game state with the given graphics.
	 * @param g The graphics context to use.
	 */
	private void renderGame(Graphics2D g){
		animationRunning = false;

		//render playfield background
		g.setColor(Theme.BACKGROUND);
		g.fillRect(0, TOP_SPACE, this.getWidth(), this.getHeight() - TOP_SPACE);
		
		//render the game
		if(menu == null && state != null){
			renderPlayfield(g);
		}
		
		//render UI shapes
		renderInterface(g);
		
		if(menu == null){
			//TODO temp dialog
			if(activeDialog != null){
				//TODO center and make look nice
				g.drawString(activeDialog.getTitle(), 100, 10 + 120);
				g.drawString(activeDialog.getSubtitle(), 100, 30 + 120);
				g.drawString("Click anywhere to close this dialog.", 100, 50 + 120);
			}

			//render results
			if(resultOverlay != null){
				animationRunning |= resultOverlay.render(g, this.getWidth(), this.getHeight());
			}
		}else{
			animationRunning |= menu.render(g, this.getWidth(), this.getHeight(), lastLocation);
		}
		
		//schedule next animation frame
		if(animationRunning){
			executor.schedule(()->this.repaint(), Constants.ANIMATION_RATE, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * Renders the user interface with the given graphics.
	 * @param g The graphics context to use.
	 */
	private void renderInterface(Graphics2D g, int width){
		renderMainInterface(g);
		
		if(state == null){
			return;
		}
		
		//render action hint
		g.setColor(state.isFinished() ? Theme.CROWN_COLOR : state.getActivePlayer().getTheme().getTextColor());
		//TODO override color ^
		renderMenuTitle(g, this.getWidth(), state.isFinished() ? "Game Finished" : (state.isSelectingSecond() ? "Merge with an object" : "Select an object"));
		
		//render player data
		List<Player> players = state.getPlayers();
		double max = players.stream().mapToDouble(Player::getArea).max().getAsDouble();
		for(int i = 0; i < players.size(); i++){
			Player player = players.get(i);
			int x = ((i * this.getWidth()) / players.size());
			g.setClip(x, 0, this.getWidth() / players.size(), TOP_SPACE);
			
			//offset
			g.setFont(Theme.PRIDI_MEDIUM_24);
			FontMetrics fm = g.getFontMetrics();
			int y = (TOP_SPACE - fm.getHeight() - CROWN_ICON_SIZE) / 2;
			x += PLAYER_TEXT_OFFSET;
			
			//player icon and name
			g.drawImage(player.isAI() ? player.getTheme().getIconAI() : player.getTheme().getIconHuman(), x, y, this);
			g.setColor(player.getTheme().getTextColor());
			g.drawString(player.getName(), x + PLAYER_ICON_SIZE + ICON_TEXT_SPACING, y + PLAYER_ICON_SIZE / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
			
			//new offset
			y += fm.getHeight();
			g.setFont(Theme.PRIDI_REGULAR_18);
			fm = g.getFontMetrics();
			
			//crown icon
			if(Double.compare(player.getArea(), max) >= 0){
				g.drawImage(Theme.CROWN_ICON, x + (PLAYER_ICON_SIZE - CROWN_ICON_SIZE) / 2, y, this);
				g.setColor(Theme.SCORE_COLOR_LEAD);
			}else{
				g.setColor(Theme.SCORE_COLOR);
			}
			
			//player score
			AffineTransform transform = g.getTransform();
			g.translate(x + PLAYER_ICON_SIZE + ICON_TEXT_SPACING, y + CROWN_ICON_SIZE / 2.0D + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D);
			animationRunning |= player.getScoreAnimation().run(g);
			g.setTransform(transform);
		}
		g.setClip(null);
	}
	
	/**
	 * Renders the playfield with the given graphics.
	 * @param g The graphics context to use.
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
				animationRunning |= obj.runAnimation(g);
			}else{
				obj.render(g);
			}
			
			if(showCentroids){
				g.setColor(Color.BLACK);
				Point2D c = obj.getCentroid();
				g.fill(new Ellipse2D.Double(c.getX() - 5, c.getY() - 5, 10, 10));	
			}
		}
		
		g.setColor(Color.WHITE);
		for(Line2D line : state.getVerticalDecompLines()){
			g.draw(line);
		}
		
		if(helperLines != null){
			g.setStroke(Theme.HELPER_STROKE);
			g.setColor(state.getActivePlayer().getTheme().getOutline());
			for(Line2D line : helperLines){
				g.draw(line);
			}
		}
		
		g.setTransform(transform);
		g.setClip(null);
	}
	
	/**
	 * Translates the given point from windows coordinate space
	 * to game coordinate space.
	 * @param x The x coordinate of the point to translate.
	 * @param y The y coordinate of the point to translate.
	 * @return The point translated to game space.
	 */
	private Point2D translateToGameSpace(double x, double y){
		double sx = (double)(this.getWidth() - 2 * SIDE_OFFSET) / (double)Constants.PLAYFIELD_WIDTH;
		double sy = (double)(this.getHeight() - TOP_SPACE - TOP_OFFSET - BOTTOM_OFFSET) / (double)Constants.PLAYFIELD_HEIGHT;
		if(sx < sy){
			return new Point2D.Double(
				(x - SIDE_OFFSET) / sx,
				(y - TOP_SPACE - TOP_OFFSET) / sx
			);
		}else{
			return new Point2D.Double(
				(x - ((this.getWidth() - Constants.PLAYFIELD_WIDTH * sy - 2 * SIDE_OFFSET) / 2.0D) - SIDE_OFFSET) / sy,
				(y - TOP_SPACE - TOP_OFFSET) / sy
			);
		}
	}
	
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		renderGame(g);
	}

	@Override
	public void mouseClicked(MouseEvent e){
	}

	@Override
	public void mousePressed(MouseEvent e){
	}

	@Override
	public void mouseReleased(MouseEvent e){
		if(resultOverlay.isEnabled() && menu == null){
			//TODO handle menu button click
			return;
		}
		
		if(menuPoly.contains(e.getPoint())){
			if(menu == null){
				//TODO open menu
				System.out.println("TODO open menu");
			}else{
				menu = null;
			}
		}
		
		if(menu != null){
			menu.handleMouseClick(e.getPoint());
			return;
		}
		
		if(infoPoly.contains(e.getPoint())){
			menu = new InfoMenu(state);
			this.repaint();
			return;
		}
		
		if(activeDialog != null){
			activeDialog = null;
			repaint();
		}else if(state.getActivePlayer().requireInput() && !state.isFinished()){
			Point2D loc = translateToGameSpace(e.getX(), e.getY());
			ConvexObject obj = state.getObject(loc);
			if(obj != null){
				ClaimResult result = state.claimObject(obj, loc);
				activeDialog = result.getMessage();
				helperLines = null;
				if(result.hasResult()){
					synchronized(state){
						state.notify();
					}
				}
				this.repaint();
			}
		}else{
			activeDialog = state.isFinished() ? MessageDialog.GAME_END : MessageDialog.NO_TURN;
			this.repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e){
	}

	@Override
	public void mouseExited(MouseEvent e){
	}

	@Override
	public void mouseDragged(MouseEvent e){
	}

	@Override
	public void mouseMoved(MouseEvent e){
		boolean onButtonBefore = infoPoly != null && menuPoly != null && (infoPoly.contains(lastLocation) || menuPoly.contains(lastLocation));
		lastLocation = e.getPoint();
		boolean onButtonAfter = infoPoly != null && menuPoly != null && (infoPoly.contains(lastLocation) || menuPoly.contains(lastLocation));
		
		if(state.getActivePlayer().requireInput() && state.isSelectingSecond()){
			helperLines = state.getHelperLines(translateToGameSpace(e.getX(), e.getY()));
			this.repaint();
		}else if(onButtonBefore != onButtonAfter){
			this.repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e){
	}

	@Override
	public void keyPressed(KeyEvent e){
	}

	@Override
	public void keyReleased(KeyEvent e){
		if(e.isControlDown()){
			if(e.getKeyCode() == KeyEvent.VK_R && resultOverlay != null){
				resultOverlay.setEnabled(!resultOverlay.isEnabled());
				state.clearSelection();
				helperLines = null;
				this.repaint();
			}else if (e.getKeyCode() == KeyEvent.VK_C){
				showCentroids = !showCentroids;
				this.repaint();
			}
		}
	}
}