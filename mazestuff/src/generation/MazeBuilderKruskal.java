package generation;
import java.util.ArrayList;
import generation.CardinalDirection;
import generation.Cells.*;

/**
* together with a solution based on a distance matrix.
* The MazeBuilder implements Runnable such that it can be run a separate thread.
* The MazeFactory has a MazeBuilder and handles the thread management.   

* 
* The maze is built with a randomized version of Kruskal's algorithm. 
* This means edges are chosen at random to be connected to form spanning trees. This process
* continues until all the spanning trees are connected and only one tree remains. Borders are left
* alone so that the maze remains enclosed.
*   
* @author Katson, Andrew
*/


public class MazeBuilderKruskal extends MazeBuilder implements Runnable{
	
	protected int[][] mazeBoard;
	
	public MazeBuilderKruskal() {
		super();
		System.out.println("MazeBuilderPrim uses Kruskal's algorithm to generate maze.");
	}
	
	public MazeBuilderKruskal(boolean det) {
		super(det);
		System.out.println("MazeBuilderPrim uses Kruskal's algorithm to generate maze.");
	}
	
	/**
	 * Initializes the mazeBoard with separate values for each position
	 * @return a filled out mazeBoard
	 */
	protected int[][] initMazeBoard(){
		mazeBoard = new int[this.width][this.height];
		int cellValCounter = 0; //counter to give a value to each cell of the maze
		for(int row = 0; row < this.width; row++) { //iterate over rows
			for(int col = 0; col < this.height; col++) { //iterate over cols
				mazeBoard[row][col] = cellValCounter;
				cellValCounter++;
			}
		}
		return mazeBoard;
	}
	
	/**
	 * This method generates pathways into the maze by using Kruskal's algorithm to generate a spanning tree for an undirected graph.
	 * The cells are the nodes of the graph and the spanning tree. An edge represents that one can move from one cell to an adjacent cell.
	 * So an edge implies that its nodes are adjacent cells in the maze and that there is no wall separating these cells in the maze. 
	 */
	@Override
	protected void generatePathways() {
		// create an initial list of all walls that could be removed
		// those walls lead to adjacent cells that are not part of the spanning tree yet.
		final ArrayList<Wall> candidates = new ArrayList<Wall>();
		getListOfWalls(candidates);
		
		//fill out a board to represent the maze cells as we combine them
		mazeBoard = initMazeBoard();
		
		Wall curWall;
		// we need to consider each candidate wall and consider it only once
		while(!candidates.isEmpty()){ //TODO make sure that all the candidate walls are used or removed
			// in order to have a randomized algorithm,
			// we randomly select and extract a wall from our candidate set
			// this also reduces the set to make sure we terminate the loop
			curWall = extractWallFromCandidateSetRandomly(candidates);
			
			//get the x and y of the selected cell and the neighboring cell that share the wall
			int row1 = curWall.getX();
			int col1 = curWall.getY();
			int row2 = curWall.getNeighborX();
			int col2 = curWall.getNeighborY();
			
			// check if wall leads to a new cell that is not connected to the spanning tree yet
			if (!checkIfSameTree(row1, col1, row2, col2))
			{
				cells.deleteWall(curWall);
				mergeCellTree(row1, col1, row2, col2); 
			}
		}
	}
	/**
	 * Pick a random position in the list of candidates, remove the candidate from the list and return it
	 * @param candidates
	 * @return candidate from the list, randomly chosen
	 */
	protected Wall extractWallFromCandidateSetRandomly(final ArrayList<Wall> candidates) {
		return candidates.remove(random.nextIntWithinInterval(0, candidates.size()-1)); 
	}
	
	
	
	/**
	 * Gets a list of all walls that could be removed from the maze based on walls towards new cells
	 * @param walls - list that will have the different walls that can be removed
	 * 
	 */
	protected void getListOfWalls(ArrayList<Wall> walls) {
		if (!walls.isEmpty())
			walls.clear();
		//iterate over all the rows
		for (int row = 0; row < this.width; row++) {
		//iterate over all the columns
			for(int col = 0; col < this.height; col++) {
				for (CardinalDirection cd : CardinalDirection.values()) {
					Wall newWall = new Wall(row, col, cd);
					if (cells.wallExists(newWall) && !isWallDuplicate(newWall, walls)) // if there is a wall between the cells and the wall has not been stored
					{
						walls.add(newWall);
					}
				}
			}
		}
	}
	

	/**
	 * @param row1
	 * @param col1
	 * @param row2
	 * @param col2
	 * @return true if the board positions have the same value (are connected)
	 */
	protected boolean checkIfSameTree(int row1, int col1, int row2, int col2) {
		
		if(mazeBoard[row1][col1] == mazeBoard[row2][col2]) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the wall is a duplicate
	 * @param checkWall - the Wall being checked to see if it is a duplicate
	 * @param listWalls - the current list of stored Walls 
	 * @return true if the wall is already stored in the ArrayList of walls
	 */
	
	protected boolean isWallDuplicate(Wall checkWall, ArrayList<Wall> listWalls) {
		if(listWalls == null) {
			return false;
		}
		for (Wall storedWall : listWalls) {
			if (storedWall.sameWall(checkWall) || storedWall.equals(checkWall)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Merge the two spanning trees by changing the values stored in one to be the same as the values stored
	 * in the other so that all the cells are the in the same tree (have the same value)
	 * @param row1 
	 * @param col1 
	 * @param row2 
	 * @param col2 
	 */
	protected void mergeCellTree(int row1, int col1, int row2, int col2) {
		int newNumId = getCellTreeID(row1, col1); //Id value associated with the Tree to be kept as the main one
		int oldNumId = getCellTreeID(row2, col2); //Id value associated with the Tree to be merged
		
		for(int row = 0; row < this.width; row++) {
			for(int col = 0; col < this.height; col++) {
				if(oldNumId == getCellTreeID(row,col)) { //if the cell belongs to the tree being merged
					setCellTreeID(row, col, newNumId); //set the cell to have the same Id as the main tree's
				}
			}
		}
	}
	
	/**
	 * @param row
	 * @param col
	 * @return the ID of the cell in the Maze Board that associates it with a tree
	 */
	protected int getCellTreeID(int row, int col) {
		return mazeBoard[row][col];
	}
	/**
	 * @param row
	 * @param col
	 * @param newID
	 * 
	 */
	protected void setCellTreeID(int row, int col, int newID) {
		mazeBoard[row][col] = newID;
	}
	
	
	////////////////////////////////SPECIAL TESTING METHODS//////////////////////
	/**
	 * Testing version of getListOfWalls. 
	 * Gets a list of all walls that could be removed from the maze based on walls towards new cells
	 * @param walls - list that will have the different walls that can be removed
	 * 
	 */
	protected void getListOfWallsStatic(ArrayList<Wall> walls) {
		if (!walls.isEmpty())
			walls.clear();
		//iterate over all the rows
		for (int row = 0; row < this.width; row++) {
		//iterate over all the columns
			for(int col = 0; col < this.height; col++) {
				for (CardinalDirection cd : CardinalDirection.values()) {
					Wall newWall = new Wall(row, col, cd);
					if (wallExists(newWall) && !isWallDuplicate(newWall, walls)) // if there is a wall between the cells
					{
						walls.add(newWall);
					}
				}
			}
		}
	}
	
	/**
	 * Helper method for testing
	 * The call to wallExists in Cells was not working for testing the getListOfWalls method alone 
	 * because the cells object became null when entering the method and wallExists is not static
	 */
	private boolean wallExists(Wall wall) {
		Cells workAround = new Cells(this.width, this.height);
		return workAround.wallExists(wall);
	}
	
}

