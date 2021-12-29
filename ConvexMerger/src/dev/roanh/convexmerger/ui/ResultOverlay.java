package dev.roanh.convexmerger.ui;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.player.Player;

public class ResultOverlay{
	private static final boolean ENABLED = true;//TODO remove
	private static final int GAP = 8;
	private static final int BAR_HEIGHT = 200;
	private static final int BAR_WIDTH = 80;
	private static final int CROWN_HEIGHT = 20;
	private static final int ROUND_RADIUS = 4;
	private static final int MAX_WIDTH = 900;
	private static final Stroke MAIN_STROKE = new BasicStroke(2.0F);
	private Player winner;
	private GameState state;

	protected ResultOverlay(GameState state){
		this.state = state;
		winner = state.getPlayers().get(0);//TODO temp
		
		
	}
	
	
	
	
	public boolean render(Graphics2D g, int width, int height){
		if(!ENABLED){
			return false;
		}
		AffineTransform transform = g.getTransform();
		
		for(Player player : state.getPlayers()){
			if(player.getArea() > winner.getArea()){
				winner = player;
			}
		}
		
		//background
		g.setColor(Theme.OVERLAY_BACKGROUND);
		g.fillRect(0, 0, width, height);
		
		//title
		g.setFont(Theme.PRIDI_MEDIUM_36);
		g.setColor(winner.getTheme().getTextColor());
		FontMetrics fm = g.getFontMetrics();
		int offset = fm.getAscent() - fm.getDescent() + 1;
		String title = "Game Finished";
		g.drawString(title, (width - fm.stringWidth(title)) / 2.0F, offset);
		
		//bar chart
		offset += GAP;
		int size = Math.min((7 * width) / 10, MAX_WIDTH);
		g.translate((width - size) / 2.0D, offset);
		renderBars(g, size);
		
		//total height: (title fm asc + desc + 1)
		
		
		
		g.setTransform(transform);
		
		return true;
	}
	
	private void renderBars(Graphics2D g, int width){
		g.setFont(Theme.PRIDI_MEDIUM_24);
		FontMetrics fm = g.getFontMetrics();
		
		int divLine = BAR_HEIGHT + CROWN_HEIGHT;
		
		g.drawLine(0, 0, width, 0);
		g.drawLine(0, CROWN_HEIGHT, width, CROWN_HEIGHT);

		g.setStroke(MAIN_STROKE);
		g.setClip(0, 0, width, divLine);
		
		List<Player> players = state.getPlayers();
		for(int i = 0; i < players.size(); i++){
			Player player = players.get(i);

			double offset = (((width - BAR_WIDTH * 5) * (2 * i + 1)) / 10.0D) + BAR_WIDTH * i;
			double height = (player.getArea() / winner.getArea()) * BAR_HEIGHT;
			
			RoundRectangle2D rect = new RoundRectangle2D.Double(offset, CROWN_HEIGHT + BAR_HEIGHT - height, BAR_WIDTH, height + ROUND_RADIUS, ROUND_RADIUS * 2, ROUND_RADIUS * 2);
			
			g.setColor(player.getTheme().getBarBody());
			g.fill(rect);
			g.setColor(player.getTheme().getBarOutline());
			g.draw(rect);

			
		}
		g.setClip(null);
		
		g.setColor(Theme.DIVIDER_COLOR);
		g.drawLine(0, divLine, width, divLine);
		
		
	}
	
	private void renderStats(Graphics2D g){
		
	}
	
	private void renderGraph(Graphics2D g){
		
	}
}
