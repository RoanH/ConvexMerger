package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_SIDE_TRIANGLE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
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
		
		double creditsHeight = 230.0D;
		int rulesHeight = 300;
		double exampleBoxHeight = height - GamePanel.TOP_SPACE - TOP_SIDE_TRIANGLE - creditsHeight - BOX_SPACING - GamePanel.BOTTOM_OFFSET - GamePanel.TOP_OFFSET;
		
		//TODO lots of magic below
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE, 0.0D, boxWidth, rulesHeight, "Rules");
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, 0.0D, boxWidth, exampleBoxHeight, "Example");
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, exampleBoxHeight + BOX_SPACING, boxWidth, 230, "Credits");

		//rules
		g.setFont(Theme.PRIDI_REGULAR_12);
		g.setColor(Theme.BOX_TEXT_COLOR);
		fm = g.getFontMetrics();
		
		//intro
		rulesHeight -= Menu.BOX_HEADER_HEIGHT + 1 + Menu.BOX_INSETS;
		double rulesWidth = boxWidth - 2 * Menu.BOX_TEXT_OFFSET;
		int y = fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, Menu.BOX_HEADER_HEIGHT + 1, (int)rulesWidth, rulesHeight, rules.get(0));
		
		//act 1
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("1. ", TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, y + fm.getHeight());
		int offset = fm.stringWidth("1. ");
		g.setColor(Theme.BOX_TEXT_COLOR);
		y = fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET + offset, y + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), rulesHeight - y + fm.getHeight(), rules.get(1));
		
		//act 2
		g.setColor(Theme.BOX_SECONDARY_COLOR);
		g.drawString("2. ", TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, y + fm.getHeight());
		g.setColor(Theme.BOX_TEXT_COLOR);
		y = fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET + offset, y + fm.getHeight() - fm.getAscent(), (int)(rulesWidth - offset), rulesHeight - y + fm.getHeight(), rules.get(2));

		//end
		fillText(g, TOP_SIDE_TRIANGLE + Menu.BOX_TEXT_OFFSET, y + fm.getHeight() - fm.getAscent(), (int)rulesWidth, rulesHeight - y + fm.getHeight(), rules.get(3));
		
		//example
		g.translate(TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, Menu.BOX_HEADER_HEIGHT + 1.0D);
		double sx = boxWidth / ExampleAnimation.WIDTH;
		double sy = (exampleBoxHeight - Menu.BOX_HEADER_HEIGHT) / ExampleAnimation.HEIGHT;
		if(sx > sy){
			g.translate((boxWidth - ExampleAnimation.WIDTH * sy) / 2.0D, 0.0D);
			g.scale(sy, sy);
		}else{
			g.translate(0.0D, (exampleBoxHeight - Menu.BOX_HEADER_HEIGHT - ExampleAnimation.HEIGHT * sx) / 2.0D);
			g.scale(sx, sx);
		}
		example.run(g);
		
		//System.out.println(boxWidth);
		
		// TODO Auto-generated method stub
		return true;
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
