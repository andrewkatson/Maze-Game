/**
 * 
 */
package gui;

import generation.Order;

import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JFrame;


/**
 * This class is a wrapper class to startup the Maze game as a Java application
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 * 
 * TODO: use logger for output instead of Sys.out
 */
public class MazeApplication extends JFrame {

	// not used, just to make the compiler, static code checker happy
	private static final long serialVersionUID = 1L;
    // need to instantiate a controller to return as a result in any case
    Controller result;
    // need to instantiate a robot to return as a result in any case
    Robot robot;
    // need to instantiate a robot driver to return as a result in any case
    RobotDriver driver;
	/**
	 * Constructor
	 */
	public MazeApplication() {
		init(null);
	}

	/**
	 * Constructor that loads a maze from a given file or uses a particular method to generate a maze
	 * or can use a particular robot dirver
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller) or robot driver Wallfollower
     * or a filename that stores an already generated maze that is then loaded, or can be null
	 */
	public MazeApplication(String parameter) {
		init(parameter);
	}
	

	/**
	 * Constructor that loads a maze from a given file or uses a particular method to generate a maze
	 * or can use a particular robot dirver
	 * @param generation can identify a generation method (Prim, Kruskal, Eller)
	 * @param driver can identify a robot driver Wallfollower
     * or a filename that stores an already generated maze that is then loaded, or can be null
	 */
	public MazeApplication(String generation, String driver) {
		init(generation, driver);
	}

	/**
	 * Instantiates a controller with settings according to the given parameter.
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
	 * or a filename that contains a generated maze that is then loaded,
	 * or can be null
	 * or a robot driver (WallFollower)
	 * @return the newly instantiated and configured controller
	 */
	 Controller createController(String parameter) {
		
	    String msg = null; // message for feedback
	    // Case 1: no input
	    if (parameter == null) {
	        msg = "MazeApplication: maze will be generated with a randomized algorithm."; 
	        result = new Controller();
	    }
	    // Case 2: Prim
	    else if ("Prim".equalsIgnoreCase(parameter))
	    {
	        msg = "MazeApplication: generating random maze with Prim's algorithm.";
	        result = new Controller();
	        result.setBuilder(Order.Builder.Prim);
	    }
	    // Case 3: Kruskal
	    else if ("Kruskal".equalsIgnoreCase(parameter))
	    {
	        msg = "MazeApplication: generating random maze with Kruskal's algorithm.";
	        result = new Controller();
	        result.setBuilder(Order.Builder.Kruskal);
	    }
	    // Case 4: Eller algorithm
	    else if ("Eller".equalsIgnoreCase(parameter))
	    {
	    	result = new Controller();
	    	// TODO: for P2 assignment, please add code to set the builder accordingly
	        throw new RuntimeException("Don't know anybody named Eller...");
	    }
	    // Case 5: A WallFollower algorithm
	    else if("WallFollower".equalsIgnoreCase(parameter))
	    {
	    	msg = "MazeApplication: solving maze with WallFollower.";
	    	result = new Controller();
	    	robot = new BasicRobot();
	    	robot.setMaze(result);
	    	driver = new WallFollower();
	    	result.setRobotAndDriver(robot, driver);
	    }
	    // Case 6: a file
	    else {
	        File f = new File(parameter) ;
	        if (f.exists() && f.canRead())
	        {
	            msg = "MazeApplication: loading maze from file: " + parameter;
	            result = new Controller();
	            result.setFileName(parameter);
	            return result;
	        }
	        else {
	            // None of the predefined strings and not a filename either: 
	        	result = new Controller();
	            msg = "MazeApplication: unknown parameter value: " + parameter + " ignored, operating in default mode.";
	        }
	    }
	    // controller instanted and attributes set according to given input parameter
	    // output message and return controller
	    System.out.println(msg);
	    return result;
	}
	 
		/**
		 * Instantiates a controller with settings according to the given parameter.
		 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
		 * or a filename that contains a generated maze that is then loaded,
		 * or can be null
		 * or a robot driver (WallFollower)
		 * @return the newly instantiated and configured controller
		 */
		 Controller createController(String generationAlgo, String driverName) {

		    String msg = null; // message for feedback
		    // Case 1: no input algorithm
		    if (generationAlgo == null && driverName != null) {
		        msg = "MazeApplication: maze will be generated with a randomized algorithm."; 
		        result = createController(driverName);
		    }
		    //Case 2: no driverName
		    else if(generationAlgo != null && driverName == null) {
		    	result = createController(generationAlgo);
		    }
		    //Case 3: no input for algorithm and driverName
		    else if (generationAlgo == null && driverName == null) {
		        msg = "MazeApplication: maze will be generated with a randomized algorithm."; 
		        result = new Controller();
		    }
		    // Case 4: Prim
		    else if ("Prim".equalsIgnoreCase(generationAlgo))
		    {
		        msg = "MazeApplication: generating random maze with Prim's algorithm.";
		        result = new Controller();
		        result.setBuilder(Order.Builder.Prim);
		    	robot = new BasicRobot();
		    	robot.setMaze(result);
		    	if (driverName.equalsIgnoreCase("Wallfollower")){
		    		driver = new WallFollower();
		    		msg = "MazeApplication: solving maze with WallFollower.";
		    	}
		    	else {
		    		throw new RuntimeException("Driver not implemented");
		    	}		    	
		    	result.setRobotAndDriver(robot, driver);
		    }
		    // Case 5: Kruskal
		    else if ("Kruskal".equalsIgnoreCase(generationAlgo))
		    {
		        msg = "MazeApplication: generating random maze with Kruskal's algorithm.";
		        result = new Controller();
		        result.setBuilder(Order.Builder.Kruskal);
		    	robot = new BasicRobot();
		    	robot.setMaze(result);
		    	if (driverName.equalsIgnoreCase("Wallfollower")){
		    		driver = new WallFollower();
		    		msg = "MazeApplication: solving maze with WallFollower.";
		    	}
		    	else {
		    		throw new RuntimeException("Driver not implemented");
		    	}		    	
		    	result.setRobotAndDriver(robot, driver);
		    }
		    // Case 6: Eller algorithm
		    else if ("Eller".equalsIgnoreCase(generationAlgo))
		    {
		    	result = new Controller();
		    	// TODO: for P2 assignment, please add code to set the builder accordingly
		        throw new RuntimeException("Don't know anybody named Eller...");
		    }
		    // Case 7: a file
		    else {
		        File f = new File(generationAlgo) ;
		        if (f.exists() && f.canRead())
		        {
		            msg = "MazeApplication: loading maze from file: " + generationAlgo;
		            result = new Controller();
		            result.setFileName(generationAlgo);
			    	robot = new BasicRobot();
			    	robot.setMaze(result);
			    	if (driverName.equalsIgnoreCase("Wallfollower")){
			    		driver = new WallFollower();
			    	}
			    	else {
			    		throw new RuntimeException("Driver not implemented");
			    	}		    	
			    	result.setRobotAndDriver(robot, driver);
		            return result;
		        }
		        else {
		            // None of the predefined strings and not a filename either: 
		        	result = new Controller();
		            msg = "MazeApplication: unknown parameter value ignored, operating in default mode.";
			    	robot = new BasicRobot();
			    	robot.setMaze(result);
			    	if (driverName.equalsIgnoreCase("Wallfollower")){
			    		driver = new WallFollower();
			    		msg = "MazeApplication: solving maze with WallFollower.";
			    	}
			    	else {
			    		throw new RuntimeException("Driver not implemented");
			    	}		    	
			    	result.setRobotAndDriver(robot, driver);
		        }
		    }
		    // controller instanted and attributes set according to given input parameter
		    // output message and return controller
		    System.out.println(msg);
		    return result;
		}

	/**
	 * Initializes some internals and puts the game on display.
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller) or a robot driver (WallFollower)
     * or a filename that contains a generated maze that is then loaded, or can be null
	 */
	private void init(String parameter) {
		System.out.println("parameter " + parameter);
	    // instantiate a game controller and add it to the JFrame
	    Controller controller = createController(parameter);
		add(controller.getPanel()) ;
		// instantiate a key listener that feeds keyboard input into the controller
		// and add it to the JFrame
		KeyListener kl = new SimpleKeyListener(this, controller) ;
		addKeyListener(kl) ;
		// set the frame to a fixed size for its width and height and put it on dispaly
		setSize(400, 400) ;
		setVisible(true) ;
		// focus should be on the JFrame of the MazeApplication and not on the maze panel
		// such that the SimpleKeyListener kl is used
		setFocusable(true) ;
		// start the game, hand over control to the game controller
		controller.start();
	}
	
	/**
	 * Initializes some internals and puts the game on display.
	 * @param generation can identify a generation method (Prim, Kruskal, Eller) 
     * or a filename that contains a generated maze that is then loaded, or can be null
     * @param driver can identify a robot driver method (Wallfollower)
	 */ 
	
	private void init(String generation, String driver) {

	    // instantiate a game controller and add it to the JFrame
	    Controller controller = createController(generation, driver);
		add(controller.getPanel()) ;
		// instantiate a key listener that feeds keyboard input into the controller
		// and add it to the JFrame
		KeyListener kl = new SimpleKeyListener(this, controller) ;
		addKeyListener(kl) ;
		// set the frame to a fixed size for its width and height and put it on dispaly
		setSize(400, 400) ;
		setVisible(true) ;
		// focus should be on the JFrame of the MazeApplication and not on the maze panel
		// such that the SimpleKeyListener kl is used
		setFocusable(true) ;
		// start the game, hand over control to the game controller
		controller.start();
	}
	
	/**
	 * Main method to launch Maze game as a java application.
	 * The application can be operated in five ways. 
	 * 1) The intended normal operation is to provide no parameters
	 * and the maze will be generated by a randomized DFS algorithm (default). 
	 * 2) If a filename is given that contains a maze stored in xml format. 
	 * The maze will be loaded from that file. 
	 * This option is useful during development to test with a particular maze.
	 * 3) A predefined constant string is given to select a maze
	 * generation algorithm, currently supported is "Prim", "Kruskal".
	 *4) A predefined constant string is given to select a maze solving algorithm, 
	 * currently supported is "Wallfollower" 
	 * 5) A predefined constant string is given to select a maze
	 * generation algorithm, currently supported is "Prim", "Kruskal" and a second
	 * predefined constant string is given to select a maze solving algorithm, 
	 * currently supported is "Wallfollower" 
	 * @param args is optional, first string can be a fixed constant like Prim or
	 * the name of a file that stores a maze in XML format
	 */
	public static void main(String[] args) {
	    JFrame app ; 

		switch (args.length) {
		case 1 : { //parameter
			app = new MazeApplication(args[0]);
			break ;
		}
		case 2:{ //tag + parameter
				app = new MazeApplication(args[1]);
				break;
		}
		case 3:{ //parameter + tag1 + parameter
			if(args[1].equalsIgnoreCase("-f") || args[1].equalsIgnoreCase("-g")) { // second parameter is the generation algo or file
				app = new MazeApplication(args[2], args[0]);
			}
			else if(args[1].equalsIgnoreCase("-d")) { //second parameter is driver
				app = new MazeApplication(args[0], args[2]);
			}
			else {
		        throw new RuntimeException("Input not properly formatted");
			}
			break;
		}
		case 4:{ //tag1 + parameter1 + tag2 + parameter 2
			// second parameter is the generation algo or file and first parameter is the driver
			if((args[0].equalsIgnoreCase("-f") || args[0].equalsIgnoreCase("-g")) && args[2].equalsIgnoreCase("-d") ) { 
				
				app = new MazeApplication(args[1], args[3]);
			}
			//second parameter is driver and first parameter is the generation
			else if(args[0].equalsIgnoreCase("-d") && (args[2].equalsIgnoreCase("-f") || args[2].equalsIgnoreCase("-g"))) { 
				app = new MazeApplication(args[3], args[1]);
				
			}
			else {
		        throw new RuntimeException("Input not properly formatted");
			}
			break;
		}
		case 0 : 
		default :{ 
			app = new MazeApplication() ;
			break ;
		}
		}
		app.repaint() ;
	}

}
