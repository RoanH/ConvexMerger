package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.net.ClientConnection;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

public class JoinMenu extends Screen{
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private TextField name = new TextField(PlayerTheme.P1.getBaseOutline());
	private TextField host = new TextField(PlayerTheme.P2.getBaseOutline());
	private ClientConnection con = null;
	private Player self;
	private volatile boolean connecting = false;
	private volatile String msg = "Enter connection details...";
	
	protected JoinMenu(ConvexMerger context){
		super(context);
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		
		name.render(g, 100.0D, 200.0D, 200.0D, 20.0D);
		host.render(g, 100.0D, 230.0D, 200.0D, 20.0D);
		g.setColor(Color.WHITE);
		g.setFont(Theme.PRIDI_MEDIUM_14);
		g.drawString(msg, 100.0F, 265.0F);
	}

	@Override
	protected void handleRightButtonClick(){
		name.removeFocus();
		host.removeFocus();
		self = new HumanPlayer(name.getText());
		connecting = true;
		System.out.println("hi");
		executor.submit(()->{
			try{
				con = ClientConnection.connect(host.getText(), self);
				
				if(con.isConnected()){
					msg = "Waiting for game to start...";
					final GameState state = con.getGameState();
					this.getContext().initialiseGame(()->state);
				}else{
					msg = con.getRejectReason().getMessage();
				}
			}catch(IOException e){
				msg = e.getClass().getSimpleName() + ": " + e.getMessage();
				if(con != null){
					con.close();
					con = null;
				}
				connecting = false;
			}
		});
	}
	
	@Override
	public void handleKeyPressed(KeyEvent event){
		if(!connecting){
			name.handleKeyEvent(event);
			host.handleKeyEvent(event);
		}
	}
	
	@Override
	public void handleMouseClick(Point2D loc, int width, int height){
		super.handleMouseClick(loc, width, height);
		if(!connecting){
			name.handleMouseClick(loc);
			host.handleMouseClick(loc);
		}
	}

	@Override
	protected boolean isLeftButtonEnabled(){
		return true;
	}

	@Override
	protected boolean isRightButtonEnabled(){
		return !connecting;//TODO remove
	}

	@Override
	protected String getLeftButtonText(){
		return "Back";
	}

	@Override
	protected String getRightButtonText(){
		return "Connect";//TODO remove
	}

	@Override
	protected void handleLeftButtonClick(){
		if(con != null){
			con.close();
		}
		executor.shutdownNow();
		switchScene(new MainMenu(this.getContext()));		
	}
}
