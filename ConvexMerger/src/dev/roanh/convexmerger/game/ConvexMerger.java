package dev.roanh.convexmerger.game;

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
				g.setColor(obj.isOwned() ? obj.getOwner().getColor() : Color.BLACK);
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
			System.out.println("release");
			if(state.getActivePlayer().isHuman()){
				ConvexObject obj = state.getObject(e.getX(), e.getY());//TODO may require transforms later
				if(obj != null){
					state.claimObject(obj);
					repaint();
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e){
		}

		@Override
		public void mouseExited(MouseEvent e){
		}
	}
}
