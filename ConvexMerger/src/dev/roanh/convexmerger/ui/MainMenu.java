package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MainMenu extends Screen{
	private Rectangle2D join = new Rectangle2D.Double(100.0D, 200.0D, 200.0D, 50.0D);
	private Rectangle2D host = new Rectangle2D.Double(100.0D, 300.0D, 200.0D, 50.0D);

	protected MainMenu(ConvexMerger context){
		super(context);
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		
		g.setColor(Color.RED);
		g.draw(join);
		g.draw(host);
		
		g.setColor(Color.WHITE);
		g.setFont(Theme.PRIDI_MEDIUM_14);
		g.drawString("Host Multiplayer", (float)host.getMinX(), (float)host.getMaxY());
		g.drawString("Join Multiplayer", (float)join.getMinX(), (float)join.getMaxY());
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
	}

	@Override
	protected boolean isLeftButtonEnabled(){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean isRightButtonEnabled(){
		return false;
	}

	@Override
	protected String getLeftButtonText(){
		// TODO Auto-generated method stub
		return "New";
	}

	@Override
	protected String getRightButtonText(){
		return null;
	}

	@Override
	protected void handleLeftButtonClick(){
		this.switchScene(new HostMenu(this.getContext()));
	}

	@Override
	protected void handleRightButtonClick(){
	}
}
