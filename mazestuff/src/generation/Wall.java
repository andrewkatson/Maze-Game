package generation;

import java.util.Arrays;
import java.util.Objects;

/**
 * Basic class to describe a wall which is located at a cell (x,y) and at that cell it is
 * located in a particular direction. One can compute the location of a neighboring cell,
 * however that location is only valid for internal wall, i.e. if the neighboring cell is inside the maze.
 * 
 * It is used to hold wall coordinates for Prims Maze Generation and for the logging mechanism.
 */
public class Wall {
	// Cell location (x,y) pair.
	private int x;
	private int y;
	private int[] d; // direction (dx,dy) pair

	/**
	 * Constructor, values have same effect has setWall(x,y,cd).
	 * @param x is the x coordinate, 0 <= x < width
	 * @param y is the y coordinate, 0 <= y < height
	 * @param cd is the direction of wall in the cell
	 */
	public Wall(int x, int y, CardinalDirection cd)
	{
		this.x = x;
		this.y = y;
		d = cd.getDirection();
	}
	/**
	 * Sets the internal fields to the given values for a (x,y)
	 * position and direction
	 * @param x is the x coordinate, 0 <= x < width
	 * @param y is the y coordinate, 0 <= y < height
	 * @param cd is the direction
	 */
	public void setWall(int x, int y, CardinalDirection cd)
	{
		this.x = x;
		this.y = y;
		d = cd.getDirection();
	}
	/**
	 * Get the x coordinate for the current (x,y) position.
	 * @return the x coordinate
	 */
	public int getX() {
		return x;
	}
	/**
	 * Get the y coordinate for the current (x,y) position.
	 * @return the y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Pick a random position (x,y) and a random direction within the 
	 * given limits and assign these values to this wall.
	 * @param width such that 0 <= x < width
	 * @param height such that 0 <= y < height
	 */
	public void setRandomly(int width, int height) {
		// pick position (x,y) with x being random, y being random
		SingleRandom random = SingleRandom.getRandom() ;
		x = random.nextIntWithinInterval(0, width-1) ;
		y = random.nextIntWithinInterval(0, height - 1);
		// pick a direction, 
		d = CardinalDirection.East.randomDirection().getDirection() ;
	}
	/**
	 * Computes the x coordinate of neighboring (adjacent) cell for internal walls.
	 * If the wall is a border wall to the outside, then the resulting value is 
	 * out of range as the cell does not exist.
	 * @return the x coordinate of adjacent cell
	 */
	public int getNeighborX() {
		return x+d[0] ;
	}
	/**
	 * Computes the y coordinate of neighboring (adjacent) cell for internal walls.
	 * If the wall is a border wall to the outside, then the resulting value is 
	 * out of range as the cell does not exist.
	 * @return  the y coordinate of adjacent cell
	 */
	public int getNeighborY() {
		return y+d[1] ;
	}
	/**
	 * Provides the direction for the wall with regard to the 
	 * internal position (x,y).
	 * @return the direction of this wall with regard to its cell location
	 */
	public CardinalDirection getDirection() {
		return CardinalDirection.getDirection(d[0], d[1]) ;
	}
	
	/**
	 * Override of equals method for walls
	 * @param obj is the object being compared
	 * @return true if the walls have the same x, y and cardinal direction, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!Wall.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final Wall comp = (Wall) obj;
		
		if(!Arrays.equals(this.d, comp.d)) {
			return false;
		}
		if(this.x != comp.x) {
			return false;
		}
		if(this.y != comp.y) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Override of the hashcode method
	 * @return hash code of the wall object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(d), x, y);
	}
	/**
	 * checks if the two walls are the same
	 * @return true if the two walls are the same, false otherwise
	 * 
	 */
	public boolean sameWall(Wall check) {
		if (this.getDirection().oppositeDirection() == check.getDirection()) {
			if(this.getOppositeX() == check.x) {
				if(this.getOppositeY() == check.y) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Gets the opposite X value given a direction
	 * @return the x (col num) of the opposite cell of this wall
	 */
	public int getOppositeX() {
		switch(this.getDirection()) {
		case North : 
			return this.x ;
		case East : 
			return this.x + 1;
		case South : 
			return this.x ;
		case West : 
			return this.x - 1;
		default:
			throw new RuntimeException("Inconsistent enum type") ;
		}
	}
	
	/**
	 * Gets the opposite Y value given a direction 
	 * @return the y (row number) of the opposite cell of this wall
	 */
	public int getOppositeY() {
		switch(this.getDirection()) {
		case North : 
			return this.y - 1 ;
		case East : 
			return this.y;
		case South : 
			return this.y + 1;
		case West : 
			return this.y;
		default:
			throw new RuntimeException("Inconsistent enum type") ;
		}
	}
}
