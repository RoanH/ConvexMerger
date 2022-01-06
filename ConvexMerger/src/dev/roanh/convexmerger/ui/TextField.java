package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Text field UI component.
 * @author Roan
 */
public class TextField{
	/**
	 * The accent color for this text field.
	 */
	private Color color;
	private Rectangle2D bounds = new Rectangle2D.Double();
	private String text = "";
	private boolean focus = false;
	
	public TextField(Color color){
		this.color = color;
	}
	
	/**
	 * Gets the text in this text field.
	 * @return The text in this text field.
	 */
	public String getText(){
		return text;
	}
	
	public boolean hasFocus(){
		return focus;
	}
	
	public void handleKeyEvent(KeyEvent event){
		if(event.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			if(!text.isEmpty()){
				text = text.substring(0, text.length() - 1);
			}
		}else if(event.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
			text += event.getKeyChar();
		}
	}
	
	public void handleMouseClick(Point2D loc){
		focus = bounds.contains(loc);
	}
	
	protected void render(Graphics2D g, double x, double y, double width, double height){
		g.setColor(Theme.DOUBLE_LIGHTEN);
		bounds = new Rectangle2D.Double(x, y, width, height);
		g.fill(bounds);
		g.setClip(bounds);
		
		g.setStroke(Theme.BORDER_STROKE);
		g.setColor(Theme.BOX_TEXT_COLOR);
		g.setFont(Theme.PRIDI_MEDIUM_14);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, (float)(x + 4.0F), (float)(y + height - fm.getMaxDescent()));
		if(focus && ((System.currentTimeMillis() / 600) % 2 == 0)){
			int lx = (int)Math.ceil(x + 4.0F + fm.stringWidth(text));
			g.setColor(color);
			g.drawLine(lx, (int)(y + 2.0F), lx, (int)(y + height - 4.0F));
		}
		
		g.setColor(color);
		g.draw(new Line2D.Double(x, y + height - 1, x + width - 1, y + height - 1));
		g.setClip(null);
	}
}
