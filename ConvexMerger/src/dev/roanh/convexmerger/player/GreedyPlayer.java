package dev.roanh.convexmerger.player;

import java.util.List;
import java.util.stream.Collectors;

import dev.roanh.convexmerger.game.ConvexObject;

/**
 * Simple AI that follows the greedy
 * strategy of maximising area again
 * in every turn.
 * @author Roan
 */
public class GreedyPlayer extends Player{

	/**
	 * Constructs a new greedy player (Isla).
	 */
	public GreedyPlayer(){
		super(true, true, "Isla");
	}
	
	@Override
	public boolean executeMove(){
		List<ConvexObject> owned = stream().collect(Collectors.toList());
		
		//find the single largest object
		ConvexObject max = findLargestUnownedObject();
		
		//merge any of our owned objects with something else to get the largest area
		MergeOption bestMerge = null;

		for(ConvexObject obj : owned){
			MergeOption option = findBestMergeFrom(obj);
			if(option != null && (bestMerge == null || option.getIncrease() > bestMerge.getIncrease())){
				bestMerge = option;
			}
		}

		if(bestMerge != null && (max == null || bestMerge.getIncrease() > max.getArea())){
			bestMerge.execute();
			return true;
		}
		
		//claiming the largest object is best
		if(max != null){
			state.claimObject(max);
			return true;
		}else{
			return false;
		}
	}
}
