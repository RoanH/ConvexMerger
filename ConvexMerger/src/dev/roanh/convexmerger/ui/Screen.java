package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.GameState;

/**
 * Base class for all screens containing shared rendering subroutines.
 * @author Roan
 */
public abstract class Screen{
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
	public static final int TOP_MIDDLE_OFFSET = 30;
	/**
	 * Height of the buttons in the bottom left and right.
	 */
	private static final int BUTTON_HEIGHT = 50;
	/**
	 * Width of the buttons in the bottom left and right.
	 */
	private static final int BUTTON_WIDTH = 150;
	/**
	 * Space between the boxes.
	 */
	public static final int BOX_SPACING = 12;
	/**
	 * Inset into a drawn box and also the corner triangle size.
	 */
	public static final int BOX_INSETS = 10;
	/**
	 * Height of a box title header.
	 */
	public static final int BOX_HEADER_HEIGHT = 28;
	/**
	 * Text offset from the side of a box.
	 */
	public static final int BOX_TEXT_OFFSET = 4;
	/**
	 * Spacing between various components.
	 */
	public static final double SPACING = 4.0D;
	/**
	 * Bottom right button polygon.
	 */
	private Polygon rightPoly = null;
	/**
	 * Bottom left button polygon.
	 */
	private Polygon leftPoly = null;
	/**
	 * Last mouse location.
	 */
	private Point2D lastLocation = new Point2D.Double();
	/**
	 * The active game context.
	 */
	private ConvexMerger context;
	
	/**
	 * Constructs a new screen with the given game context.
	 * @param context The game context.
	 */
	protected Screen(ConvexMerger context){
		this.context = context;
	}
	
	/**
	 * Gets the game context this screen is associated with.
	 * @return The game context.
	 */
	protected ConvexMerger getContext(){
		return context;
	}
	
	/**
	 * Switches the context from the current screen to the given screen.
	 * @param next The screen to switch to.
	 */
	protected void switchScene(Screen next){
		context.switchScene(next);
	}

	/**
	 * Renders this screen with the given graphics and dimensions.
	 * @param g The graphics context to use.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public void render(Graphics2D g, int width, int height){
		g.setColor(Theme.BACKGROUND);
		g.fillRect(0, 0, width, height);
		
		render(g, width, height, lastLocation);
	}
	
	/**
	 * Renders the content of this screen with the given graphics.
	 * @param g The graphics context to use.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 * @param mouseLoc The current cursor location.
	 */
	protected abstract void render(Graphics2D g, int width, int height, Point2D mouseLoc);
	
	/**
	 * Computes how wide the content should be given a screen
	 * width, width usage ratio and maximum width. So effectively
	 * this computes <code>min(ratio * width, max)</code>.
	 * @param width The width of the screen.
	 * @param ratio The fraction of the width to use.
	 * @param max The maximum width.
	 * @return The content width.
	 */
	protected static double getMaxWidth(int width, double ratio, int max){
		return Math.min(ratio * width, max);
	}
	
	/**
	 * Draws the main screen title.
	 * @param g The graphics context to use.
	 * @param width The width of the screen.
	 */
	protected void drawTitle(Graphics2D g, int width){
		g.setColor(Color.WHITE);
		g.setFont(Theme.PRIDI_MEDIUM_30);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(Constants.TITLE, (width - fm.stringWidth(Constants.TITLE)) / 2.0F, (GamePanel.TOP_SPACE + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
	}
	
	/**
	 * Draws a titled box according to the given parameters.
	 * @param g The graphics context to use.
	 * @param gradient The gradient to use.
	 * @param x The x location of the top left corner of the box.
	 * @param y The y location of the top left corner of the box.
	 * @param w The width of the box.
	 * @param h The height of the box.
	 * @param title The header title of the box.
	 */
	protected void drawTitledBox(Graphics2D g, Paint gradient, double x, double y, double w, double h, String title){
		g.setColor(Theme.MENU_BODY);
		drawBox(g, x, y, w, h);
		
		g.setStroke(Theme.BORDER_STROKE);
		g.setPaint(gradient);
		g.draw(new Line2D.Double(x, y + BOX_HEADER_HEIGHT + 1.0D, x + w, y + BOX_HEADER_HEIGHT + 1.0D));
		g.setPaint(null);
		
		g.setFont(Theme.PRIDI_REGULAR_18);
		g.setColor(Theme.BOX_TEXT_COLOR);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title, (float)(x + (w - fm.stringWidth(title)) / 2.0F), (float)(y + (BOX_HEADER_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F));
		
	}
	
	/**
	 * Draws a box according to the given parameters.
	 * @param g The graphics context to use.
	 * @param x The x location of the top left corner of the box.
	 * @param y The y location of the top left corner of the box.
	 * @param w The width of the box.
	 * @param h The height of the box.
	 */
	protected void drawBox(Graphics2D g, double x, double y, double w, double h){
		g.fill(computeBox(x, y, w, h, BOX_INSETS));
	}
	
	/**
	 * Computes the bounds of a box if draw according to the given parameters.
	 * @param x The x location of the top left corner of the box.
	 * @param y The y location of the top left corner of the box.
	 * @param w The width of the box.
	 * @param h The height of the box.
	 * @param inset The box insets, see {@link #BOX_INSETS}.
	 * @return The box bounds.
	 */
	protected Path2D computeBox(double x, double y, double w, double h, double inset){
		Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 8);
		path.moveTo(x, y + inset);
		path.lineTo(x + inset, y);
		path.lineTo(x + w - inset, y);
		path.lineTo(x + w, y + inset);
		path.lineTo(x + w, y + h - inset);
		path.lineTo(x + w - inset, y + h);
		path.lineTo(x + inset, y + h);
		path.lineTo(x, y + h - inset);
		path.closePath();
		return path;
	}
	
	/**
	 * Draws a button at the given location with the given text.
	 * @param g The graphics context to use.
	 * @param text The button text.
	 * @param x The x location of the top left corner of the button.
	 * @param y The y location of the top left corner of the button.
	 * @param w The width of the button.
	 * @param h The height of the button.
	 * @param mouseLoc The cursor location or <code>null</code>.
	 * @return The bounds of the drawn button.
	 */
	protected Path2D drawButton(Graphics2D g, String text, double x, double y, double w, double h, Point2D mouseLoc){
		g.setColor(Theme.MENU_BODY);
		Path2D button = computeBox(x, y, w, h, BOX_INSETS);
		g.fill(button);
		g.setFont(Theme.PRIDI_REGULAR_18);
		g.setColor(Theme.DOUBLE_LIGHTEN);
		Path2D border = computeBox(x + SPACING, y + SPACING, w - SPACING * 2.0D, h - SPACING * 2.0D, BOX_INSETS);
		g.draw(border);
		if(mouseLoc != null && button.contains(mouseLoc)){
			g.fill(border);
		}
		g.setColor(Theme.ADD_COLOR_HIGHLIGHT);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, (float)(x + SPACING + (w - fm.stringWidth(text)) / 2.0D), (float)(y + h - (h - fm.getAscent() + fm.getDescent() + fm.getLeading()) / 2.0D));
		
		return button;
	}
	
	/**
	 * Renders a screen sub title.
	 * @param g The graphics context to use.
	 * @param width The width of the screen.
	 * @param title The sub title to draw.
	 */
	protected void renderMenuTitle(Graphics2D g, int width, String title){
		g.setFont(Theme.PRIDI_REGULAR_18);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title, Math.floorDiv(width, 2) - (TOP_MIDDLE_WIDTH / 2) + (TOP_MIDDLE_WIDTH - fm.stringWidth(title)) / 2.0F, TOP_SPACE + TOP_OFFSET - fm.getDescent() - TOP_MIDDLE_TEXT_OFFSET);
	}
	
	/**
	 * Fills the given area with the given text. The text is given
	 * as a list of words and it is assumed that each word should
	 * be separated by a single space. The words automatically get
	 * wrapped and no words will go outside the bounding box.
	 * @param g The graphics context to use.
	 * @param rx The x coordinate of the top left corner of the area.
	 * @param ry The y coordinate of the top left corner of the area.
	 * @param width The width of the area.
	 * @param height The height of the area.
	 * @param text The list of words to draw.
	 * @return The y coordinate of the last text baseline used.
	 */
	protected int fillText(Graphics2D g, int rx, int ry, int width, int height, List<String> text){
		g.setClip(rx, ry, width, height);
		FontMetrics fm = g.getFontMetrics();
		int x = rx;
		int y = ry + fm.getAscent();
		for(String word : text){
			int w = fm.stringWidth(word);
			if(x + w - rx > width){
				x = rx;
				y += fm.getHeight();
				if(y > ry + height){
					y -= fm.getHeight();
					break;
				}
			}
			g.drawString(word + " ", x, y);
			x += w + fm.stringWidth(" ");
		}
		g.setClip(null);
		return y;
	}
	
	/**
	 * Renders the main interface, the top panel and button left and
	 * right buttons.
	 * @param g The graphics context to use.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 * @param state The game state to use to determine the gradient
	 *        to use, can be <code>null</code>.
	 */
	protected void renderMainInterface(Graphics2D g, int width, int height, GameState state){
		g.setColor(Theme.MENU_BODY);
		int sideOffset = Math.floorDiv(width, 2) - (TOP_MIDDLE_WIDTH / 2);
		Polygon topPoly = new Polygon(new int[]{
				0,
				0,
				TOP_SIDE_TRIANGLE,
				sideOffset - TOP_MIDDLE_OFFSET,
				sideOffset,
				width - sideOffset,
				width - sideOffset + TOP_MIDDLE_OFFSET,
				width - TOP_SIDE_TRIANGLE,
				width,
				width
			},
			new int[]{
				0,
				TOP_SIDE_TRIANGLE + TOP_SPACE,
				TOP_SPACE,
				TOP_SPACE,
				TOP_SPACE + TOP_MIDDLE_OFFSET,
				TOP_SPACE + TOP_MIDDLE_OFFSET,
				TOP_SPACE,
				TOP_SPACE,
				TOP_SIDE_TRIANGLE + TOP_SPACE,
				0
			},
			10
		);
		g.fill(topPoly);
		
		g.setFont(Theme.PRIDI_REGULAR_24);
		FontMetrics fm = g.getFontMetrics();
		
		rightPoly = new Polygon(
			new int[]{
				width,
				width - BUTTON_WIDTH,
				width - BUTTON_WIDTH + BUTTON_HEIGHT,
				width
			},
			new int[]{
				height,
				height,
				height - BUTTON_HEIGHT,
				height - BUTTON_HEIGHT
			},
			4
		);
		g.setColor((isRightButtonEnabled() && rightPoly.contains(lastLocation)) ? Theme.BUTTON_HOVER_COLOR : Theme.MENU_BODY);
		g.fill(rightPoly);
		String text = getRightButtonText();
		if(text != null){
			g.setColor((isRightButtonEnabled() && rightPoly.contains(lastLocation)) ? Color.WHITE : Theme.BUTTON_TEXT_COLOR);
			g.drawString(text, width - BUTTON_WIDTH / 2.0F + BUTTON_HEIGHT / 4.0F - fm.stringWidth(text) / 2.0F, height + (fm.getAscent() - BUTTON_HEIGHT - fm.getDescent() - fm.getLeading()) / 2.0F);
		}
		
		leftPoly = new Polygon(
			new int[]{
				0,
				0,
				BUTTON_WIDTH - BUTTON_HEIGHT,
				BUTTON_WIDTH
			},
			new int[]{
				height,
				height - BUTTON_HEIGHT,
				height - BUTTON_HEIGHT,
				height
			},
			4
		);
		g.setColor((isLeftButtonEnabled() && leftPoly.contains(lastLocation)) ? Theme.BUTTON_HOVER_COLOR : Theme.MENU_BODY);
		g.fill(leftPoly);
		text = getLeftButtonText();
		if(text != null){
			g.setColor((isLeftButtonEnabled() && leftPoly.contains(lastLocation)) ? Color.WHITE : Theme.BUTTON_TEXT_COLOR);
			g.drawString(text, BUTTON_WIDTH / 2.0F - BUTTON_HEIGHT / 4.0F - fm.stringWidth(text) / 2.0F, height + (fm.getAscent() - BUTTON_HEIGHT - fm.getDescent() - fm.getLeading()) / 2.0F);
		}
		
		//render UI borders
		g.setPaint(Theme.constructBorderGradient(state, width));
		g.setStroke(Theme.BORDER_STROKE);
		
		Path2D infoPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
		infoPath.moveTo(rightPoly.xpoints[1], rightPoly.ypoints[1] - 1);
		infoPath.lineTo(rightPoly.xpoints[2], rightPoly.ypoints[2] - 1);
		infoPath.lineTo(rightPoly.xpoints[3] - 1, rightPoly.ypoints[3] - 1);
		g.draw(infoPath);
		
		Path2D menuPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
		menuPath.moveTo(leftPoly.xpoints[1], leftPoly.ypoints[1] - 1);
		menuPath.lineTo(leftPoly.xpoints[2], leftPoly.ypoints[2] - 1);
		menuPath.lineTo(leftPoly.xpoints[3] + 1, leftPoly.ypoints[3]);
		g.draw(menuPath);
		
		Path2D topPath = new Path2D.Double(Path2D.WIND_NON_ZERO, topPoly.npoints - 2);
		topPath.moveTo(topPoly.xpoints[1], topPoly.ypoints[1]);
		for(int i = 2; i < topPoly.npoints - 1; i++){
			topPath.lineTo(topPoly.xpoints[i], topPoly.ypoints[i]);
		}
		g.draw(topPath);
	}
	
	/**
	 * Handles a mouse button release on this screen.
	 * If this method is overridden a super call needs to be made.
	 * @param loc The location that was clicked.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public void handleMouseRelease(Point2D loc, int width, int height){
		if(isLeftButtonEnabled() && leftPoly.contains(loc)){
			handleLeftButtonClick();
		}else if(isRightButtonEnabled() && rightPoly.contains(loc)){
			handleRightButtonClick();
		}
	}
	
	/**
	 * Handles a mouse button press on this screen.
	 * @param loc The location that was clicked.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public void handleMousePress(Point2D loc, int width, int height){
	}
	
	/**
	 * Handles a mouse move on this screen. If this method
	 * is overridden a super call needs to be made.
	 * @param loc The current cursor location.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public void handleMouseMove(Point2D loc, int width, int height){
		lastLocation = loc;
	}
	
	/**
	 * Handles a mouse move on this screen. If this method
	 * is overridden a super call needs to be made.
	 * @param loc The current cursor location.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public void handleMouseDrag(Point2D loc, int width, int height){
		lastLocation = loc;
	}
	
	/**
	 * Handles a key press event.
	 * @param event The key press event.
	 */
	public void handleKeyPressed(KeyEvent event){
	}

	/**
	 * Handles a key release event.
	 * @param event The key release event.
	 */
	public void handleKeyReleased(KeyEvent event){
	}
	
	/**
	 * Checks if the bottom left button is enabled.
	 * @return True if the bottom left button is enabled.
	 */
	protected abstract boolean isLeftButtonEnabled();
	
	/**
	 * Checks if the bottom right button is enabled.
	 * @return True if the bottom right button is enabled.
	 */
	protected abstract boolean isRightButtonEnabled();
	
	/**
	 * Gets the text for the bottom left button.
	 * @return The text for the bottom left button.
	 */
	protected abstract String getLeftButtonText();
	
	/**
	 * Gets the text for the bottom right button.
	 * @return The text for the bottom right button.
	 */
	protected abstract String getRightButtonText();
	
	/**
	 * Handles the bottom left button being clicked.
	 */
	protected abstract void handleLeftButtonClick();
	
	/**
	 * Handles the bottom right button being clicked.
	 */
	protected abstract void handleRightButtonClick();
}
