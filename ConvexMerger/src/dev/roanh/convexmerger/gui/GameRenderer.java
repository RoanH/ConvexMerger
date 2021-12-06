package dev.roanh.convexmerger.gui;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.roanh.convexmerger.game.GameState;

public class GameRenderer{
	private JFrame frame = new JFrame("Convex Merger");
	private GameState game;
	
	
	
	
	private static class GamePanel extends JPanel{
		
		
		@Override
		public void paintComponent(Graphics g){
			
		}
	}
}
