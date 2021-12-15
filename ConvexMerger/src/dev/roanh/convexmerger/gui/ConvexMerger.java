package dev.roanh.convexmerger.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.HumanPlayer;
import dev.roanh.convexmerger.game.PlayfieldGenerator;

public class ConvexMerger{
	private JFrame frame = new JFrame(Constants.TITLE);
	private GameState state;
	
	
	
	
	
	public void showGame(){
		
		
		
		JPanel content = new JPanel(new BorderLayout());
		
		
		content.add(new GamePanel(), BorderLayout.CENTER);
		
		
		
		frame.add(content);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void initialiseGame(){
		
		
		//TODO this is just fixed static data
		state = new GameState(new PlayfieldGenerator().generatePlayfield(), Arrays.asList(new HumanPlayer(), new HumanPlayer()));
		
	}
	
	
	private final class GamePanel extends JPanel implements MouseListener{
		
		private GamePanel(){
			this.addMouseListener(this);
		}
		
		public void renderGame(Graphics2D g){
			//TODO temp
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			
			for(ConvexObject obj : state.getObjects()){
				//TODO temp
				g.fill(obj.getShape());
			}
			
			g.setColor(Color.BLACK);
			for(Line2D line : state.getVerticalDecompLines()){
				g.draw(line);
			}
		}
		
		@Override
		public void paintComponent(Graphics g1){
			Graphics2D g = (Graphics2D)g1;

			renderGame(g);
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
