package dev.roanh.convexmerger.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;

public class ConvexMerger{
	private static final int TOP_SPACE = 150;
	private static final Font MSG_TITLE = new Font("Dialog", Font.PLAIN, 20);
	private static final Font MSG_SUBTITLE = new Font("Dialog", Font.PLAIN, 14);
	private JFrame frame = new JFrame(Constants.TITLE);
	private GameState state;
	
	
	
	
	
	public void showGame(){
		
		
		
		JPanel content = new JPanel(new BorderLayout());
		
		
		content.add(new GamePanel(), BorderLayout.CENTER);
		
		
		
		frame.add(content);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		Insets insets = frame.getInsets();
		frame.setMinimumSize(new Dimension(
			16 * Constants.INIT_SIZE + insets.left + insets.right,
			TOP_SPACE + 9 * Constants.INIT_SIZE + insets.top + insets.bottom)
		);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void initialiseGame(){
		
		
		//TODO this is just fixed static data
		state = new GameState(new PlayfieldGenerator().generatePlayfield(), Arrays.asList(new HumanPlayer(), new HumanPlayer()));
		
	}
	
	
	private final class GamePanel extends JPanel implements MouseListener{
		/**
		 * Serial ID.
		 */
		private static final long serialVersionUID = 5749409248962652951L;
		private MessageDialog activeDialog = null;
		
		private GamePanel(){
			this.addMouseListener(this);
		}
		
		public void renderGame(Graphics2D g){
			g.setColor(Color.RED);
			g.fillRect(0, 0, this.getWidth(), TOP_SPACE);
			
			//TODO temp
			
			g.translate(0, TOP_SPACE);
			double sx = (double)this.getWidth() / (double)Constants.PLAYFIELD_WIDTH;
			double sy = (double)(this.getHeight() - TOP_SPACE) / (double)Constants.PLAYFIELD_HEIGHT;
			if(sx < sy){
				g.scale(sx, sx);
			}else{
				g.translate((this.getWidth() - Constants.PLAYFIELD_WIDTH * sy) / 2.0D, 0.0D);
				g.scale(sy, sy);
			}
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			g.setColor(Color.white);//TODO texture?
			g.fillRect(0, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			
			for(ConvexObject obj : state.getObjects()){
				//TODO temp
				g.setColor(obj.isOwned() ? obj.getOwner().getColor() : Color.BLACK);
				g.fill(obj.getShape());
			}
			
			g.setColor(Color.BLACK);
			for(Line2D line : state.getVerticalDecompLines()){
				g.draw(line);
			}
			
			if(activeDialog != null){
				//TODO center and make look nice
				g.setColor(new Color(0.0F, 0.0F, 0.0F, 0.5F));
				g.drawString(activeDialog.getTitle(), 10, 100);
				g.drawString(activeDialog.getSubtitle(), 10, 150);
			}
		}
		
		@Override
		public void paintComponent(Graphics g1){
			Graphics2D g = (Graphics2D)g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
			if(activeDialog != null){
				activeDialog = null;
				repaint();
			}else if(state.getActivePlayer().isHuman()){
				ConvexObject obj = state.getObject(e.getX(), e.getY() - TOP_SPACE);//TODO may require transforms later
				if(obj != null){
					activeDialog = state.claimObject(obj);
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
