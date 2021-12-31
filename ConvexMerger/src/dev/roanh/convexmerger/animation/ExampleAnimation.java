package dev.roanh.convexmerger.animation;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Arrays;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.player.Player;
import dev.roanh.convexmerger.ui.Theme;
import dev.roanh.convexmerger.ui.Theme.PlayerTheme;

/**
 * The example animation shown on the information menu.
 * @author Roan
 */
public class ExampleAnimation implements Animation{
	/**
	 * Width of the animation.
	 */
	public static final int WIDTH = 600;
	/**
	 * Height of the animation.
	 */
	public static final int HEIGHT = 300;
	/**
	 * First player.
	 */
	private static final Player pink = new DummyPlayer(PlayerTheme.P1);
	/**
	 * Second player.
	 */
	private static final Player blue = new DummyPlayer(PlayerTheme.P2);
	/**
	 * Leftmost object.
	 */
	private static final ConvexObject first = new ConvexObject(106, 268, 45, 128, 176, 88, 187, 179);
	/**
	 * Copy of the leftmost object that is always owned by the first player.
	 */
	private static final ConvexObject firstCopy = new ConvexObject(106, 268, 45, 128, 176, 88, 187, 179);
	/**
	 * Middle object.
	 */
	private static final ConvexObject second = new ConvexObject(297, 209, 251, 116, 367, 127);
	/**
	 * Rightmost object.
	 */
	private static final ConvexObject third = new ConvexObject(482, 225, 412, 124, 485, 40, 540, 92);
	/**
	 * The result of merging the first and third object.
	 */
	private static final ConvexObject result;
	/**
	 * Current animation state.
	 */
	private int state = 0;
	/**
	 * Saved timestamp for animation.
	 */
	private long time;
	
	@Override
	public boolean run(Graphics2D g){
		switch(state){
		case 0:
			time = System.currentTimeMillis();
			state++;
			//$FALL-THROUGH$
		case 1:
			first.render(g);
			second.render(g);
			third.render(g);
			
			if(System.currentTimeMillis() - time > 1000L){
				first.setOwner(pink);
				first.setAnimation(new ClaimAnimation(first, first.getCentroid()));
				state++;
			}
			break;
		case 2:
			second.render(g);
			third.render(g);
			
			if(first.hasAnimation()){
				first.runAnimation(g);
			}else{
				first.render(g);
				time = System.currentTimeMillis();
				state++;
			}
			break;
		case 3:
			first.render(g);
			second.render(g);
			third.render(g);
			
			if(System.currentTimeMillis() - time > 1000L){
				second.setOwner(blue);
				second.setAnimation(new ClaimAnimation(second, second.getCentroid()));
				state++;
			}
			break;
		case 4:
			first.render(g);
			third.render(g);
			
			if(second.hasAnimation()){
				second.runAnimation(g);
			}else{
				second.render(g);
				time = System.currentTimeMillis();
				state++;
			}
			break;
		case 5:
			first.render(g);
			second.render(g);
			third.render(g);
			
			if(System.currentTimeMillis() - time > 1000L){
				time = System.currentTimeMillis();
				state++;
			}
			break;
		case 6:
			first.render(g);
			second.render(g);
			third.render(g);
			
			g.setColor(pink.getTheme().getOutline());
			g.setStroke(Theme.HELPER_STROKE);
			long elapsed = System.currentTimeMillis() - time;
			double f = Math.min(1.0D, elapsed / 1000.0D);
			g.draw(new Line2D.Double(106.0D, 268.0D, 106.0D + (482.0D - 106.0D) * f, 268.0D + (225.0D - 268.0D) * f));
			g.draw(new Line2D.Double(176.0D, 88.0D, 176.0D + (485.0D - 176.0D) * f, 88.0D + (40.0D - 88.0D) * f));
			
			if(elapsed > 1000){
				time = System.currentTimeMillis();
				state++;
			}
			break;
		case 7:
			first.render(g);
			second.render(g);
			third.render(g);
			
			Composite composite = g.getComposite();
			g.setStroke(Theme.HELPER_STROKE);
			g.setColor(pink.getTheme().getOutline());
			long ms = System.currentTimeMillis() - time;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.0F, 1.0F - Math.min(1.0F, ms / 500.0F))));
			g.draw(new Line2D.Double(106.0D, 268.0D, 482.0D, 225.0D));
			g.draw(new Line2D.Double(176.0D, 88.0D, 485.0D, 40.0D));
			g.setComposite(composite);
			
			if(ms > 500){
				result.setAnimation(new MergeAnimation(firstCopy, third, result, Arrays.asList(second)));
				state++;
			}
			break;
		case 8:
			if(result.hasAnimation()){
				result.runAnimation(g);
			}else{
				result.render(g);
				time = System.currentTimeMillis();
				state++;
			}
			break;
		case 9:
			result.render(g);
			if(System.currentTimeMillis() - time > 1500L){
				first.setOwner(null);
				second.setOwner(null);
				third.setOwner(null);
				time = System.currentTimeMillis();
				state++;
			}
			break;
		case 10:
			composite = g.getComposite();
			g.setStroke(Theme.HELPER_STROKE);
			g.setColor(pink.getTheme().getOutline());
			long spent = System.currentTimeMillis() - time;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.0F, 1.0F - Math.min(1.0F, spent / 1000.0F))));
			result.render(g);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.0F, Math.min(1.0F, spent / 1000.0F))));
			first.render(g);
			second.render(g);
			third.render(g);
			g.setComposite(composite);
			
			if(spent > 1000){
				state = 0;
			}
			break;
		}
		
		return true;
	}
	
	static{
		result = first.merge(third);
		result.setOwner(pink);
		firstCopy.setOwner(pink);
	}

	/**
	 * Dummy player instance to reuse convex object logic.
	 * @author Roan
	 */
	private static class DummyPlayer extends Player{

		/**
		 * Constructs a new dummy player with the given theme.
		 * @param theme The theme for this player.
		 */
		protected DummyPlayer(PlayerTheme theme){
			super(false, null);
			this.init(null, theme);
		}

		@Override
		public boolean executeMove(){
			return false;
		}
	}
}
