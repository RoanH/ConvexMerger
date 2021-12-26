package dev.roanh.convexmerger.animation;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.ConvexUtil;

public class MergeAnimation extends ClaimAnimation{
	private boolean unclaimed;
	private ConvexObject owned;
	private Point[] mergeLines;
	
	
	public MergeAnimation(ConvexObject owned, ConvexObject target, ConvexObject result, List<ConvexObject> contained){
		super(target, target.getCentroid());
		unclaimed = !target.isOwned();
		target.setOwner(owned.getOwner());
		this.owned = owned;
		
		mergeLines = ConvexUtil.computeMergeLines(owned.getPoints(), target.getPoints(), result.getPoints());
	}

	@Override
	public boolean run(Graphics2D g){
		if(unclaimed){
			owned.render(g);
			if(!super.run(g)){
				unclaimed = false;
			}
			return true;
		}

		
		
		
		// TODO Auto-generated method stub
		return false;
	}

}
