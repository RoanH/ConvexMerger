package dev.roanh.convexmerger.ui;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class MainMenu extends Screen{

	protected MainMenu(ConvexMerger context){
		super(context);
	}

	@Override
	public void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		
	}

	@Override
	public boolean isLeftButtonEnabled(){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isRightButtonEnabled(){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLeftButtonText(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRightButtonText(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleLeftButtonClick(){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRightButtonClick(){
		// TODO Auto-generated method stub
		
	}

}
