package dev.roanh.convexmerger.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Optional;

import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.net.InternalServer;
import dev.roanh.convexmerger.net.InternalServer.InternalServerListener;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Menu used to host a multiplayer match.
 * @author Roan
 */
public class HostMenu extends NewGameMenu implements InternalServerListener{
	/**
	 * Last name used by the host.
	 */
	private static String lastHostName = null;
	/**
	 * The multiplayer server.
	 */
	private InternalServer server;
	/**
	 * Error message, if any.
	 */
	private String error = null;

	/**
	 * Constructs a new host menu with the given game context.
	 * @param context The game context.
	 */
	public HostMenu(ConvexMerger context){
		super(context);
		p1 = new HostPanel(PlayerTheme.P1);
		server = new InternalServer(this);
	}
	
	@Override
	protected void handleStart(List<Player> players, PlayfieldGenerator gen){
		this.getContext().initialiseGame(server.startGame(players, gen, showDecomp));
	}
	
	@Override
	public void handlePlayerJoin(Player player){
		synchronized(server){
			if(!p2.hasPlayer()){
				p2 = new RemotePanel(PlayerTheme.P2, player);
			}else if(!p3.hasPlayer()){
				p3 = new RemotePanel(PlayerTheme.P3, player);
			}else if(!p4.hasPlayer()){
				p4 = new RemotePanel(PlayerTheme.P4, player);
			}
		}
	}
	
	@Override
	protected void handleLeftButtonClick(){
		server.shutdown();
		super.handleLeftButtonClick();
	}

	@Override
	public void handleException(Exception e){
		server.shutdown();
		error = e.getClass().getSimpleName() + ": " + e.getMessage();
	}
	
	@Override
	protected String getButtonMessage(){
		return error == null ? super.getButtonMessage() : error;
	}
	
	@Override
	protected boolean canStart(){
		return error == null && super.canStart();
	}
	
	@Override
	public void handleMouseRelease(Point2D loc, int width, int height){
		if(error == null){
			synchronized(server){
				super.handleMouseRelease(loc, width, height);
			}
		}else{
			super.handleMouseRelease(loc, width, height);
		}
	}
	
	@Override
	protected String getMenuTitle(){
		return "Host Multiplayer";
	}
	
	/**
	 * Special player panel that cannot be removed.
	 * @author Roan
	 */
	private class HostPanel extends PlayerPanel{

		/**
		 * Constructs a new host panel with the given theme.
		 * @param theme The panel theme.
		 */
		private HostPanel(PlayerTheme theme){
			super(theme);
			setHuman(lastHostName);
		}
		
		@Override
		protected Optional<Player> getPlayer(){
			Optional<Player> player = super.getPlayer();
			player.map(Player::getName).ifPresent(name->lastHostName = name);
			return player;
		}
		
		@Override
		protected void renderRemoveButton(Graphics2D g, double x, double y, Point2D mouseLoc){
			g.setColor(Theme.DOUBLE_LIGHTEN);
			g.setStroke(Theme.BUTTON_STROKE);
			g.draw(computeBox(x, y, CONTENT_WIDTH, CONTENT_HEIGHT, 5.0D));
			
			g.setFont(Theme.PRIDI_REGULAR_14);
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Theme.ADD_COLOR);
			g.drawString("Host", (float)(x + (CONTENT_WIDTH - fm.stringWidth("Host")) / 2.0D), (float)(y + CONTENT_HEIGHT - 1.0D - (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
		}
	}
	
	/**
	 * Special player panel that cannot be removed or edited.
	 * @author Roan
	 */
	private class RemotePanel extends PlayerPanel{
		/**
		 * The remote player associated with this panel.
		 */
		private Player player;
		
		/**
		 * Constructs a new remote panel for the given player.
		 * @param theme The panel theme.
		 * @param player The remote player.
		 */
		private RemotePanel(PlayerTheme theme, Player player){
			super(theme);
			this.player = player;
			setHuman(player.getName());
		}
		
		@Override
		public Optional<Player> getPlayer(){
			return Optional.of(player);
		}
		
		@Override
		protected void renderRemoveButton(Graphics2D g, double x, double y, Point2D mouseLoc){
			g.setColor(Theme.DOUBLE_LIGHTEN);
			g.setStroke(Theme.BUTTON_STROKE);
			g.draw(computeBox(x, y, CONTENT_WIDTH, CONTENT_HEIGHT, 5.0D));
			
			g.setFont(Theme.PRIDI_REGULAR_14);
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Theme.ADD_COLOR);
			g.drawString("Remote", (float)(x + (CONTENT_WIDTH - fm.stringWidth("Remote")) / 2.0D), (float)(y + CONTENT_HEIGHT - 1.0D - (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0D));
		}
		
		@Override
		protected void handleMouseClick(Point2D loc){
		}
	}
}
