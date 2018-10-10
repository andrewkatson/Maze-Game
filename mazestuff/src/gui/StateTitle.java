package gui;

import java.awt.Graphics;

import gui.Constants.StateGUI;
import gui.Constants.UserInput;


/**
 * Class handles the user interaction
 * while the game is in the first stage
 * where the user can select the skill-level.
 * This class is part of a state pattern for the
 * Controller class. It is a ConcreteState.
 * 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a Controller object by way of the keyDown method.
 * 
 * Responsibilities
 * Show the title screen
 * Accept input for skill level  
 * Switch to the generating screen
 * If given a filename, pass it on to the generating screen
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StateTitle extends DefaultState {
    MazeView view;
    MazePanel panel;
    Controller control;
    
    /** filename is null: generate a maze 
     * filename is set to a name of an xml file with a maze: load maze from file
     * The user may play several rounds, so filename is reset to null once
     * we loaded the maze from file for one round of the game.
     */
    String filename; 
    
    /** used to enforce ordering constraint on method calls
     * start() must be called before keyDown()
     * to make sure control variable has been set.
     * initial setting: false, start sets it to true.
     */
    boolean started;  
    
    public StateTitle() {
        started = false;
    }
    
    @Override
    public void setFileName(String filename) {
        this.filename = filename;  
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
        // if given a filename, show a message and move to the loading screen
        // otherwise, show message that we wait for the skill level for input
        Graphics g = panel.getBufferGraphics() ;
        if (null == g) {
            System.out.println("StateTitle.start: can't get graphics object to draw on, skipping redraw operation") ;
        }
        else {
            view.redrawTitle(g,filename);
            panel.update();
        }
        
        if (filename != null) {
            // todo: wait 3 sec, then load file
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            control.switchFromTitleToGenerating(filename);
        }
        // reset filename such that in next round, user can select skill-level
        filename = null;
    }
    
    /**
     * Method incorporates all reactions to keyboard input in original code, 
     * The simple key listener calls this method to communicate input.
     */
    public boolean keyDown(UserInput key, int value) {
        if (!started)
            return false;

        // keys describe level of expertise
        // create a maze according to the user's selected level
        // todo: check ranges of values, use 0 as default
        if (value < 0 || value > 16) {
            value = 0;
        }
        // user types wrong key, just use 0 as a possible default value
        if (key == UserInput.Start) {
            control.switchFromTitleToGenerating(value);
        }
        else {
            System.out.println("StateTitle:unexpected command:" + key);
        }    
        return true;
    }

}



