package dev.roanh.convexmerger.ui;

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
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Overlay that shows at the end of a game with a lot of stats.
 * @author Roan
 */
public class ResultOverlay{
	/**
	 * Gap between main result screen components (title, bars, stats, graph).
	 */
	private static final int GAP = 8;
	/**
	 * Maximum height of a bar in the bar chart.
	 */
	private static final int BAR_HEIGHT = 200;
	/**
	 * Width of a bar chart bar.
	 */
	private static final int BAR_WIDTH = 80;
	/**
	 * Bar chart bar corner rounding radius.
	 */
	private static final int ROUND_RADIUS = 4;
	/**
	 * Maximum width of the information part of the overlay.
	 */
	private static final int MAX_WIDTH = 900;
	/**
	 * Gap between the crown and the winning player name.
	 */
	private static final int CROWN_GAP = 4;
	/**
	 * Space between titled borders.
	 */
	private static final int BORDER_GAP = 8;
	/**
	 * Internal titled border offset to the text inside.
	 */
	private static final int TEXT_OFFSET = 8;
	/**
	 * Height of the graph.
	 */
	private static final int GRAPH_HEIGHT = 150;
	/**
	 * Score interval at which score markers are placed in the graph.
	 */
	private static final double GRAPH_MARKER_INTERVAL = 100_000.0D;
	/**
	 * The minimum score a player score has to be past a marker for a
	 * new top marker to be generated.
	 */
	private static final double GRAPH_MARKER_OFFSET = 20_000.0D;
	/**
	 * Whether or not this overlay is enabled and currently showing.
	 */
	private boolean enabled = false;
	/**
	 * The player that is currently winning.
	 */
	private Player winner;
	/**
	 * The active/completed game.
	 */
	private GameState state;
	/**
	 * Dummy player returning average player data.
	 */
	private Player average = new AveragePlayer();

	/**
	 * Constructs a new result overlay for the given game.
	 * @param state The game to show results for.
	 */
	protected ResultOverlay(GameState state){
		this.state = state;
		winner = state.getPlayers().get(0);
		average.init(state, PlayerTheme.UNOWNED);
	}
	
	/**
	 * Enables or disables this overlay.
	 * @param enabled True if the overlay should be shown,
	 *        false otherwise.
	 */
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	/**
	 * Checks if this overlay is enabled and showing.
	 * @return True if this overlay is enabled and showing.
	 */
	public boolean isEnabled(){
		return enabled;
	}
	
	/**
	 * Renders this overlay if it is enabled.
	 * @param g The graphics to use for rendering.
	 * @param width The current width of the viewport.
	 * @param height The current height of the viewport.
	 * @return True if an animation is actively playing.
	 * @see #isEnabled()
	 */
	public boolean render(Graphics2D g, int width, int height){
		if(!enabled){
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
		
		return false;
	}
	
	/**
	 * Renders the bar chart shown on the result screen.
	 * @param g The graphics context to use.
	 * @param width The width of the results section.
	 */
	private void renderBars(Graphics2D g, int width){
		int divLine = BAR_HEIGHT + Theme.CROWN_ICON_LARGE_SIZE;

		g.setStroke(Theme.RESULTS_STROKE);
		g.setClip(0, 0, width, divLine);
		
		List<Player> players = state.getPlayers();
		for(int i = 0; i <= players.size(); i++){
			Player player = i == players.size() ? average : players.get(i);

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
			FontMetrics fm = g.getFontMetrics();
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
	
	/**
	 * Computes the horizontal offset the left edge of the bar
	 * for the player with the given index is at.
	 * @param i The index of the player to compute the offset for.
	 * @param players The total number of players in the game.
	 * @param width The width of the results section.
	 * @return The offset to the left edge of the bar for the
	 *         player with the given index.
	 */
	private float computeBarStart(int i, int players, int width){
		return (((width - BAR_WIDTH * (players + 1)) * (2 * i + 1)) / 10.0F) + BAR_WIDTH * i;
	}
	
	/**
	 * Renders the statistics shown on the result screen.
	 * @param g The graphics context to use.
	 * @param width The width of the results section.
	 */
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
		g.setColor(Theme.SCORE_COLOR_LEAD);
		
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
		for(int i = 0; i <= players.size(); i++){
			Player player = i == players.size() ? average : players.get(i);
			PlayerStats stats = player.getStats();

			float offset = computeBarStart(i, players.size(), width);
			
			//claims
			String str = String.valueOf(stats.getClaims());
			g.setColor(i != players.size() && players.stream().anyMatch(p->p.getStats().getClaims() > stats.getClaims()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);

			//merges
			str = String.valueOf(stats.getMerges());
			g.setColor(i != players.size() && players.stream().anyMatch(p->p.getStats().getMerges() > stats.getMerges()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) * 2.0F + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
			
			//absorbed
			str = String.valueOf(stats.getAbsorbed());
			g.setColor(i != players.size() && players.stream().anyMatch(p->p.getStats().getAbsorbed() > stats.getAbsorbed()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) * 3.0F + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
			
			//claims
			str = String.valueOf(formatTime(stats.getAverageTurnTime()));
			g.setColor(i != players.size() && players.stream().anyMatch(p->p.getStats().getAverageTurnTime() < stats.getAverageTurnTime()) ? Theme.SCORE_COLOR_LEAD : player.getTheme().getBaseOutline());
			g.drawString(str, offset + (BAR_WIDTH - fm.stringWidth(str)) / 2.0F, (BORDER_GAP + height) * 4.0F + TEXT_OFFSET + fm.getAscent() / 2.0F + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
		}
	}
	
	/**
	 * Formats the given time in milliseconds to something
	 * human readable with seconds as the smallest unit and
	 * milliseconds as the precision. 
	 * @param ms The millisecond time to format.
	 * @return The formatted time.
	 */
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
	
	/**
	 * Renders a titled border with the given location, dimensions and title.
	 * @param g The graphics context to use.
	 * @param x The x-coordinate of the upper left corner of the border to render.
	 * @param y The y-coordinate of the upper left corner of the border to render.
	 * @param w The width of the border.
	 * @param h The height of the border.
	 * @param title The title to display on the border.
	 */
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
	
	/**
	 * Renders the graph shown on the result screen.
	 * @param g The graphics context to use.
	 * @param size The width of the results section.
	 */
	private void renderGraph(Graphics2D g, int size){
		double max = winner.getArea();
		int rounds = state.getRounds();
		
		g.setFont(Theme.PRIDI_MEDIUM_10);
		FontMetrics fm = g.getFontMetrics();
		g.setClip(0, 0, size, GRAPH_HEIGHT);

		for(int i = 1; i <= Math.floor((max - GRAPH_MARKER_OFFSET) / GRAPH_MARKER_INTERVAL); i++){
			double y = (GRAPH_HEIGHT - 2) - ((i * GRAPH_MARKER_INTERVAL * (GRAPH_HEIGHT - 2)) / max);
			
			g.setColor(Theme.PRIMARY_COLOR);
			g.draw(new Line2D.Double(0.0D, y, 16.0D, y));
			
			String str = Theme.formatScore(i * GRAPH_MARKER_INTERVAL);
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
				graph.lineTo(((i + 1) * size) / (double)rounds, 2.0D + (GRAPH_HEIGHT - 3.0D) - ((stats.getScoreHistory().get(Math.min(stats.getTurns() - 1, i)) * (GRAPH_HEIGHT - 3.0D)) / max));
			}

			g.setColor(player.getTheme().getBaseOutline());
			g.setStroke(Theme.GRAPH_STROKE);
			g.draw(graph);
		}
		
		g.setClip(null);
		renderBorder(g, 0.0F, 0.0F, size, GRAPH_HEIGHT, "Score Graph");
	}
	
	/**
	 * Dummy player instance that averages play results.
	 * @author Roan
	 * @see Player
	 */
	private static class AveragePlayer extends Player{

		/**
		 * Constructs a new average player.
		 */
		private AveragePlayer(){
			super(false, "Average");
			stats = new PlayerStats(){
				
				@Override
				public int getClaims(){
					return (int)Math.round(state.getPlayers().stream().map(Player::getStats).mapToInt(PlayerStats::getClaims).average().getAsDouble());
				}

				@Override
				public int getMerges(){
					return (int)Math.round(state.getPlayers().stream().map(Player::getStats).mapToInt(PlayerStats::getMerges).average().getAsDouble());
				}

				@Override
				public int getAbsorbed(){
					return (int)Math.round(state.getPlayers().stream().map(Player::getStats).mapToInt(PlayerStats::getAbsorbed).average().getAsDouble());
				}

				@Override
				public long getAverageTurnTime(){
					return state.getPlayers().stream().map(Player::getStats).mapToLong(PlayerStats::getAverageTurnTime).sum() / state.getPlayerCount();
				}
			};
		}
		
		@Override
		public double getArea(){
			return state == null ? 0.0D : state.getPlayers().stream().mapToDouble(Player::getArea).average().orElse(0.0D);
		}

		@Override
		public boolean executeMove(){
			return false;
		}
	}
}
