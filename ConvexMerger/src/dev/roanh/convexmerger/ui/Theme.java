package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.Theme.PlayerTheme.*;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;

public final class Theme{
	public static final Color BACKGROUND = new Color(21, 25, 30);
	public static final Color MENU_BODY = new Color(31, 37, 46);
	public static final Color SCORE_COLOR = new Color(255, 255, 255, (3 * 255) / 4);
	public static final Color SCORE_COLOR_LEAD = new Color(255, 255, 255, (9 * 255) / 10);
	public static final Color CROWN_COLOR = new Color(237, 214, 9);
	public static final Stroke POLY_STROKE = new BasicStroke(4.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final Stroke BORDER_STROKE = new BasicStroke(1.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final Stroke HELPER_STROKE = new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0F, new float[]{3.0F, 5.0F}, 0.0F);
	public static final Font PRIDI_REGULAR_24;
	public static final Font PRIDI_REGULAR_18;
	public static final Font PRIDI_MEDIUM_24;
	public static final int PLAYER_ICON_SIZE = 24;
	public static final int CROWN_ICON_SIZE = 18;
	public static final BufferedImage CROWN_ICON;
	
	
	
	
	public static final Color getPlayerBody(ConvexObject obj){
		return (obj.isOwned() ? obj.getOwner().getTheme() : UNOWNED).getBody();
	}
	
	public static final Color getPlayerOutline(ConvexObject obj){
		return (obj.isOwned() ? obj.getOwner().getTheme() : UNOWNED).getOutline();
	}
	
	public static final LinearGradientPaint constructBorderGradient(GameState state, int width){
		switch(state == null ? 4 : state.getPlayerCount()){
		case 0:
		case 1:
		case 2:
			return new LinearGradientPaint(
				0.0F,
				0.0F,
				width,
				0.0F,
				new float[]{
					0.0F,
					0.1F,
					0.9F,
					1.0F
				},
				new Color[]{
					P1.gradient,
					P1.gradient,
					P2.gradient,
					P2.gradient
				}
			);
		case 3:
			return new LinearGradientPaint(
				0.0F,
				0.0F,
				width,
				0.0F,
				new float[]{
					0.0F,
					0.07F,
					0.5F,
					0.93F,
					1.0F
				},
				new Color[]{
					P1.gradient,
					P1.gradient,
					P2.gradient,
					P3.gradient,
					P3.gradient
				}
			);
		case 4:
		default:
			return new LinearGradientPaint(
				0.0F,
				0.0F,
				width,
				0.0F,
				new float[]{
					0.0F,
					0.05F,
					0.35F,
					0.65F,
					0.95F,
					1.0F
				},
				new Color[]{
					P1.gradient,
					P1.gradient,
					P2.gradient,
					P3.gradient,
					P4.gradient,
					P4.gradient
				}
			);
		}
	}
	
	private static final BufferedImage loadImage(InputStream in, int size, Color color) throws IOException{
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		BufferedImage raw = ImageIO.read(in);
		Image data = raw.getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH);
		
		Graphics2D g = img.createGraphics();
		g.drawImage(data, 0, 0, null);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0, 0, size, size);
		g.dispose();
		data.flush();
		raw.flush();
		
		return img;
	}
	
	static{
		try{
			Font regular = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("assets/fonts/Pridi-Regular.ttf"));
			PRIDI_REGULAR_18 = regular.deriveFont(18.0F);
			PRIDI_REGULAR_24 = regular.deriveFont(24.0F);
			PRIDI_MEDIUM_24 = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("assets/fonts/Pridi-Medium.ttf")).deriveFont(24.0F);
			CROWN_ICON = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/crown.png"), CROWN_ICON_SIZE, CROWN_COLOR);
		}catch(IOException | FontFormatException e){
			//this should not happen
			throw new RuntimeException("Failed to load fonts or icons", e);
		}
	}
	
	public static enum PlayerTheme{
		UNOWNED(102, 103, 104, 174, 175, 175, 191, 191, 191),
		P1(166, 13, 140, 235, 106, 218, 255, 115, 236, 234, 108, 217),
		P2(14, 135, 180, 2, 197, 232, 0, 217, 255, 5, 208, 245),
		P3(13, 153, 45, 22, 207, 68, 30, 250, 85, 19, 242, 75),
		P4(175, 104, 21, 243, 187, 27, 255, 198, 28, 247, 207, 5);
		
		private final Color text;
		private final Color body;
		private final Color outline;
		private final Color gradient;
		private final BufferedImage ai;
		private final BufferedImage human;
		
		private PlayerTheme(int br, int bg, int bb, int or, int og, int ob, int tr, int tg, int tb, int gr, int gg, int gb){
			this(br, bg, bb, or, og, ob, tr, tg, tb, new Color(gr, gg, gb));
		}
		
		private PlayerTheme(int br, int bg, int bb, int or, int og, int ob, int tr, int tg, int tb){
			this(br, bg, bb, or, og, ob, tr, tg, tb, null);
		}
		
		private PlayerTheme(int br, int bg, int bb, int or, int og, int ob, int tr, int tg, int tb, Color gradient){
			body = new Color(br, bg, bb);
			outline = new Color(or, og, ob);
			text = new Color(tr, tg, tb, (9 * 255) / 10);
			this.gradient = gradient;
			try{
				ai = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/ai.png"), PLAYER_ICON_SIZE, outline);
				human = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/human.png"), PLAYER_ICON_SIZE, outline);
			}catch(IOException e){
				//should not happen
				throw new RuntimeException("Failed to load icons", e);
			}
		}
		
		public BufferedImage getIconAI(){
			return ai;
		}
		
		public BufferedImage getIconHuman(){
			return human;
		}
		
		public Color getTextColor(){
			return text;
		}
		
		public Color getBody(){
			return body;
		}
		
		public Color getOutline(){
			return outline;
		}
		
		public static final PlayerTheme get(int i){
			return values()[i];
		}
	}
}
