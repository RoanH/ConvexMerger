/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.ui;

import java.awt.Desktop;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import dev.roanh.convexmerger.Constants;
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
	private static final int VERSION_HEIGHT = 70;
	/**
	 * Height of the credits box.
	 */
	private static final int CREDITS_HEIGHT = 174;
	/**
	 * Height of the shortcuts box.
	 */
	private static final int KEYS_HEIGHT = 231;
	/**
	 * Width of a keyboard key frame.
	 */
	private static final double KEY_WIDTH = 31.0D;
	/**
	 * The latest version of the program.
	 */
	private static volatile String version = "Checking...";
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
	 * Bounds of the current version link.
	 */
	private Rectangle2D currentBox = new Rectangle2D.Double();
	/**
	 * Bounds of the latest version link.
	 */
	private Rectangle2D latestBox = new Rectangle2D.Double();
	/**
	 * Bounds of the GitHub link.
	 */
	private Rectangle2D githubBox = new Rectangle2D.Double();
	
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
		
		double size = Screen.getMaxWidth(width, 0.9D, MAX_WIDTH);
		double offset = (width - size) / 2.0D;
		double boxWidth = (size - BOX_SPACING) / 2.0D;
		Paint gradient = Theme.constructBorderGradient(game, width);
		double rulesHeight = height - TOP_SPACE - TOP_MIDDLE_OFFSET - BOX_SPACING * 3.0D - BOTTOM_OFFSET - KEYS_HEIGHT;
		double exampleBoxHeight = height - TOP_SPACE - TOP_MIDDLE_OFFSET - CREDITS_HEIGHT - BOX_SPACING * 4.0D - BOTTOM_OFFSET - VERSION_HEIGHT;
		
		renderExample(g, gradient, offset + boxWidth + BOX_SPACING, TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING, boxWidth, exampleBoxHeight);
		renderRules(g, gradient, offset, TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING, boxWidth, rulesHeight);
		renderCredits(g, gradient, offset + boxWidth + BOX_SPACING, exampleBoxHeight + TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D, boxWidth, CREDITS_HEIGHT);
		renderVersion(g, gradient, offset + boxWidth + BOX_SPACING, exampleBoxHeight + TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING * 3.0D + CREDITS_HEIGHT, boxWidth, VERSION_HEIGHT, mouseLoc);
		renderKeys(g, gradient, offset, rulesHeight + BOX_SPACING + TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING, boxWidth, KEYS_HEIGHT);
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
		g.drawString("Show Vertical Decomposition", (float)(x + (KEY_WIDTH + SPACING) * 2.0D), (float)(y + fm.getAscent()));
		y += fm.getHeight() + SPACING;
		drawKeyFrame(g, x, y, "Ctrl");
		drawKeyFrame(g, x + KEY_WIDTH + SPACING, y, "S");
		g.drawString("Show Segment Partition Tree (conjugation)", (float)(x + (KEY_WIDTH + SPACING) * 2.0D), (float)(y + fm.getAscent()));
		y += fm.getHeight() + SPACING;
		drawKeyFrame(g, x, y, "Ctrl");
		drawKeyFrame(g, x + KEY_WIDTH + SPACING, y, "K");
		g.drawString("Show Segment Partition Tree (kd-tree)", (float)(x + (KEY_WIDTH + SPACING) * 2.0D), (float)(y + fm.getAscent()));
		y += fm.getHeight() + SPACING;
		drawKeyFrame(g, x, y, "Ctrl");
		drawKeyFrame(g, x + KEY_WIDTH + SPACING, y, "M");
		g.drawString("Show Merge Calipers", (float)(x + (KEY_WIDTH + SPACING) * 2.0D), (float)(y + fm.getAscent()));
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
	 * @param mouseLoc The current cursor location.
	 */
	private void renderVersion(Graphics2D g, Paint gradient, double x, double y, double w, double h, Point2D mouseLoc){
		drawTitledBox(g, gradient, x, y, w, h, "Version");
		
		g.setFont(Theme.PRIDI_REGULAR_14);
		g.setStroke(Theme.BORDER_STROKE);
		FontMetrics fm = g.getFontMetrics();
		
		y += Screen.BOX_HEADER_HEIGHT + 1;
		y += fm.getAscent() + fm.getDescent();
		x += Screen.BOX_TEXT_OFFSET;
		
		final String ver = version;
		float dx = (float)(x + (w - fm.stringWidth("Current:  \u2022 Latest:  \u2022 Visit GitHub Page") - fm.stringWidth(ver) - fm.stringWidth(Constants.VERSION)) / 2.0F);
		
		String text = "Current: ";
		g.drawString(text, dx, (float)y);
		dx += fm.stringWidth(text);
		
		g.setColor(Theme.LINK_COLOR);
		g.drawString(Constants.VERSION, dx, (float)y);
		currentBox.setRect(dx, y - fm.getAscent(), fm.stringWidth(Constants.VERSION), fm.getAscent() + fm.getDescent());
		dx += currentBox.getWidth();
		if(currentBox.contains(mouseLoc)){
			g.draw(new Line2D.Double(currentBox.getMinX(), y + fm.getDescent() / 2.0D, currentBox.getMaxX(), y + fm.getDescent() / 2.0D));
		}
		
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		text = " \u2022 ";
		g.drawString(text, dx, (float)y);
		dx += fm.stringWidth(text);
		
		g.setColor(Theme.BOX_TEXT_COLOR);
		text = "Latest: ";
		g.drawString(text, dx, (float)y);
		dx += fm.stringWidth(text);
		
		g.setColor(Theme.LINK_COLOR);
		g.drawString(ver, dx, (float)y);
		latestBox.setRect(dx, y - fm.getAscent(), fm.stringWidth(ver), fm.getAscent() + fm.getDescent());
		dx += latestBox.getWidth();
		if(latestBox.contains(mouseLoc)){
			g.draw(new Line2D.Double(latestBox.getMinX(), y + fm.getDescent() / 2.0D, latestBox.getMaxX(), y + fm.getDescent() / 2.0D));
		}
		
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		text = " \u2022 ";
		g.drawString(text, dx, (float)y);
		dx += fm.stringWidth(text);

		g.setColor(Theme.LINK_COLOR);
		text = "Visit GitHub Page";
		g.drawString(text, dx, (float)y);
		githubBox.setRect(dx, y - fm.getAscent(), fm.stringWidth(text), fm.getAscent() + fm.getDescent());
		if(githubBox.contains(mouseLoc)){
			g.draw(new Line2D.Double(githubBox.getMinX(), y + fm.getDescent() / 2.0D, githubBox.getMaxX(), y + fm.getDescent() / 2.0D));
		}
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
		int dy = fillText(g, (int)x + BOX_INSETS, (int)(y + BOX_HEADER_HEIGHT + 1), (int)Math.ceil(rulesWidth), (int)h, rules.get(0));
		
		//act 1
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("1. ", (int)x + BOX_INSETS, dy + fm.getHeight());
		int offset = fm.stringWidth("1. ");
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, (int)x + BOX_INSETS + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + y + fm.getHeight()), rules.get(1));
		
		//act 2
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("2. ", (int)x + BOX_INSETS, dy + fm.getHeight());
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, (int)x + BOX_INSETS + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + y + fm.getHeight()), rules.get(2));

		//end
		fillText(g, (int)x + BOX_INSETS, dy + fm.getHeight() - fm.getAscent(), (int)rulesWidth, (int)(h - dy + y + fm.getHeight()), rules.get(3));
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
		drawTitledBox(g, gradient, x, y, w, h, "Example");
		
		AffineTransform transform = g.getTransform();
		g.translate(x, y + Screen.BOX_HEADER_HEIGHT + 1.0D);
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
	public void handleMouseRelease(Point2D loc, int width, int height){
		super.handleMouseRelease(loc, width, height);
		
		try{
			if(currentBox.contains(loc)){
				Desktop.getDesktop().browse(new URI("https://github.com/RoanH/ConvexMerger/releases/tag/" + Constants.VERSION));
			}
			
			if(latestBox.contains(loc)){
				String uri = "https://github.com/RoanH/ConvexMerger/releases";
				if(version.matches("v\\d\\.\\d")){
					uri = uri + "/tag/" + version;
				}
				Desktop.getDesktop().browse(new URI(uri));
			}
			
			if(githubBox.contains(loc)){
				Desktop.getDesktop().browse(new URI("https://github.com/RoanH/ConvexMerger"));
			}
		}catch(IOException | URISyntaxException e){
			//pity but not important
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
		this.switchScene(prev);
	}

	@Override
	protected void handleRightButtonClick(){
	}
	
	static{
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("assets/text/rules.txt")))){
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
		credits.add(new SimpleEntry<String, String>("Phosphor Icons: ", "UI Icons"));
		credits.add(new SimpleEntry<String, String>("Cadson Demak: ", "Pridi Font"));
		
		Util.checkVersion("RoanH", "ConvexMerger", ver->version = ver.orElse("Unknown"));
	}
}
