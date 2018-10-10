package gui;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import gui.BasicRobot;
import gui.Robot.Direction;
import gui.Robot.Turn;
import generation.MazeFactory;
import generation.Order;
import generation.CardinalDirection;
import generation.MazeConfiguration;
import generation.OrderStub;
import generation.Cells;
import generation.Wall;

/**
 * Testing the methods of the BasicRobot class
 * @author Katson, Andrew
 *
 */

public class BasicRobotTest {

	private BasicRobot robot;
	private RobotDriver driver;
	private MazeFactory factory;
	private MazeConfiguration config;
	private OrderStub stub;
	private Controller controller;
	

	
	/**
	 * Setup performed before each test
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception{
		
		robot = new BasicRobot();
		driver = new WallFollower();
		factory = new MazeFactory(true);
		stub = new OrderStub(1, true, Order.Builder.Kruskal);
		controller = new Controller();		
		factory.order(stub);
		factory.waitTillDelivered();		
		controller.setMazeConfiguration(stub.getMazeConfig());
		controller.setRobotAndDriver(robot, driver);
		robot.setMaze(controller);
		config = stub.getMazeConfig();
	}
	
	/**
	 * Cleanup performed after each test
	 *  @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception{

	}
	
	/**
	 * Test all setup objects are non-Null and initialized to the correct values
	 * @precondition getCurrentDirection is tested
	 * @precondition getOdomoeterReading is tested
	 * @precondition hasStopped is tested
	 * @precondition getBattery Level is tested
	 * @precondition hasDistanceSensor is tested
	 * @precondition getMazeConfiguration is tested
	 * @precondition getController is tested
	 * @precondition getCells is tested
	 */
	@Test
	public void testSetUp() {
		//test the instance variables have the correct initializes value
		assertEquals(CardinalDirection.East, robot.getCurrentDirection());
		assertEquals(0, robot.getOdometerReading());
		assertEquals(false, robot.hasStopped());
		assertEquals((int)2500.0f, (int)robot.getBatteryLevel());
		
		assertFalse(robot.hasDistanceSensor(Direction.BACKWARD));
		assertFalse(robot.hasDistanceSensor(Direction.FORWARD));
		assertFalse(robot.hasDistanceSensor(Direction.LEFT));
		assertFalse(robot.hasDistanceSensor(Direction.RIGHT));
		
		assertNotNull(robot.getController());
		
	}
	
	/**
	 * Test the move method
	 * No exception should be thrown when moving forward one space
	 */
	@Test
	public void testMove() {
		boolean checkWork = false;
		try {
			boolean manual = false;
			robot.move(1, manual);
			checkWork = true;
		} catch(Exception e) {
			assert false;
			checkWork = false;
		}finally {
			assertTrue(checkWork);
		}
		
		assertTrue(robot.batteryLev == 2495.0f);
		
	}
	
	/**
	 * Try move into a wall
	 * The robot should stop if it tries to move into a wall
	 */
	@Test
	public void testMoveBreak() {
		
		boolean manual = false;
		
		robot.facing = CardinalDirection.North;
		
		robot.move(1, manual);
		
		assertTrue(robot.stopped == true);
		
	}
	
	/**
	 * Test the robot can get its position correctly
	 * The robot position should be non null
	 * @throws Exception
	 */
	@Test
	public void testGetCurrentPosition() throws Exception{
		assertNotNull(robot.getCurrentPosition());
		
	}
	
	/**
	 * Test that the robot throws an error when it is outside the bounds of the maze
	 * Exceptions should be thrown when trying to get the position when one coordinate
	 * is outside of the bounds of the maze
	 */
	@Test
	public void testGetCurrentPositionBreak() {
		
		boolean checkError = false;
		
		robot.position[0] = Integer.MAX_VALUE; // try to break when x is too big
		
		
		try {
			robot.getCurrentPosition();
		} catch (Exception E) {
			checkError = true;
		}
		finally {
			assertTrue(checkError);
		}
		
		robot.position[0] = 0;
		robot.position[1] = Integer.MAX_VALUE; // try to break when y is too big
		
		checkError = false;
		
		try {
			robot.getCurrentPosition();
		} catch (Exception E) {
			checkError = true;
		}
		finally {
			assertTrue(checkError);
		}
		
	}
	
	/**
	 * Test that setMaze set the controller properly 
	 * The maze controller should be non-null and no exceptions should be thrown
	 */
	@Test
	public void testSetMaze() {
		
		assertNotNull(robot.getController());	
		
		
		
		boolean checkError = false;
		try {
			robot.setMaze(null);
		} catch (Exception e) {
			checkError = true;
		} finally {
			assertTrue(checkError);
		}

	}
	
	/**
	 * Test the robot can tell if it is at the exit and whether it 
	 * throws an exception when outside the maze and checking if it 
	 * is at an exit
	 */
	@Test
	public void testIsAtExit() {
		int exitPos[] = {Integer.MAX_VALUE,Integer.MAX_VALUE}; //initially set the exit to be a large number
		int failPos[] = {Integer.MAX_VALUE,Integer.MAX_VALUE};  
		
		int xPosMax = config.getWidth();
		int yPosMax = config.getHeight();
		
		Cells cells = config.getMazecells();
		
		for (int x = 0; x < xPosMax; x++) {
			for(int y = 0; y < yPosMax; y++) {
				if(cells.isExitPosition(x, y)) { //run through mazeBoard until you find the exit
					exitPos[0] = x;
					exitPos[1] = y;
				}
			}
		}
		
		robot.position[0] = exitPos[0];
		robot.position[1] = exitPos[1];
		
		//test if the robot is at the exit when we know it is at the exit
		assertTrue(robot.isAtExit());
		
		robot.position[0] = failPos[0];
		robot.position[1] = failPos[1];
		
		
		//test if an error is thrown when the robot is out of the maze
		boolean checkError = false;
		try {
			robot.isAtExit();
		} catch (Exception e) {
			checkError = true;
		} finally {
			assertTrue(checkError);
		}
	}
	
	
	/**
	 * Test if the robot can tell if it is in a room
	 */
	@Test
	public void testIsInRoom() {
		
		assertFalse(robot.isInsideRoom());
		
		int roomPos[] = {Integer.MAX_VALUE,Integer.MAX_VALUE}; //initially set the room to be a large number
		
		int xPosMax = config.getWidth();
		int yPosMax = config.getHeight();
		
		Cells cells = config.getMazecells();
		
		
		for (int x = 0; x < xPosMax; x++) {
			for(int y = 0; y < yPosMax; y++) {
				if(cells.isInRoom(x, y)) { //run through mazeBoard until you are in a room 
					roomPos[0] = x;
					roomPos[1] = y;
				}
			}
		}
		
		robot.position[0] = roomPos[0];
		robot.position[1] = roomPos[1];
		
		//If we are not in the original 
		if(roomPos[0] != Integer.MAX_VALUE && roomPos[1] != Integer.MAX_VALUE) {
			assertTrue(robot.isInsideRoom());
		}
		else {
			
			boolean checkError = false;
			try {
				robot.isInsideRoom();
			} catch (Exception e) {
				checkError = true;
			} finally {
				assertTrue(checkError);
			}
		}
		
		
		//tests if the robot can tell when it is placed in a room
		robot.position[0] = 4;
		robot.position[1] = 4;
		
		cells.markAreaAsRoom(2, 2, 3, 3, 4, 4);
		
		assertTrue(robot.isInsideRoom());		
	}
	
	
	/**
	 * Test if the robot has a room sensor
	 */
	@Test
	public void testHasRoomSensor() {
		
		assertTrue(robot.hasRoomSensor());
		
	}
	
	
	/**
	 * Test if the robot is accurately storing its current direction
	 * each turn should accurately update the direction the robot
	 * is changing 
	 * @precondition rotate is tested
	 */
	@Test
	public void testGetCurrentDiretion() {
	
		assertEquals(CardinalDirection.East, robot.getCurrentDirection());
		
		robot.rotate(Turn.RIGHT);
		
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		
		robot.rotate(Turn.RIGHT);
		
		assertEquals(CardinalDirection.West, robot.getCurrentDirection());
		
		robot.rotate(Turn.RIGHT);
		
		assertEquals(CardinalDirection.North, robot.getCurrentDirection());
	}
	
	/**
	 * Test if the robot is accurately storing its battery level
	 * 
	 */
	@Test
	public void testGetBatteryLevel() {
		
		assertEquals((int)2500.0f, (int)robot.getBatteryLevel());
		
		robot.batteryLev = 1000.0f;
		
		assertEquals((int)1000.f, (int)robot.getBatteryLevel());
		
	}
	
	/**
	 * Test if the battery level can be set
	 * @precondition getBatteryLevel is tested
	 */
	@Test
	public void testSetBatteryLevel() {
		
		robot.setBatteryLevel(100.0f);
		
		assertEquals((int) 100.0f, (int) robot.getBatteryLevel());
		
		robot.setBatteryLevel((Float.MAX_VALUE));
		
		assertEquals((int) Float.MAX_VALUE, (int) robot.getBatteryLevel());
		
		robot.setBatteryLevel(0.0f);
		
		assertEquals(0, (int)robot.getBatteryLevel());
		
		boolean checkError = false;
		
		try {
			robot.setBatteryLevel(-1000.0f);
		}catch (Exception e) {
			checkError = true;
		} finally {
			assertTrue(checkError);
		}
	}
	
	/**
	 * Test if the odometer is stored correctly
	 */
	@Test
	public void testGetOdometerReading() {
		
		assertEquals(0, robot.getOdometerReading());
		
		robot.odometer = 100;
		
		assertEquals(100, robot.getOdometerReading());
		
		robot.odometer = Integer.MIN_VALUE;
		
		assertEquals(Integer.MIN_VALUE, robot.getOdometerReading());
	}
	
	/**
	 * Test if the odometer is reset to 0 through a reset function
	 * @precondition testGetOdometerReading is run
	 */
	@Test
	public void testResetOdometer() {
		
		assertEquals(0, robot.getOdometerReading());
		
		robot.odometer = 1000;
		
		robot.resetOdometer();
		
		assertEquals(0, robot.getOdometerReading());
	}
	/**
	 * Test if the right battery level is deducted for a full rotation
	 * @preconditon testGetBatteryLevel is run
	 */
	@Test
	public void testGetEnergyForFullRotation() {
		
		int energyFullRotation = (int) robot.getEnergyForFullRotation();
		
		assertEquals(12, energyFullRotation);
	}
	
	/**
	 * Test if the right battery level is deducted for a step
	 * @preconditon testGetBatteryLevel is run
	 */
	@Test
	public void testGetEnergyForStepForward() {
		
		int energyStepForward = (int) robot.getEnergyForStepForward();
		
		assertEquals(5, energyStepForward);
	}
	
	/**
	 * Test if the robot can tell when it is stopped
	 */
	@Test
	public void testHasStopped() {
		
		assertFalse(robot.hasStopped());
		
	}
	
	/**
	 * Test if the robot rotates and changes its direction
	 * 
	 */
	@Test
	public void testRotate() {
		
		robot.rotate(Turn.LEFT);
		
		assertEquals(CardinalDirection.North, robot.facing);
		
		robot.rotate(Turn.AROUND);
		
		assertEquals(CardinalDirection.South, robot.facing);
		
		robot.rotate(Turn.RIGHT);
		
		assertEquals(CardinalDirection.West, robot.facing);
		
		boolean checkError = false;
		
		try {
			robot.rotate(null);
		} catch (Exception e) {
			checkError = true;
		} finally {
			assertTrue(checkError);
		}
		
		assertTrue(robot.batteryLev != 2500.0f);
	}
	
	/**
	 * Test if the robot can see the exit
	 * 
	 */
	@Test
	public void testCanSeeExit() {
		
		for(Direction direction : Direction.values()) {
			assertFalse(robot.canSeeExit(direction));
		}
		int exitPos[] = {Integer.MAX_VALUE,Integer.MAX_VALUE}; //initially set the exit to be a large number 
		
		int xPosMax = config.getWidth();
		int yPosMax = config.getHeight();
		
		Cells cells = config.getMazecells();
		
		for (int x = 0; x < xPosMax; x++) {
			for(int y = 0; y < yPosMax; y++) {
				if(cells.isExitPosition(x, y)) { //run through mazeBoard until you find the exit
					exitPos[0] = x;
					exitPos[1] = y;
				}
			}
		}
		
		
		robot.position[0] = exitPos[0];
		robot.position[1] = exitPos[1];
		
		boolean checkFacingExit = false;
		
		for(Direction direction : Direction.values()) { // if at the exit position the robot should face the exit in some direction
			if(robot.canSeeExit(direction)) {
				checkFacingExit = true;
			}
		}
		
		assertTrue(checkFacingExit);
		
		//set the robot outside the maze
		robot.position[0] = Integer.MAX_VALUE;
		robot.position[1] = Integer.MAX_VALUE;
		
		for(Direction direction : Direction.values()) {
			assertFalse(robot.canSeeExit(direction));
		}
		
		//move back a step and check to see if the exit is visible
		robot.position[0] = 2;
		robot.position[1] = 10;
		
		Wall wall = new Wall(1, 10, CardinalDirection.West);
		
		cells.deleteWall(wall);
		
		wall = new Wall(2, 10, CardinalDirection.West);
		
		cells.deleteWall(wall);
		
		checkFacingExit = false;
		
		for(Direction direction : Direction.values()) { // if at the exit position the robot should face the exit in some direction
			if(robot.canSeeExit(direction)) {
				checkFacingExit = true;
			}
		}
		
		assertTrue(checkFacingExit);
	}
	
	
	
	/**
	 * Test if the robot can calculate the distance to an obstacle
	 */
	@Test
	public void testDistanceToObstacle() {
		
		for(Direction direction : Direction.values()){
			assertNotNull(robot.distanceToObstacle(direction));
		}
		
		//test distance to obstacle when set outside the maze
		robot.position[0] = Integer.MAX_VALUE;
		robot.position[1] = Integer.MAX_VALUE;
		
		assertEquals(Integer.MAX_VALUE, robot.distanceToObstacle(Direction.FORWARD));
	}
	
	/**
	 * Test if the direction to CardinalDirection method returns the right values
	 */
	@Test
	public void testDirectionToCD() {
		
		assertEquals(CardinalDirection.East, robot.getCurrentDirection());
		
		assertEquals(CardinalDirection.East, robot.directionToCD(Direction.FORWARD));
		
		assertEquals(CardinalDirection.South, robot.directionToCD(Direction.RIGHT));
		
		assertEquals(CardinalDirection.West, robot.directionToCD(Direction.BACKWARD));
		
		assertEquals(CardinalDirection.North, robot.directionToCD(Direction.LEFT));
	}
	
	/**
	 * Test if the robot knows when it is in the boundaries of the maze
	 */
	@Test
	public void testInMaze() {
		
		int xPos = 0;
		int yPos = 0;
		
		assertTrue(robot.inMaze(xPos, yPos));
		
		robot.position[0] = 0;
		robot.position[0] = 0;
		
		xPos = Integer.MAX_VALUE;
		yPos = Integer.MAX_VALUE;
		
		assertFalse(robot.inMaze(xPos, yPos));
		
	}
	
	/**
	 * Test if the robot has distance sensors
	 */
	@Test
	public void testHasDistanceSensor() {
		
		for(Direction direction : Direction.values()) {
			assertFalse(robot.hasDistanceSensor(direction));
		}
	}
	
}	

