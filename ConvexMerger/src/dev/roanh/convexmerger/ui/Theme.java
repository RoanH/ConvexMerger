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

/**
 * Class holding various UI design related constants.
 * @author Roan (implementation)
 * @author RockRoller (design)
 */
public final class Theme{
	/**
	 * Playfield background color.
	 */
	public static final Color BACKGROUND = new Color(21, 25, 30);
	/**
	 * Playfield menu/score display area background.
	 */
	public static final Color MENU_BODY = new Color(31, 37, 46);
	/**
	 * Playfield player score color.
	 */
	public static final Color SCORE_COLOR = new Color(255, 255, 255, (3 * 255) / 4);
	/**
	 * Playfield leading player score color.
	 */
	public static final Color SCORE_COLOR_LEAD = new Color(255, 255, 255, (9 * 255) / 10);
	/**
	 * Result screen bar chart player name color.
	 */
	public static final Color BAR_NAME_COLOR = SCORE_COLOR_LEAD;
	/**
	 * Result screen bar chart score color.
	 */
	public static final Color BAR_SCORE_COLOR = new Color(255, 255, 255, (85 * 255) / 100);
	/**
	 * Leading crown icon color.
	 */
	public static final Color CROWN_COLOR = new Color(237, 214, 9);
	/**
	 * Color used for the result screen divider and graph markers.
	 */
	public static final Color PRIMARY_COLOR = new Color(31, 37, 46);
	/**
	 * Color used to darken the playfield when on the result screen.
	 */
	public static final Color OVERLAY_BACKGROUND = new Color(0, 0, 0, (8 * 255) / 10);
	/**
	 * Result screen border color.
	 */
	public static final Color BORDER_COLOR = new Color(41, 49, 61);
	/**
	 * Result screen titled border text color.
	 */
	public static final Color BORDER_TEXT_COLOR = new Color(255, 255, 255, (75 * 255) / 100);
	/**
	 * Result screen graph marker text color.
	 */
	public static final Color GRAPH_MARK_COLOR = new Color(255, 255, 255, (5 * 255) / 10);
	/**
	 * Box header color.
	 */
	public static final Color BOX_TEXT_COLOR = new Color(255, 255, 255, (95 * 255) / 100);
	/**
	 * Box text color for bullet points and such.
	 */
	public static final Color BOX_SECONDARY_COLOR = GRAPH_MARK_COLOR;
	/**
	 * Button on hover background color.
	 */
	public static final Color BUTTON_HOVER_COLOR = new Color(41, 49, 61);
	/**
	 * Button text color.
	 */
	public static final Color BUTTON_TEXT_COLOR = BORDER_TEXT_COLOR;
	/**
	 * Lighten new game screen color.
	 */
	public static final Color LIGHTEN = BUTTON_HOVER_COLOR;
	/**
	 * Double lighten new game screen color.
	 */
	public static final Color DOUBLE_LIGHTEN = new Color(57, 69, 87);
	/**
	 * Color used for the add button icons and text.
	 */
	public static final Color ADD_COLOR = new Color(147, 151, 157);
	/**
	 * Color used for the add button icons and text when highlighted.
	 */
	public static final Color ADD_COLOR_HIGHLIGHT = new Color(233, 234, 235);
	/**
	 * Remove button highlight color.
	 */
	public static final Color REMOVE_BUTTON_HIGHLIGHT = new Color(244, 244, 245);
	/**
	 * Button select color for the new game screen.
	 */
	public static final Color BUTTON_SELECT = new Color(2, 169, 229);
	/**
	 * Result screen main menu button text color.
	 */
	public static final Color MAIN_MENU_BUTTON = new Color(255, 255, 255, (6 * 255) / 10);
	/**
	 * Result screen main menu button hover text color.
	 */
	public static final Color MAIN_MENU_BUTTON_HOVER = SCORE_COLOR_LEAD;
	/**
	 * Stroke used to draw the outline of playfield convex objects.
	 */
	public static final Stroke POLY_STROKE = new BasicStroke(4.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	/**
	 * Stroke used to draw the menu panel gradient border.
	 */
	public static final Stroke BORDER_STROKE = new BasicStroke(1.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	/**
	 * Stroke used to draw the playfield merge helper lines.
	 */
	public static final Stroke HELPER_STROKE = new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0F, new float[]{3.0F, 5.0F}, 0.0F);
	/**
	 * Stroke used for borders on the result screen.
	 */
	public static final Stroke RESULTS_STROKE = new BasicStroke(3.0F);
	/**
	 * Stroke used for the graph player data lines.
	 */
	public static final Stroke GRAPH_STROKE = new BasicStroke(1.5F);
	/**
	 * Stroke used for the graph marker lines.
	 */
	public static final Stroke GRAPH_MARK_STROKE = new BasicStroke(1.0F);
	/**
	 * Stroke for the new game double lighten borders.
	 */
	public static final Stroke BUTTON_STROKE = new BasicStroke(2.0F);
	/**
	 * Pridi regular font with size 12, used for the rules.
	 */
	public static final Font PRIDI_REGULAR_12;
	/**
	 * Pridi regular font with size 14, used for the rules.
	 */
	public static final Font PRIDI_REGULAR_14;
	/**
	 * Pridi regular font with size 18, used for the playfield hint.
	 */
	public static final Font PRIDI_REGULAR_16;
	/**
	 * Pridi regular font with size 16, used for the playfield hint.
	 */
	public static final Font PRIDI_REGULAR_18;
	/**
	 * Pridi regular font with size 24, used for buttons.
	 */
	public static final Font PRIDI_REGULAR_24;
	/**
	 * Pridi medium font with size 10, used for graph markers.
	 */
	public static final Font PRIDI_MEDIUM_10;
	/**
	 * Pridi medium font with size 12, used for titled borders.
	 */
	public static final Font PRIDI_MEDIUM_12;
	/**
	 * Pridi medium font with size 13, used for bar chart scores.
	 */
	public static final Font PRIDI_MEDIUM_13;
	/**
	 * Pridi medium font with size 14, used for text fields.
	 */
	public static final Font PRIDI_MEDIUM_14;
	/**
	 * Pridi medium font with size 16, used for bar chart names and result overlay stats.
	 */
	public static final Font PRIDI_MEDIUM_16;
	/**
	 * Pridi medium font with size 24, used for playfield player names.
	 */
	public static final Font PRIDI_MEDIUM_24;
	/**
	 * Pridi medium font with size 30, used for the info menu title.
	 */
	public static final Font PRIDI_MEDIUM_30;
	/**
	 * Pridi medium font with size 36, used for the result overlay title.
	 */
	public static final Font PRIDI_MEDIUM_36;
	/**
	 * Dimensions of the player icon.
	 */
	public static final int PLAYER_ICON_SIZE = 24;
	/**
	 * Dimensions of the small player icon.
	 */
	public static final int PLAYER_ICON_SIZE_SMALL = 20;
	/**
	 * Dimensions of the crown image.
	 * @see #CROWN_ICON
	 */
	public static final int CROWN_ICON_SIZE = 18;
	/**
	 * Dimensions of the large crown image.
	 * @see #CROWN_ICON_LARGE
	 */
	public static final int CROWN_ICON_LARGE_SIZE = 24;
	/**
	 * Size of the player/ai add button icons.
	 */
	public static final int ADD_ICON_SIZE = 40;
	/**
	 * Size of the remove thrash can icon.
	 */
	public static final int REMOVE_ICON_SIZE = 16;
	/**
	 * Size of the chevron icon.
	 */
	public static final int CHEVRON_ICON_SIZE = 20;
	/**
	 * Smaller crown image with size {@value #CROWN_ICON_SIZE}.
	 */
	public static final BufferedImage CROWN_ICON;
	/**
	 * Larger crown image with size {@value #CROWN_ICON_LARGE_SIZE}.
	 */
	public static final BufferedImage CROWN_ICON_LARGE;
	/**
	 * AI add image.
	 */
	public static final BufferedImage AI_ADD;
	/**
	 * AI add image when highlighted.
	 */
	public static final BufferedImage AI_ADD_HIGHLIGHT;
	/**
	 * Player add image.
	 */
	public static final BufferedImage PLAYER_ADD;
	/**
	 * Player add image when highlighted.
	 */
	public static final BufferedImage PLAYER_ADD_HIGHLIGHT;
	/**
	 * Remove icon.
	 */
	public static final BufferedImage REMOVE_ICON;
	/**
	 * Remove icon when highlighted.
	 */
	public static final BufferedImage REMOVE_ICON_HIGHLIGHT;
	/**
	 * Chevron icon used for the combo box.
	 */
	public static final BufferedImage CHEVRON_ICON;

	/**
	 * Formats the given area by rounding to an integer and
	 * adding dots to separate digits into groups of three.
	 * @param area The area score to format.
	 * @return The formatted area score.
	 */
	public static String formatScore(double area){
		String str = "0";
		for(int total = (int)Math.round(area); total != 0; total /= 1000){
			str = str.equals("0") ? "" : ("." + str);
			str = String.format(total >= 1000 ? "%03d" : "%d", total % 1000) + str;

		}
		return str;
	}
	
	/**
	 * Gets the color to use to draw the body for the given convex object.
	 * @param obj The convex object to get the body color for.
	 * @return The body color for the given convex object.
	 */
	public static final Color getPlayerBody(ConvexObject obj){
		return (obj.isOwned() ? obj.getOwner().getTheme() : UNOWNED).getBody();
	}
	
	/**
	 * Gets the color to use to draw the outline for the given convex object.
	 * @param obj The convex object to get the outline color for.
	 * @return The outline color for the given convex object.
	 */
	public static final Color getPlayerOutline(ConvexObject obj){
		return (obj.isOwned() ? obj.getOwner().getTheme() : UNOWNED).getOutline();
	}
	
	/**
	 * Constructs a linear rainbow gradient whose colours depend on
	 * the number of players in the given game state.
	 * @param state The active game, <code>null</code> will construct
	 *        a gradient with 4 colours.
	 * @param width The current viewport width.
	 * @return The linear rainbow gradient.
	 */
	public static final LinearGradientPaint constructBorderGradient(GameState state, int width){
		switch(state == null ? 4 : state.getPlayerCount()){
		case 0:
		case 1:
			return new LinearGradientPaint(
				0.0F,
				0.0F,
				width,
				0.0F,
				new float[]{
					0.0F,
					1.0F
				},
				new Color[]{
					P1.gradient,
					P1.gradient,
				}
			);
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
	
	/**
	 * Loads an image from the given input stream, resizes it to the given
	 * size and recolours it with the given color.
	 * @param in The input stream to read the image from.
	 * @param size The size to rescale to.
	 * @param color The color to recolour to.
	 * @return The loaded, rescaled, recoloured image.
	 * @throws IOException When an IOException occurs.
	 */
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
			PRIDI_REGULAR_12 = regular.deriveFont(12.0F);
			PRIDI_REGULAR_14 = regular.deriveFont(14.0F);
			PRIDI_REGULAR_16 = regular.deriveFont(16.0F);
			PRIDI_REGULAR_18 = regular.deriveFont(18.0F);
			PRIDI_REGULAR_24 = regular.deriveFont(24.0F);
			
			Font medium = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("assets/fonts/Pridi-Medium.ttf"));
			PRIDI_MEDIUM_10 = medium.deriveFont(10.0F);
			PRIDI_MEDIUM_12 = medium.deriveFont(12.0F);
			PRIDI_MEDIUM_13 = medium.deriveFont(13.0F);
			PRIDI_MEDIUM_14 = medium.deriveFont(14.0F);//TODO technically needs spacing
			PRIDI_MEDIUM_16 = medium.deriveFont(16.0F);
			PRIDI_MEDIUM_24 = medium.deriveFont(24.0F);//TODO technically needs spacing
			PRIDI_MEDIUM_30 = medium.deriveFont(30.0F);//TODO technically needs spacing
			PRIDI_MEDIUM_36 = medium.deriveFont(36.0F);//TODO technically needs spacing
			
			CROWN_ICON = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/crown.png"), CROWN_ICON_SIZE, CROWN_COLOR);
			CROWN_ICON_LARGE = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/crown.png"), CROWN_ICON_LARGE_SIZE, CROWN_COLOR);
			AI_ADD = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/add_ai.png"), ADD_ICON_SIZE, ADD_COLOR);
			AI_ADD_HIGHLIGHT = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/add_ai.png"), ADD_ICON_SIZE, ADD_COLOR_HIGHLIGHT);
			PLAYER_ADD = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/add_human.png"), ADD_ICON_SIZE, ADD_COLOR);
			PLAYER_ADD_HIGHLIGHT = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/add_human.png"), ADD_ICON_SIZE, ADD_COLOR_HIGHLIGHT);
			REMOVE_ICON = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/remove.png"), REMOVE_ICON_SIZE, ADD_COLOR);
			REMOVE_ICON_HIGHLIGHT = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/remove.png"), REMOVE_ICON_SIZE, REMOVE_BUTTON_HIGHLIGHT);
			CHEVRON_ICON = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/chevron.png"), CHEVRON_ICON_SIZE, ADD_COLOR_HIGHLIGHT);
		}catch(IOException | FontFormatException e){
			//this should not happen
			throw new RuntimeException("Failed to load fonts or icons", e);
		}
	}
	
	/**
	 * Enum with specific colours for the different players.
	 * @author Roan
	 */
	public static enum PlayerTheme{
		/**
		 * Color data for things not associated with any player.
		 */
		UNOWNED(102, 103, 104, 174, 175, 175, 191, 191, 191, 129, 129, 129),
		/**
		 * Color data for the first player (purple/pink).
		 */
		P1(
			166, 13, 140,//body playfield
			235, 106, 218,//outline playfield
			255, 115, 236,//outline base type
			214, 0, 177,//body base type
			234, 108, 217//gradient
		),
		/**
		 * Color data for the second player (blue).
		 */
		P2(
			14, 135, 180,
			2, 197, 232,
			0, 217, 255,
			11, 171, 229,
			5, 208, 245
		),
		/**
		 * Color data for the third player (green).
		 */
		P3(
			13, 153, 45,
			22, 207, 68,
			30, 250, 85,
			10, 196, 50,
			19, 242, 75
		),
		/**
		 * Color data for the fourth player (yellow/gold).
		 */
		P4(
			175, 104, 21,
			243, 187, 27,
			255, 198, 28,
			227, 131, 18,
			247, 207, 5
		);
		
		/**
		 * Player theme text color.
		 */
		private final Color text;
		/**
		 * Player theme object body color.
		 */
		private final Color body;
		/**
		 * Player theme object outline color.
		 */
		private final Color outline;
		/**
		 * Player theme gradient color.
		 */
		private final Color gradient;
		/**
		 * Player theme bar chart bar body color.
		 */
		private final Color barBody;
		/**
		 * Player theme bar chart bar outline color.
		 */
		private final Color barOutline;
		/**
		 * Player theme base outline color.
		 */
		private final Color baseOutline;
		/**
		 * AI icon in the color for this player theme.
		 */
		private final BufferedImage ai;
		/**
		 * Human icon in the color for this player theme.
		 */
		private final BufferedImage human;
		/**
		 * Small AI icon in the color for this player theme.
		 */
		private final BufferedImage aiSmall;
		/**
		 * Small human icon in the color for this player theme.
		 */
		private final BufferedImage humanSmall;
		
		/**
		 * Constructs a new player theme with the given colours.
		 * @param br The object body color red component.
		 * @param bg The object body color green component.
		 * @param bb The object body color blue component.
		 * @param or The object outline color red component.
		 * @param og The object outline color green component.
		 * @param ob The object outline color blue component.
		 * @param tr The text/outline color red component.
		 * @param tg The text/outline color green component.
		 * @param tb The text/outline color blue component.
		 * @param cr The bar chart body color red component.
		 * @param cg The bar chart body color green component.
		 * @param cb The bar chart body color blue component.
		 * @param gr The gradient color red component.
		 * @param gg The gradient color green component.
		 * @param gb The gradient color blue component.
		 */
		private PlayerTheme(int br, int bg, int bb, int or, int og, int ob, int tr, int tg, int tb, int cr, int cg, int cb, int gr, int gg, int gb){
			this(br, bg, bb, or, og, ob, tr, tg, tb, cr, cg, cb, new Color(gr, gg, gb));
		}
		
		/**
		 * Constructs a new player theme with the given colours.
		 * @param br The object body color red component.
		 * @param bg The object body color green component.
		 * @param bb The object body color blue component.
		 * @param or The object outline color red component.
		 * @param og The object outline color green component.
		 * @param ob The object outline color blue component.
		 * @param tr The text/outline color red component.
		 * @param tg The text/outline color green component.
		 * @param tb The text/outline color blue component.
		 * @param cr The bar chart body color red component.
		 * @param cg The bar chart body color green component.
		 * @param cb The bar chart body color blue component.
		 */
		private PlayerTheme(int br, int bg, int bb, int or, int og, int ob, int tr, int tg, int tb, int cr, int cg, int cb){
			this(br, bg, bb, or, og, ob, tr, tg, tb, cr, cg, cb, null);
		}
		
		/**
		 * Constructs a new player theme with the given colours.
		 * @param br The object body color red component.
		 * @param bg The object body color green component.
		 * @param bb The object body color blue component.
		 * @param or The object outline color red component.
		 * @param og The object outline color green component.
		 * @param ob The object outline color blue component.
		 * @param tr The text/outline color red component.
		 * @param tg The text/outline color green component.
		 * @param tb The text/outline color blue component.
		 * @param cr The bar chart body color red component.
		 * @param cg The bar chart body color green component.
		 * @param cb The bar chart body color blue component.
		 * @param gradient The gradient color.
		 */
		private PlayerTheme(int br, int bg, int bb, int or, int og, int ob, int tr, int tg, int tb, int cr, int cg, int cb, Color gradient){
			body = new Color(br, bg, bb);
			outline = new Color(or, og, ob);
			text = new Color(tr, tg, tb, (9 * 255) / 10);
			barOutline = text;
			barBody = new Color(cr, cg, cb, (75 * 255) / 100);
			baseOutline = text;
			this.gradient = gradient;
			try{
				ai = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/ai.png"), PLAYER_ICON_SIZE, outline);
				human = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/human.png"), PLAYER_ICON_SIZE, outline);
				aiSmall = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/ai.png"), PLAYER_ICON_SIZE_SMALL, outline);
				humanSmall = loadImage(ClassLoader.getSystemResourceAsStream("assets/icons/human.png"), PLAYER_ICON_SIZE_SMALL, outline);
			}catch(IOException e){
				//should not happen
				throw new RuntimeException("Failed to load icons", e);
			}
		}
		
		/**
		 * Gets the AI icon for this theme.
		 * @return The AI icon for this theme.
		 */
		public BufferedImage getIconAI(){
			return ai;
		}
		
		/**
		 * Gets the human icon for this theme.
		 * @return The human icon for this theme.
		 */
		public BufferedImage getIconHuman(){
			return human;
		}
		
		/**
		 * Gets the small AI icon for this theme.
		 * @return The small AI icon for this theme.
		 */
		public BufferedImage getSmallIconAI(){
			return aiSmall;
		}
		
		/**
		 * Gets the small human icon for this theme.
		 * @return The small human icon for this theme.
		 */
		public BufferedImage getSmallIconHuman(){
			return humanSmall;
		}
		
		/**
		 * Gets the text color for this theme.
		 * @return The text color for this theme.
		 */
		public Color getTextColor(){
			return text;
		}
		
		/**
		 * Gets the object body color for this theme.
		 * @return The object body color for this theme.
		 */
		public Color getBody(){
			return body;
		}
		
		/**
		 * Gets the object outline color for this theme.
		 * @return The object outline color for this theme.
		 */
		public Color getOutline(){
			return outline;
		}
		
		/**
		 * Gets the bar chart body color for this theme.
		 * @return The bar chart body color for this theme.
		 */
		public Color getBarBody(){
			return barBody;
		}
		
		/**
		 * Gets the bar chart outline color for this theme.
		 * @return The bar chart outline color for this theme.
		 */
		public Color getBarOutline(){
			return barOutline;
		}
		
		/**
		 * Gets the base outline color for this theme.
		 * @return The base outline color for this theme.
		 */
		public Color getBaseOutline(){
			return baseOutline;
		}
		
		/**
		 * Gets the player theme for the player with the given ID.
		 * @param i The ID of the player to get (starting from 1).
		 * @return The theme for the player with the given ID.
		 */
		public static final PlayerTheme get(int i){
			return values()[i];
		}
	}
}
