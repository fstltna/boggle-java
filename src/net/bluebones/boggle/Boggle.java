/*
 * Boggle
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-04
 *
 * bluebones.net Boggle - network-aware multiplayer word game.
 * Copyright (C) 2004-5 Thomas David Baker <bakert@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

//TODO Would be nice to know theoretical maximum in results (for long games run 
// best AI)

package net.bluebones.boggle;

import java.io.InputStream;
import java.io.IOException;
import java.net.URLDecoder;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class for a network-aware, computerised version of Boggle.  Class to be 
 * run on each client machine.  RMI registry is started automatically on every
 * machine to do network comms.
 * <br /><br />
 * <code>$ java net.bluebones.boggle.Boggle</code>.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Boggle {

    /** Number of seconds the timer will count down during a game. */
    public static int timeInSecs = 180;
    /** Whether the game is in "debug" mode or not (will print messages to 
        stdout). */
    public static final boolean DEBUG = true;
    
    /** Path to the dictionary. */
    // TODO Should perhaps hide this and throw exceptions right up to the top
    // level where needed.
    public final static String DICT_PATH = "/resources/dict.txt";
    
    /** Dictionary that provides the list of acceptable words. */
    public static Dictionary dictionary;
    
    private static Player player;
    
    private static int totalDice = Die.dice.length;
    
    private Boggle() {}
    
    /**
     * Initialises the application.
     *
     * @param   args    Commandline arguments.
     */
    public static void main(String[] args) {

        String errMsg = "Could not set look and feel because of ";
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            Boggle.debug(errMsg + e);
        } catch (InstantiationException e) {
            Boggle.debug(errMsg + e);
        } catch (IllegalAccessException e) {
            Boggle.debug(errMsg + e);
        } catch (UnsupportedLookAndFeelException e) {
            Boggle.debug(errMsg + e);
        }
        
        player = new HumanPlayer();
        /* TODO AI that adjusts to situation:
        board quality + shortness = low number
        crap board + fastness = high number 
        do a pass at 3 then another at 5 then another at 8? */
    
        loadDictionary();
    }
    
    /** The total number of dice on the board. */
    public static final int totalDice() {
        return Boggle.totalDice;
    }
    
    /** The number of dice along one side of the board. */
    public static final int sideLength() {
        return (int) Math.sqrt(Boggle.totalDice);
    }
    
    //TODO why has this class got responsibility for this?  Should be UI probs.
    /** Set the number of dice being used on this machine. */
    public static void setTotalDice(int totalDice) {
        Boggle.debug("Setting totalDice to " + totalDice + ", was: " 
            + Boggle.totalDice);
        Boggle.totalDice = totalDice;
    }
    
    /**
     * Displays the specified error message to the user.
     *
     * @param   msg Message to display.
     */
    public static void problem(String msg) {
        Boggle.player.problem(msg);
    }
    
    /**
     * Writes the specified message to stdout if in debug mode.  A line 
     * separator is appended.
     *
     * @param   msg Message to write.
     */
    public static void debug(String msg) {
        if (Boggle.DEBUG) System.out.println(msg);
    }
    
    /** Writes a blank line to stdout if in debug mode. */
    public static void debug() {
        if (Boggle.DEBUG) System.out.println();
    }
    
    /**
     * Writes the specified char to stdout if in debug mode.  No line separator 
     * is appended.
     *
     * @param   c   Message to write.
     */
    public static void debug(char c) {
        if (Boggle.DEBUG) System.out.print(c);
    }
    
    /**
     * Prints a text representation of the specified letters if in debug mode.
     *
     * @param   letters Letters to print.
     */
    public static void debugBoard(char[][] letters) {
        for (int x = 0; x < letters.length; x++) {
            for (int y = 0; y < letters[0].length; y++) {
                Boggle.debug(letters[x][y]);
            }
            Boggle.debug();
        }
    }
    
    //TODO could know install dir.
    //TODO will this be a jar at some point and thus this is wrong?
    private static void loadDictionary() {

        String errMsg = "Could not find a dictionary.\nYou will be "
            + "unable to host a game but may still participate.\nTo "
            + "correct this problem please supply a dictionary at "
            + Boggle.DICT_PATH;
        try {
            InputStream is = Boggle.class.getResourceAsStream(Boggle.DICT_PATH);
            Boggle.dictionary = new Dictionary(is);
        } catch (IOException e) {
            //TODO
            Boggle.debug(e.toString());
            player.problem(errMsg);
        }
    }
    
    /**
     * Copies a <code>boolean[][]</code>.
     *
     * @param   orig    <code>boolean[][]</code> to copy.
     * @return          A copy of <code>orig</code>.
     */
    public static boolean[][] copy(boolean[][] orig) {
        //TODO Put this into a superclass/shared class for WordFinder and 
        // BoardChecker instead of having it here which is silly.
        boolean[][] copy = new boolean[orig.length][orig[0].length];
        for (int x = 0; x < orig.length; x++) {
            for (int y = 0; y < orig.length; y++) {
                copy[x][y] = orig[x][y];
            }
        }
        return copy;
    }
}
