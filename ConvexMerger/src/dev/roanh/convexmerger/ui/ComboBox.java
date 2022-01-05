package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

public class ComboBox<T>{
	private static final double CELL_HEIGHT = 20.0D;
	private Color color;
	private Rectangle2D bounds = new Rectangle2D.Double();
	private Rectangle2D list = null;
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
		int idx = getSelectedIndex(loc);
		if(idx != -1){
			value = values[idx];
			list = null;
		}
	}
	
	public boolean hasFocus(){
		return focus;
	}
	
	private int getSelectedIndex(Point2D loc){
		if(list != null && list.contains(loc)){
			return (int)Math.floor((loc.getY() - list.getY()) / CELL_HEIGHT);
		}else{
			return -1;
		}
	}
	
	public boolean render(Graphics2D g, double x, double y, double width, double height, Point2D loc){
		g.setColor(Theme.DOUBLE_LIGHTEN);
		bounds = new Rectangle2D.Double(x, y, width, height);
		g.fill(bounds);
		
		g.setStroke(Theme.BORDER_STROKE);
		g.setColor(Theme.BOX_TEXT_COLOR);
		g.setFont(Theme.PRIDI_MEDIUM_14);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(toString.apply(value), (float)(x + 4.0F), (float)(y + height - fm.getMaxDescent()));
		
		if(focus){
			list = new Rectangle2D.Double(x, y + height, width, CELL_HEIGHT * values.length);
			g.setColor(Theme.DOUBLE_LIGHTEN);
			g.fill(list);
			
			for(int i = 0; i < values.length; i++){
				double lh = y + height + CELL_HEIGHT * (i + 1);
				if(list.contains(loc) && loc.getY() > lh - CELL_HEIGHT && loc.getY() < lh){
					g.setColor(color);
					g.fill(new Rectangle2D.Double(x, lh - CELL_HEIGHT, width, CELL_HEIGHT));
				}
				g.setColor(Theme.BOX_TEXT_COLOR);
				g.drawString(toString.apply(values[i]), (float)(x + 4.0F), (float)(lh - fm.getMaxDescent() + 1.0D));
			}
		}
		
		g.setColor(color);
		g.draw(new Line2D.Double(x, y + height - 1, x + width - 1, y + height - 1));
		
		return focus;
	}
}
