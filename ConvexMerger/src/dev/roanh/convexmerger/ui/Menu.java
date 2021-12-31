package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_WIDTH;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_TEXT_OFFSET;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_SPACE;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_OFFSET;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Base class for all menus containing shared rendering subroutines.
 * @author Roan
 */
public abstract interface Menu{
	public static final int BOX_INSETS = 10;
	public static final int BOX_HEADER_HEIGHT = 28;
	public static final int BOX_TEXT_OFFSET = 4;

	public abstract boolean render(Graphics2D g, int width, int height);
	
	public default void drawTitledBox(Graphics2D g, Paint gradient, double x, double y, double w, double h, String title){
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
	
	public default void drawBox(Graphics2D g, double x, double y, double w, double h){
		g.setColor(Theme.MENU_BODY);
		
		Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 8);
		path.moveTo(x, y + BOX_INSETS);
		path.lineTo(x + BOX_INSETS, y);
		path.lineTo(x + w - BOX_INSETS, y);
		path.lineTo(x + w, y + BOX_INSETS);
		path.lineTo(x + w, y + h - BOX_INSETS);
		path.lineTo(x + w - BOX_INSETS, y + h);
		path.lineTo(x + BOX_INSETS, y + h);
		path.lineTo(x, y + h - BOX_INSETS);
		path.closePath();
		
		g.fill(path);
	}
	
	public default void renderMenuTitle(Graphics2D g, int width, String title){
		g.setFont(Theme.PRIDI_REGULAR_18);
		g.setColor(Theme.CROWN_COLOR);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title, Math.floorDiv(width, 2) - (TOP_MIDDLE_WIDTH / 2) + (TOP_MIDDLE_WIDTH - fm.stringWidth(title)) / 2.0F, TOP_SPACE + TOP_OFFSET - fm.getDescent() - TOP_MIDDLE_TEXT_OFFSET);
	}
	
	public default int fillText(Graphics2D g, int rx, int ry, int width, int height, List<String> text){
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
		return y;
	}
}
