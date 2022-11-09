/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Function;

/**
 * Represents a UI combo box.
 * @author Roan
 * @param <T> The value data type.
 */
public class ComboBox<T>{
	/**
	 * Height of the combo box field and drop down item fields.
	 */
	private static final double CELL_HEIGHT = 20.0D;
	/**
	 * Combo box accent color.
	 */
	private Color color;
	/**
	 * The bounds for the main field of this combo box.
	 */
	private Rectangle2D bounds = new Rectangle2D.Double();
	/**
	 * The bounds for the drop down list of this combo box.
	 */
	private Rectangle2D list = null;
	/**
	 * The currently selected value.
	 */
	private T value;
	/**
	 * The list of possible values.
	 */
	private T[] values;
	/**
	 * The function to use to turn the values into strings.
	 */
	private Function<T, String> toString;
	/**
	 * Whether this combo box currently has focus.
	 */
	private boolean focus = false;
	
	/**
	 * Constructs a new combo box with the given values, to string
	 * function and accent color. The first given value will be
	 * selected initially.
	 * @param values The values for this combo box.
	 * @param toString The function to convert the values to a string.
	 * @param color The accent color.
	 */
	public ComboBox(T[] values, Function<T, String> toString, Color color){
		this.color = color;
		this.value = values[0];
		this.values = values;
		this.toString = toString;
	}
	
	/**
	 * Gets the value selected in this combo box.
	 * @return The selected value.
	 */
	public T getValue(){
		return value;
	}
	
	/**
	 * Handles a mouse click on this combo box.
	 * @param loc The location that was clicked.
	 */
	public void handleMouseClick(Point2D loc){
		int idx = getSelectedIndex(loc);
		if(idx != -1){
			value = values[idx];
			list = null;
		}
		
		if(bounds.contains(loc)){
			focus = true;
		}else{
			list = null;
			focus = false;
		}
	}
	
	/**
	 * Checks if this combo box currently has focus.
	 * @return True if this combo box has focus.
	 */
	public boolean hasFocus(){
		return focus;
	}
	
	/**
	 * Computes the drop down list selected index given
	 * the list bounds.
	 * @param loc The location selected.
	 * @return The selected index or -1 if none.
	 */
	private int getSelectedIndex(Point2D loc){
		if(list != null && list.contains(loc)){
			return (int)Math.floor((loc.getY() - list.getY()) / CELL_HEIGHT);
		}else{
			return -1;
		}
	}
	
	/**
	 * Renders this combo box according to the given parameters.
	 * @param g The graphics context to use.
	 * @param x The x coordinate of the top left corner.
	 * @param y The y coordinate of the top left corner.
	 * @param width The width of the combo box.
	 * @param height The height of the main field of the combo box.
	 * @param loc The current mouse location.
	 */
	protected void render(Graphics2D g, double x, double y, double width, double height, Point2D loc){
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
		g.drawImage(Theme.CHEVRON_ICON, (int)(x + width - 1 - Theme.CHEVRON_ICON_SIZE), (int)y, null);
	}
}
