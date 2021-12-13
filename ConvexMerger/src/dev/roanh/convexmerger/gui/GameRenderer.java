package dev.roanh.convexmerger.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;

public class GameRenderer{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -3795126566870826056L;
	private GameState game;
	
	
	
	
	public void render(Graphics2D g){
		for(ConvexObject obj : game.getObjects()){
			//TODO temp
			g.fill(obj.getShape());
		}
		
		g.setColor(Color.BLACK);
		for(Line2D line : game.getVerticalDecompLines()){
			g.draw(line);
		}
	}
}
