package gui;

import generation.Distance;
import generation.Order.*;
import gui.Robot.Direction;
import gui.Robot.Turn;
import gui.Constants.UserInput;
/**
 * Implementation of the RobotDriver interface. Wallfollower uses a one sided sensor to follow the walls
 * of the maze until it reaches the exit. 
 * @author Katson, Andrew
 *
 */

public class WallFollower implements RobotDriver{
	
	protected Robot robot;	
	protected Distance distance;
	protected int pathLen;
	protected int width;
	protected int height;
	protected float startBatteryLev;
	
	/**
	 *Default Constructor 
	 */
	public WallFollower() {
		this.pathLen = 0;
		this.width = 0;
		this.height = 0;
		this.startBatteryLev = 0;
		this.robot = null;
		this.distance = null;
		
		
	}
	
	/**
	 * Assigns a robot platform to the driver. 
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r)  
	{
		this.robot = r;
		
		this.startBatteryLev = robot.getBatteryLevel();
	}
	
	/**
	 * Provides the robot driver with information on the dimensions of the 2D maze
	 * measured in the number of cells in each direction.
	 * @param width of the maze
	 * @param height of the maze
	 * @precondition 0 <= width, 0 <= height of the maze.
	 */
	@Override
	public void setDimensions(int width, int height) 
	{
		this.width = width;
		this.height = height;
	}
	/**
	 * Provides the robot driver with information on the distance to the exit.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a full functional distance object for the current maze.
	 */
	@Override
	public void setDistance(Distance distance) {
		this.distance = distance;
	}
	/**
	 * Drives the robot towards the exit given it exists and 
	 * given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws exception if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		boolean manual = false; 
		
		
		
		//as long as the robot has not reached the exit and it has not taken far more steps than there are cells in the maze
		while (!this.robot.isAtExit() && (this.pathLen < this.width * this.height * this.width * this.height * this.width * this.height)) { 
			
			if(this.robot.getBatteryLevel() !=  0) {
				
				int pos[] = robot.getCurrentPosition();
				
				//if there is a wall on its left and there is space to go in front of it 
				if((this.robot.distanceToObstacle(Direction.LEFT) == 0) && this.robot.distanceToObstacle(Direction.FORWARD) > 0) {
					this.robot.move(1,manual); //move forward one space
					this.robot.getController().keyDown(UserInput.Up, 1);
					Thread.sleep(10); //give the java thread handler back control
				}
				//if there is a wall to its left and/or there is no space ahead of it then 
				else {
					if(this.robot.distanceToObstacle(Direction.LEFT) > 0) { //turn left
						this.robot.rotate(Turn.LEFT);
						this.robot.getController().keyDown(UserInput.Left, 1);
						this.robot.move(1, manual);
						this.robot.getController().keyDown(UserInput.Up, 1);
						Thread.sleep(10);
					}
					else if(this.robot.distanceToObstacle(Direction.RIGHT) > 0) { //turn right
						this.robot.rotate(Turn.RIGHT);
						this.robot.getController().keyDown(UserInput.Right, 1);
						this.robot.move(1, manual);
						this.robot.getController().keyDown(UserInput.Up, 1);
						Thread.sleep(10);
					}
					else { //turn around
						this.robot.rotate(Turn.AROUND);
						this.robot.getController().keyDown(UserInput.Right, 1);
						this.robot.getController().keyDown(UserInput.Right, 1);
						this.robot.move(1, manual);
						this.robot.getController().keyDown(UserInput.Up, 1);
						Thread.sleep(10);
					}
				}
				this.pathLen++; //increase length of path with every step taken
				
			}
			else { //if the robot has stopped and has no energy
				throw new RuntimeException("No battery");
			}
			
		}
		
		
		//Check if the robot has reached the exit
		if(this.robot.canSeeExit(Direction.FORWARD)) {
			if(!this.robot.hasStopped()) {
				while(!this.robot.isAtExit()) {
					this.robot.move(1, false);
					this.robot.getController().keyDown(UserInput.Up, 1);
				}
			}
			else {
				return false;
			}
			
			return true;
		}
		else if(this.robot.canSeeExit(Direction.LEFT)) {
			if(!this.robot.hasStopped()) {
				this.robot.rotate(Turn.LEFT);
				this.robot.getController().keyDown(UserInput.Left, 1);
				while(!this.robot.isAtExit()) {
					this.robot.move(1, false);
					this.robot.getController().keyDown(UserInput.Up, 1);
				}
			}
			else {
				return false;
			}
			
			return true;
		}
		else if (this.robot.canSeeExit(Direction.RIGHT)) {
			if(!this.robot.hasStopped()) {
				this.robot.rotate(Turn.RIGHT);
				this.robot.getController().keyDown(UserInput.Right, 1);
				while(!this.robot.isAtExit()) {
					this.robot.move(1, false);
					this.robot.getController().keyDown(UserInput.Up, 1);
				}
			}
			else {
				return false;
			}
	
			return true;
		}
		else if(this.robot.canSeeExit(Direction.BACKWARD)) {
			if(!this.robot.hasStopped()) {
				this.robot.rotate(Turn.AROUND);
				this.robot.getController().keyDown(UserInput.Right, 1);
				this.robot.getController().keyDown(UserInput.Right, 1);
				while(!this.robot.isAtExit()) {
					this.robot.move(1, false);
					this.robot.getController().keyDown(UserInput.Up, 1);
				}
			}
			else {
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	@Override
	public float getEnergyConsumption() {
	
		return startBatteryLev - this.robot.getBatteryLevel();
	}
	
	/**
	 * Returns the total length of the journey in number of cells traversed. 
	 * Being at the initial position counts as 0. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	@Override
	public int getPathLength() {
		return this.pathLen;
	}

}
