package dev.roanh.convexmerger.player;

import java.util.Comparator;
import java.util.Optional;

import dev.roanh.convexmerger.game.ConvexObject;

public class SmallPlayer extends LocalPlayer{

	public SmallPlayer(){
		super("Shiro");
	}
	
	@Override
	protected boolean claimNewObject(){
		Optional<ConvexObject> obj = state.stream().filter(ConvexObject::canClaim).sorted(Comparator.comparingDouble(ConvexObject::getArea)).filter(this::hasMergeFrom).findFirst();
		if(obj.isPresent()){
			target = state.claimObject(obj.get()).getResult();
			return true;
		}else{
			return super.claimNewObject();
		}
	}
}
