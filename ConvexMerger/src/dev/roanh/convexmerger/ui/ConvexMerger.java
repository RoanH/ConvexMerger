package dev.roanh.convexmerger.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.GameConstructor;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.player.Player;

/**
 * Main game entry point, manages the main state of the game.
 * @author Roan
 */
public class ConvexMerger implements KeyEventDispatcher{
	/**
	 * Application main frame.
	 */
	private JFrame frame = new JFrame(Constants.TITLE);
	/**
	 * Window size before switching to full screen.
	 */
	private Dimension lastSize = null;
	/**
	 * Window location before switching to full screen.
	 */
	private Point lastLocation = null;
	/**
	 * The renderer rending the active game screen.
	 */
	private ScreenRenderer renderer = new ScreenRenderer(new MainMenu(this));
	/**
	 * The thread running the active game (if any).
	 */
	private GameThread gameThread;
	/**
	 * Cached new game menu to persist settings.
	 */
	private NewGameMenu newGame = new NewGameMenu(this);
	
	/**
	 * Shows the main game window.
	 */
	public void showGame(){
		JPanel content = new JPanel(new BorderLayout());
		content.add(renderer, BorderLayout.CENTER);
		
		try{
			frame.setIconImages(Arrays.asList(
				ImageIO.read(ClassLoader.getSystemResourceAsStream("assets/logo/16.png")),
				ImageIO.read(ClassLoader.getSystemResourceAsStream("assets/logo/32.png")),
				ImageIO.read(ClassLoader.getSystemResourceAsStream("assets/logo/48.png")),
				ImageIO.read(ClassLoader.getSystemResourceAsStream("assets/logo/64.png")),
				ImageIO.read(ClassLoader.getSystemResourceAsStream("assets/logo/96.png")),
				ImageIO.read(ClassLoader.getSystemResourceAsStream("assets/logo/256.png"))
			));
		}catch(IOException e1){
			//not important and internally resources should load
		}
		
		frame.add(content);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		Insets insets = frame.getInsets();
		Dimension minSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setMinimumSize(new Dimension(
			Math.min(16 * Constants.MIN_SIZE + insets.left + insets.right + 2 * Screen.SIDE_OFFSET, minSize.width),
			Math.min(Screen.TOP_SPACE + 9 * Constants.MIN_SIZE + insets.top + insets.bottom + Screen.TOP_OFFSET + Screen.BOTTOM_OFFSET, minSize.height)
		));
		frame.setSize(new Dimension(
			Math.min(16 * Constants.INIT_SIZE + insets.left + insets.right + 2 * Screen.SIDE_OFFSET, minSize.width),
			Math.min(Screen.TOP_SPACE + 9 * Constants.INIT_SIZE + insets.top + insets.bottom + Screen.TOP_OFFSET + Screen.BOTTOM_OFFSET, minSize.height)
		));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}
	
	/**
	 * Exits this game context, possibly exiting
	 * the application as a whole.
	 */
	public void exit(){
		frame.dispose();
	}
	
	/**
	 * Shows the new game menu.
	 */
	public void showNewGame(){
		newGame.reset();
		switchScene(newGame);
	}
	
	/**
	 * Initialises a new game with the given game constructor.
	 * @param ctor The constructor to build the game.
	 */
	public void initialiseGame(GameConstructor ctor){
		gameThread = new GameThread(ctor);
		gameThread.start();
	}
	
	/**
	 * Switches the scene being displayed to the user
	 * to the given screen.
	 * @param next The new screen to display.
	 * @return The previous screen on display.
	 */
	public Screen switchScene(Screen next){
		return renderer.setScreen(next);
	}
	
	/**
	 * Aborts the active game, if any.
	 */
	public void abortGame(){
		if(gameThread != null){
			gameThread.interrupt();
		}
	}
	
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
	
	/**
	 * Thread responsible for managing the game turns,
	 * generating the playfield and executing AI moves.
	 * @author Roan
	 */
	private final class GameThread extends Thread{
		/**
		 * The constructor to use to create the game state.
		 */
		private GameConstructor ctor;
		
		/**
		 * Constructs a new game thread with the given game constructor.
		 * @param ctor The constructor to create the game state with.
		 */
		private GameThread(GameConstructor ctor){
			this.setName("GameThread");
			this.setDaemon(true);
			this.ctor = ctor;
		}
		
		@Override
		public void run(){
			GameState state = ctor.create();
			try{
				SwingUtilities.invokeAndWait(()->renderer.setScreen(new GamePanel(ConvexMerger.this, state)));
				
				if(state.getActivePlayer().isAI()){
					Thread.sleep(Constants.MIN_TURN_TIME);
				}
				
				while(!state.isFinished() && !this.isInterrupted()){
					Player player = state.getActivePlayer();
					
					long start = System.currentTimeMillis();
					state.executePlayerTurn();
					long duration = System.currentTimeMillis() - start;
					player.getStats().addTurnTime(duration);
					if(duration < Constants.MIN_TURN_TIME){
						Thread.sleep(Constants.MIN_TURN_TIME - duration);
					}
					
					frame.repaint();
				}
			}catch(InvocationTargetException e){
				//never happens
			}catch(InterruptedException e){
				//happens when the game is aborted
				state.abort();
			}
		}
	}
}
