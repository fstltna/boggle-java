/*
 * AIPlayer
 *
 * Thomas David  Baker <bakert@gmail.com>, 2004-09-22
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

package net.bluebones.boggle;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Computer opponent.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.6 $
 */
public class AIPlayer extends Player {

    private Set<String> words;
    private WordFinder wordFinder;
    private Player parentPlayer;
    private int rating;
    final static int STRONGEST = 999;
    final static int WEAKEST = 0;
    
    // TODO AIPlayer always starts in the top-right.  Best tactic against them
    // is to start bottom left.  Anything over a minute and they tend to finish
    // a 4x4 board so it doesn't really matter but would be nice to jump around
    // if we want to implement it some time.
    
    /*
     TODO AIs don't have individual names thus their error messages and info 
          messages are not personalised.  They don't do much of that though 
          so it probably doesn't matter.
    */

    /**
     * Initializes an <code>AIPlayer</code> that can find words of any length.
     *
     * @param   parentPlayer    The "owner" of this <code>AIPlayer</code>.
     */
    public AIPlayer(Player parentPlayer) {
        this(parentPlayer, AIPlayer.STRONGEST);
    }

    /**    
     * Initializes an <code>AIPlayer</code> with the specified rating.
     *
     * @param   parentPlayer    The "owner" of this <code>AIPlayer</code>.
     * @param   rating          The relative strenght of this 
     *                          <code>AIPlayer</code>.
     */
    public AIPlayer(Player parentPlayer, int rating) {
        this.parentPlayer = parentPlayer;
        this.rating = rating;
    }
    
    /**
     * Relays the specified message to the owner of this <code>AIPlayer</code>.
     *
     * @param   msg Message to relay.
     */
    public void info(String msg) {
        this.parentPlayer.info("AI: " + msg);
    }
    
    /**
     * Notifies the owner of this <code>AIPlayer</code> of the specified 
     * problem.
     *
     * @param   msg Message to relay.
     */
    public void problem(String msg) {
        this.parentPlayer.info("AI: " + msg);    
    }
    
    /** Does nothing (interface requirement). */
    public void startNetworkGame() {}

    //TODO needs to be a way to block IPs
   
    /**
     * Starts a local game.
     *
     * @param   letters Letters to initialize the board with.
     * @param   time    Duration of game in seconds.
     */
    public void startLocalGame(char[][] letters, int time) {
        Boggle.debug("AI has been asked to start a game with ");
        Boggle.debugBoard(letters);
        if (this.wordFinder != null) {
            this.wordFinder.stopFinding();
        }
        this.words = Collections.synchronizedSet(new HashSet<String>());
        Boggle.debug("Been to startLocalGame in AI");
        super.startLocalGame(letters, time);
        int longestWord = (rating / 100) + 3;
        try {
            this.wordFinder = new WordFinder(this, letters, longestWord);
        } catch (DictionaryUnavailableException e) {
            //TODO make sure all error messages offer a solution.
            String errMsg = "Cannot find dictionary for AI Player.";
            Boggle.debug(errMsg + "  Exception was: " + e);
            problem(errMsg);
        }
        Boggle.debug("Made it to thread start.");
        new Thread(wordFinder).start();                
    }
    
    /**
     * Adds <code>word</code> to the list of words this AI has found.
     *
     * @param   word    <code>String</code> word to add.
     */
    public void addWord(String word) {
        int difficulty = (word.length() - 3) * 10;
        Random r = new Random();
        if ((r.nextInt(AIPlayer.STRONGEST) + difficulty) <= this.rating) {
            this.words.add(word.toLowerCase());
        }
        try {
            Thread.sleep(AIPlayer.STRONGEST - this.rating);
        } catch (InterruptedException e) {
            Boggle.debug("That's interesting: " + e);
        }
    }

    /**
     * Gets the words this AI has found.
     *
     * @return  <code>String[]</code> of all words the AI has found this turn.
     */
    public String[] getWords() {
        return (String[]) this.words.toArray(new String[this.words.size()]);
    }
    
    /** Stops this AI finding words. */
    public void stopGame() {
        if (this.wordFinder != null) {
            this.wordFinder.stopFinding();
        }
    }
        
    /** Does nothing (interface requirement). */
    public void hostGame() {}

    /** Does nothing (interface requirement). */
    public void setBoard(char[][] letters) {}

    
    /** Does nothing (interface requirement). */
    public void setTimer(int amount) {}
    
    /** Does nothing (interface requirement). */
    public void init(char[][] letters, int timeInSecs) {}
    
    /** Does nothing (interface requirement). */
    public void setRubber(Rubber r) {}
    
    /** Does nothing (interface requirement). */
    public void setConnection(ConnectionType type, String addr, 
        int clients) {}
    
    /** Does nothing (interface requirement). */
    public void setConnection(ConnectionType type, String addr, 
        int clients, int dots) {}
    
    /** 
     * Does nothing (interface requirement).
     * 
     * @throws  IllegalArgumentException    If called with non-<code>null</code> 
     *                                      <code>server</code>.
     */
    public void setLocalServer(Server server) {
        //TODO should more of the do nothing methods throw exceptions if called?
        if (server != null) {
            throw new IllegalArgumentException("Cannot set local server in "
                + "AI Player - it doesn't know how to be a host.");
        }
    }
    
    /** Returns null as AI does not use a local server. */
    public IServer getLocalServer() { return null; }
    
    /** Does nothing (interface requirement). */
    public void results(Round round) {}
    
    /**
     * Creates a group of AI players to use as standard.
     *
     * @param   parentPlayer    The "owner" of these AIs.
     * @return                  <code>AIPlayer[]</code> of AIs.
     */
    public static AIPlayer[] getAIPlayers(Player parentPlayer) {
        //TODO standard AIs must have names.
        return new AIPlayer[] { 
            new AIPlayer(parentPlayer, 10),
            new AIPlayer(parentPlayer, 25),
            new AIPlayer(parentPlayer, 50),
            new AIPlayer(parentPlayer, 100),
            new AIPlayer(parentPlayer, 200),
            new AIPlayer(parentPlayer, 300),
            new AIPlayer(parentPlayer, 400),
            new AIPlayer(parentPlayer, 500),
            new AIPlayer(parentPlayer, 600),
            new AIPlayer(parentPlayer, 700),
            new AIPlayer(parentPlayer, 800),
            new AIPlayer(parentPlayer, 900),
            new AIPlayer(parentPlayer, 999)
        };
    }
    
    /**
     * Gets a <code>String</code> representation of this AI.
     *
     * @return  <code>String</code> representation.
     */
    public String toString() {
        return "AI (rating " + this.rating + ")";
    }
}
