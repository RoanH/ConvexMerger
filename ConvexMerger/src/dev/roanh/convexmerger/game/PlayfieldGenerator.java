package dev.roanh.convexmerger.game;

import java.util.ArrayList;
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
		//TODO you may assume that the playfield is 0~1600 (Constants.PLAYFIELD_WIDTH) on the x-axis
		//and 0~900 (Constants.PLAYFIELD_HEIGHT) on the y-axis the goal is then to fill the entire
		//playfield with convex objects that are made up of 3 to 4 points, the object should not overlap,
		//fill the entire playfield, be of varying sizes and not be too close to each other (they need to
		//appear as distinct objects to the player) there is no requirement on the total number of objects,
		//when generating random numbers use the provided random instance. Convex objects can vary in total
		//area, but not by too much to prevent giving the first player an unfair advantage.
		
		// x: 0 - 1600
		// y: 0 - 900
		// generate random number of objects
		// The area size has to be more or less same for every object
		
		List<ConvexObject> objects = new ArrayList<ConvexObject>();
		//objects.add(new ConvexObject(0, 0, 60, 0, 50, 50, 10, 50));
		//objects.add(new ConvexObject(100, 100, 150, 100, 200, 150));
		//objects.add(new ConvexObject(200, 300, 500, 550, 400, 450));
		//objects.add(new ConvexObject(1000, 800, 1500, 900, 1200, 850));
		//objects.add(new ConvexObject(700, 700, 900, 300, 800, 600));
		
		//int randomNumObject = random.nextInt(1)+1;
		//System.out.println(randomNumObject);


		int xMin = 0;
		int xMax = 1600;
		int yMin = 0;
		int yMax = 900;
		
		int offset = 100;

		int rangeMin = 0;
		int rangeMax = 100;
		
		int i = 100;
		do {
			// generate the center of the triangle or quadrilateral randomly
			int centerX = random.nextInt((xMax - offset) - (xMin + offset)) + (xMin + offset);
			int centerY = random.nextInt((yMax - offset) - (yMin + offset)) + (yMin + offset);
			
			// top right
			int topRightX = centerX + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			int topRightY = centerY + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			
			// top left
			int topLeftX = centerX + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			int topLeftY = centerY + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			
			// bottom left
			int bottomLeftX = centerX + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			int bottomLeftY = centerY + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			
			// bottom right
			int bottomRightX = centerX + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			int bottomRightY = centerY + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			
			objects.add(new ConvexObject(topRightX, topRightY, topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY));
			
			i--;
		} while(i>0);
		
/*		
		int min = 2;	// adjust this number
		int max = 4;	// adjust this number
		int randomNumObject = random.nextInt(max - min) + min; // generate random number of convex  objects in the play field
		do {
			int randomTriQuad = random.nextInt(2);
			if(randomTriQuad==0) {
				// for generating convex quadrilateral
				objects.add(new ConvexObject(0, 0, 60, 0, 50, 50, 10, 50));		// TODO: THIS CONVEX QUAD HAS TO BE RANDOMIZED
				// TODO: CHECK AREA
				// TODO: CHECK THE INTERSECTION
				randomNumObject--;
			} else {
				// for generating triangles 
				objects.add(new ConvexObject(100, 100, 150, 100, 200, 150));	// TODO: THIS TRINANGLE HAS TO BE RANDOMIZED
				// TODO: CHECK AREA
				// TODO: CHECK THE INTERSECTION
				randomNumObject--;
			}
		} while(randomNumObject>0);
		
		System.out.println(objects.get(0).getArea());
		System.out.println(objects.get(0).intersects(objects.get(1)));
*/		
		
		// objects.add(new ConvexObject(100, 0, 100, 100, 0, 100, 200, 200));	// TESTING: the nonconvex quadrilateral will turn to triangle
		
		return objects;
	}
}
