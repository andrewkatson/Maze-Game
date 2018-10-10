package generation;

import generation.Order.Builder;

/**
 * A stub order class for MazeFactory tests. Fulfills the Order interface.
 * @author Katson,Andrew
 */

public class OrderStub implements Order {
	private Builder builder;
	private MazeConfiguration mazeConfig;
	private int skill;
	private boolean perfect;
	int percentDone;
	
	/**
	 * Constructor
	 * @param skill - the skill level of the maze
	 * @param perfect - whether it is perfect or not 
	 * @param build - the builder for the maze
	 * 
	 */
	public OrderStub(int skill, boolean perfect, Builder build) {
		this.skill = skill;
		this.perfect = perfect;
		this.builder = build;
	}
	/**
	 * Gives the required skill level, range of values 0,1,2,...,15
	 */
	public int getSkillLevel() {
		return this.skill;
	}
	/** 
	 * Gives the requested builder algorithm, possible values 
	 * are listed in the Builder enum type.
	 */
	public Builder getBuilder() {
		return this.builder;
	}

	/**
	 * Describes if the ordered maze should be perfect, i.e. there are 
	 * no loops and no isolated areas, which also implies that 
	 * there are no rooms as rooms can imply loops
	 */
	public boolean isPerfect() {
		return this.perfect;
	}
	/**
	 * Delivers the produced maze. 
	 * This method is called by the factory to provide the 
	 * resulting maze as a MazeConfiguration.
	 * @param mazeConfig - the maze configuration object with information about the maze
	 */
	public void deliver(MazeConfiguration mazeConfig) {
		this.mazeConfig = mazeConfig;
	}
	
	/**
	 * Gets the maze configuration
	 * 
	 */	
	public MazeConfiguration getMazeConfig() {
		return this.mazeConfig;
	}
	
	/**
	 * Provides an update on the progress being made on 
	 * the maze production. This method is called occasionally
	 * during production, there is no guarantee on particular values.
	 * Percentage will be delivered in monotonously increasing order,
	 * the last call is with a value of 100 after delivery of product.
	 * @param current percentage of job completion
	 */
	public void updateProgress(int percentage) {
		this.percentDone = percentage;
	}
}
