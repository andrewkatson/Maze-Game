package gui;

import generation.Cells;

import java.util.Arrays;

import generation.CardinalDirection;
import generation.Distance;
import generation.MazeConfiguration;
import gui.Robot.Direction;
import gui.Robot.Turn;

/**
 * Implementation of the Robot interface. The BasicRobot uses a driver to move around the maze
 * and interacts with the controller of the maze to change its position. 
 * @author Katson, Andrew
 *
 */

public class BasicRobot implements Robot{
	
	public boolean roomSensor;
	
	protected int[] position;
	protected int odometer;
	protected CardinalDirection facing;
	protected Controller controller;
	protected boolean stopped;	
	
	
	//distance sensors TODO implement if have time
	public boolean forward;
	public boolean backward;
	public boolean left;
	public boolean right;
	
	protected float batteryLev; //TODO implement if have time
	
	BasicRobot(){
		
		
		this.facing = CardinalDirection.East;
		this.odometer = 0;
		this.stopped = false;
		
		//TODO change to true if implemented
		this.forward = false;
		this.backward = false;
		this.left = false;
		this.right = false;
		
		
		this.batteryLev = 2500.0f; //TODO implement if have time
		
		this.roomSensor = true; 
		
		this.position = new int[2];
		this.position[0] = 0;
		this.position[1] = 0;
	}
	

	/**
	 * Moves robot forward a given number of steps. A step matches a single cell.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * If the robot hits an obstacle like a wall, it depends on the mode of operation
	 * what happens. If an algorithm drives the robot, it remains at the position in front 
	 * of the obstacle and also hasStopped() == true as this is not supposed to happen.
	 * This is also helpful to recognize if the robot implementation and the actual maze
	 * do not share a consistent view on where walls are and where not.
	 * If a user manually operates the robot, this behavior is inconvenient for a user,
	 * such that in case of a manual operation the robot remains at the position in front
	 * of the obstacle but hasStopped() == false and the game can continue.
	 * @param distance is the number of cells to move in the robot's current forward direction 
	 * @param manual is true if robot is operated manually by user, false otherwise
	 * @precondition distance >= 0
	 */
	@Override
	public void move(int distance, boolean manual) {
		int xpos = this.position[0];
		int ypos = this.position[1];
		this.stopped = false;
		

		
		while(distance > 0 && !this.hasStopped()) { 

			if(this.batteryLev >= 5.0f) {
				if(controller.getMazeConfiguration().getMazecells().hasWall(xpos, ypos, this.facing) && manual == false) {
					this.stopped = true;
				}
			
				//separated them even though they are the same because it is easier to see what they mean
				xpos = stepInDirectionX(xpos, this.facing);
				ypos = stepInDirectionY(ypos, this.facing);
				this.position[0] = xpos;
				this.position[1] = ypos;
				this.controller.setCurrentPosition(xpos, ypos);
			
				this.odometer++;
				this.batteryLev-=5.0f;
			
				if(manual == false) {
					distance--; //subtract from distance for every step taken if not in manual
				}
			}
			else {
				this.stopped = true;
			}
		}
		
	}
	/**
	 * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
	 * @postcondition 0 <= x < width, 0 <= y < height of the maze. 
	 * @return array of length 2, x = array[0], y=array[1]
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() throws Exception  {
		if(this.position[0] < 0 || this.position[0] >= controller.getMazeConfiguration().getWidth()) {
			throw new RuntimeException("Outside Maze");
		}
		else if(this.position[1] < 0 ||  this.position[1] >= controller.getMazeConfiguration().getHeight()) {
			throw new RuntimeException("Outside Maze");
		}
		return this.position;
	}
	/**
	 * Provides the robot with a reference to the controller to cooperate with.
	 * The robot memorizes the controller such that this method is most likely called only once
	 * and for initialization purposes. The controller serves as the main source of information
	 * for the robot about the current position, the presence of walls, the reaching of an exit.
	 * The controller is assumed to be in the playing state.
	 * 
	 * @param controller is the communication partner for robot
	 * @precondition controller != null, controller is in playing state and has a maze
	 */
	@Override
	public void setMaze(Controller controller) {
		if (controller == null) {
			throw new RuntimeException("null controller");
		}
		this.controller = controller;

		
	}
	
	/**
	 * Tells if current position (x,y) is right at the exit but still inside the maze. 
	 * Used to recognize termination of a search.
	 * @return true if robot is at the exit, false otherwise
	 */
	@Override
	public boolean isAtExit() {
		int x = this.position[0];
		int y = this.position[1];
		
		if(this.inMaze(x, y)) {
			if(controller.getMazeConfiguration().getMazecells().isExitPosition(x, y)) {
				return true;
			}
		}
		else {
			throw new RuntimeException("Not at a valid position") ;
		}

		return false;
	}
	/**
	 * Tells if current position is inside a room. 
	 * @return true if robot is inside a room, false otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */	
	@Override
	public boolean isInsideRoom() throws UnsupportedOperationException {
		int x = this.position[0];
		int y = this.position[1];
		
		if(this.inMaze(x, y)) {
			if(controller.getMazeConfiguration().getMazecells().isInRoom(x, y)) {
				this.roomSensor = true;
				return true;
			}
		}
		else {
			throw new RuntimeException("Not at a valid position") ;
		}
		
		this.roomSensor = false;
		return false;
	}
	/**
	 * Tells if the robot has a room sensor.
	 */
	@Override
	public boolean hasRoomSensor() {
		return true;
	}
	/**
	 * Provides the current cardinal direction.
	 * 
	 * @return cardinal direction is robot's current direction in absolute terms
	 */	
	@Override
	public CardinalDirection getCurrentDirection() {
		
		return this.facing;
	}
	/**
	 * Returns the current battery level.
	 * The robot has a given battery level (energy level) 
	 * that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call 
	 * for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @return current battery level, level is > 0 if operational. 
	 */
	@Override
	public float getBatteryLevel() {
		
		return this.batteryLev;
	}
	/**
	 * Sets the current battery level.
	 * The robot has a given battery level (energy level) 
	 * that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call 
	 * for distance2Obstacle may use less energy than a move forward operation.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 * @precondition level >= 0 
	 */
	@Override
	public void setBatteryLevel(float level) {
		if (level < 0) {
			throw new RuntimeException("Battery Level was set to below 0");
		}
		this.batteryLev = level;
		
		if(this.batteryLev <= 0) {
			this.stopped = true;
		}
	}
	/** 
	 * Gets the distance traveled by the robot.
	 * The robot has an odometer that calculates the distance the robot has moved.
	 * Whenever the robot moves forward, the distance 
	 * that it moves is added to the odometer counter.
	 * The odometer reading gives the path length if its setting is 0 at the start of the game.
	 * The counter can be reset to 0 with resetOdomoter().
	 * @return the distance traveled measured in single-cell steps forward
	 */
	@Override
	public int getOdometerReading() {
		
		return this.odometer;
		
	}
	/** 
     * Resets the odomoter counter to zero.
     * The robot has an odometer that calculates the distance the robot has moved.
     * Whenever the robot moves forward, the distance 
     * that it moves is added to the odometer counter.
     * The odometer reading gives the path length if its setting is 0 at the start of the game.
     */
	@Override
	public void resetOdometer() {
		
		this.odometer = 0;
	}
	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		
		return 12.0f;
	}
	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary 
	 * to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	@Override
	public float getEnergyForStepForward() {
		
		return 5.0f;
	}
	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an obstacle, etc.
	 * @return true if the robot has stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {
		
		return this.stopped;
	}
	/**
	 * Turn robot on the spot for amount of degrees. 
	 * If robot runs out of energy, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * @param turn to turn and relative to current forward direction. 
	 */

	@Override
	public void rotate(gui.Robot.Turn turn) {
		
		if(this.batteryLev >=3) {
			switch(turn) {
				case LEFT:{
					this.facing = this.facing.rotateCounterClockwise();
					this.batteryLev-=3.0f;
					break;
				}
				case RIGHT:{
					this.facing = this.facing.rotateClockwise();
					this.batteryLev-=3.0f;
					break;
				}
				case AROUND:{
					if(this.batteryLev >= 6.0f) {
						this.facing = this.facing.oppositeDirection();
						this.batteryLev-=6.0f;
					}
					else {
						this.stopped = true;
					}
					break;
				}
				default:{
					throw new RuntimeException("Inconsistent enum type") ;
				}
			}
		}
		else {
			this.stopped = true;
		}
	}
	/**
	 * Tells if a sensor can identify the exit in given direction relative to 
	 * the robot's current forward direction from the current position.
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	*/
	@Override
	public boolean canSeeExit(gui.Robot.Direction direction) throws UnsupportedOperationException {
		int x = this.position[0];
		int y = this.position[1];
		
		CardinalDirection checkDirection = this.directionToCD(direction);
		
		while(inMaze(x,y)) { //while in the maze
			if(controller.getMazeConfiguration().getMazecells().hasWall(x,y,checkDirection)) { //if you hit a wall then you cannot see the exit presumably
				return false;
			}
			if(controller.getMazeConfiguration().getMazecells().isExitPosition(x, y)) { 
				return true;
			}
			x = stepInDirectionX(x, checkDirection);
			y = stepInDirectionY(y, checkDirection);
		}
		
		
		return false;
	}
	/**
	 * Tells the distance to an obstacle (a wall or border) 
	 * in a direction as given and relative to the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if current cell has a wall in this direction, 
	 * 1 if it is one step forward before directly facing a wall,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */
	@Override
	public int distanceToObstacle(gui.Robot.Direction direction) throws UnsupportedOperationException {
		int x = this.position[0];
		int y = this.position[1];
		
		//turn the Cardinal Direction of the robot into the desired direction
		CardinalDirection dir = directionToCD(direction);	
		
		//the amount of steps to a wall
		int steps = 0;
		
		//step in the given direction until a wall is hit or until out of the bounds of the maze
		while(inMaze(x,y)) {
			if(controller.getMazeConfiguration().getMazecells().hasWall(x, y, dir)) {
				return steps;
			}
			x = stepInDirectionX(x, dir);
			y = stepInDirectionY(y, dir);

			steps++;
		}
		
		return Integer.MAX_VALUE; //if a wall was not hit then steps is not returned and the max value is returned
	}
	
	/**
	 * Takes a Direction and converts to the appropriate Cardinal Direction
	 * @param direction the direction that will be converted to CardinalDirection
	 *  @return a CardinalDirection corresponding to the passed direction and the Cardinal Direction
	 *  the robot is facing in
	 */
	public CardinalDirection directionToCD(Direction direction) {
		CardinalDirection cd;
		
		if(direction == Direction.BACKWARD) {
			cd = this.facing.oppositeDirection();
		}
		else if(direction == Direction.FORWARD) {
			cd = this.facing;
		}
		else if(direction == Direction.LEFT) {
			cd = this.facing.rotateCounterClockwise();
		}
		else if(direction == Direction.RIGHT) {
			cd = this.facing.rotateClockwise();
		}
		else {
			throw new RuntimeException("Inconsistent enum type") ;
		}
		
		return cd;
	}
	
	/**
	 * @param x xposition 
	 * @param y yposition 
	 * @return if the current position is outside of the bounds of the maze
	 */
	public boolean inMaze(int x, int y) {
		if(x >=0 && x < controller.getMazeConfiguration().getWidth() && y >= 0 && y < controller.getMazeConfiguration().getHeight()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a new x position relative to the direction you want to step in. 
	 * @param x
	 * @param direction
	 * @return x modified to reflect the direction of movement
	 */
	private int stepInDirectionX(int x, CardinalDirection direction) {
		switch(direction) {
		case North : 
			break;
		case East : 
			x++;
			break;
		case South : 
			break;
		case West : 
			x--;
			break;
		default:
			throw new RuntimeException("Inconsistent enum type") ;
		}
		return x;
	}
	
	/**
	 * Returns a new y position relative to the direction you want to step in. 
	 * @param y
	 * @param direction
	 * @return y modified to reflect the direction of movement
	 */
	private int stepInDirectionY(int y, CardinalDirection direction) {
		switch(direction) {
		case North : 
			y--;
			break;
		case East : 			
			break;
		case South : 
			y++;
			break;
		case West : 			
			break;
		default:
			throw new RuntimeException("Inconsistent enum type") ;
		}
		return y;
	}
	
	
	/**
	 * Tells if the robot has a distance sensor for the given direction.
	 * Since this interface is generic and may be implemented with robots 
	 * that are more or less equipped. The purpose is to allow for a flexible
	 * robot driver to adapt its driving strategy according the features it
	 * finds supported by a robot.
	 */
	@Override
	public boolean hasDistanceSensor(gui.Robot.Direction direction) {
		if (direction == Direction.FORWARD) {
			return this.forward;
		}
		else if (direction == Direction.BACKWARD) {
			return this.backward;
		}
		else if (direction == Direction.LEFT) {
			return this.left;
		}
		else {
			return this.right;
		}	
		
	}
	
	/**
	 * Returns the Controller instance variable for testing
	 * @return the controller object
	 */
	@Override
	public Controller getController() {
		return this.controller;
	}


}
