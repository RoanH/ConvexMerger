package dev.roanh.convexmerger.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.player.Player;

public class ResultOverlay{
	private static final boolean ENABLED = false;//TODO remove
	private static final int GAP = 8;
	private Player winner;

	protected ResultOverlay(GameState state){
		winner = state.getPlayers().get(0);//TODO temp
		
		
	}
	
	
	
	
	public void render(Graphics2D g, int width, int height){
		if(!ENABLED){
			return;
		}
		
		//background
		g.setColor(Theme.OVERLAY_BACKGROUND);
		g.fillRect(0, 0, width, height);
		
		//title
		g.setFont(Theme.PRIDI_MEDIUM_36);
		g.setColor(winner.getTheme().getTextColor());
		FontMetrics fm = g.getFontMetrics();
		int offset = fm.getAscent() - fm.getDescent() + 1;
		g.drawString("Game Finished", 0, offset);
		
		//bar chart
		offset += GAP;
		g.translate(0, offset);
		renderBars(g);
		
		//total height: (title fm asc + desc + 1)
		
		
	}
	
	private void renderBars(Graphics2D g){
		
	}
	
	private void renderStats(Graphics2D g){
		
	}
	
	private void renderGraph(Graphics2D g){
		
	}
}
