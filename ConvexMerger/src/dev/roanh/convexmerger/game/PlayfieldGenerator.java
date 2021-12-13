package dev.roanh.convexmerger.game;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generator to generate a playfield of randomly placed convex objects
 * on a rectangular plane. The plane has size 0~1600 on the x-axis and
 * 0~900 on the y-axis. Generated playfields will never contain
 * overlapping objects and objects will be distributed evenly. Each
 * of the convex objects will be defined by 3 to 4 points and all
 * convex objects will have roughly the same area.
 * @author Roan
 * @author Thiam-Wai
 */
public class PlayfieldGenerator{
	/**
	 * Local random instance to use the generate the playfield.
	 */
	private final Random random;

	/**
	 * Constructs a new playfield generator with a random seed.
	 */
	public PlayfieldGenerator(){
		this(ThreadLocalRandom.current().nextLong());
	}
	
	/**
	 * Constructs a new playfield generate with the given seed.
	 * @param seed The random seed to use.
	 */
	public PlayfieldGenerator(long seed){
		random = new Random(seed);
	}

	/**
	 * Generates a new game playfield with convex objects made up of 3 to 4 points.
	 * The objects will have coordinates within 0~1600 on the x-axis and within
	 * 0~900 on the y-axis. In addition they will be evenly distributed across this
	 * plane. Object will also not overlap and all have approximately the same area.
	 * @return A list of convex objects representing the generated playfield.
	 */
	public List<ConvexObject> generatePlayfield(){
		//TODO you may assume that the playfield is 0~1600 on the x-axis and 0~900 on the y-axis
		//the goal is then to fill the entire playfield with convex objects that are made up of
		//3 to 4 points, the object should not overlap, fill the entire playfield, be of varying
		//sizes and not be too close to each other (they need to appear as distinct objects to the player)
		//there is no requirement on the total number of objects, when generating random numbers use the
		//provided random instance. Convex objects can vary in total area, but not by too much to prevent
		//giving the first player an unfair advantage.
		
		return Arrays.asList(
			new ConvexObject(0, 0, 50, 50, 10, 50),
			new ConvexObject(100, 100, 150, 100, 200, 150)
		);
	}
}
