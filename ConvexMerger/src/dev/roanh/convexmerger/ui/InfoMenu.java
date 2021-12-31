package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_SIDE_TRIANGLE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.animation.Animation;
import dev.roanh.convexmerger.animation.ExampleAnimation;

/**
 * Menu showing the rules, credits and version.
 * @author Roan
 */
public class InfoMenu implements Menu{
	private static final List<List<String>> rules = new ArrayList<List<String>>(4);
	private static final int BOX_SPACING = 12;
	private Animation example = new ExampleAnimation();

	@Override
	public boolean render(Graphics2D g, int width, int height){
		renderMenuTitle(g, width, "Information");
		
		//title
		g.setColor(Color.WHITE);
		g.setFont(Theme.PRIDI_MEDIUM_30);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(Constants.TITLE, (width - fm.stringWidth(Constants.TITLE)) / 2.0F, (GamePanel.TOP_SPACE + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
		
		g.translate(0, GamePanel.TOP_SPACE + TOP_SIDE_TRIANGLE);
		
		double boxWidth = (width - TOP_SIDE_TRIANGLE * 2 - BOX_SPACING) / 2.0D;
		
		Paint gradient = Theme.constructBorderGradient(null, width);
		
		//TODO lots of magic below
		double creditsHeight = 230.0D;
		int rulesHeight = 300;
		double exampleBoxHeight = height - GamePanel.TOP_SPACE - TOP_SIDE_TRIANGLE - creditsHeight - BOX_SPACING - GamePanel.BOTTOM_OFFSET - GamePanel.TOP_OFFSET;
		
		renderExample(g, gradient, TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, 0.0D, boxWidth, exampleBoxHeight);
		renderRules(g, gradient, TOP_SIDE_TRIANGLE, 0.0D, boxWidth, rulesHeight);
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, exampleBoxHeight + BOX_SPACING, boxWidth, 230, "Credits");

		
		return true;
	}
	
	private void renderRules(Graphics2D g, Paint gradient, double x, double y, double w, double h){
		drawTitledBox(g, gradient, x, y, w, h, "Rules");
		
		g.setFont(Theme.PRIDI_REGULAR_12);
		g.setColor(Theme.BOX_TEXT_COLOR);
		FontMetrics fm = g.getFontMetrics();
		h -= Menu.BOX_HEADER_HEIGHT + 1 + Menu.BOX_INSETS;
		double rulesWidth = w - 2 * Menu.BOX_TEXT_OFFSET;
		
		//intro
		int dy = fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, Menu.BOX_HEADER_HEIGHT + 1, (int)rulesWidth, (int)h, rules.get(0));
		
		//act 1
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("1. ", TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, dy + fm.getHeight());
		int offset = fm.stringWidth("1. ");
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + fm.getHeight()), rules.get(1));
		
		//act 2
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("2. ", TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, dy + fm.getHeight());
		g.setColor(Theme.BOX_TEXT_COLOR);
		dy = fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET + offset, dy + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), (int)(h - dy + fm.getHeight()), rules.get(2));

		//end
		fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, dy + fm.getHeight() - fm.getAscent(), (int)rulesWidth, (int)(h - dy + fm.getHeight()), rules.get(3));
	}
	
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
	}
}
