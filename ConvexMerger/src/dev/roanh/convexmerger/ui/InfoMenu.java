package dev.roanh.convexmerger.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
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
 * Menu showing the rules, credits and version.
 * @author Roan
 */
public class InfoMenu extends Menu{
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
	/**
	 * The latest version of the program.
	 */
	private static String version = null;
	/**
	 * Example animation that is shown in the example box.
	 */
	private Animation example = new ExampleAnimation();
	private GameState game;
	
	protected InfoMenu(GameState game){
		this.game = game;
	}

	@Override
	public void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMenuTitle(g, width, "Information");
		drawTitle(g, width);
		
		double size = Menu.getMaxWidth(width, 0.9D, MAX_WIDTH);
		double offset = (width - size) / 2.0D;
		g.translate(0, GamePanel.TOP_SPACE + TOP_SIDE_TRIANGLE);
		double boxWidth = (size - BOX_SPACING) / 2.0D;
		Paint gradient = Theme.constructBorderGradient(game, width);
		double rulesHeight = height - GamePanel.TOP_SPACE - TOP_SIDE_TRIANGLE - VERSION_HEIGHT - BOX_SPACING - GamePanel.BOTTOM_OFFSET - GamePanel.TOP_OFFSET;
		double exampleBoxHeight = height - GamePanel.TOP_SPACE - TOP_SIDE_TRIANGLE - CREDITS_HEIGHT - BOX_SPACING - GamePanel.BOTTOM_OFFSET - GamePanel.TOP_OFFSET;
		
		renderExample(g, gradient, offset + boxWidth + BOX_SPACING, 0.0D, boxWidth, exampleBoxHeight);
		renderRules(g, gradient, offset, 0.0D, boxWidth, rulesHeight);
		renderCredits(g, gradient, offset + boxWidth + BOX_SPACING, exampleBoxHeight + BOX_SPACING, boxWidth, CREDITS_HEIGHT);
		renderVersion(g, gradient, offset, rulesHeight + BOX_SPACING, boxWidth, VERSION_HEIGHT);
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
		
		y += Menu.BOX_HEADER_HEIGHT + 1;
		y += fm.getAscent();
		x += Menu.BOX_TEXT_OFFSET;
		
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
		
		y += Menu.BOX_HEADER_HEIGHT + 1;
		y += fm.getAscent();
		x += Menu.BOX_TEXT_OFFSET;
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
		h -= Menu.BOX_HEADER_HEIGHT + 1 + Menu.BOX_INSETS;
		double rulesWidth = w - 2 * Menu.BOX_TEXT_OFFSET;
		
		//intro
		int dy = fillText(g, (int)x + Menu.BOX_TEXT_OFFSET, Menu.BOX_HEADER_HEIGHT + 1, (int)rulesWidth, (int)h, rules.get(0));
		
		//act 1
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("1. ", (int)x + Menu.BOX_TEXT_OFFSET, dy + fm.getHeight());
		int offset = fm.stringWidth("1. ");
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, (int)x + Menu.BOX_TEXT_OFFSET + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + fm.getHeight()), rules.get(1));
		
		//act 2
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("2. ", (int)x + Menu.BOX_TEXT_OFFSET, dy + fm.getHeight());
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, (int)x + Menu.BOX_TEXT_OFFSET + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + fm.getHeight()), rules.get(2));

		//end
		fillText(g, (int)x + Menu.BOX_TEXT_OFFSET, dy + fm.getHeight() - fm.getAscent(), (int)rulesWidth, (int)(h - dy + fm.getHeight()), rules.get(3));
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
		g.translate(x, Menu.BOX_HEADER_HEIGHT + 1.0D);
		double sx = w / ExampleAnimation.WIDTH;
		double sy = (h - Menu.BOX_HEADER_HEIGHT) / ExampleAnimation.HEIGHT;
		if(sx > sy){
			g.translate((w - ExampleAnimation.WIDTH * sy) / 2.0D, 0.0D);
			g.scale(sy, sy);
		}else{
			g.translate(0.0D, (h - Menu.BOX_HEADER_HEIGHT - ExampleAnimation.HEIGHT * sx) / 2.0D);
			g.scale(sx, sx);
		}
		example.run(g);
		g.setTransform(transform);
	}

	@Override
	public boolean isLeftButtonEnabled(){
		return true;
	}

	@Override
	public boolean isRightButtonEnabled(){
		return false;
	}

	@Override
	public String getLeftButtonText(){
		return "Back";
	}

	@Override
	public String getRightButtonText(){
		return null;
	}

	@Override
	public void handleLeftButtonClick(){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRightButtonClick(){
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
