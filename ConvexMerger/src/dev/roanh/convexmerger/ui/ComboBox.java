package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

public class ComboBox<T>{
	private Color color;
	private Rectangle2D bounds = new Rectangle2D.Double();
	private T value;
	private T[] values;
	private Function<T, String> toString;
	private boolean focus = false;
	
	public ComboBox(T[] values, Function<T, String> toString, Color color){
		this.color = color;
		this.value = values[0];
		this.values = values;
		this.toString = toString;
	}
	
	public void handleMouseClick(Point2D loc){
		focus = bounds.contains(loc);
	}
	
	public boolean render(Graphics2D g, double x, double y, double width, double height){
		g.setColor(Theme.DOUBLE_LIGHTEN);
		bounds = new Rectangle2D.Double(x, y, width, height);
		g.fill(bounds);
		
		g.setStroke(Theme.BORDER_STROKE);
		g.setColor(Theme.BOX_TEXT_COLOR);
		g.setFont(Theme.PRIDI_MEDIUM_14);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(toString.apply(value), (float)(x + 4.0F), (float)(y + height - fm.getMaxDescent()));
		
		if(focus){
			//TODO draw options and store bounding
		}else{
			g.setColor(color);
			g.draw(new Line2D.Double(x, y + height - 1, x + width - 1, y + height - 1));
		}
		
		return focus;
	}
}
