package dev.roanh.convexmerger.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;

public class ScreenRenderer extends JPanel{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1921051341650291287L;
	/**
	 * Executor service used to run animations.
	 */
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private Menu screen;
	
	public ScreenRenderer(Menu screen){
		this.setFocusable(true);
		setScreen(screen);
	}
	
	public void setScreen(Menu screen){
		this.removeMouseListener(this.screen);
		this.removeMouseMotionListener(this.screen);
		//TODO this.removeKeyListener(this.screen);
		this.addMouseListener(screen);
		this.addMouseMotionListener(screen);
		if(screen instanceof KeyListener){
			//TODO this.addKeyListener(screen);
		}
		this.screen = screen;
	}
	
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		screen.render(g, this.getWidth(), this.getHeight());
		executor.schedule(()->this.repaint(), Constants.ANIMATION_RATE, TimeUnit.MILLISECONDS);
	}
}
