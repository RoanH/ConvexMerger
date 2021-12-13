package dev.roanh.convexmerger.game;

import java.util.ArrayList;
import java.util.List;

import dev.roanh.convexmerger.Constants;

public class GameState{
	private List<ConvexObject> objects = new ArrayList<ConvexObject>();
	private List<Player> players = new ArrayList<Player>();
	private VerticalDecomposition decomp = new VerticalDecomposition(Constants.DECOMP_BOUNDS);
	
	
	
}
