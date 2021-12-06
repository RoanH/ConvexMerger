package dev.roanh.convexmerger.game;

import java.util.Arrays;
import java.util.List;

public class PlayfieldGenerator{

	public static List<ConvexObject> generatePlayfield(){
		//you may assume that the playfield is 0~1600 on the x-axis and 0~900 on the y-axis
		
		return Arrays.asList(
			new ConvexObject(0, 0, 50, 50, 10, 50),
			new ConvexObject(100, 100, 150, 100, 200, 150)
		);
	}
}
