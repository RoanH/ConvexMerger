package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;

public class ScoreAnimation implements Animation{
	private static final int SCORE_PER_MS = 100;
	private Player player;
	private double area;
	private long last = System.currentTimeMillis();
	
	public ScoreAnimation(Player player){
		this.player = player;
		area = player.getArea();
	}

	@Override
	public boolean run(Graphics2D g){
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
		
		last = time;
		return Double.compare(area, player.getArea()) != 0;
	}
}
