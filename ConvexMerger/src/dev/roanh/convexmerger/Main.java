package dev.roanh.convexmerger;

import dev.roanh.convexmerger.ui.ConvexMerger;
import dev.roanh.util.Util;

public class Main{

	public static void main(String[] args){
		Util.installUI();
		new Thread(){
			@Override
			public void run(){
				ConvexMerger game = new ConvexMerger();
				//game.initialiseGame();
				
				game.hostMultiplayerGame();
				
				//game.showGame();
				//TODO ...
			}
		}.start();
		
		try{
			Thread.sleep(5000);
			System.out.println("try connect");
		}catch(InterruptedException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConvexMerger game2 = new ConvexMerger();
		game2.joinMultiplayerGame();
		
		//game2.showGame();
	}
}
