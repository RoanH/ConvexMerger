package dev.roanh.convexmerger.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import dev.roanh.convexmerger.Constants;

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
	 * The area of the base triangle is divided this to set the
	 * minimum required area for an object
	 */
	private static final int AREA_DIV_NUM = 6;
	/**
	 * Ratio between the length of both diagonals in an object (recommended between 1.0 and 1.8).
	 */
	private static final double DIAGONAL_LENGTH_RATIO = 1.5D;
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
	 * Function to check if 3 points are collinear.
	 * @param x1 The x coordinate of the first point.
	 * @param y1 The y coordinate of the first point.
	 * @param x2 The x coordinate of the second point.
	 * @param y2 The y coordinate of the second point.
	 * @param x3 The x coordinate of the third point.
	 * @param y3 The y coordinate of the third point.
	 * @return True if the given points are (close to) collinear.
	 */
	public boolean collinear(int x1, int y1, int x2, int y2, int x3, int y3){
		return Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) < 0.000006D;//account for FP rounding errors
	}

	/**
	 * Function to determine the approximate minimum area for an object
	 * @param rMax The maximum range for an object.
	 * @return The computed minimum area for an object.
	 */
	public double areaObject(int rMax){
		return rMax * rMax * (Math.sqrt(0.5D) + 0.5D) / AREA_DIV_NUM;
	}

	/**
	 * Generates a new game playfield with convex objects made up of 3 to 4 points.
	 * The objects will have coordinates within 0~{@value Constants#PLAYFIELD_WIDTH}
	 * on the x-axis and within 0~{@value Constants#PLAYFIELD_HEIGHT} on the y-axis.
	 * In addition they will be evenly distributed across this plane. Object will also
	 * not overlap and all have approximately the same area.
	 * @param rangeMin The minimum size range for convex objects.
	 * @param rangeMax The maximum size range for convex objects. Small values for
	 *        this parameter may resulting in generating the playfield taking longer,
	 *        values larger than 20 are recommended.
	 * @param coverage The minimum total area of the whole playfield that should be
	 *        covered by convex objects. High values for this parameter may result in
	 *        extremely long or even infinite generation times. It is recommended to
	 *        use a value of 0.5 or less.
	 * @return A list of convex objects representing the generated playfield.
	 */
	public List<ConvexObject> generatePlayfield(int rangeMin, int rangeMax, double coverage){
		List<ConvexObject> objects = new ArrayList<ConvexObject>();
		double totalArea = 0.0;//total area of all generated objects
		double areaObject = areaObject(rangeMax);//minimum object area

		main: do{
			//generate the center (x,y) of the triangle or quadrilateral randomly at least rangeMax from the sides
			int centerX = random.nextInt(Constants.PLAYFIELD_WIDTH - 2 * rangeMax) + rangeMax;
			int centerY = random.nextInt(Constants.PLAYFIELD_HEIGHT - 2 * rangeMax) + rangeMax;

			//top right of the triangle or quadrilateral	
			int topRightX = centerX + random.nextInt(rangeMax - rangeMin) + rangeMin;
			int topRightY = centerY + random.nextInt(rangeMax - rangeMin) + rangeMin;

			//top left of the triangle or quadrilateral
			int topLeftX = centerX - random.nextInt(rangeMax - rangeMin) - rangeMin;
			int topLeftY = centerY + random.nextInt(rangeMax - rangeMin) + rangeMin;

			//bottom left of the triangle or quadrilateral
			int bottomLeftX = centerX - random.nextInt(rangeMax - rangeMin) - rangeMin;
			int bottomLeftY = centerY - random.nextInt(rangeMax - rangeMin) - rangeMin;

			//bottom right of the triangle or quadrilateral
			int bottomRightX = centerX + random.nextInt(rangeMax - rangeMin) + rangeMin;
			int bottomRightY = centerY - random.nextInt(rangeMax - rangeMin) - rangeMin;

			double diagonalLength1 = Math.hypot(topRightX - bottomLeftX, topRightY - bottomLeftY);
			double diagonalLength2 = Math.hypot(bottomRightX - topLeftX, bottomRightY - topLeftY);
			if(Math.min(diagonalLength1 / diagonalLength2, diagonalLength2 / diagonalLength1) > DIAGONAL_LENGTH_RATIO){
				//if the ratio of the diagonals is high, thin long object has potential to be generated
				continue;
			}

			//ensure no 3 points are collinear
			if(collinear(topRightX, topRightY, topLeftX, topLeftY, bottomLeftX, bottomLeftY)){
				continue;
			}else if(collinear(topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY)){
				continue;
			}else if(collinear(topRightX, topRightY, topLeftX, topLeftY, bottomRightX, bottomRightY)){
				continue;
			}else if(collinear(topRightX, topRightY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY)){
				continue;
			}

			//construct the new convex object
			ConvexObject obj = new ConvexObject(topRightX, topRightY, topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY);

			//skip the newly generated object if its area less than the required minimum area setting
			double area = obj.getArea();
			if(area < areaObject){
				continue;
			}

			//skip triangles or convex quadrilaterals that intersects with other triangles or quadrilaterals
			for(ConvexObject object : objects){
				if(obj.intersects(object)){
					continue main;
				}
			}
			
			//add the object to the final result
			totalArea += area;
			objects.add(obj);

		}while(totalArea < (Constants.PLAYFIELD_WIDTH * Constants.PLAYFIELD_HEIGHT) * coverage);

		return objects;
	}
}
