package gui;

import java.awt.Graphics;

import gui.Constants.StateGUI;
import gui.Constants.UserInput;


/**
 * Class handles the user interaction
 * while the game is in the final stage
 * where the user sees the final screen
 * and can restart the game.
 * This class is part of a state pattern for the
 * Controller class. It is a ConcreteState.
 * 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a Controller object by way of the keyDown method.
 * 
 * Responsibilities
 * Show the final screen
 * Accept input of any kind to return to title screen  
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StateWinning extends DefaultState {
    MazeView view;
    MazePanel panel;
    Controller control;
    
    
    boolean started;
    int pathLength;
    float energyConsumption;
    
    public StateWinning() {
        pathLength = 0;
        started = false;
        energyConsumption = 0.0f;
    }
    
    /**
     * Start the game by showing the title screen.
     */
    public void start(Controller controller, MazePanel panel) {
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
        control = controller;
        // keep the reference to the panel for drawing
        this.panel = panel;
        // init mazeview, controller not needed for title
        view = new MazeView(null);

        if (panel == null) {
    		System.out.println("StateWinning.start: warning: no panel, dry-run game without graphics!");
    		return;
    	}
        // show view
        Graphics g = panel.getBufferGraphics() ;
        if (null == g) {
            System.out.println("StateWinning.start: can't get graphics object to draw on, skipping redraw operation") ;
        }
        else {
            // todo: adjust parameter settings for direct call to MazeView.redraw()
            //view.redraw(g, StateGUI.STATE_FINISH, 0, 0, 0, 0, 0, Constants.VIEW_OFFSET, null, 0) ;
        	view.setPathLen(this.pathLength);
        	view.setEnergy(this.energyConsumption);
        	view.redrawFinish(g);
            panel.update();
        }

    }
    
    /**
     * Method incorporates all reactions to keyboard input in original code, 
     * The simple key listener calls this method to communicate input.
     */
    public boolean keyDown(UserInput key, int value) {
        if (!started)
            return false;
        control.switchToTitle();    
        return true;
    }

    @Override
    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }
    
    @Override
    public void setEnergyConsumption(float energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
    
    
    public void setRobotandDriver(Robot robot, RobotDriver driver) {
    	view.setRobotandDriver(robot, driver);
    }
}



