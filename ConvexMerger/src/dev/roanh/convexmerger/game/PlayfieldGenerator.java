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

		int xMin = 0;		// the minimum scale value in the x-axis
		int xMax = 1600;	// the maximum scale value in the x-axis
		int yMin = 0;		// the minimum scale value in the y-axis
		int yMax = 900;		// the maximum scale value in the y-axis
		
		int offset = 100;	// offset to make sure the generated objects are not beyond the screen

		int rangeMin = 0;	// minimum value in the range of a vertex in a object 
		int rangeMax = 100;	// maximum value in the range of a vertex in a object
		
//		int maxLoop = 1000;	// maximum loop for terminating the do-while loop
		int numPolygons = 1000;	// maximum number of objects to be generated 
		
		do {
			// generate the center (x,y) of the triangle or quadrilateral randomly
			int centerX = random.nextInt((xMax - offset) - (xMin + offset)) + (xMin + offset);
			int centerY = random.nextInt((yMax - offset) - (yMin + offset)) + (yMin + offset);
			
			// top right of the triangle or quadrilateral	
			int topRightX = centerX + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			int topRightY = centerY + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			
			// top left of the triangle or quadrilateral
			int topLeftX = centerX + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			int topLeftY = centerY + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			
			// bottom left of the triangle or quadrilateral
			int bottomLeftX = centerX + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			int bottomLeftY = centerY + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			
			// bottom right of the triangle or quadrilateral
			int bottomRightX = centerX + (random.nextInt(rangeMax - rangeMin) + rangeMin);
			int bottomRightY = centerY + (-(random.nextInt(rangeMax - rangeMin) + rangeMin));
			
			// add the generated triangle or quadrilateral into the the objects arraylist
			objects.add(new ConvexObject(topRightX, topRightY, topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY));
			
			// eliminate the triangle or convex quadrilateral that intersects with other triangle or quadrilateral
			for(int k = 0; k < objects.size()-1; k++) {
				if(objects.get(objects.size()-1).intersects(objects.get(k))) {
					objects.remove(objects.size()-1);	// remove the newly generated triangle or quadrilateral if it intersected with other triangle or quadrilateral 
					break;
				}
			}
			
			numPolygons--;
		} while(numPolygons > 0);
		
		return objects;
	}
}
