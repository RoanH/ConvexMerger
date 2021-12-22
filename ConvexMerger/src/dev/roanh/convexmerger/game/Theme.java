package dev.roanh.convexmerger.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public final class Theme{
	public static final Color BACKGROUND = new Color(21, 25, 30);
	public static final Color MENU_BODY = new Color(31, 37, 46);
	public static final Stroke POLY_STROKE = new BasicStroke(4.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final Stroke HELPER_STROKE = new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0F, new float[]{3.0F, 5.0F}, 0.0F);
	
	
	
	
	
	public static final Color getPlayerBody(ConvexObject obj){
		return (obj.isOwned() ? obj.getOwner().getTheme() : PlayerTheme.UNOWNED).getBody();
	}
	
	public static final Color getPlayerOutline(ConvexObject obj){
		return (obj.isOwned() ? obj.getOwner().getTheme() : PlayerTheme.UNOWNED).getOutline();
	}
	
	public static enum PlayerTheme{
		UNOWNED(129, 129, 129, 0.75D, 191, 191, 191, 0.90D),
		P1(214, 9, 177, 0.75D, 255, 115, 236, 0.90D),
		P2(11, 171, 229, 0.75D, 0, 217, 255, 0.90D),
		P3(227, 131, 18, 0.75D, 255, 198, 28, 0.90D),
		P4(10, 196, 50, 0.75D, 30, 250, 85, 0.90D);
		
		private final Color body;
		private final Color outline;
		
		private PlayerTheme(int br, int bg, int bb, double ba, int or, int og, int ob, double oa){
			body = new Color(br, bg, bb, (int)(ba * 255.0D));
			outline = new Color(or, og, ob, (int)(oa * 255.0D));
		}
		
		public Color getBody(){
			return body;
		}
		
		public Color getOutline(){
			return outline;
		}
	}
}
