package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.Theme.CROWN_ICON_SIZE;
import static dev.roanh.convexmerger.ui.Theme.PLAYER_ICON_SIZE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ClaimResult;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.player.GreedyPlayer;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.LocalPlayer;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.SmallPlayer;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

public class ConvexMerger{
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static final boolean SHOW_CENTROID = false;
	private static final long ANIMATION_RATE = 33;
	private static final int TOP_SPACE = 100;
	private static final int SIDE_OFFSET = 20 + 1;
	private static final int TOP_OFFSET = 30 + 1;
	private static final int BOTTOM_OFFSET = 50 + 1;
	private static final int TOP_SIDE_TRIANGLE = 50;
	private static final int TOP_MIDDLE_OFFSET = 30;
	private static final int TOP_MIDDLE_WIDTH = 200;//even
	private static final int BUTTON_HEIGHT = 50;
	private static final int BUTTON_WIDTH = 150;
	private static final int TOP_MIDDLE_TEXT_OFFSET = 2;
	/**
	 * Number of pixels between the player icon and the text.
	 */
	private static final int ICON_TEXT_SPACING = 4;
	/**
	 * Number of pixels from the left text border to the player info.
	 */
	private static final int PLAYER_TEXT_OFFSET = 24;
	private JFrame frame = new JFrame(Constants.TITLE);
	private GameState state;
	private Object turnLock = new Object();
	
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
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher(){
			private Dimension lastSize = null;
			private Point lastLocation = null;
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_F11 && e.getID() == KeyEvent.KEY_RELEASED){
					if(frame.isUndecorated()){
						frame.setVisible(false);
						frame.dispose();
						frame.setUndecorated(false);
						frame.setSize(lastSize);
						frame.setLocation(lastLocation);
						frame.setVisible(true);
					}else{
						lastSize = frame.getSize();
						lastLocation = frame.getLocation();
						frame.setVisible(false);
						frame.dispose();
						frame.setUndecorated(true);
						frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
						frame.setLocationRelativeTo(null);
						frame.setVisible(true);
					}
				}
				return false;
			}
		});
	}
	
	public void initialiseGame(){
		//TODO this is just fixed static data
		state = new GameState(new PlayfieldGenerator().generatePlayfield(), Arrays.asList(
			new HumanPlayer(),
			new SmallPlayer()//,
			//new LocalPlayer(),
			//new GreedyPlayer()
		));
		
		GameThread thread = new GameThread();
		thread.setName("GameThread");
		thread.setDaemon(true);
		thread.start();
	}
	
	private final class GameThread extends Thread{
		
		@Override
		public void run(){
			try{
				Player player = null;
				do{
					if(player != null && player.isHuman()){
						synchronized(turnLock){
							turnLock.wait();
						}
					}
					frame.repaint();
					player = state.getActivePlayer();
					if(player.isAI()){
						Thread.sleep(400);
					}
					state.executePlayerTurn();
				}while(!state.isFinished());
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("game end");
			frame.repaint();

		}
	}
	
	//TODO extract
	private final class GamePanel extends JPanel implements MouseListener, MouseMotionListener{
		/**
		 * Serial ID.
		 */
		private static final long serialVersionUID = 5749409248962652951L;
		private Polygon infoPoly = null;
		private Polygon menuPoly = null;
		private MessageDialog activeDialog = null;
		private List<Line2D> helperLines = null;
		private boolean animationRunning;
		
		private GamePanel(){
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}
		
		private void renderGame(Graphics2D g){
			animationRunning = false;

			//render playfield background
			g.setColor(Theme.BACKGROUND);
			g.fillRect(0, TOP_SPACE, this.getWidth(), this.getHeight() - TOP_SPACE);
			
			//render the game
			renderPlayfield(g);
			
			//render UI shapes
			renderInterface(g);
			
			//TODO temp dialog
			if(activeDialog != null){
				//TODO center and make look nice
				g.drawString(activeDialog.getTitle(), 100, 10 + 120);
				g.drawString(activeDialog.getSubtitle(), 100, 30 + 120);
				g.drawString("Click anywhere to close this dialog.", 100, 50 + 120);
			}
			
			if(animationRunning){
				executor.schedule(()->this.repaint(), ANIMATION_RATE, TimeUnit.MILLISECONDS);
			}
		}
		
		private void renderInterface(Graphics2D g){
			g.setColor(Theme.MENU_BODY);
			int sideOffset = Math.floorDiv(this.getWidth(), 2) - (TOP_MIDDLE_WIDTH / 2);
			Polygon topPoly = new Polygon(new int[]{
					0,
					0,
					TOP_SIDE_TRIANGLE,
					sideOffset - TOP_MIDDLE_OFFSET,
					sideOffset,
					this.getWidth() - sideOffset,
					this.getWidth() - sideOffset + TOP_MIDDLE_OFFSET,
					this.getWidth() - TOP_SIDE_TRIANGLE,
					this.getWidth(),
					this.getWidth()
				},
				new int[]{
					0,
					TOP_SIDE_TRIANGLE + TOP_SPACE,
					TOP_SPACE,
					TOP_SPACE,
					TOP_SPACE + TOP_MIDDLE_OFFSET,
					TOP_SPACE + TOP_MIDDLE_OFFSET,
					TOP_SPACE,
					TOP_SPACE,
					TOP_SIDE_TRIANGLE + TOP_SPACE,
					0
				},
				10
			);
			g.fill(topPoly);
			
			infoPoly = new Polygon(
				new int[]{
					this.getWidth(),
					this.getWidth() - BUTTON_WIDTH,
					this.getWidth() - BUTTON_WIDTH + BUTTON_HEIGHT,
					this.getWidth()
				},
				new int[]{
					this.getHeight(),
					this.getHeight(),
					this.getHeight() - BUTTON_HEIGHT,
					this.getHeight() - BUTTON_HEIGHT
				},
				4
			);
			g.fill(infoPoly);
			
			menuPoly = new Polygon(
				new int[]{
					0,
					0,
					BUTTON_WIDTH - BUTTON_HEIGHT,
					BUTTON_WIDTH
				},
				new int[]{
					this.getHeight(),
					this.getHeight() - BUTTON_HEIGHT,
					this.getHeight() - BUTTON_HEIGHT,
					this.getHeight()
				},
				4
			);
			g.fill(menuPoly);
			
			//render UI borders
			g.setPaint(Theme.constructBorderGradient(state, this.getWidth()));
			g.setStroke(Theme.BORDER_STROKE);
			
			Path2D infoPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
			infoPath.moveTo(infoPoly.xpoints[1], infoPoly.ypoints[1] - 1);
			infoPath.lineTo(infoPoly.xpoints[2], infoPoly.ypoints[2] - 1);
			infoPath.lineTo(infoPoly.xpoints[3] - 1, infoPoly.ypoints[3] - 1);
			g.draw(infoPath);
			
			Path2D menuPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
			menuPath.moveTo(menuPoly.xpoints[1], menuPoly.ypoints[1] - 1);
			menuPath.lineTo(menuPoly.xpoints[2], menuPoly.ypoints[2] - 1);
			menuPath.lineTo(menuPoly.xpoints[3] + 1, menuPoly.ypoints[3]);
			g.draw(menuPath);
			
			Path2D topPath = new Path2D.Double(Path2D.WIND_NON_ZERO, topPoly.npoints - 2);
			topPath.moveTo(topPoly.xpoints[1], topPoly.ypoints[1]);
			for(int i = 2; i < topPoly.npoints - 1; i++){
				topPath.lineTo(topPoly.xpoints[i], topPoly.ypoints[i]);
			}
			g.draw(topPath);
			
			//render action hint
			g.setFont(Theme.PRIDI_REGULAR_18);
			g.setColor(state.isFinished() ? PlayerTheme.UNOWNED.getOutline() : state.getActivePlayer().getTheme().getOutline());
			FontMetrics fm = g.getFontMetrics();
			String msg = state.isFinished() ? "Game Finished" : (state.isSelectingSecond() ? "Merge with an object" : "Select an object");
			g.drawString(msg, sideOffset + (TOP_MIDDLE_WIDTH - fm.stringWidth(msg)) / 2.0F, TOP_SPACE + TOP_OFFSET - fm.getDescent() - TOP_MIDDLE_TEXT_OFFSET);
			
			//render player data
			List<Player> players = state.getPlayers();
			double max = players.stream().mapToDouble(Player::getArea).max().getAsDouble();
			for(int i = 0; i < players.size(); i++){
				Player player = players.get(i);
				int x = ((i * this.getWidth()) / players.size());
				g.setClip(x, 0, this.getWidth() / players.size(), TOP_SPACE);
				
				//offset
				g.setFont(Theme.PRIDI_MEDIUM_24);//TODO technically needs spacing
				fm = g.getFontMetrics();
				int y = (TOP_SPACE - fm.getHeight() - CROWN_ICON_SIZE) / 2;
				x += PLAYER_TEXT_OFFSET;
				
				//player icon and name
				g.drawImage(player.isAI() ? player.getTheme().getIconAI() : player.getTheme().getIconHuman(), x, y, this);
				g.setColor(player.getTheme().getOutline());
				g.drawString(player.getName(), x + PLAYER_ICON_SIZE + ICON_TEXT_SPACING, y + PLAYER_ICON_SIZE / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
				
				//new offset
				y += fm.getHeight();
				g.setFont(Theme.PRIDI_REGULAR_18);
				fm = g.getFontMetrics();
				
				//crown icon
				if(Double.compare(player.getArea(), max) >= 0){
					g.drawImage(Theme.CROWN_ICON, x + (PLAYER_ICON_SIZE - CROWN_ICON_SIZE) / 2, y, this);
					g.setColor(Theme.SCORE_COLOR_LEAD);
				}else{
					g.setColor(Theme.SCORE_COLOR);
				}
				
				//player score
				AffineTransform transform = g.getTransform();
				g.translate(x + PLAYER_ICON_SIZE + ICON_TEXT_SPACING, y + CROWN_ICON_SIZE / 2.0D + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D);
				animationRunning |= player.getScoreAnimation().run(g);
				g.setTransform(transform);
			}
			g.setClip(null);
		}
		
		private void renderPlayfield(Graphics2D g){
			AffineTransform transform = g.getTransform();
			g.translate(SIDE_OFFSET, TOP_SPACE + TOP_OFFSET);
			double sx = (double)(this.getWidth() - 2 * SIDE_OFFSET) / (double)Constants.PLAYFIELD_WIDTH;
			double sy = (double)(this.getHeight() - TOP_SPACE - TOP_OFFSET - BOTTOM_OFFSET) / (double)Constants.PLAYFIELD_HEIGHT;
			if(sx < sy){
				g.scale(sx, sx);
			}else{
				g.translate((this.getWidth() - Constants.PLAYFIELD_WIDTH * sy - 2 * SIDE_OFFSET) / 2.0D, 0.0D);
				g.scale(sy, sy);
			}
			
			for(ConvexObject obj : state.getObjects()){
				if(obj.hasAnimation()){
					animationRunning |= obj.runAnimation(g);
				}else{
					obj.render(g);
				}
				
				if(SHOW_CENTROID){
					g.setColor(Color.BLACK);
					Point2D c = obj.getCentroid();
					g.fill(new Ellipse2D.Double(c.getX() - 5, c.getY() - 5, 10, 10));	
				}
			}
			
			g.setColor(Color.WHITE);
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
			
			g.setTransform(transform);
			g.setClip(null);
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
			Graphics2D g = (Graphics2D)g1.create();
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
			}else if(state.getActivePlayer().isHuman() && !state.isFinished()){
				Point2D loc = translateToGameSpace(e.getX(), e.getY());
				ConvexObject obj = state.getObject(loc);
				if(obj != null){
					ClaimResult result = state.claimObject(obj, loc);
					activeDialog = result.getMessage();
					helperLines = null;
					if(result != ClaimResult.EMPTY){
						synchronized(turnLock){
							turnLock.notify();
						}
					}
					repaint();
				}
			}else{
				activeDialog = state.isFinished() ? MessageDialog.GAME_END : MessageDialog.NO_TURN;
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
