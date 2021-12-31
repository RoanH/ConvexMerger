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

/**
 * Menu showing the rules, credits and version.
 * @author Roan
 */
public class InfoMenu implements Menu{
	private static final int BOX_SPACING = 12;

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
		
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE, 0.0D, boxWidth, 400, "Rules");
		drawTitledBox(g, gradient, TOP_SIDE_TRIANGLE + boxWidth + BOX_SPACING, 0.0D, boxWidth, 400, "Example");

		
		List<String> text = Arrays.asList("These are positioned in the center of the 28px main body. After these 28px there is a 1px border that goes from the left to the right edge, which is colored in the gradient the playfield broder uses.".split(" "));
		Rectangle rect = new Rectangle(TOP_SIDE_TRIANGLE + 20, 40, 200, 260);
		g.setColor(Color.RED);
		g.draw(rect);
		fillText(g, rect.x, rect.y, rect.width, rect.height, text);
		
		
		
		
		// TODO Auto-generated method stub
		return true;
	}

	
	
}
