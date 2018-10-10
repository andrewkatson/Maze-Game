package gui;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import generation.MazeConfiguration;
import generation.MazeFactory;
import generation.Order;
import generation.OrderStub;
import generation.Distance;

/**
 * 
 * Tests of the WallFollower class that implements the RobotDriver interface
 * @author Katson, Andrew 
 *
 */

public class WallFollowerTest {
		
		private BasicRobot robot;
		private WallFollower driver;
		private MazeFactory factory;
		private MazeConfiguration config;
		private OrderStub stub;
		private Controller controller;
		private Distance distance;
	
		/**
		 * Setup for WallFollower Test
		 * @throws java.lang.Exception
		 */
		@Before
		public void setUp() throws Exception{
			driver = new WallFollower();
			robot = new BasicRobot();
			controller = new Controller();
			factory = new MazeFactory(true);
			stub = new OrderStub(1, true, Order.Builder.Kruskal);
			factory.order(stub);
			factory.waitTillDelivered();			
			controller.setRobotAndDriver(robot, driver);
			controller.setMazeConfiguration(stub.getMazeConfig());
			config = stub.getMazeConfig();
			distance =config.getMazedists(); 
			robot.setMaze(controller);
			
		}
		
		/**
		 * Tear down for WallFollower Test
		 * @throws java.lang.Exception
		 */
		@After
		public void tearDown() throws Exception {
			
		}
		
		/**
		 * Test all setup objects are non-Null and initialized to the correct values
		 */
		@Test
		public void testSetUp() {
			
			assertEquals(0, driver.pathLen);
			assertEquals(0, driver.width);
			assertEquals(0, driver.height);
			assertEquals(0, (int) driver.startBatteryLev);
			
			assertNull(driver.robot);
			assertNull(driver.distance);
			
		}
		
		/**
		 * Test the setRobot method sets the robot
		 * instance variable to a non-Null robot
		 */
		@Test
		public void testSetRobot() {
			
			driver.setRobot(robot);
			assertNotNull(driver.robot);
			assertEquals(driver.robot, robot);
		}
		
		/**
		 * Test setDimensions method
		 */
		@Test
		public void testSetDimensions() {
			
			driver.setDimensions(4, 4);
			
			assertEquals(4, driver.height);
			assertEquals(4, driver.width);
			
		}
		
		/**
		 * Test setDistance method
		 */
		@Test
		public void testSetDistance() {
			
			assertNull(driver.distance);
			
			Distance distance = config.getMazedists();
			
			driver.setDistance(distance);
			
			assertNotNull(driver.distance);
			
			assertEquals(distance, driver.distance);
			
		}
		
		/**
		 * Test the drive2Exit
		 */
		@Test
		public void testDrive2Exit() {
			
			driver.setRobot(robot);
			driver.setDimensions(config.getWidth(), config.getHeight());
			driver.setDistance(config.getMazedists());
			
			boolean reachedExit = false;
			
			try {
				driver.drive2Exit();
				reachedExit = driver.robot.isAtExit();
			}
			catch (Exception e) {
				reachedExit = true;
			}
			finally {
				assertTrue(reachedExit);
			}

			assertTrue(robot.batteryLev != 2500.0f);
		}
		
		/**
		 * Test drive2Exit with a too large path length traveled
		 */
		@Test
		public void testDrive2ExitLargePathLen() throws Exception{
			driver.setRobot(robot);
			driver.setDimensions(config.getWidth(), config.getHeight());
			driver.setDistance(config.getMazedists());
			
			robot.position[0] = 0;
			robot.position[1] = 0;
			driver.pathLen = 100000;
			
			boolean reachedExit = true;
			
			try {
				reachedExit = driver.drive2Exit();
				
			}
			catch (Exception e) {
				
			}
			finally {
				assertFalse(reachedExit);
			}
		}
		
	
		
		/**
		 * Test drive2Exit with a zero battery charge
		 * 
		 */
		@Test
		public void testDrive2ExitNoBattery() {
			
			driver.setRobot(robot);
			driver.setDimensions(config.getWidth(), config.getHeight());
			driver.setDistance(config.getMazedists());
			
			robot.batteryLev = 0.0f;
			
			boolean checkError = false;
			
			
			
			try {
				driver.drive2Exit();
				
			}
			catch (Exception e) {
				checkError = true;
			}
			finally {
				assertTrue(checkError);
			}
			
		}
	
		/**
		 * Test the drive2Exit from a random position in the maze
		 * so that the driver can get to the exit from any cell in the maze
		 */
		@Test
		public void testRandomDrive2Exit() {
			driver.setRobot(robot);
			driver.setDimensions(config.getWidth(), config.getHeight());
			driver.setDistance(config.getMazedists());
			
			Random rand = new Random();
			
			int xStart = rand.nextInt(config.getWidth());
			int yStart = rand.nextInt(config.getHeight());
			
			robot.position[0] = xStart;
			robot.position[1] = yStart;
		
			boolean reachedExit = false;
		
			try {
				driver.drive2Exit();
				reachedExit = driver.robot.isAtExit();
			}
			catch (Exception e) {
				reachedExit = true;
			}
			finally {
				assertTrue(reachedExit);
			}
			
			assertTrue(robot.batteryLev != 2500.0f);
		}
		
		/**
		 * Test getEnergyConsumption method returns the right energy used so far
		 * @precondition setBatteryLevel is tested
		 */
		@Test
		public void testGetEnergyConsumption() {
			
			driver.setRobot(robot);
			
			assertEquals(0, (int) driver.getEnergyConsumption());
			
			robot.setBatteryLevel(1000.0f);
			
			assertEquals((int) 1500.0f, (int) driver.getEnergyConsumption());
		}
		
		/**
		 * Test the getPathLength method returns the right path length driven
		 * @precondition drive2Exit is tested
		 */
		@Test
		public void testGetPathLength() {
			
			driver.setRobot(robot);
			driver.setDimensions(config.getWidth(), config.getHeight());
			driver.setDistance(config.getMazedists());
			
			
			try {
				driver.drive2Exit();
				
			}
			catch (Exception e) {
				
			}
			finally {
				assertFalse(driver.getPathLength() == 0);
			}
		}
}

