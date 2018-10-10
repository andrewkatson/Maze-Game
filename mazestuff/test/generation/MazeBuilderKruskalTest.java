package generation;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import generation.Order.Builder;
import generation.MazeBuilderKruskal;

import org.junit.Test;

/**
 * White box tests for the random Kruskal Maze algorithm. 
 * 
 * @author Katson, Andrew
 *
 */

public class MazeBuilderKruskalTest extends MazeBuilderKruskal {
	private MazeBuilderKruskal testMaze;
	
	/**
	 * Initializes the testMaze to the given dimensions
	 * @param width
	 * @param height
	 * @param isDeterministic
	 */	 
	private void setUpTestMaze(int width, int height, boolean isDeterministic) {
		if(isDeterministic) {
			testMaze = new MazeBuilderKruskal(true);
		}
		else {
			testMaze = new MazeBuilderKruskal();
		}
		testMaze.setHeight(height);
		testMaze.setWidth(width);
		testMaze.mazeBoard = new int[width][height];
		
	}
	/**
	 * @throws java.lang.Exception
	 */
	@After 
	public void tearDown() throws Exception{
		testMaze = null;
	}
	
	/**
	 * Tests if the maze is created properly and all cells have the correct initial value
	 */
	@Test
	public void testKruskalSetup(){
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		
		int checkAgainstMaze[][] = {{0,1,2,3}, {4,5,6,7}, {8,9,10,11}, {12,13,14,15}};
		
		testMaze.initMazeBoard();
		
		assertTrue(Arrays.deepEquals(testMaze.mazeBoard, checkAgainstMaze));
	}
	
	/**
	 * Tests if merge works so that when two cells are used for a merge of two spanning trees all other cells
	 * with the same value as the cell being merged into the other will change to the 
	 * value of the main spanning tree
	 * @precondition testKruskalSetup is run
	 */
	@Test
	public void testMerge() {
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		testMaze.initMazeBoard();
		
		mazeBoard = testMaze.mazeBoard;
		
		int spanValMain = 1; //value given to the main spanning tree in the test merge
		int spanValMerge = 5; //value given to the spanning tree to be merged with the main one
		
		//Changing board to have two primary spanning trees
		mazeBoard[0][0] = spanValMain;
		mazeBoard[0][2] = spanValMain;
		mazeBoard[2][1] = spanValMerge;
		mazeBoard[3][1] = spanValMerge;
		
		int chosenMainRow = 0;
		int chosenMainCol = 0;
		int mergeRow = 2;
		int mergeCol = 1;
		
		testMaze.mergeCellTree(chosenMainRow, chosenMainCol, mergeRow, mergeCol);
		
		assertTrue(mazeBoard[1][1] == spanValMain);
		assertTrue(mazeBoard[2][1] == spanValMain);
		assertTrue(mazeBoard[3][1] == spanValMain);
	}
	
	/**
	 * Tests if the list of walls is created properly and the correct number of expected walls is generated
	 * @precondition testKruskalSetup is run
	 */
	@Test
	public void testWalls() {
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		
		testMaze.initMazeBoard();
		
		ArrayList<Wall> walls = new ArrayList<Wall>();
		
		Wall duplicate = new Wall(0,0,CardinalDirection.East);
		//set it to a random wall
		duplicate.setRandomly(chosenWidth, chosenHeight);
		walls.add(duplicate);
		
		testMaze.getListOfWallsStatic(walls); //set the list to the calculated list of walls
		
		int expectedNumWalls = 40;
		int numOfWalls = walls.size(); //the number of walls found by the getListOfWalls
		assertEquals(expectedNumWalls, numOfWalls);
		
	}
	
	/**
	 * Tests if the wall removal method is working properly (not retuning null)
	 * @precondition testKruskalSetup is run
	 * @precondition testWalls is run
	 */
	@Test
	public void testRandomWallRemoval() {
		int chosenWidth = 6;
		int chosenHeight = 6;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		
		testMaze.initMazeBoard();
		final ArrayList<Wall> walls = new ArrayList<Wall>();
		testMaze.getListOfWallsStatic(walls);
		
		assertNotNull(testMaze.extractWallFromCandidateSetRandomly(walls)); //check if the function returns a wall
	}
	
	/**
	 * Test if the right id is returned (id is the value)
	 * @precondition testKruskalSetup is run
	 */
	@Test
	public void testRightIdReturn() {
		int checkAgainstMaze[][] = {{0,1,2,3}, {4,5,6,7}, {8,9,10,11}, {12,13,14,15}};
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		
		testMaze.initMazeBoard();
		int checkMaze[][] = testMaze.mazeBoard;
		Cells doublecheck = new Cells(checkMaze);
		
		for(int row = 0; row < chosenWidth; row++) {
			for(int col = 0; col < chosenHeight; col++) {
				int expectedVal = checkAgainstMaze[row][col];
				int returnedVal = testMaze.getCellTreeID(row, col);
				int cellVal = doublecheck.getValueOfCell(row, col);
				assertEquals(expectedVal, returnedVal);
				assertEquals(expectedVal, cellVal);
			}
		}
		
		
	}
	
	/**
	 * Tests if id setting works (id is the value)
	 * @precondition testKruskalSetup is run
	 * 
	 */
	@Test
	public void testRightIdSet() {
		int checkAgainstMaze[][] = {{0,0,0,0}, {5,5,5,5}, {10,10,10,10}, {15,15,15,15}};
		Cells checkAgainst = new Cells(checkAgainstMaze);
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		
		testMaze.initMazeBoard();
		
		for(int row = 0; row < chosenWidth; row++) {
			for(int col = 0; col < chosenHeight; col++) {
				switch(row) {
				case 0:{
					testMaze.setCellTreeID(row, col, 0);
					break;
				}
				case 1:{
					testMaze.setCellTreeID(row, col, 5);
					break;
				}
				case 2:{
					testMaze.setCellTreeID(row, col, 10);
					break;
				}
				case 3:{
					testMaze.setCellTreeID(row, col, 15);
					break;
				}
				default:{
					assertTrue(false); //should not ever get a row that is beyond the dimensions set
				}
					
			}			
					
			}
		}
		int checkMaze[][] = testMaze.mazeBoard;
		Cells doublecheck = new Cells(checkMaze);
		assertTrue(doublecheck.equals(checkAgainst));
		
		assertTrue(Arrays.deepEquals(checkMaze, checkAgainstMaze));
	}
	
	/**
	 * Test if checking if two cells are part of the same tree works
	 * @precondition testKruskalSetup is run
	 * @precondition testRightIdSet is run
	 * @precondition testRightIdReturn is run
	 * @precondition testMerge is run
	 */
	@Test
	public void testCheckSameTrees() {
		
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		
		testMaze.initMazeBoard();
		
		int idMainTree = 0;
		int mergeTreeID = 15;
		int mainTreePos[] = {0,0};
		int mergeTreePos[] = {3,3};
		int notTreePos[] = {0,3};
		
		//Set up main span tree
		testMaze.setCellTreeID(1, 0, idMainTree);
		testMaze.setCellTreeID(2, 0, idMainTree);
		testMaze.setCellTreeID(3, 0, idMainTree);
		testMaze.setCellTreeID(1, 1, idMainTree);
		testMaze.setCellTreeID(3, 1, idMainTree);
		
		//Set up the second span tree
		testMaze.setCellTreeID(1, 2, mergeTreeID);
		testMaze.setCellTreeID(2, 2, mergeTreeID);
		testMaze.setCellTreeID(2, 3, mergeTreeID);
		
		testMaze.mergeCellTree(mainTreePos[0], mainTreePos[1], mergeTreePos[0], mergeTreePos[1]);
		
		assertTrue(testMaze.checkIfSameTree(mainTreePos[0], mainTreePos[1], mergeTreePos[0], mergeTreePos[1]));
		assertFalse(testMaze.checkIfSameTree(mainTreePos[0], mainTreePos[1], notTreePos[0], notTreePos[1]));
	}
	/**
	 * Test the SingleRandom class to see if the seed is the correct
	 * determinsitic seed
	 * 
	 */
	@Test
	public void testSingleRandom() {
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, true);
		
		testMaze.initMazeBoard();
		
		int expectedSeed = 3;
		int seed = testMaze.getSingleRandom().getSeed();
		assertEquals(expectedSeed, seed);
	}
	/**
	 * Test the SingleRandom class to see if a random number can be generated
	 * determinsitic seed
	 * 
	 */
	@Test
	public void testSingleRandomGenerator() {
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, true);
		
		testMaze.initMazeBoard();
		
		int randNum = testMaze.getSingleRandom().nextInt();
		assertNotNull(randNum);
	}
	/**
	 * Tests the Cells class equals
	 */
	@Test
	public void testCellsEquals() {
		int checkAgainstMaze[][] = {{0,1,2,3,5}, {4,5,6,7,8}, {8,9,10,11,12}, {12,13,14,15,16}};
		int checkOtherMaze[][] = {{0,1,2,3}, {4,5,6,7}, {8,9,0,11}, {12,13,14,15}};
		int chosenWidth = 4;
		int chosenHeight = 4;
		boolean notDeterministic = false;
		setUpTestMaze(chosenWidth, chosenHeight, notDeterministic);
		
		testMaze.initMazeBoard();
		int checkMaze[][] = testMaze.mazeBoard;
		
		Cells compareMaze = new Cells(checkMaze);
		Cells mainMaze = new Cells(checkAgainstMaze);
		Cells differentValMaze = new Cells(checkOtherMaze);
		
		assertTrue(mainMaze.equals(mainMaze));
		assertFalse(mainMaze.equals(null));
		assertFalse(mainMaze.equals(checkMaze));
		assertFalse(mainMaze.equals(differentValMaze));
	}
}
