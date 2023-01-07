/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.net.ClientConnection;
import dev.roanh.convexmerger.player.HumanPlayer;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Menu shown to connect to a remote server.
 * @author Roan
 */
public class JoinMenu extends Screen{
	/**
	 * Maximum width used by the boxes.
	 */
	private static final int MAX_WIDTH = 1200;
	/**
	 * Height of the connect button.
	 */
	private static final double CONNECT_HEIGHT = 100.0D;
	/**
	 * Height of the details box.
	 */
	private static final double boxSize = 100.0D;
	/**
	 * Width of the text fields.
	 */
	private static final double fieldWidth = 200.0D;
	/**
	 * Height of the text fields.
	 */
	private static final double fieldHeight = 20.0D;
	/**
	 * Last used host name.
	 */
	private static String lastHost = "";
	/**
	 * Last used player name.
	 */
	private static String lastName = System.getProperty("user.name", "Player 1");
	/**
	 * Executor used to connect to the remote server.
	 */
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	/**
	 * Player name input field.
	 */
	private TextField name = new TextField(PlayerTheme.P1.getBaseOutline());
	/**
	 * Host name input field.
	 */
	private TextField host = new TextField(PlayerTheme.P2.getBaseOutline());
	/**
	 * Server connection.
	 */
	private ClientConnection con = null;
	/**
	 * Local player.
	 */
	private Player self;
	/**
	 * True is currently attempting to establish a connection.
	 */
	private volatile boolean connecting = false;
	/**
	 * Current status message.
	 */
	private volatile String msg = "Enter connection details...";
	/**
	 * Connect button bounds.
	 */
	private Path2D connect = new Path2D.Double();
	
	/**
	 * Constructs a new join menu with the given game context.
	 * @param context The game context to use.
	 */
	protected JoinMenu(ConvexMerger context){
		super(context);
		host.setText(lastHost);
		name.setText(lastName);
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		renderMenuTitle(g, width, "Join Multiplayer");
		drawTitle(g, width);
		
		double size = getMaxWidth(width, 0.8D, MAX_WIDTH);
		double offset = (width - size) / 2.0D;
		double y = TOP_SPACE + TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D;
		drawTitledBox(g, Theme.constructBorderGradient(null, width), offset, y, size, boxSize, "Enter Details");
		
		g.setFont(Theme.PRIDI_MEDIUM_14);
		FontMetrics fm = g.getFontMetrics();
		name.render(g, offset + size / 2.0D - fieldWidth - BOX_SPACING, y + BOX_HEADER_HEIGHT + BOX_SPACING * 2.0D, fieldWidth, fieldHeight);
		host.render(g, offset + size / 2.0D + fm.stringWidth("Host") + SPACING + BOX_SPACING, y + BOX_HEADER_HEIGHT + BOX_SPACING * 2.0D, fieldWidth, fieldHeight);

		g.setColor(Theme.BOX_TEXT_COLOR);
		g.drawString("Name", (float)(offset + size / 2.0D - fieldWidth - BOX_SPACING - fm.stringWidth("Name") - SPACING), (float)(y + BOX_HEADER_HEIGHT + BOX_SPACING * 2.0D + fieldHeight - fm.getMaxDescent()));
		g.drawString("Host", (float)(offset + size / 2.0D + BOX_SPACING), (float)(y + BOX_HEADER_HEIGHT + BOX_SPACING * 2.0D + fieldHeight - fm.getMaxDescent()));
		
		y += boxSize + BOX_SPACING;
		connect = drawButton(g, "Connect", offset + (size / 3.0D), y, (size / 3.0D), CONNECT_HEIGHT, connecting ? null : mouseLoc);
		g.setFont(Theme.PRIDI_REGULAR_12);
		fm = g.getFontMetrics();
		g.setColor(Theme.ADD_COLOR);
		g.drawString(msg, (float)(offset + (size / 3.0D) + SPACING + ((size / 3.0D) - fm.stringWidth(msg)) / 2.0D), (float)(y + g.getFontMetrics(Theme.PRIDI_REGULAR_18).getHeight() + (CONNECT_HEIGHT - fm.getAscent() + fm.getDescent() + fm.getLeading()) / 2.0D));
	}

	/**
	 * Attempts to establish a connection to the server.
	 */
	private void connect(){
		name.removeFocus();
		host.removeFocus();
		self = new HumanPlayer(name.getText());
		connecting = true;
		msg = "Connecting...";
		executor.execute(()->{
			try{
				con = ClientConnection.connect(host.getText(), self);
				if(con.isConnected()){
					lastHost = host.getText();
					lastName = self.getName();
					msg = "Waiting for the host to start the game...";
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
			if(event.getKeyCode() == KeyEvent.VK_TAB){
				if(name.hasFocus()){
					name.removeFocus();
					host.giveFocus();
				}else if(host.hasFocus()){
					host.removeFocus();
					name.giveFocus();
				}
			}else{
				name.handleKeyEvent(event);
				host.handleKeyEvent(event);
			}
		}
	}
	
	@Override
	public void handleMouseRelease(Point2D loc, int width, int height){
		super.handleMouseRelease(loc, width, height);
		if(!connecting){
			name.handleMouseClick(loc);
			host.handleMouseClick(loc);
			if(connect.contains(loc)){
				connect();
			}
		}
	}

	@Override
	protected boolean isLeftButtonEnabled(){
		return true;
	}

	@Override
	protected boolean isRightButtonEnabled(){
		return false;
	}

	@Override
	protected String getLeftButtonText(){
		return "Back";
	}

	@Override
	protected String getRightButtonText(){
		return null;
	}

	@Override
	protected void handleLeftButtonClick(){
		if(con != null){
			con.close();
		}
		executor.shutdownNow();
		switchScene(new MainMenu(this.getContext()));
	}

	@Override
	protected void handleRightButtonClick(){
	}
}
