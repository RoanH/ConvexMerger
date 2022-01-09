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

public class HostMenu extends NewGameMenu implements InternalServerListener{
	private InternalServer server;

	public HostMenu(ConvexMerger context){
		super(context);
		p1 = new HostPanel(PlayerTheme.P1);
		server = new InternalServer(this);
	}
	
	@Override
	protected void handleStart(List<Player> players, PlayfieldGenerator gen){
		this.getContext().initialiseGame(server.startGame(players, gen));
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleMouseClick(Point2D loc, int width, int height){
		synchronized(server){
			super.handleMouseClick(loc, width, height);
		}
	}
	
	@Override
	protected String getMenuTitle(){
		return "Host Game";
	}
	
	private class HostPanel extends PlayerPanel{

		private HostPanel(PlayerTheme theme){
			super(theme);
			setHuman();
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
	
	private class RemotePanel extends PlayerPanel{
		private Player player;
		
		private RemotePanel(PlayerTheme theme, Player player){
			super(theme);
			this.player = player;
			setHuman();
			name.setText(player.getName());
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
