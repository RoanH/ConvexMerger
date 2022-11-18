/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
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
	 * Whether to render the FPS counter.
	 */
	private boolean showFPS = false;
	
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
		this.setFocusTraversalKeysEnabled(false);
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
		AffineTransform transform = g.getTransform();
		
		long start = System.currentTimeMillis();
		screen.render(g, this.getWidth(), this.getHeight());
		long delta = System.currentTimeMillis() - start;
		executor.schedule(()->this.repaint(), Math.max(0, Constants.ANIMATION_RATE - delta), TimeUnit.MILLISECONDS);
		
		if(showFPS){
			g.setTransform(transform);
			g.setFont(Theme.PRIDI_MEDIUM_14);
			g.setColor(Color.RED);
			long max = 1000 / Constants.ANIMATION_RATE;
			g.drawString("FPS: " + String.valueOf(delta == 0 ? max : Math.min(max, 1000 / delta)) + "/" + max + " (" + delta + "ms)", 7, g.getFontMetrics().getAscent());
		}
	}

	@Override
	public void keyTyped(KeyEvent e){
	}

	@Override
	public void keyPressed(KeyEvent e){
		screen.handleKeyPressed(e);
		if(e.getKeyCode() == KeyEvent.VK_F3){
			showFPS = !showFPS;
		}
	}

	@Override
	public void keyReleased(KeyEvent e){
		screen.handleKeyReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e){
		screen.handleMouseDrag(e.getPoint(), this.getWidth(), this.getHeight());
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
		screen.handleMousePress(e.getPoint(), this.getWidth(), this.getHeight());
	}

	@Override
	public void mouseReleased(MouseEvent e){
		screen.handleMouseRelease(e.getPoint(), this.getWidth(), this.getHeight());
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
