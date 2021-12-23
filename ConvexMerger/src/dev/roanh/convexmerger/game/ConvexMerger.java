package dev.roanh.convexmerger.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.Theme.PlayerTheme;

public class ConvexMerger{
	private static final int TOP_SPACE = 100;
	private static final int SIDE_OFFSET = 20;
	private static final int TOP_OFFSET = 30;
	private static final int BOTTOM_OFFSET = 50;
	private static final int TOP_SIDE_TRIANGLE = 50;
	private static final int TOP_MIDDLE_OFFSET = 30;
	
	
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
			16 * Constants.MIN_SIZE + insets.left + insets.right + 2 * SIDE_OFFSET,
			TOP_SPACE + 9 * Constants.MIN_SIZE + insets.top + insets.bottom + TOP_OFFSET + BOTTOM_OFFSET)
		);
		frame.setSize(new Dimension(
			16 * Constants.INIT_SIZE + insets.left + insets.right + 2 * SIDE_OFFSET,
			TOP_SPACE + 9 * Constants.INIT_SIZE + insets.top + insets.bottom + TOP_OFFSET + BOTTOM_OFFSET)
		);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void initialiseGame(){
		
		
		//TODO this is just fixed static data
		state = new GameState(new PlayfieldGenerator().generatePlayfield(), Arrays.asList(new HumanPlayer(PlayerTheme.P1), new GreedyPlayer(PlayerTheme.P2)));
		
	}
	
	
	private final class GamePanel extends JPanel implements MouseListener, MouseMotionListener{
		/**
		 * Serial ID.
		 */
		private static final long serialVersionUID = 5749409248962652951L;
		private MessageDialog activeDialog = null;
		private List<Line2D> helperLines = null;
		
		private GamePanel(){
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}
		
		public void renderGame(Graphics2D g){
			g.setColor(Theme.MENU_BODY);
			Polygon top = new Polygon(new int[10], new int[10], 10);
			top.addPoint(0, 0);
			top.addPoint(0, TOP_SIDE_TRIANGLE + TOP_SPACE);
			top.addPoint(TOP_SIDE_TRIANGLE, TOP_SPACE);
			//TODO middle stuff
			top.addPoint(this.getWidth() - TOP_SIDE_TRIANGLE, TOP_SPACE);
			top.addPoint(this.getWidth(), TOP_SIDE_TRIANGLE + TOP_SPACE);
			top.addPoint(this.getWidth(), 0);
			g.fill(top);
			
			//g.setColor(Color.BLACK);
			//g.fillRect(0, TOP_SPACE, this.getWidth(), this.getHeight() - TOP_SPACE);
			
			g.setColor(Color.WHITE);
			int yoff = 20;
			for(Player player : state.getPlayers()){
				g.drawString(player.getName() + ": " + (int)Math.round(player.getArea()), 10, yoff);
				yoff += 20;
			}
			
			if(activeDialog != null){
				//TODO center and make look nice
				g.drawString(activeDialog.getTitle(), 100, 10);
				g.drawString(activeDialog.getSubtitle(), 100, 30);
				g.drawString("Click anywhere to continue playing.", 100, 50);
			}
			
			g.translate(SIDE_OFFSET, TOP_SPACE + TOP_OFFSET);
			double sx = (double)(this.getWidth() - 2 * SIDE_OFFSET) / (double)Constants.PLAYFIELD_WIDTH;
			double sy = (double)(this.getHeight() - TOP_SPACE - TOP_OFFSET - BOTTOM_OFFSET) / (double)Constants.PLAYFIELD_HEIGHT;
			if(sx < sy){
				g.scale(sx, sx);
			}else{
				g.translate((this.getWidth() - Constants.PLAYFIELD_WIDTH * sy - 2 * SIDE_OFFSET) / 2.0D, 0.0D);
				g.scale(sy, sy);
			}
			
			g.setColor(Theme.BACKGROUND);
			g.clipRect(0, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			g.fillRect(0, 0, Constants.PLAYFIELD_WIDTH, Constants.PLAYFIELD_HEIGHT);
			
			for(ConvexObject obj : state.getObjects()){
				g.setColor(Theme.getPlayerBody(obj));
				g.fill(obj.getShape());
			}
			
			g.setStroke(Theme.POLY_STROKE);
			for(ConvexObject obj : state.getObjects()){
				g.setColor(Theme.getPlayerOutline(obj));
				g.draw(obj.getShape());
			}
			
			g.setColor(Color.BLACK);
			for(Line2D line : state.getVerticalDecompLines()){
				g.draw(line);
			}
			
			if(helperLines != null){
				g.setStroke(Theme.HELPER_STROKE);
				g.setColor(state.getActivePlayer().getTheme().getOutline());
				for(Line2D line : helperLines){
					g.draw(line);
				}
			}
		}
		
		/**
		 * Translates the given point from windows coordinate space
		 * to game coordinate space.
		 * @param x The x coordinate of the point to translate.
		 * @param y The y coordinate of the point to translate.
		 * @return The point translated to game space.
		 */
		private Point2D translateToGameSpace(double x, double y){
			double sx = (double)(this.getWidth() - 2 * SIDE_OFFSET) / (double)Constants.PLAYFIELD_WIDTH;
			double sy = (double)(this.getHeight() - TOP_SPACE - TOP_OFFSET - BOTTOM_OFFSET) / (double)Constants.PLAYFIELD_HEIGHT;
			if(sx < sy){
				return new Point2D.Double(
					(x - SIDE_OFFSET) / sx,
					(y - TOP_SPACE - TOP_OFFSET) / sx
				);
			}else{
				return new Point2D.Double(
					(x - ((this.getWidth() - Constants.PLAYFIELD_WIDTH * sy - 2 * SIDE_OFFSET) / 2.0D) - SIDE_OFFSET) / sy,
					(y - TOP_SPACE - TOP_OFFSET) / sy
				);
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
				ConvexObject obj = state.getObject(translateToGameSpace(e.getX(), e.getY()));
				if(obj != null){
					activeDialog = state.claimObject(obj);
					helperLines = null;
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

		@Override
		public void mouseDragged(MouseEvent e){
		}

		@Override
		public void mouseMoved(MouseEvent e){
			if(state.getActivePlayer().isHuman() && state.isSelectingSecond()){
				Point2D pos = translateToGameSpace(e.getX(), e.getY());
				helperLines = state.getHelperLines((int)Math.round(pos.getX()), (int)Math.round(pos.getY()));
				repaint();
			}
		}
	}
}
