package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Locale;

import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.player.Player.PlayerStats;

public class ResultOverlay{
	public static boolean ENABLED = true;//TODO remove
	private static final int GAP = 8;
	private static final int BAR_HEIGHT = 200;
	private static final int BAR_WIDTH = 80;
	private static final int ROUND_RADIUS = 4;
	private static final int MAX_WIDTH = 900;
	private static final int CROWN_GAP = 4;
	private static final int BORDER_GAP = 8;
	private static final int TEXT_OFFSET = 8;
	private static final int GRAPH_HEIGHT = 150;
	private Player winner;
	private GameState state;

	protected ResultOverlay(GameState state){
		this.state = state;
		winner = state.getPlayers().get(0);
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
		
		//center
		double statsHeight = BORDER_GAP * 4.0D + 5.0D * (TEXT_OFFSET * 2.0D + g.getFontMetrics(Theme.PRIDI_MEDIUM_16).getAscent());
		double total = offset + BAR_HEIGHT + Theme.CROWN_ICON_LARGE_SIZE + 2.0D * GAP + 3.0D * BORDER_GAP + statsHeight + GRAPH_HEIGHT;
		g.translate(0.0D, (height - total) / 2.0D);
		
		//title part 2
		String title = state.isFinished() ? "Game Finished" : "Game Progress";
		g.drawString(title, (width - fm.stringWidth(title)) / 2.0F, offset);
		
		//bar chart
		int size = Math.min((7 * width) / 10, MAX_WIDTH);
		g.translate((width - size) / 2.0D, offset + GAP);
		renderBars(g, size);
		
		//stats
		g.translate(0, BAR_HEIGHT + Theme.CROWN_ICON_LARGE_SIZE + GAP + BORDER_GAP);
		renderStats(g, size);
		
		//graph
		g.translate(0, statsHeight + GAP + BORDER_GAP);
		renderGraph(g, size);
		
		g.setTransform(transform);
		
		return true;//TOOD
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

			float offset = computeBarStart(i, players.size(), width);
			double height = (player.getArea() / winner.getArea()) * BAR_HEIGHT;
			
			RoundRectangle2D rect = new RoundRectangle2D.Double(offset, Theme.CROWN_ICON_LARGE_SIZE + BAR_HEIGHT - height, BAR_WIDTH, height + ROUND_RADIUS, ROUND_RADIUS * 2, ROUND_RADIUS * 2);
			
			g.setColor(player.getTheme().getBarBody());
			g.fill(rect);
			g.setColor(player.getTheme().getBarOutline());
			g.draw(rect);
			
			g.setFont(Theme.PRIDI_MEDIUM_13);
			g.setColor(Theme.BAR_SCORE_COLOR);
			String str = Theme.formatScore(player.getArea());
			fm = g.getFontMetrics();
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (float)(Theme.CROWN_ICON_LARGE_SIZE + BAR_HEIGHT - height + fm.getAscent()));
			
			g.setFont(Theme.PRIDI_MEDIUM_16);
			g.setColor(Theme.BAR_NAME_COLOR);
			fm = g.getFontMetrics();
			str = player.getName();
			float y = (float)(Theme.CROWN_ICON_LARGE_SIZE + BAR_HEIGHT - height - fm.getDescent());
			if(Double.compare(0.0D, player.getArea()) != 0 && player.equals(winner)){
				g.drawImage(
					Theme.CROWN_ICON_LARGE,
					(int)(offset + (BAR_WIDTH - fm.stringWidth(str) - CROWN_GAP - Theme.CROWN_ICON_LARGE_SIZE) / 2.0F),
					(int)(y - (Theme.CROWN_ICON_LARGE_SIZE - (fm.getAscent() - fm.getDescent() - fm.getLeading())) / 2 - (fm.getAscent() - fm.getDescent() - fm.getLeading()) - 1),
					null
				);
				offset += (CROWN_GAP + Theme.CROWN_ICON_LARGE_SIZE) / 2;
			}
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, y);
		}
		g.setClip(null);
		
		g.setColor(Theme.PRIMARY_COLOR);
		g.drawLine(0, divLine, width, divLine);
	}
	
	private float computeBarStart(int i, int players, int width){
		return (((width - BAR_WIDTH * (players + 1)) * (2 * i + 1)) / 10.0F) + BAR_WIDTH * i;
	}
	
	private void renderStats(Graphics2D g, int width){
		FontMetrics fm = g.getFontMetrics(Theme.PRIDI_MEDIUM_16);
		float height = TEXT_OFFSET * 2.0F + fm.getAscent();
		float subWidth = (width - BORDER_GAP * 2) / 3.0F;
		
		//borders
		renderBorder(g, 0.0F, 0.0F, subWidth, height, "Game Time");
		renderBorder(g, subWidth + BORDER_GAP, 0.0F, subWidth, height, "Rounds");
		renderBorder(g, (subWidth + BORDER_GAP) * 2.0F, 0.0F, subWidth, height, "Seed");
		renderBorder(g, 0.0F, BORDER_GAP + height, width, height, "Objects Claimed");
		renderBorder(g, 0.0F, (BORDER_GAP + height) * 2.0F, width, height, "Merges");
		renderBorder(g, 0.0F, (BORDER_GAP + height) * 3.0F, width, height, "Objects Absorbed");
		renderBorder(g, 0.0F, (BORDER_GAP + height) * 4.0F, width, height, "Average Turn Time");
		
		g.setFont(Theme.PRIDI_MEDIUM_16);
		
		//game time
		String time = formatTime(state.getGameTime()).toUpperCase(Locale.ROOT);
		g.drawString(time, (subWidth - fm.stringWidth(time)) / 2.0F, TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);

		//rounds
		String rounds = String.valueOf(state.getRounds());
		g.drawString(rounds, subWidth + BORDER_GAP + (subWidth - fm.stringWidth(rounds)) / 2.0F, TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
		
		//seed
		String seed = "TODO";//TODO
		g.drawString(seed, (subWidth + BORDER_GAP) * 2.0F + (subWidth - fm.stringWidth(seed)) / 2.0F, TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
		
		//player data
		List<Player> players = state.getPlayers();
		for(int i = 0; i < players.size(); i++){
			Player player = players.get(i);
			PlayerStats stats = player.getStats();

			float offset = computeBarStart(i, players.size(), width);
			
			//claims
			String str = String.valueOf(stats.getClaims());
			g.setColor(players.stream().anyMatch(p->p.getStats().getClaims() > stats.getClaims()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);

			//merges
			str = String.valueOf(stats.getMerges());
			g.setColor(players.stream().anyMatch(p->p.getStats().getMerges() > stats.getMerges()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) * 2.0F + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
			
			//absorbed
			str = String.valueOf(stats.getAbsorbed());
			g.setColor(players.stream().anyMatch(p->p.getStats().getAbsorbed() > stats.getAbsorbed()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) * 3.0F + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
			
			//claims
			str = String.valueOf(formatTime(stats.getAverageTurnTime()));
			g.setColor(players.stream().anyMatch(p->p.getStats().getAverageTurnTime() < stats.getAverageTurnTime()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) * 4.0F + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
		}
	}
	
	private String formatTime(long ms){
		String time;
		
		if(ms < 1000){
			time = "0.";
			time += String.valueOf(ms / 100);
			ms %= 100;
			if(ms != 0){
				time += String.valueOf(ms / 10);
				ms %= 10;
				if(ms != 0){
					time += String.valueOf(ms);
				}
			}
			return time + "s";
		}
		
		time = "";
		if(ms >= 1000 * 60 * 60){
			time += (ms / (1000 * 60 * 60)) + "h ";
			ms %= 1000 * 60 * 60;
		}
		if(ms >= 1000 * 60 || !time.isEmpty()){
			time += (ms / (1000 * 60)) + "m ";
			ms %= 1000 * 60;
		}
		time += String.valueOf((int)Math.round(ms / 1000.0D)) + "s";
		
		return time;
	}
	
	private void renderBorder(Graphics2D g, float x, float y, float w, float h, String title){
		g.setFont(Theme.PRIDI_MEDIUM_12);
		g.setColor(Theme.BORDER_COLOR);
		g.setStroke(Theme.RESULTS_STROKE);
		FontMetrics fm = g.getFontMetrics();
		
		Area clip = new Area(new Rectangle2D.Float(x - 10.0F, y - 10.0F, w + 20.0F, h + 20.0F));
		clip.subtract(new Area(new Rectangle2D.Float(x + ROUND_RADIUS * 2, y - fm.getHeight() / 2.0F, 4.0F + fm.stringWidth(title), fm.getHeight())));
		g.setClip(clip);
		g.draw(new RoundRectangle2D.Float(x, y, w, h, ROUND_RADIUS * 2.0F, ROUND_RADIUS * 2.0F));
		
		g.setClip(null);
		g.setColor(Theme.BORDER_TEXT_COLOR);
		g.drawString(title, (x + 2.0F + ROUND_RADIUS * 2.0F), y + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
	}
	
	private void renderGraph(Graphics2D g, int size){
		double max = winner.getArea();
		int rounds = state.getRounds();
		
		g.setFont(Theme.PRIDI_MEDIUM_10);
		FontMetrics fm = g.getFontMetrics();
		g.setClip(0, 0, size, GRAPH_HEIGHT);

		for(int i = 1; i <= Math.floor((max - 20_000.0D) / 100_000.0D); i++){//TODO magic
			double y = (GRAPH_HEIGHT - 2) - ((i * 100_000.0D * (GRAPH_HEIGHT - 2)) / max);
			
			g.setColor(Theme.PRIMARY_COLOR);
			g.draw(new Line2D.Double(0.0D, y, 16.0D, y));
			
			String str = Theme.formatScore(i * 100_000.0D);
			g.setColor(Theme.GRAPH_MARK_COLOR);
			g.drawString(str, 18.0F, (float)(y + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F));
			
			g.setColor(Theme.PRIMARY_COLOR);
			g.draw(new Line2D.Double(20.0D + fm.stringWidth(str), y, size, y));
		}
		
		for(Player player : state.getPlayers()){
			PlayerStats stats = player.getStats();
			
			Path2D graph = new Path2D.Double();
			graph.moveTo(0.0D, GRAPH_HEIGHT - 2.0D);
			for(int i = 0; i < rounds; i++){
				graph.lineTo(((i + 1) * size) / (double)rounds, 2.0D + (GRAPH_HEIGHT - 3.0D) - ((stats.getScoreHistory().get(Math.min(stats.getTurns(), i)) * (GRAPH_HEIGHT - 3.0D)) / max));
			}

			g.setColor(player.getTheme().getBaseOutline());
			g.setStroke(Theme.GRAPH_STROKE);
			g.draw(graph);
		}
		
		g.setClip(null);
		renderBorder(g, 0.0F, 0.0F, size, GRAPH_HEIGHT, "Score Graph");
	}
}
