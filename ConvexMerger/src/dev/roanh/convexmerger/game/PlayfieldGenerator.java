package dev.roanh.convexmerger.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
	private Random random;
	/**
	 * The random seed for this playfield generator.
	 */
	private long seed;
	/**
	 * Minimum object size.
	 */
	private int rangeMin;
	/**
	 * Maximum object size.
	 */
	private int rangeMax;
	/**
	 * Coverage value in range 0-255.
	 * @see #coverage
	 */
	private int coverageNum;
	/**
	 * Scaling value in range 0-255.
	 * @see #scale
	 */
	private int scaleNum;
	/**
	 * Desired playfield coverage.
	 */
	private float coverage;
	/**
	 * Object scaling factor.
	 */
	private float scale;
	/**
	 * Listener to notify of generation progress.
	 */
	private GeneratorProgressListener listener;
	
	/**
	 * Constructs a new playfield generator with a random seed,
	 * range of 0-100, coverage of 0.4471 and scale of 1.
	 */
	public PlayfieldGenerator(){
		init(ThreadLocalRandom.current().nextLong(), 0, 100, 114, 255);
	}
	
	/**
	 * Sets the listener to notify of playfield generation progress.
	 * @param listener The listener to register.
	 * @see GeneratorProgressListener
	 */
	public void setProgressListener(GeneratorProgressListener listener){
		this.listener = listener;
	}

	/**
	 * Constructs a new playfield generate with the given seed.
	 * @param seed The seed to use.
	 * @throws IllegalArgumentException When the given seed is invalid
	 */
	public PlayfieldGenerator(String seed) throws IllegalArgumentException{
		setSeed(seed);
	}
	
	/**
	 * Initialises the internal generator state.
	 * @param seed Random number generator seed.
	 * @param rangeMin Minimum object size.
	 * @param rangeMax Maximum object size.
	 * @param coverageNum Playfield coverage.
	 * @param scaleNum Object scaling.
	 */
	private void init(long seed, int rangeMin, int rangeMax, int coverageNum, int scaleNum){
		random = new Random(seed);
		this.seed = seed;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.coverageNum = coverageNum;
		this.scaleNum = scaleNum;
		this.coverage = coverageNum / 255.0F;
		this.scale = scaleNum / 255.0F;
	}
	
	public void setSeed(String seed) throws IllegalArgumentException{
		long lower = Long.parseUnsignedLong(seed.substring(seed.length() - 13), 36);
		long upper = Long.parseUnsignedLong(seed.substring(0, seed.length() - 13), 36);
		
		if(!hasSeedMarker(upper)){
			throw new IllegalArgumentException("Invalid seed");
		}
		
		init(lower, (int)((upper & 0xFF000000L) >> 24), (int)((upper & 0xFF0000) >> 16), (int)((upper & 0xFF00) >> 8), (int)(upper & 0xFF));
	}
	
	public int getRangeMin(){
		return rangeMin;
	}
	
	public int getRangeMax(){
		return rangeMax;
	}
	
	public int getCoverage(){
		return coverageNum;
	}
	
	public int getScaling(){
		return scaleNum;
	}
	
	/**
	 * Gets the seed that describes this random generator.
	 * @return The seed for this random generator.
	 */
	public String getSeed(){
		//96 bits: [2 version][8 range min][8 range max][8 coverage][8 scale][64 seed]
		long upper = 0x200000000L | ((rangeMin & 0xFFL) << 24) | ((rangeMax & 0xFF) << 16) | ((coverageNum & 0xFF) << 8) | (scaleNum & 0xFF);
		return String.format("%s%13s", Long.toUnsignedString(upper, 36), Long.toUnsignedString(seed, 36)).replace(' ', '0').toUpperCase(Locale.ROOT);
	}
	
	/**
	 * Sets the size range of generated objects.
	 * @param min The minimum size range for convex objects (at least 0).
	 * @param max The maximum size range for convex objects. Small values for
	 *        this parameter may resulting in generating the playfield taking longer,
	 *        values larger than 20 are recommended (at most 255).
	 * @throws IllegalArgumentException When the given range is invalid.
	 */
	public void setRange(int min, int max) throws IllegalArgumentException{
		if(0 > min || 255 < max || min > max){
			throw new IllegalArgumentException("Invalid range (0 <= min < max <= 255)");
		}
		rangeMin = min;
		rangeMax = max;
	}
	
	/**
	 * Sets the minimum total area of the whole playfield that should be
	 * covered by convex objects before scaling. High values for this
	 * parameter may result in extremely long or even infinite generation
	 * times. It is recommended to use a value such that
	 * <code>coverage / 255</code> is 0.5 or less (so at most 127).
	 * @param coverage The new coverage value.
	 * @throws IllegalArgumentException When the given value is out of range (0 - 255).
	 */
	public void setCoverage(int coverage) throws IllegalArgumentException{
		if(0 > coverage || coverage > 255){
			throw new IllegalArgumentException("Invalid coverage range (0 <= coverage <= 255)");
		}
		coverageNum = coverage;
		this.coverage = coverage / 255.0F;
	}
	
	/**
	 * Sets the factor by with which to scale all objects after generation.
	 * @param scaling The scaling factor, computed as <code>scaling / 255</code>.
	 * @throws IllegalArgumentException When the given value is out of range (0 - 255).
	 */
	public void setScaling(int scaling) throws IllegalArgumentException{
		if(0 > scaling || scaling > 255){
			throw new IllegalArgumentException("Invalid scaling range (0 <= scaling <= 255)");
		}
		scaleNum = scaling;
		scale = scaling / 255.0F;
	}

	/**
	 * Function to determine the approximate minimum area for an object
	 * @param rMax The maximum range for an object.
	 * @return The computed minimum area for an object.
	 */
	private double areaObject(int rMax){
		return rMax * rMax * (Math.sqrt(0.5D) + 0.5D) / AREA_DIV_NUM;
	}

	/**
	 * Generates a new game playfield with convex objects made up of 3 to 4 points.
	 * The objects will have coordinates within 0~{@value Constants#PLAYFIELD_WIDTH}
	 * on the x-axis and within 0~{@value Constants#PLAYFIELD_HEIGHT} on the y-axis.
	 * In addition they will be evenly distributed across this plane. Objects will also
	 * not overlap and all have approximately the same area and conform to the set parameters.
	 * @return A list of convex objects representing the generated playfield.
	 */
	public List<ConvexObject> generatePlayfield(){
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
			if(ConvexUtil.checkCollinear(topRightX, topRightY, topLeftX, topLeftY, bottomLeftX, bottomLeftY)){
				continue;
			}else if(ConvexUtil.checkCollinear(topLeftX, topLeftY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY)){
				continue;
			}else if(ConvexUtil.checkCollinear(topRightX, topRightY, topLeftX, topLeftY, bottomRightX, bottomRightY)){
				continue;
			}else if(ConvexUtil.checkCollinear(topRightX, topRightY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY)){
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
			
			if(listener != null){
				listener.update(Math.min(1.0D, totalArea / ((Constants.PLAYFIELD_WIDTH * Constants.PLAYFIELD_HEIGHT) * coverage)));
			}
		}while(totalArea < (Constants.PLAYFIELD_WIDTH * Constants.PLAYFIELD_HEIGHT) * coverage);
		
		objects.forEach(obj->obj.scale(scale));

		return objects;
	}
	
	public static boolean isValidSeed(String seed){
		try{
			return seed.length() >= 13 && hasSeedMarker(Long.parseUnsignedLong(seed.substring(0, seed.length() - 13), 36));
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	private static boolean hasSeedMarker(long high){
		return (0xFFFFFFFF00000000L & high) == 0x200000000L;
	}
	
	/**
	 * Listener interface for classes that want to
	 * informed of playfield generation progress.
	 * @author Roan
	 */
	public static abstract interface GeneratorProgressListener{
		
		/**
		 * Updates the current progress.
		 * @param progress A progress value between 0 and 1 where
		 *        0 means no progress and 1 means done.
		 */
		public abstract void update(double progress);
	}
}
