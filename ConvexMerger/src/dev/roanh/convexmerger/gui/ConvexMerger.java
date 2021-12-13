package dev.roanh.convexmerger.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.GameState;

public class ConvexMerger{
	private JFrame frame = new JFrame(Constants.TITLE);
	private GameState state;
	
	
	
	
	
	public void showGame(){
		
		
		
		
		
		
		
		
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	
	private final class GamePanel extends JPanel implements MouseListener{
		
		private GamePanel(){
			this.addMouseListener(this);
		}
		
		@Override
		public void paintComponent(Graphics g1){
			Graphics2D g = (Graphics2D)g1;

		}

		@Override
		public void mouseClicked(MouseEvent e){
		}

		@Override
		public void mousePressed(MouseEvent e){
		}

		@Override
		public void mouseReleased(MouseEvent e){
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e){
		}

		@Override
		public void mouseExited(MouseEvent e){
		}
	}
}
