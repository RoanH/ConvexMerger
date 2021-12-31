package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import dev.roanh.convexmerger.Constants;

/**
 * Menu showing the rules, credits and version.
 * @author Roan
 */
public class InfoMenu implements Menu{

	@Override
	public boolean render(Graphics2D g, int width, int height){
		renderMenuTitle(g, width, "Information");
		
		//title
		g.setColor(Color.WHITE);
		g.setFont(Theme.PRIDI_MEDIUM_30);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(Constants.TITLE, (width - fm.stringWidth(Constants.TITLE)) / 2.0F, (GamePanel.TOP_SPACE + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
		
		
		
		
		
		
		
		
		
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
