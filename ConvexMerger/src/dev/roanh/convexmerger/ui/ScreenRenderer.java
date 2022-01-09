package dev.roanh.convexmerger.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import dev.roanh.convexmerger.Constants;

/**
 * Main game renderer responsible for rendering screens.
 * @author Roan
 */
public class ScreenRenderer extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ThreadFactory{
	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1921051341650291287L;
	/**
	 * Executor service used to run animations.
	 */
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(this);
	/**
	 * The active screen to render.
	 */
	private Screen screen;
	
	/**
	 * Constructs a new screen renderer with the given
	 * initial screen to render.
	 * @param screen The screen to render.
	 */
	public ScreenRenderer(Screen screen){
		this.setFocusable(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		setScreen(screen);
	}
	
	/**
	 * Sets the screen to render.
	 * @param screen The new screen to render.
	 * @return The previous screen.
	 */
	public Screen setScreen(Screen screen){
		Screen old = this.screen;
		this.screen = screen;
		return old;
	}
	
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		screen.render(g, this.getWidth(), this.getHeight());
		executor.schedule(()->this.repaint(), Constants.ANIMATION_RATE, TimeUnit.MILLISECONDS);
	}

	@Override
	public void keyTyped(KeyEvent e){
	}

	@Override
	public void keyPressed(KeyEvent e){
		screen.handleKeyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e){
		screen.handleKeyReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e){
	}

	@Override
	public void mouseMoved(MouseEvent e){
		screen.handleMouseMove(e.getPoint(), this.getWidth(), this.getHeight());
	}

	@Override
	public void mouseClicked(MouseEvent e){
	}

	@Override
	public void mousePressed(MouseEvent e){
	}

	@Override
	public void mouseReleased(MouseEvent e){
		screen.handleMouseClick(e.getPoint(), this.getWidth(), this.getHeight());
	}

	@Override
	public void mouseEntered(MouseEvent e){
	}

	@Override
	public void mouseExited(MouseEvent e){
	}

	@Override
	public Thread newThread(Runnable r){
		Thread thread = Executors.defaultThreadFactory().newThread(r);
		thread.setDaemon(true);
		thread.setName("AnimationThread-" + thread.getName());
		return thread;
	}
}
