package dev.roanh.convexmerger.animation;

import java.awt.Graphics2D;

import dev.roanh.convexmerger.player.Player;

/**
 * Animation that shows the player score increasing or decreasing.
 * @author Roan
 */
public class ScoreAnimation implements Animation{
	/**
	 * Number of score points to add each millisecond.
	 */
	private static final int SCORE_PER_MS = 51;
	/**
	 * Player whose score to display.
	 */
	private Player player;
	/**
	 * Current area claimed by the player as
	 * shown by the animation.
	 */
	private double area;
	/**
	 * Timestamp the last animation frame was rendered.
	 */
	private long last = -1L;
	
	/**
	 * Constructs a new score animation for the given player.
	 * @param player The player whose score to animate.
	 */
	public ScoreAnimation(Player player){
		this.player = player;
		area = player.getArea();
	}

	@Override
	public boolean run(Graphics2D g){
		if(last == -1L){
			last = System.currentTimeMillis();
		}
		
		long time = System.currentTimeMillis();
		if(area <= player.getArea()){
			area = Math.min(player.getArea(), area + (time - last) * SCORE_PER_MS);
		}else{
			area = Math.max(player.getArea(), area - (time - last) * SCORE_PER_MS);
		}
		
		String str = "0";
		for(int total = (int)Math.round(area); total != 0; total /= 1000){
			str = str.equals("0") ? "" : ("." + str);
			str = String.format(total > 1000 ? "%03d" : "%d", total % 1000) + str;
		}
		g.drawString(str, 0, 0);
		
		if(Double.compare(area, player.getArea()) != 0){
			last = time;
			return true;
		}else{
			last = -1L;
			return false;
		}
	}
}
