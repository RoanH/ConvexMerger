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
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.net.ClientConnection;
import dev.roanh.convexmerger.net.InternalServer;
import dev.roanh.convexmerger.player.GreedyPlayer;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.LocalPlayer;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.SmallPlayer;

public class ConvexMerger{
	private JFrame frame = new JFrame(Constants.TITLE);
	private GameState state;//TODO required?
	private GamePanel game = new GamePanel();
	
	public void showGame(){
		JPanel content = new JPanel(new BorderLayout());
		content.add(game, BorderLayout.CENTER);
		
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
				}else if(e.getKeyCode() == KeyEvent.VK_F){//TODO remove
					for(ConvexObject obj : state.getObjects()){
						obj.scale(0.99D);
					}
					frame.repaint();
				}
				return false;
			}
		});
	}
	
	public void initialiseGame(){
		//TODO this is just fixed static data
		
		//easy: 50-100 0.45
		//normal: 0-100 0.45
		//ai fun: 10-20 0.45 mini size
		initialiseGame(new GameState(new PlayfieldGenerator().generatePlayfield(), Arrays.asList(
			new HumanPlayer(),
			//new HumanPlayer()
			//new SmallPlayer(),
			new LocalPlayer(),
			new GreedyPlayer(),
			new SmallPlayer()
		)));
	}
	
	private void initialiseGame(GameState state){
		this.state = state;
		game.setGameState(state);
		game.repaint();
		
		GameThread thread = new GameThread();
		thread.setName("GameThread");
		thread.setDaemon(true);
		thread.start();
	}
	
	public void hostMultiplayerGame(){
		Player self = new HumanPlayer();
		PlayfieldGenerator gen = new PlayfieldGenerator();
		gen.setRange(50, 100);
		gen.setScaling(200);
		
		InternalServer server = new InternalServer(self, gen, player->{
			System.out.println("new player joined with name " + player.getName() + " and id " + player.getID());
		});
		
		while(server.getPlayerCount() < 3){
			try{
				System.out.println("playres: " + server.getPlayerCount());
				Thread.sleep(1000);
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("player count hit start game");
		
		GameState state = server.startGame();
		initialiseGame(state);
		showGame();
	}
	
	public void joinMultiplayerGame(){
		try{
			Player player = new HumanPlayer();
			ClientConnection con = ClientConnection.connect("localhost", player);
			
			System.out.println("connected as client with player id " + player.getID());
			
			GameState state = con.getGameState();
			
			initialiseGame(state);
			showGame();
			
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private final class GameThread extends Thread{
		
		@Override
		public void run(){
			try{
				//Thread.sleep(4000);
				while(!state.isFinished()){
					Player player = state.getActivePlayer();
					if(player.isAI()){
						Thread.sleep(400);//TODO config
					}
					long start = System.currentTimeMillis();
					state.executePlayerTurn();
					player.getStats().addTurnTime(System.currentTimeMillis() - start);
					frame.repaint();
				}
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("game end");
			game.showResults();
			frame.repaint();

		}
	}
}
