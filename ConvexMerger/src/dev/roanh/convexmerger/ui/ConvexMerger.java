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
public class ConvexMerger{
	private JFrame frame = new JFrame(Constants.TITLE);
	private ScreenRenderer renderer = new ScreenRenderer(new MainMenu(this));
	private GameThread gameThread;
	
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		frame.add(content);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		Insets insets = frame.getInsets();
		frame.setMinimumSize(new Dimension(
			16 * Constants.MIN_SIZE + insets.left + insets.right + 2 * GamePanel.SIDE_OFFSET,
			GamePanel.TOP_SPACE + 9 * Constants.MIN_SIZE + insets.top + insets.bottom + GamePanel.TOP_OFFSET + GamePanel.BOTTOM_OFFSET)
		);
		frame.setSize(new Dimension(
			16 * Constants.INIT_SIZE + insets.left + insets.right + 2 * GamePanel.SIDE_OFFSET,
			GamePanel.TOP_SPACE + 9 * Constants.INIT_SIZE + insets.top + insets.bottom + GamePanel.TOP_OFFSET + GamePanel.BOTTOM_OFFSET)
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
	
	public void initialiseGame(GameConstructor ctor){
		gameThread = new GameThread(ctor);
		gameThread.start();
	}
	
	public Screen switchScene(Screen next){
		return renderer.setScreen(next);
	}
	
	public void abortGame(){
		if(gameThread != null){
			gameThread.interrupt();
		}
	}
	
	/**
	 * Thread responsible for managing the game turns,
	 * generating the playfield and executing AI moves.
	 * @author Roan
	 */
	private final class GameThread extends Thread{
		private GameConstructor ctor;
		
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
				
				while(!state.isFinished() && !this.isInterrupted()){
					Player player = state.getActivePlayer();
					if(player.isAI()){
						Thread.sleep(Constants.AI_TURN_TIME);
					}
					long start = System.currentTimeMillis();
					state.executePlayerTurn();
					player.getStats().addTurnTime(System.currentTimeMillis() - start);
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
