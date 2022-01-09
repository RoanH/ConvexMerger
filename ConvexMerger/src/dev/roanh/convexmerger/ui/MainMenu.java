package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MainMenu extends Screen{
	private Rectangle2D join = new Rectangle2D.Double();
	private Rectangle2D host = new Rectangle2D.Double();
	private Rectangle2D single = new Rectangle2D.Double();
	private Rectangle2D info = new Rectangle2D.Double();
	private Rectangle2D quit = new Rectangle2D.Double();

	protected MainMenu(ConvexMerger context){
		super(context);
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		
		single = new Rectangle2D.Double((width - 200.0D) / 2.0D, 200.0D, 200.0D, 50.0D);
		host = new Rectangle2D.Double((width - 200.0D) / 2.0D, 300.0D, 200.0D, 50.0D);
		join = new Rectangle2D.Double((width - 200.0D) / 2.0D, 400.0D, 200.0D, 50.0D);
		info = new Rectangle2D.Double((width - 200.0D) / 2.0D, 500.0D, 200.0D, 50.0D);
		quit = new Rectangle2D.Double((width - 200.0D) / 2.0D, 600.0D, 200.0D, 50.0D);
		
		g.setColor(Color.RED);
		g.draw(single);
		g.draw(join);
		g.draw(host);
		g.draw(info);
		g.draw(quit);
		
		g.setColor(Color.WHITE);
		g.setFont(Theme.PRIDI_MEDIUM_14);
		FontMetrics fm = g.getFontMetrics();
		g.drawString("Single player", (float)(single.getMinX() + (single.getWidth() - fm.stringWidth("Single player")) / 2.0F), (float)(single.getMaxY() - (single.getHeight() - fm.getAscent() + fm.getDescent()) / 2.0F));
		g.drawString("Host Multiplayer", (float)(host.getMinX() + (host.getWidth() - fm.stringWidth("Host Multiplayer")) / 2.0F), (float)(host.getMaxY() - (host.getHeight() - fm.getAscent() + fm.getDescent()) / 2.0F));
		g.drawString("Join Multiplayer", (float)(join.getMinX() + (join.getWidth() - fm.stringWidth("Join Multiplayer")) / 2.0F), (float)(join.getMaxY() - (join.getHeight() - fm.getAscent() + fm.getDescent()) / 2.0F));
		g.drawString("Info & Rules", (float)(info.getMinX() + (info.getWidth() - fm.stringWidth("Info & Rules")) / 2.0F), (float)(info.getMaxY() - (info.getHeight() - fm.getAscent() + fm.getDescent()) / 2.0F));
		g.drawString("Quit", (float)(quit.getMinX() + (quit.getWidth() - fm.stringWidth("Quit")) / 2.0F), (float)(quit.getMaxY() - (quit.getHeight() - fm.getAscent() + fm.getDescent()) / 2.0F));
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
