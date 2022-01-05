package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_WIDTH;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_TEXT_OFFSET;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_SPACE;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_OFFSET;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.Constants;
import dev.roanh.convexmerger.game.GameState;

/**
 * Base class for all screens containing shared rendering subroutines.
 * @author Roan
 */
public abstract class Screen{
	/**
	 * Dimensions of the triangles on the left and right side of the top part.
	 */
	public static final int TOP_SIDE_TRIANGLE = 50;
	/**
	 * Height of the middle text area attached to the top part.
	 */
	private static final int TOP_MIDDLE_OFFSET = 30;
	/**
	 * Height of the buttons in the bottom left and right.
	 */
	private static final int BUTTON_HEIGHT = 50;
	/**
	 * Width of the buttons in the bottom left and right.
	 */
	private static final int BUTTON_WIDTH = 150;
	/**
	 * Space between the boxes.
	 */
	public static final int BOX_SPACING = 12;
	public static final int BOX_INSETS = 10;
	public static final int BOX_HEADER_HEIGHT = 28;
	public static final int BOX_TEXT_OFFSET = 4;
	/**
	 * Bottom right button polygon.
	 */
	private Polygon rightPoly = null;
	/**
	 * Bottom left button polygon.
	 */
	private Polygon leftPoly = null;
	private Point2D lastLocation = new Point2D.Double();
	private ConvexMerger context;
	
	protected Screen(ConvexMerger context){
		this.context = context;
	}
	
	protected ConvexMerger getContext(){
		return context;
	}
	
	public Screen switchScene(Screen next){
		return context.switchScene(next);
	}

	public void render(Graphics2D g, int width, int height){
		g.setColor(Theme.BACKGROUND);
		g.fillRect(0, 0, width, height);
		
		render(g, width, height, lastLocation);
	}
	
	public abstract void render(Graphics2D g, int width, int height, Point2D mouseLoc);
	
	public static double getMaxWidth(int width, double ratio, int max){
		return Math.min(ratio * width, max);
	}
	
	public void drawTitle(Graphics2D g, int width){
		g.setColor(Color.WHITE);
		g.setFont(Theme.PRIDI_MEDIUM_30);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(Constants.TITLE, (width - fm.stringWidth(Constants.TITLE)) / 2.0F, (GamePanel.TOP_SPACE + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F);
	}
	
	public void drawTitledBox(Graphics2D g, Paint gradient, double x, double y, double w, double h, String title){
		g.setColor(Theme.MENU_BODY);
		drawBox(g, x, y, w, h);
		
		g.setStroke(Theme.BORDER_STROKE);
		g.setPaint(gradient);
		g.draw(new Line2D.Double(x, y + BOX_HEADER_HEIGHT + 1.0D, x + w, y + BOX_HEADER_HEIGHT + 1.0D));
		g.setPaint(null);
		
		g.setFont(Theme.PRIDI_REGULAR_18);
		g.setColor(Theme.BOX_TEXT_COLOR);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title, (float)(x + (w - fm.stringWidth(title)) / 2.0F), (float)(y + (BOX_HEADER_HEIGHT + fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2.0F));
		
	}
	
	public void drawBox(Graphics2D g, double x, double y, double w, double h){
		g.fill(computeBox(g, x, y, w, h, BOX_INSETS));
	}
	
	public Path2D computeBox(Graphics2D g, double x, double y, double w, double h, double inset){
		Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 8);
		path.moveTo(x, y + inset);
		path.lineTo(x + inset, y);
		path.lineTo(x + w - inset, y);
		path.lineTo(x + w, y + inset);
		path.lineTo(x + w, y + h - inset);
		path.lineTo(x + w - inset, y + h);
		path.lineTo(x + inset, y + h);
		path.lineTo(x, y + h - inset);
		path.closePath();
		return path;
	}
	
	public void renderMenuTitle(Graphics2D g, int width, String title){
		g.setFont(Theme.PRIDI_REGULAR_18);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(title, Math.floorDiv(width, 2) - (TOP_MIDDLE_WIDTH / 2) + (TOP_MIDDLE_WIDTH - fm.stringWidth(title)) / 2.0F, TOP_SPACE + TOP_OFFSET - fm.getDescent() - TOP_MIDDLE_TEXT_OFFSET);
	}
	
	public int fillText(Graphics2D g, int rx, int ry, int width, int height, List<String> text){
		FontMetrics fm = g.getFontMetrics();
		int x = rx;
		int y = ry + fm.getAscent();
		for(String word : text){
			int w = fm.stringWidth(word);
			if(x + w - rx > width){
				x = rx;
				y += fm.getHeight();
				if(y > ry + height){
					y -= fm.getHeight();
					break;
				}
			}
			g.drawString(word + " ", x, y);
			x += w + fm.stringWidth(" ");
		}
		return y;
	}
	
	//state optional
	public void renderMainInterface(Graphics2D g, int width, int height, GameState state){
		g.setColor(Theme.MENU_BODY);
		int sideOffset = Math.floorDiv(width, 2) - (TOP_MIDDLE_WIDTH / 2);
		Polygon topPoly = new Polygon(new int[]{
				0,
				0,
				TOP_SIDE_TRIANGLE,
				sideOffset - TOP_MIDDLE_OFFSET,
				sideOffset,
				width - sideOffset,
				width - sideOffset + TOP_MIDDLE_OFFSET,
				width - TOP_SIDE_TRIANGLE,
				width,
				width
			},
			new int[]{
				0,
				TOP_SIDE_TRIANGLE + TOP_SPACE,
				TOP_SPACE,
				TOP_SPACE,
				TOP_SPACE + TOP_MIDDLE_OFFSET,
				TOP_SPACE + TOP_MIDDLE_OFFSET,
				TOP_SPACE,
				TOP_SPACE,
				TOP_SIDE_TRIANGLE + TOP_SPACE,
				0
			},
			10
		);
		g.fill(topPoly);
		
		g.setFont(Theme.PRIDI_REGULAR_24);
		FontMetrics fm = g.getFontMetrics();
		
		rightPoly = new Polygon(
			new int[]{
				width,
				width - BUTTON_WIDTH,
				width - BUTTON_WIDTH + BUTTON_HEIGHT,
				width
			},
			new int[]{
				height,
				height,
				height - BUTTON_HEIGHT,
				height - BUTTON_HEIGHT
			},
			4
		);
		g.setColor((isRightButtonEnabled() && rightPoly.contains(lastLocation)) ? Theme.BUTTON_HOVER_COLOR : Theme.MENU_BODY);
		g.fill(rightPoly);
		String text = getRightButtonText();
		if(text != null){
			g.setColor((isRightButtonEnabled() && rightPoly.contains(lastLocation)) ? Color.WHITE : Theme.BUTTON_TEXT_COLOR);
			g.drawString(text, width - BUTTON_WIDTH / 2.0F + BUTTON_HEIGHT / 4.0F - fm.stringWidth(text) / 2.0F, height + (fm.getAscent() - BUTTON_HEIGHT - fm.getDescent() - fm.getLeading()) / 2.0F);
		}
		
		leftPoly = new Polygon(
			new int[]{
				0,
				0,
				BUTTON_WIDTH - BUTTON_HEIGHT,
				BUTTON_WIDTH
			},
			new int[]{
				height,
				height - BUTTON_HEIGHT,
				height - BUTTON_HEIGHT,
				height
			},
			4
		);
		g.setColor((isLeftButtonEnabled() && leftPoly.contains(lastLocation)) ? Theme.BUTTON_HOVER_COLOR : Theme.MENU_BODY);
		g.fill(leftPoly);
		text = getLeftButtonText();
		if(text != null){
			g.setColor((isLeftButtonEnabled() && leftPoly.contains(lastLocation)) ? Color.WHITE : Theme.BUTTON_TEXT_COLOR);
			g.drawString(text, BUTTON_WIDTH / 2.0F - BUTTON_HEIGHT / 4.0F - fm.stringWidth(text) / 2.0F, height + (fm.getAscent() - BUTTON_HEIGHT - fm.getDescent() - fm.getLeading()) / 2.0F);
		}
		
		//render UI borders
		g.setPaint(Theme.constructBorderGradient(state, width));
		g.setStroke(Theme.BORDER_STROKE);
		
		Path2D infoPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
		infoPath.moveTo(rightPoly.xpoints[1], rightPoly.ypoints[1] - 1);
		infoPath.lineTo(rightPoly.xpoints[2], rightPoly.ypoints[2] - 1);
		infoPath.lineTo(rightPoly.xpoints[3] - 1, rightPoly.ypoints[3] - 1);
		g.draw(infoPath);
		
		Path2D menuPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
		menuPath.moveTo(leftPoly.xpoints[1], leftPoly.ypoints[1] - 1);
		menuPath.lineTo(leftPoly.xpoints[2], leftPoly.ypoints[2] - 1);
		menuPath.lineTo(leftPoly.xpoints[3] + 1, leftPoly.ypoints[3]);
		g.draw(menuPath);
		
		Path2D topPath = new Path2D.Double(Path2D.WIND_NON_ZERO, topPoly.npoints - 2);
		topPath.moveTo(topPoly.xpoints[1], topPoly.ypoints[1]);
		for(int i = 2; i < topPoly.npoints - 1; i++){
			topPath.lineTo(topPoly.xpoints[i], topPoly.ypoints[i]);
		}
		g.draw(topPath);
	}
	
	public void handleMouseClick(Point2D loc, int width, int height){
		if(isLeftButtonEnabled() && leftPoly.contains(loc)){
			handleLeftButtonClick();
		}else if(isRightButtonEnabled() && rightPoly.contains(loc)){
			handleRightButtonClick();
		}
	}
	
	public void handleMouseMove(Point2D loc, int width, int height){
		lastLocation = loc;
	}
	
	public void handleKeyPressed(KeyEvent event){
	}

	public void handleKeyReleased(KeyEvent event){
	}
	
	public abstract boolean isLeftButtonEnabled();
	
	public abstract boolean isRightButtonEnabled();
	
	public abstract String getLeftButtonText();
	
	public abstract String getRightButtonText();
	
	public abstract void handleLeftButtonClick();
	
	public abstract void handleRightButtonClick();
}
