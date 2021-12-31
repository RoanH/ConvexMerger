package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_SIDE_TRIANGLE;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
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
		double exampleBoxHeight = height - GamePanel.TOP_SPACE - TOP_SIDE_TRIANGLE - creditsHeight - BOX_SPACING - GamePanel.BOTTOM_OFFSET - GamePanel.TOP_OFFSET;
		
		//TODO lots of magic below
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE, 0.0D, boxWidth, 400, "Rules");
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, 0.0D, boxWidth, exampleBoxHeight, "Example");
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, exampleBoxHeight + BOX_SPACING, boxWidth, 230, "Credits");

		
		List<String> text = Arrays.asList("These are positioned in the center of the 28px main body. After these 28px there is a 1px border that goes from the left to the right edge, which is colored in the gradient the playfield broder uses.".split(" "));
		Rectangle rect = new Rectangle(TOP_SIDE_TRIANGLE + 40, 40, 200, 260);
		g.setColor(Color.RED);
		g.draw(rect);
		fillText(g, rect.x, rect.y, rect.width, rect.height, text);
		
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
	
}
