package dev.roanh.convexmerger.ui;

import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_WIDTH;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_MIDDLE_TEXT_OFFSET;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_SPACE;
import static dev.roanh.convexmerger.ui.GamePanel.TOP_OFFSET;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import dev.roanh.convexmerger.Constants;

/**
 * Base class for all menus containing shared rendering subroutines.
 * @author Roan
 */
public abstract class Menu implements MouseMotionListener{
	/**
	 * Space between the boxes.
	 */
	public static final int BOX_SPACING = 12;
	public static final int BOX_INSETS = 10;
	public static final int BOX_HEADER_HEIGHT = 28;
	public static final int BOX_TEXT_OFFSET = 4;
	/**
	 * Info (bottom right) button polygon.
	 */
	private Polygon infoPoly = null;
	/**
	 * Menu (bottom left) button polygon.
	 */
	private Polygon menuPoly = null;
	private Point lastLocation = new Point();

	public abstract boolean render(Graphics2D g, int width, int height, Point2D mouseLoc);
	
	@Deprecated
	public abstract void handleMouseClick(Point2D loc);
	
	@Deprecated
	public abstract void handleKeyTyped(KeyEvent event);
	
	@Deprecated
	public void repaint(){
		//TODO ???
	}
	
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
		g.setColor(Theme.CROWN_COLOR);
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
	
	public void renderMainInterface(Graphics2D g, int width, int height){
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
		
		infoPoly = new Polygon(
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
		if(menu == null && !resultOverlay.isEnabled()){
			if(infoPoly.contains(lastLocation)){
				g.setColor(Theme.BUTTON_HOVER_COLOR);
			}else{
				g.setColor(Theme.MENU_BODY);
			}
		}
		g.setColor((menu == null && !resultOverlay.isEnabled() && infoPoly.contains(lastLocation)) ? Theme.BUTTON_HOVER_COLOR : Theme.MENU_BODY);
		g.fill(infoPoly);
		if(menu == null){
			g.setColor((!resultOverlay.isEnabled() && infoPoly.contains(lastLocation)) ? Color.WHITE : Theme.BUTTON_TEXT_COLOR);
			g.drawString("Info", this.getWidth() - BUTTON_WIDTH / 2.0F + BUTTON_HEIGHT / 4.0F - fm.stringWidth("Info") / 2.0F, this.getHeight() + (fm.getAscent() - BUTTON_HEIGHT - fm.getDescent() - fm.getLeading()) / 2.0F);
		}
		
		menuPoly = new Polygon(
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
		g.setColor(((!resultOverlay.isEnabled() || menu != null) && menuPoly.contains(lastLocation)) ? Theme.BUTTON_HOVER_COLOR : Theme.MENU_BODY);
		g.fill(menuPoly);
		String menuText = menu == null ? "Menu" : "Back";
		g.setColor(((!resultOverlay.isEnabled() || menu != null) && menuPoly.contains(lastLocation)) ? Color.WHITE : Theme.BUTTON_TEXT_COLOR);
		g.drawString(menuText, BUTTON_WIDTH / 2.0F - BUTTON_HEIGHT / 4.0F - fm.stringWidth(menuText) / 2.0F, this.getHeight() + (fm.getAscent() - BUTTON_HEIGHT - fm.getDescent() - fm.getLeading()) / 2.0F);
		
		//render UI borders
		g.setPaint(Theme.constructBorderGradient(state, this.getWidth()));
		g.setStroke(Theme.BORDER_STROKE);
		
		Path2D infoPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
		infoPath.moveTo(infoPoly.xpoints[1], infoPoly.ypoints[1] - 1);
		infoPath.lineTo(infoPoly.xpoints[2], infoPoly.ypoints[2] - 1);
		infoPath.lineTo(infoPoly.xpoints[3] - 1, infoPoly.ypoints[3] - 1);
		g.draw(infoPath);
		
		Path2D menuPath = new Path2D.Double(Path2D.WIND_NON_ZERO, 3);
		menuPath.moveTo(menuPoly.xpoints[1], menuPoly.ypoints[1] - 1);
		menuPath.lineTo(menuPoly.xpoints[2], menuPoly.ypoints[2] - 1);
		menuPath.lineTo(menuPoly.xpoints[3] + 1, menuPoly.ypoints[3]);
		g.draw(menuPath);
		
		Path2D topPath = new Path2D.Double(Path2D.WIND_NON_ZERO, topPoly.npoints - 2);
		topPath.moveTo(topPoly.xpoints[1], topPoly.ypoints[1]);
		for(int i = 2; i < topPoly.npoints - 1; i++){
			topPath.lineTo(topPoly.xpoints[i], topPoly.ypoints[i]);
		}
		g.draw(topPath);
	}
	
	public abstract boolean isLeftButtonEnabled();
	
	public abstract boolean isRightButtonEnabled();
	
	public abstract String getLeftButtonText();
	
	public abstract String getRightButtonText();
	
	@Override
	public void mouseEntered(MouseEvent e){
	}

	@Override
	public void mouseExited(MouseEvent e){
	}

	@Override
	public void mouseDragged(MouseEvent e){
	}

	@Override
	public void mouseMoved(MouseEvent e){
		lastLocation = e.getPoint();
	}
}
