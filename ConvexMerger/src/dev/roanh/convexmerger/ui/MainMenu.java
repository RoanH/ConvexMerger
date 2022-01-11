package dev.roanh.convexmerger.ui;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.concurrent.ThreadLocalRandom;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * Game main menu screen.
 * @author Roan
 */
public class MainMenu extends Screen{
	/**
	 * Width of the buttons.
	 */
	private static final double BUTTON_WIDTH = 250.0D;
	/**
	 * Height of the buttons.
	 */
	private static final double BUTTON_HEIGHT = 70.0D;
	/**
	 * Bounds of the multiplayer join button.
	 */
	private Path2D join = new Path2D.Double();
	/**
	 * Bounds of the multiplayer host button.
	 */
	private Path2D host = new Path2D.Double();
	/**
	 * Bounds of the single player button.
	 */
	private Path2D single = new Path2D.Double();
	/**
	 * Bounds of the info button.
	 */
	private Path2D info = new Path2D.Double();
	/**
	 * Bounds of the quit button.
	 */
	private Path2D quit = new Path2D.Double();
	/**
	 * Main menu show case objects.
	 */
	private Path2D[] objects;

	/**
	 * Constructs a new main menu with the given game context.
	 * @param context The game context.
	 */
	protected MainMenu(ConvexMerger context){
		super(context);
		ThreadLocalRandom r = ThreadLocalRandom.current();
		objects = new Path2D[]{
			new ConvexObject(
				r.nextDouble(), r.nextDouble(),
				1.0D + r.nextDouble(), r.nextDouble(),
				r.nextDouble(), 1.0D + r.nextDouble(),
				1.0D + r.nextDouble(), 1.0D + r.nextDouble()
			).getShape(),
			new ConvexObject(
				r.nextDouble(), r.nextDouble(),
				1.0D + r.nextDouble(), r.nextDouble(),
				r.nextDouble(), 1.0D + r.nextDouble(),
				1.0D + r.nextDouble(), 1.0D + r.nextDouble()
			).getShape(),
			new ConvexObject(
				r.nextDouble(), r.nextDouble(),
				1.0D + r.nextDouble(), r.nextDouble(),
				r.nextDouble(), 1.0D + r.nextDouble(),
				1.0D + r.nextDouble(), 1.0D + r.nextDouble()
			).getShape(),
			new ConvexObject(
				r.nextDouble(), r.nextDouble(),
				1.0D + r.nextDouble(), r.nextDouble(),
				r.nextDouble(), 1.0D + r.nextDouble(),
				1.0D + r.nextDouble(), 1.0D + r.nextDouble()
			).getShape()
		};
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		renderMenuTitle(g, width, "Main Menu");
		drawTitle(g, width);
		
		single = drawButton(g, "Single Player", (width - BUTTON_WIDTH) / 2.0D, TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D + TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT, mouseLoc);
		host = drawButton(g, "Host Multiplayer", (width - BUTTON_WIDTH) / 2.0D, TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D + TOP_SPACE + BOX_SPACING + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, mouseLoc);
		join = drawButton(g, "Join Multiplayer", (width - BUTTON_WIDTH) / 2.0D, TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D + TOP_SPACE + (BOX_SPACING + BUTTON_HEIGHT) * 2.0D, BUTTON_WIDTH, BUTTON_HEIGHT, mouseLoc);
		info = drawButton(g, "Info & Rules", (width - BUTTON_WIDTH) / 2.0D, TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D + TOP_SPACE + (BOX_SPACING + BUTTON_HEIGHT) * 3.0D, BUTTON_WIDTH, BUTTON_HEIGHT, mouseLoc);
		quit = drawButton(g, "Quit", (width - BUTTON_WIDTH) / 2.0D, TOP_MIDDLE_OFFSET + BOX_SPACING * 2.0D + TOP_SPACE + (BOX_SPACING + BUTTON_HEIGHT) * 4.0D, BUTTON_WIDTH, BUTTON_HEIGHT, mouseLoc);
		
		double w = (width - BUTTON_WIDTH - TOP_SIDE_TRIANGLE * 4.0D) / 2.0D;
		double h = (height - TOP_SPACE - TOP_SIDE_TRIANGLE * 2.0D - BOX_INSETS) / 2.0D;
		AffineTransform transform = AffineTransform.getScaleInstance(0.5D * w, 0.5D * h);
		
		g.translate(TOP_SIDE_TRIANGLE, TOP_SPACE + TOP_SIDE_TRIANGLE);
		render(g, objects[0].createTransformedShape(transform), PlayerTheme.P1);
		
		g.translate(0.0D, h);
		render(g, objects[1].createTransformedShape(transform), PlayerTheme.P2);
		
		g.translate(TOP_SIDE_TRIANGLE * 2.0D + BUTTON_WIDTH + w, 0.0D);
		render(g, objects[2].createTransformedShape(transform), PlayerTheme.P3);
		
		g.translate(0.0D, -h);
		render(g, objects[3].createTransformedShape(transform), PlayerTheme.P4);
	}
	
	/**
	 * Renders the given shape in
	 * the convex object style.
	 * @param g The graphics context to use.
	 * @param obj The shape to render.
	 * @param theme The theme to use.
	 */
	private void render(Graphics2D g, Shape obj, PlayerTheme theme){
		g.setColor(theme.getBody());
		g.fill(obj);
		g.setStroke(Theme.POLY_STROKE);
		g.setColor(theme.getOutline());
		g.draw(obj);
	}
	
	@Override
	public void handleMouseClick(Point2D loc, int width, int height){
		super.handleMouseClick(loc, width, height);
		
		if(join.contains(loc)){
			this.switchScene(new JoinMenu(this.getContext()));
		}
		
		if(host.contains(loc)){
			this.switchScene(new HostMenu(this.getContext()));
		}
		
		if(single.contains(loc)){
			this.switchScene(new NewGameMenu(this.getContext()));
		}
		
		if(info.contains(loc)){
			this.switchScene(new InfoMenu(this.getContext(), null, this));
		}
		
		if(quit.contains(loc)){
			this.getContext().exit();
		}
	}

	@Override
	protected boolean isLeftButtonEnabled(){
		return false;
	}

	@Override
	protected boolean isRightButtonEnabled(){
		return false;
	}

	@Override
	protected String getLeftButtonText(){
		return null;
	}

	@Override
	protected String getRightButtonText(){
		return null;
	}

	@Override
	protected void handleLeftButtonClick(){
	}

	@Override
	protected void handleRightButtonClick(){
	}
}
