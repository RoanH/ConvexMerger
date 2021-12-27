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
	 * Function to check if 3 points are collinear
	 */
	public boolean collinear(int x1, int y1, int x2,
							int y2, int x3, int y3){
		double area = Math.abs(0.5*(x1 * (y2 - y3) +
				x2 * (y3 - y1) +
				x3 * (y1 - y2)));
		if(area < 0.000003) {
			return true;
		}
		return false;
	}
	
	/**
	 * Function to determine the approximate minimum area for an object
	 */
	public double areaObject(int rMax) {
		double area;
		int divNum = 6;
		double x1 = (Math.sqrt(2)/2)*(double) rMax;
		double y1 = -(Math.sqrt(2)/2)*(double) rMax;
		double x2 = -(Math.sqrt(2)/2)*(double) rMax;
		double y2 = -(Math.sqrt(2)/2)*(double) rMax;
		double x3 = 0;
		double y3 = (double) rMax;
		// shoelace formula (for area of triangle)
		// the computed area is divided by divNum to set the minimum requirement area for an object
		area = Math.abs((0.5*(x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2)))/divNum);
		System.out.println(area);
		return area;
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

		// variables rangeMin and rangeMax Can be adjusted by user
		// WARNING: the smaller rangeMax, longer time it takes to generate the playfield
		// it depends on the total area coverage (totalAreaCoverage) setting as well
		int rangeMin = 0;	// minimum value in the range of a vertex in a object 
		int rangeMax = 25;	// maximum value in the range of a vertex in a object (recommended more than or equal to 20)
		
		// variable totalAreaCoverage can be adjusted by user 
		// WARNING: the larger the total area coverage, longer time it takes to generate the playfield
		// it depends on the range max (rangeMax) setting as well
		double totalAreaCoverage = 0.45;	// percentage minimum area coverage of all generated objects (recommended less than or equal to 0.5)

		// NOT recommend to be adjusted by the user
		double diagonalLengthRatio = 1.5;	// ratio between the length of both diagonals in an object (recommended between 1.0 and 1.8)
		
		int offset = rangeMax;	// offset to make sure the generated objects are not beyond the screen
		
//		int maxLoop = 1000;	// maximum loop for terminating the do-while loop
//		int numPolygons = 10;	// maximum number of objects to be generated 
		
		double totalArea = 0.0;	// minimum total area of all generated objects
		
		double areaObject = areaObject(rangeMax);	// minimum generated object area by calling function areaObjet()
		
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
			
			// calculate the diagonal length between points
			// for avoiding generation of tin long triangle or quadrilateral
			double diagonalLength_1 = Math.hypot(topRightX-bottomLeftX, topRightY-bottomLeftY);
			double diagonalLength_2 = Math.hypot(bottomRightX-topLeftX, bottomRightY-topLeftY);
			if(Math.min(diagonalLength_1/diagonalLength_2, diagonalLength_2/diagonalLength_1) > diagonalLengthRatio) {
				// if the ratio of the diagonals is high, thin long object has potential to be generated
				// continue to the next iteration of the do-while loop for generating non-thin long objects
				continue;
			}
			
			// check if 3 points are collinear
			// continue to the next iteration of the do-while loop if the 3 points are collinear 
			if(collinear(topRightX, topRightY, topLeftX, topLeftY, bottomLeftX, bottomLeftY)) {
				continue;
			} else if(collinear(topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY)){
				continue;
			} else if(collinear(topRightX, topRightY, topLeftX, topLeftY, bottomRightX, bottomRightY)) {
				continue;
			} else if(collinear(topRightX, topRightY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY)) {
				continue;
			}
			
			// add the generated triangle or quadrilateral into the the objects arraylist
			objects.add(new ConvexObject(topRightX, topRightY, topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY));
			
			double area = objects.get(objects.size()-1).getArea();	// get the area of the generated object
			
			// remove the newly generated object if its area less than the required minimum area setting
			// and then continue to the next iteration of the do-while loop for generating non-thin long objects
			if(area < areaObject) {
				objects.remove(objects.size()-1);
				continue;
			}
			
			totalArea += area;	// add the area of the generated object to the variable totalArea
			
			// eliminate the triangle or convex quadrilateral that intersects with other triangle or quadrilateral
			for(int k = 0; k < objects.size()-1; k++) {
				if(objects.get(objects.size()-1).intersects(objects.get(k))) {
					objects.remove(objects.size()-1);	// remove the newly generated triangle or quadrilateral if it intersected with other triangle or quadrilateral
					totalArea -= area;	// deduct the area of the generated object to the variable totalArea 
					break;	// the for loop can be break because this newly generated object is intersected with other object
				}
			}
			
			// numPolygons--;	// while(numPolygons > 0);	// use this setting if the maximum output of the number of objects is chosen
		} while(totalArea < (xMax*yMax)*totalAreaCoverage);
				
		return objects;
	}
}
