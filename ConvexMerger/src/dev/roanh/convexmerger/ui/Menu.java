package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_WIDTH;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_TEXT_OFFSET;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_SPACE;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_OFFSET;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;

public abstract interface Menu{

	public abstract boolean render(Graphics2D g, int width, int height);
	
	public default void drawTitledBox(Graphics2D g, Paint gradient, double x, double y, double w, double h){
		
	}
	
	public default void drawBox(Graphics2D g, double x, double y, double w, double h){
		//g.setColor(c);
	}
	
	public default void renderMenuTitle(Graphics2D g, int width, String title){
		g.setFont(Theme.PRIDI_REGULAR_18);
		g.setColor(Theme.CROWN_COLOR);
		FontMetrics fm = g.getFontMetrics();
		String msg = "Information";
		g.drawString(msg, Math.floorDiv(width, 2) - (TOP_MIDDLE_WIDTH / 2) + (TOP_MIDDLE_WIDTH - fm.stringWidth(msg)) / 2.0F, TOP_SPACE + TOP_OFFSET - fm.getDescent() - TOP_MIDDLE_TEXT_OFFSET);
	}
}
