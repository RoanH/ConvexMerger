package dev.roanh.convexmerger.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.player.Player;

public class ResultOverlay{
	private static final boolean ENABLED = true;//TODO remove
	private static final int GAP = 8;
	private static final int BAR_HEIGHT = 200;
	private static final int BAR_WIDTH = 80;
	private static final int ROUND_RADIUS = 4;
	private static final int MAX_WIDTH = 900;
	private static final int CROWN_GAP = 4;
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
		int size = Math.min((7 * width) / 10, MAX_WIDTH);
		g.translate((width - size) / 2.0D, offset + GAP);
		renderBars(g, size);
		
		//stats
		g.translate(0, BAR_HEIGHT + Theme.CROWN_ICON_LARGE_SIZE + GAP);
		renderStats(g, size);
		
		//total height: (title fm asc + desc + 1)
		
		
		
		g.setTransform(transform);
		
		return true;
	}
	
	private void renderBars(Graphics2D g, int width){
		g.setFont(Theme.PRIDI_MEDIUM_24);
		FontMetrics fm = g.getFontMetrics();
		
		int divLine = BAR_HEIGHT + Theme.CROWN_ICON_LARGE_SIZE;

		g.setStroke(Theme.RESULTS_STROKE);
		g.setClip(0, 0, width, divLine);
		
		List<Player> players = state.getPlayers();
		for(int i = 0; i < players.size(); i++){
			Player player = players.get(i);

			double offset = (((width - BAR_WIDTH * (players.size() + 1)) * (2 * i + 1)) / 10.0D) + BAR_WIDTH * i;
			double height = (player.getArea() / winner.getArea()) * BAR_HEIGHT;
			
			RoundRectangle2D rect = new RoundRectangle2D.Double(offset, Theme.CROWN_ICON_LARGE_SIZE + BAR_HEIGHT - height, BAR_WIDTH, height + ROUND_RADIUS, ROUND_RADIUS * 2, ROUND_RADIUS * 2);
			
			g.setColor(player.getTheme().getBarBody());
			g.fill(rect);
			g.setColor(player.getTheme().getBarOutline());
			g.draw(rect);
			
			g.setFont(Theme.PRIDI_MEDIUM_12);
			g.setColor(Theme.BAR_SCORE_COLOR);
			String str = Theme.formatScore(player.getArea());
			fm = g.getFontMetrics();
			g.drawString(str, (float)(offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F), (float)(Theme.CROWN_ICON_LARGE_SIZE + BAR_HEIGHT - height + fm.getAscent()));
			
			g.setFont(Theme.PRIDI_MEDIUM_14);
			g.setColor(Theme.BAR_NAME_COLOR);
			fm = g.getFontMetrics();
			str = player.getName();
			float y = (float)(Theme.CROWN_ICON_LARGE_SIZE + BAR_HEIGHT - height - fm.getDescent());
			if(player.equals(winner)){
				g.drawImage(
					Theme.CROWN_ICON_LARGE,
					(int)(offset + (BAR_WIDTH - fm.stringWidth(str) - CROWN_GAP - Theme.CROWN_ICON_LARGE_SIZE) / 2.0F),
					(int)(y - (Theme.CROWN_ICON_LARGE_SIZE - (fm.getAscent() - fm.getDescent() - fm.getLeading())) / 2 - (fm.getAscent() - fm.getDescent() - fm.getLeading()) - 1),
					null
				);
				offset += (CROWN_GAP + Theme.CROWN_ICON_LARGE_SIZE) / 2;
			}
			g.drawString(str, (float)(offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F), y);
		}
		g.setClip(null);
		
		g.setColor(Theme.DIVIDER_COLOR);
		g.drawLine(0, divLine, width, divLine);
	}
	
	private void renderStats(Graphics2D g, int width){
		g.setColor(Color.RED);
		g.drawLine(0, 0, width, 0);
		
		renderBorder(g, 10, 10, 100, 20, "Test");
		
	}
	
	private void renderBorder(Graphics2D g, double x, double y, double w, double h, String title){
		Rectangle2D in = new Rectangle2D.Double(x + 5, y - 5, 20, 10);
		Area a = new Area();
			a.subtract(new Area(in));
		g.draw(in);
		//g.setClip(a);
		g.draw(new RoundRectangle2D.Double(x, y, w, h, ROUND_RADIUS * 2, ROUND_RADIUS * 2));
		
	}
	
	private void renderGraph(Graphics2D g){
		
	}
}
