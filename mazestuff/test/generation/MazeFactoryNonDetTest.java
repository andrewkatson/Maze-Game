package generation;

	import static org.junit.Assert.*;
	import java.util.Arrays;
	import org.junit.After;
	import org.junit.Before;
	import org.junit.Test;

	import generation.Order.Builder;
	import gui.Constants;
	import generation.SingleRandom;
	/**
	 * Blackbox testing for the MazeBuilder class for all algorithms (deterministic)
	 * i.e. DFS, Kruskal, Prim
	 * @author Katson, Andrew
	 *
	 */

	public class MazeFactoryNonDetTest {
		
		//factory that will create the Maze's of the specified types 
		//e.g. Prim's, Kruskals, DFS
		private MazeFactory mazeFactory;
		//configuration object will hold all the detailed information about the maze
		private MazeConfiguration config;
		//the order stub 
		private OrderStub stub;
		
		/**
		 * @throws java.lang.Exception
		 */
		@Before
		public void setUpForTests() throws Exception {
			boolean det = false; // deterministic maze or not
			mazeFactory = new MazeFactory(det);
			
		}

		/**
		 * @throws java.lang.Exception
		 */
		@After
		public void tearDownForTests() throws Exception {
		}
		
		/**
		 * Tests each possible maze setup as non null
		 */
		@Test
		public void testSetUp() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};

			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS, Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
													
							assertNotNull(mazeFactory); //check for existence of mazeFactory
							assertNotNull(config); //check for existence of configuration
							assertNotNull(stub); //check for existence of the Order
							
					}
				}
			}
		}
		
		/**
		 * Tests if correct minimum number of walls exist (checks the borders)
		 */
		@Test
		public void testCorrectWalls() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};

			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS,  Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
													
							int numBorderWalls = 0;	//the count of the current number of border walls
							int exitAndEntrance = 2; //represents that one wall on the border should be the exit and another is the entrance
							int totalRows = config.getWidth();
							int totalCols = config.getHeight();
							//there should be walls on every side of the border except for the entrance and exit
							int expectedBorderWalls = totalRows * 2 + totalCols * 2 - exitAndEntrance;
							Cells allCells = config.getMazecells();
							
							for(int row = 0; row < totalRows; row++) {
								for(int col = 0; col < totalCols; col++) {
									for(CardinalDirection direction : CardinalDirection.values()) {
										int directionCoordinates[] = direction.getDirection();
										int directionX = directionCoordinates[0];
										int directionY = directionCoordinates[1];
										if(allCells.hasBorderTest(row, col, directionX, directionY)) {
											numBorderWalls++;
										}
									}
								}
							}
							assertTrue(numBorderWalls > expectedBorderWalls); //does this maze have the right number of walls around its border
					}
				}
			}
		}
		
		/**
		 * Test for only one exit
		 */
		@Test
		public void testOneExit() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};
			
			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS, Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
													
							int numExits = 0; //should be one at the end of the check
							int totalRows = config.getWidth();
							int totalCols = config.getHeight();
							int expectedExitDistance = 1;
							int expectedNumExits = 1;
							
							/**
							 * Array with the distance to the exit from each spot in the maze
							 */
							Distance allDistances = config.getMazedists();							
							
							for(int row = 0; row < totalRows; row++) {
								for(int col = 0; col < totalCols; col++) {
									int currDistance = allDistances.getDistance(row, col);
									if(currDistance == expectedExitDistance) {
										numExits++;
									}
								}
							}
							assertEquals(expectedNumExits, numExits);
						}
					}
				}
			}
		
		
		/**
		 * Test of all cells having path to exit (non-infinity)
		 */
		@Test
		public void testPathsToExitExist() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};
			
			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS, Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							//rooms can exist in a non-Perfect maze
							if(!perfect) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
													
							int totalRows = config.getWidth();
							int totalCols = config.getHeight();
							Distance allDistances = config.getMazedists();
							
							for(int row = 0; row < totalRows; row++) {
								for(int col = 0; col < totalCols; col++) {
									int currDistance = allDistances.getDistance(row, col);
									assertTrue(0 < currDistance && currDistance < Distance.INFINITY); //check if the distance is a valid computed number.
								}
							}
						
					}
				}
			}
		}
		
		/**
		 * Tests that start cell is the furthest cell from exit
		 */
		@Test
		public void testStartisFurthest() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};
			
			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS, Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
							
							Distance allDistances = config.getMazedists();
							int maxDistance = allDistances.getMaxDistance();
							int startPosition[] = allDistances.getStartPosition();
							int distanceForStartPosition = allDistances.getDistance(startPosition[0], startPosition[1]);
							
							//check if the position has the maximum distance from the exit
							assertEquals(maxDistance, distanceForStartPosition); 
							
							int startPositionConfig[] = config.getStartingPosition();
							
							//check if config returns the same coordinates for the starting position returned by Distance
							assertTrue(Arrays.equals(startPosition, startPositionConfig));
							

						
					}
				}
			}
		}
		
		/**
		 * Tests if the exit is marked as the right cell (the one with distance = 1)
		 */
		@Test
		public void testExitPosition() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};
			
			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			//whether they are deterministic
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS, Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
													
							int totalRows = config.getHeight();
							int totalCols = config.getWidth();
							int currMinDistance = Distance.INFINITY;
							int currMinPosition[] = {0,0};
							Distance allDistances = config.getMazedists();
							
							for(int row = 0; row < totalRows; row++) {
								for(int col = 0; col < totalCols; col++) {
									int currDistance = allDistances.getDistance(row, col);
									if(currDistance < currMinDistance) {
										currMinDistance = currDistance;
										currMinPosition[0] = row;
										currMinPosition[1] = col;
									}								
								}
							}
							int exitPosition[] = allDistances.getExitPosition();
							assertTrue(Arrays.equals(currMinPosition, exitPosition));
						}
					}
				}
			}
		
		
		/**
		 * Tests if the maze has the correct dimensions
		 */
		@Test
		public void testDimensions() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};
			
			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS, Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
													
							int totalRows = config.getWidth();
							int totalCols = config.getHeight();
							int expectedCells = totalRows * totalCols;
							int cellCount = 0;
							Distance allDistance = config.getMazedists();
							
							for(int row = 0; row < totalRows; row++) {
								for(int col = 0; col < totalCols; col++) {
									cellCount++;
								}
							}
							
							int skill = stub.getSkillLevel();
							int expectedRows = Constants.SKILL_X[skill];
							int expectedCols = Constants.SKILL_Y[skill];
							int expectedCalculatedCells = expectedRows * expectedCols;
							
							assertEquals(expectedCells, cellCount);
							assertEquals(expectedCalculatedCells, cellCount);
							
							assertEquals(expectedRows, totalRows);
							assertEquals(expectedCols, totalCols);
								
							}
						
					}
				}
			}
		
		
		/**
		 * Tests if the maze has no rooms if it is perfect
		 * If it is not perfect then the number of rooms cannot exceed the specified
		 * number allowed for the skill level
		 */
		@Test
		public void testRooms() {
			int skillLevelMaxNum = 1; 
			Boolean[] isPerfect = {true, false};
			
			//Makes mazes of all skill levels, build types, whether they are perfect or not, and
			//whether they are deterministic
			for (int i = 0; i <= skillLevelMaxNum; i++) { //skill level
				for(Builder currBuild : Builder.values()) { //builder type, DFS, Kruskal, Prim
					for(boolean perfect : isPerfect) { //perfect or not
							
							//do not allow Eller until implemented
							if(currBuild == Builder.Eller) {
								continue;
							}
							
							stub  = new OrderStub(i, perfect, currBuild);
							mazeFactory.order(stub);
							mazeFactory.waitTillDelivered();
							config = stub.getMazeConfig();
													
							int totalRows = config.getWidth();
							int totalCols = config.getHeight();
							int countRooms = 0;
							Distance allDistances = config.getMazedists();
							
							for(int row = 0; row < totalRows; row++) {
								for(int col = 0; col < totalCols; col++) {
									int currDistance = allDistances.getDistance(row, col);
									if(currDistance == Distance.INFINITY) { //check if the distance is a valid computed number.
										countRooms++;
									}
								}
							}
							
							if(perfect) { //if maze is perfect there should be no rooms
								assertTrue(countRooms == 0);
							}
							else {
								int skill = stub.getSkillLevel();
								int expectedRooms = Constants.SKILL_ROOMS[skill];
								System.out.println(countRooms);
								assertTrue(countRooms <= expectedRooms); //make sure that the number of rooms is less than or equal to the amount expected
							}
						
					}
				}
			}
		}
		

	}
