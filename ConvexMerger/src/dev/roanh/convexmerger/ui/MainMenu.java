package dev.roanh.convexmerger.ui;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class MainMenu extends Screen{

	protected MainMenu(ConvexMerger context){
		super(context);
	}

	@Override
	protected void render(Graphics2D g, int width, int height, Point2D mouseLoc){
		renderMainInterface(g, width, height, null);
		
	}

	@Override
	protected boolean isLeftButtonEnabled(){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean isRightButtonEnabled(){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getLeftButtonText(){
		// TODO Auto-generated method stub
		return "New";
	}

	@Override
	protected String getRightButtonText(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handleLeftButtonClick(){
		this.switchScene(new NewGameMenu(this.getContext()));
	}

	@Override
	protected void handleRightButtonClick(){
		// TODO Auto-generated method stub
		
	}

}
