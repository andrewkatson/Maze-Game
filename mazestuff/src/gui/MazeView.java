package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import gui.Constants.StateGUI;

/**
 * Implements the screens that are displayed whenever the game is not in 
 * the playing state. The screens shown are the title screen, 
 * the generating screen with the progress bar during maze generation,
 * and the final screen when the game finishes.
 * 
 * @author Peter Kemper
 *
 */
public class MazeView {

	private StateGenerating controllerState; // used for generating screen
	
    private Robot robot;
    private RobotDriver driver;
    private int pathLen;
    private float energy;
	
    public MazeView(StateGenerating c) {
        super() ;
        controllerState = c ;

    }
    /**
     * Helper method for redraw to draw the title screen, screen is hard coded
     * @param  gc graphics is the off screen image
     */
    public void redrawTitle(Graphics gc, String filename) {
        // produce white background
        gc.setColor(Color.white);
        gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
        // write the title 
        gc.setFont(largeBannerFont);
        FontMetrics fm = gc.getFontMetrics();
        gc.setColor(Color.red);
        centerString(gc, fm, "MAZE", 100);
        // write the reference to falstad
        gc.setColor(Color.blue);
        gc.setFont(smallBannerFont);
        fm = gc.getFontMetrics();
        centerString(gc, fm, "by Paul Falstad", 160);
        centerString(gc, fm, "www.falstad.com", 190);
        // write the instructions
        gc.setColor(Color.black);
        if (filename == null) {
            // default instructions
        centerString(gc, fm, "To start, select a skill level.", 250);
        centerString(gc, fm, "(Press a number from 0 to 9,", 300);
        centerString(gc, fm, "or a letter from A to F)", 320);
        }
        else {
            centerString(gc, fm, "Loading maze from file:", 250);
            centerString(gc, fm, filename, 300);
        }
        centerString(gc, fm, "Version 3.0", 350);
    }

	
	public void redraw(Graphics gc, StateGUI state, int px, int py, int view_dx,
			int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		//dbg("redraw") ;
		switch (state) {
		case STATE_TITLE:
			redrawTitle(gc);
			break;
		case STATE_GENERATING:
			redrawGenerating(gc);
			break;
		case STATE_PLAY:
			// skip this one
			break;
		case STATE_FINISH:
			redrawFinish(gc);
			break;
		}
	}
	
	private void dbg(String str) {
		System.out.println("MazeView:" + str);
	}
	
	// 
	
	/**
	 * Helper method for redraw to draw the title screen, screen is hard coded
	 * @param  gc graphics is the off screen image
	 */
	void redrawTitle(Graphics gc) {
		// produce white background
		gc.setColor(Color.white);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		// write the title 
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.red);
		centerString(gc, fm, "MAZE", 100);
		// write the reference to falstad
		gc.setColor(Color.blue);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		centerString(gc, fm, "by Paul Falstad", 160);
		centerString(gc, fm, "www.falstad.com", 190);
		// write the instructions
		gc.setColor(Color.black);
		centerString(gc, fm, "To start, select a skill level.", 250);
		centerString(gc, fm, "(Press a number from 0 to 9,", 300);
		centerString(gc, fm, "or a letter from A to F)", 320);
		centerString(gc, fm, "Version 2.0", 350);
	}
	/**
	 * Helper method for redraw to draw final screen, screen is hard coded
	 * @param gc graphics is the off screen image
	 */
	void redrawFinish(Graphics gc) {
		// produce blue background
		gc.setColor(Color.blue);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		// write the title 
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.yellow);
		centerString(gc, fm, "You won!", 100);
		// write some extra blurb
		gc.setColor(Color.orange);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		centerString(gc, fm, "Congratulations!", 160);
		// write the instructions
		gc.setColor(Color.white);
		centerString(gc, fm, "Hit any key to restart", 300);
		
		//draw path and energy information on final screen if it is set
		if(this.energy != 0 && this.pathLen != 0) {
			
			
			// write battery used
			gc.setColor(Color.RED);
			gc.setFont(smallBannerFont);
			fm = gc.getFontMetrics();
			centerString(gc, fm, "Battery Used: " + Float.toString(this.energy), 180);
			
			// write path length
			gc.setColor(Color.GREEN);
			gc.setFont(smallBannerFont);
			fm = gc.getFontMetrics();
			centerString(gc, fm, "Path length: " + Integer.toString(this.pathLen), 200);
			
		}
	}
	
	/**
	 * Helper method to set a robot and driver.
	 * Used to draw Finish screen
	 * @param robot 
	 * @param driver
	 */
	public void setRobotandDriver(Robot robot, RobotDriver driver) {
		this.robot = robot;
		this.driver = driver;
 	}
	
	/**
	 * Helper method to set the path length
	 * @param pathlen
	 */
	public void setPathLen(int pathlen) {
		this.pathLen = pathlen;
	}
	
	/**
	 * Helper method to set the energy consumption 
	 * @param energyUsed
	 */
	public void setEnergy(float energyUsed) {
		this.energy = energyUsed;
	}

	/**
	 * Helper method for redraw to draw screen during phase of maze generation, screen is hard coded
	 * only attribute percentdone is dynamic
	 * @param gc graphics is the off screen image
	 */
	void redrawGenerating(Graphics gc) {
		// produce yellow background
		gc.setColor(Color.yellow);
		gc.fillRect(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
		// write the title 
		gc.setFont(largeBannerFont);
		FontMetrics fm = gc.getFontMetrics();
		gc.setColor(Color.red);
		centerString(gc, fm, "Building maze", 150);
		gc.setFont(smallBannerFont);
		fm = gc.getFontMetrics();
		// show progress
		gc.setColor(Color.black);
		if (null != controllerState) 
		    centerString(gc, fm, controllerState.getPercentDone()+"% completed", 200);
		else
			centerString(gc, fm, "Error: no controller, no progress", 200);
		// write the instructions
		centerString(gc, fm, "Hit escape to stop", 300);
	}
	
	private void centerString(Graphics g, FontMetrics fm, String str, int ypos) {
		g.drawString(str, (Constants.VIEW_WIDTH-fm.stringWidth(str))/2, ypos);
	}

	final Font largeBannerFont = new Font("TimesRoman", Font.BOLD, 48);
	final Font smallBannerFont = new Font("TimesRoman", Font.BOLD, 16);

}
