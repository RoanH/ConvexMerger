package dev.roanh.convexmerger.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import dev.roanh.convexmerger.animation.Animation;
import dev.roanh.convexmerger.animation.ExampleAnimation;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.util.Util;

/**
 * Screen showing the rules, credits and version.
 * @author Roan
 */
public class InfoMenu extends Screen{
	/**
	 * Maximum width used by the boxes.
	 */
	private static final int MAX_WIDTH = 1200;
	/**
	 * Strings in the rules box.
	 */
	private static final List<List<String>> rules = new ArrayList<List<String>>(4);
	/**
	 * Entries in the credits box.
	 */
	private static final List<Entry<String, String>> credits = new ArrayList<Entry<String, String>>(6);
	/**
	 * Height of the version box.
	 */
	private static final int VERSION_HEIGHT = 80;
	/**
	 * Height of the credits box.
	 */
	private static final int CREDITS_HEIGHT = 174;
	private static final double KEY_WIDTH = 31.0D;
	/**
	 * The latest version of the program.
	 */
	private static String version = null;
	/**
	 * Example animation that is shown in the example box.
	 */
	private Animation example = new ExampleAnimation();
	/**
	 * The state for the current game.
	 */
	private GameState game;
	/**
	 * The screen to switch to after this screen is closed.
	 */
	private Screen prev;
	
	/**
	 * Constructs a new info menu with the given game context and state
	 * and screen to return to.
	 * @param context The game context.
	 * @param game The game state, possibly <code>null</code>.
	 * @param prev The screen to switch to when this screen is closed.
	 */
	protected InfoMenu(ConvexMerger context, GameState game, Screen prev){
		super(context);
		this.game = game;
		this.prev = prev;
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, game);
		renderMenuTitle(g, width, "Information");
		drawTitle(g, width);
		
		//TODO magic
		double keysHeight = 150.0D;
		
		double size = Screen.getMaxWidth(width, 0.9D, MAX_WIDTH);
		double offset = (width - size) / 2.0D;
		g.translate(0, Screen.TOP_SPACE + TOP_SIDE_TRIANGLE);
		double boxWidth = (size - BOX_SPACING) / 2.0D;
		Paint gradient = Theme.constructBorderGradient(game, width);
		double rulesHeight = height - Screen.TOP_SPACE - TOP_SIDE_TRIANGLE - VERSION_HEIGHT - BOX_SPACING * 2.0D - Screen.BOTTOM_OFFSET - Screen.TOP_OFFSET - keysHeight;
		double exampleBoxHeight = height - Screen.TOP_SPACE - TOP_SIDE_TRIANGLE - CREDITS_HEIGHT - BOX_SPACING - Screen.BOTTOM_OFFSET - Screen.TOP_OFFSET;
		
		renderExample(g, gradient, offset + boxWidth + BOX_SPACING, 0.0D, boxWidth, exampleBoxHeight);
		renderRules(g, gradient, offset, 0.0D, boxWidth, rulesHeight);
		renderCredits(g, gradient, offset + boxWidth + BOX_SPACING, exampleBoxHeight + BOX_SPACING, boxWidth, CREDITS_HEIGHT);
		renderVersion(g, gradient, offset, rulesHeight + BOX_SPACING * 2.0D + keysHeight, boxWidth, VERSION_HEIGHT);
		renderKeys(g, gradient, offset, rulesHeight + BOX_SPACING, boxWidth, keysHeight);
	}
	
	/**
	 * Renders the shortcuts box.
	 * @param g The graphics context to use.
	 * @param gradient The paint to use to draw the gradient of the box.
	 * @param x The x position to render the box at.
	 * @param y The y position to render the box at.
	 * @param w The width of the box to render.
	 * @param h The height of the box to render.
	 */
	private void renderKeys(Graphics2D g, Paint gradient, double x, double y, double w, double h){
		drawTitledBox(g, gradient, x, y, w, h, "Shortcuts");
		
		g.setFont(Theme.PRIDI_REGULAR_14);
		FontMetrics fm = g.getFontMetrics();
		
		x += BOX_INSETS;
		y += BOX_HEADER_HEIGHT + BOX_TEXT_OFFSET * 2.0D;
		drawKeyFrame(g, x, y, "F11");
		g.drawString("FullScreen", (float)(x + KEY_WIDTH + SPACING), (float)(y + fm.getAscent()));
		y += fm.getHeight() + SPACING;
		drawKeyFrame(g, x, y, "Ctrl");
		drawKeyFrame(g, x + KEY_WIDTH + SPACING, y, "R");
		g.drawString("Game Progress", (float)(x + (KEY_WIDTH + SPACING) * 2.0D), (float)(y + fm.getAscent()));
		y += fm.getHeight() + SPACING;
		drawKeyFrame(g, x, y, "Ctrl");
		drawKeyFrame(g, x + KEY_WIDTH + SPACING, y, "C");
		g.drawString("Show Centroids", (float)(x + (KEY_WIDTH + SPACING) * 2.0D), (float)(y + fm.getAscent()));
		y += fm.getHeight() + SPACING;
		drawKeyFrame(g, x, y, "Ctrl");
		drawKeyFrame(g, x + KEY_WIDTH + SPACING, y, "D");
		g.drawString("Vertical Decomposition", (float)(x + (KEY_WIDTH + SPACING) * 2.0D), (float)(y + fm.getAscent()));
	}
	
	/**
	 * Renders a keyboard key in a frame.
	 * @param g The graphics context to use.
	 * @param x The x position to render the frame at.
	 * @param y The y position to render the frame at.
	 * @param key The key to put in the box.
	 */
	private void drawKeyFrame(Graphics2D g, double x, double y, String key){
		Path2D box = computeBox(x, y, KEY_WIDTH, 22.0D, 5.0D);
		g.setColor(Theme.LIGHTEN);
		g.fill(box);
		g.setColor(Theme.DOUBLE_LIGHTEN);
		g.setStroke(Theme.BORDER_STROKE);
		g.draw(box);
		
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Theme.BOX_TEXT_COLOR);
		g.drawString(key, (float)(x + (KEY_WIDTH - fm.stringWidth(key)) / 2.0D), (float)(y + fm.getAscent()));
	}
	
	/**
	 * Renders the version box.
	 * @param g The graphics context to use.
	 * @param gradient The paint to use to draw the gradient of the box.
	 * @param x The x position to render the box at.
	 * @param y The y position to render the box at.
	 * @param w The width of the box to render.
	 * @param h The height of the box to render.
	 */
	private void renderVersion(Graphics2D g, Paint gradient, double x, double y, double w, double h){
		drawTitledBox(g, gradient, x, y, w, h, "Version");
		
		g.setFont(Theme.PRIDI_REGULAR_14);
		FontMetrics fm = g.getFontMetrics();
		
		y += Screen.BOX_HEADER_HEIGHT + 1;
		y += fm.getAscent();
		x += Screen.BOX_TEXT_OFFSET;
		
		g.drawString("Current: TODO " + version, (float)x, (float)y);
	}
	
	/**
	 * Renders the credits box.
	 * @param g The graphics context to use.
	 * @param gradient The paint to use to draw the gradient of the box.
	 * @param x The x position to render the box at.
	 * @param y The y position to render the box at.
	 * @param w The width of the box to render.
	 * @param h The height of the box to render.
	 */
	private void renderCredits(Graphics2D g, Paint gradient, double x, double y, double w, double h){
		drawTitledBox(g, gradient, x, y, w, h, "Credits");
		
		g.setFont(Theme.PRIDI_REGULAR_14);
		FontMetrics fm = g.getFontMetrics();
		
		y += BOX_HEADER_HEIGHT + 1;
		y += fm.getAscent();
		x += BOX_INSETS;
		for(Entry<String, String> entry : credits){
			g.setColor(Theme.BOX_TEXT_COLOR);
			String name = entry.getKey();
			g.drawString(name, (float)x, (float)y);
			
			g.setColor(Theme.BOX_SECONDARY_COLOR);
			g.drawString(entry.getValue(), (float)(x + fm.stringWidth(name)), (float)y);
			
			y += fm.getHeight();
		}
	}
	
	/**
	 * Renders the rules box.
	 * @param g The graphics context to use.
	 * @param gradient The paint to use to draw the gradient of the box.
	 * @param x The x position to render the box at.
	 * @param y The y position to render the box at.
	 * @param w The width of the box to render.
	 * @param h The height of the box to render.
	 */
	private void renderRules(Graphics2D g, Paint gradient, double x, double y, double w, double h){
		drawTitledBox(g, gradient, x, y, w, h, "Rules");
		
		g.setFont(Theme.PRIDI_REGULAR_14);
		g.setColor(Theme.BOX_TEXT_COLOR);
		FontMetrics fm = g.getFontMetrics();
		h -= BOX_HEADER_HEIGHT + 1 + BOX_INSETS;
		double rulesWidth = w - 2 * BOX_INSETS;
		
		//intro
		int dy = fillText(g, (int)x + BOX_INSETS, BOX_HEADER_HEIGHT + 1, (int)Math.ceil(rulesWidth), (int)h, rules.get(0));
		
		//act 1
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("1. ", (int)x + BOX_INSETS, dy + fm.getHeight());
		int offset = fm.stringWidth("1. ");
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, (int)x + BOX_INSETS + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + fm.getHeight()), rules.get(1));
		
		//act 2
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("2. ", (int)x + BOX_INSETS, dy + fm.getHeight());
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, (int)x + BOX_INSETS + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + fm.getHeight()), rules.get(2));

		//end
		fillText(g, (int)x + BOX_INSETS, dy + fm.getHeight() - fm.getAscent(), (int)rulesWidth, (int)(h - dy + fm.getHeight()), rules.get(3));
	}
	
	/**
	 * Renders the example box.
	 * @param g The graphics context to use.
	 * @param gradient The paint to use to draw the gradient of the box.
	 * @param x The x position to render the box at.
	 * @param y The y position to render the box at.
	 * @param w The width of the box to render.
	 * @param h The height of the box to render.
	 */
	private void renderExample(Graphics2D g, Paint gradient, double x, double y, double w, double h){
		drawTitledBox(g, gradient, x, 0.0D, w, h, "Example");
		
		AffineTransform transform = g.getTransform();
		g.translate(x, Screen.BOX_HEADER_HEIGHT + 1.0D);
		double sx = w / ExampleAnimation.WIDTH;
		double sy = (h - Screen.BOX_HEADER_HEIGHT) / ExampleAnimation.HEIGHT;
		if(sx > sy){
			g.translate((w - ExampleAnimation.WIDTH * sy) / 2.0D, 0.0D);
			g.scale(sy, sy);
		}else{
			g.translate(0.0D, (h - Screen.BOX_HEADER_HEIGHT - ExampleAnimation.HEIGHT * sx) / 2.0D);
			g.scale(sx, sx);
		}
		example.run(g);
		g.setTransform(transform);
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
		this.switchScene(prev);
	}

	@Override
	protected void handleRightButtonClick(){
	}
	
	static{
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("assets/text/rules.txt")));
			String line;
			while((line = reader.readLine()) != null){
				rules.add(Arrays.asList(line.split(" ")));
			}
			reader.close();
		}catch(IOException e){
			//should not happen
			throw new RuntimeException("Failed to load internal resources.", e);
		}
		
		credits.add(new SimpleEntry<String, String>("Roan (RoanH): ", "Game Design & Implementation"));
		credits.add(new SimpleEntry<String, String>("RockRoller: ", "UI Design & Logo"));
		credits.add(new SimpleEntry<String, String>("Thiam-Wai: ", "Playfield Generation"));
		credits.add(new SimpleEntry<String, String>("Emiliyan: ", "Vertical Decomposition"));
		credits.add(new SimpleEntry<String, String>("phosphoricons.com: ", "UI Icons"));
		credits.add(new SimpleEntry<String, String>("Cadson Demak: ", "Pridi Font"));
		
		Thread versionChecker = new Thread(){
			
			@Override
			public void run(){
				version = Util.checkVersion("RoanH", "ConvexMerger");
				if(version == null){
					version = "Unknown";
				}
			}
		};
		versionChecker.setDaemon(true);
		versionChecker.setName("VersionChecker");
		versionChecker.start();
	}
}
