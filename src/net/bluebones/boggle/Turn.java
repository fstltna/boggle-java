/*
 * Turn
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

package net.bluebones.boggle;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents the result of a round for a given player.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Turn implements Serializable {
    
    private boolean marked = false;
    private Set<String> words = new HashSet<String>();
    private Set<String> uniqueWords = new HashSet<String>();
    private Set<String> duplicateWords = new HashSet<String>();
    private Set<String> misspeltWords = new HashSet<String>();
    private Set<String> notOnBoardWords = new HashSet<String>();
    private Set<String> tooShortWords = new HashSet<String>();
    private NamedClient namedClient;
    
    /**
     * Initialises a turn with the specified values.
     *
     * @param   namedClient   NamedClient whose turn this is.
     * @param   words           Words that this client came up with this turn.
     */
    public Turn(NamedClient namedClient, String[] words) {
        //TODO could check on the client for validity of words (could use same 
        // BoardChecker class and beep on entry if not correct).
        this.namedClient = namedClient;
        for (int i = 0; i < words.length; i++) {
            this.words.add(words[i]);
        }
    }
    
    /**
     * Gets the score that this Turn is worth.
     *
     * @return                          This Turn's score.
     * @throws  IllegalStateException   If this method is called before the Turn 
     *                                  has been marked (by Round).
     * @see     Round
     */
    public int score() throws IllegalStateException {
        if (! this.hasBeenMarked()) {
            throw new IllegalStateException(
                "Tried to get the score of a turn that hasn't been marked.");
        }
        int score = 0;
        //TODO must also check word is legal on the board.
        for (Iterator iter = uniqueWords.iterator(); iter.hasNext(); ) {
            String word = (String) iter.next();
            //TODO tie this in with Help, Scoring so they both come from the 
            // same place.
            if (word.length() == 3 || word.length() == 4) {
                score += 1;
            } else if (word.length() == 5) {
                score += 2;
            } else if (word.length() == 6) {
                score += 3;
            } else if (word.length() == 7) {
                score += 5;
            } else if (word.length() >= 8) {
                score += 11;
            }
        }
        return score;
    }
    
    /**
     * Whether this Turn has been marked yet (by Round).
     *
     * @return  boolean of whether this Turn has been marked yet.
     * @see     Round
     */
    public boolean hasBeenMarked() {
        return this.marked;
    }
    
    /**
     * Sets whether this turn has been marked or not.
     *
     * @param   value   boolean to set whether marked (true) or not (false).
     */
    public void setMarked(boolean value) {
        this.marked = value;
    }
    
    /**
     * Gets the unique words in this Turn.
     *
     * @return                          Set of Strings of the unique words.
     * @throws  IllegalStateException   If this method is called before the Turn 
     *                                  has been marked (by Round).
     * @see     Round
     */
    public Set getUniqueWords() {
        if (! hasBeenMarked()) {
            throw new IllegalStateException("Cannot get unique words "
                + "because this Turn has not been marked");
        }
        return this.uniqueWords;
    }
    
    /**
     * Gets the duplicate words in this Turn.
     *
     * @return                          Set of Strings of the duplicate words.
     * @throws  IllegalStateException   If this method is called before the Turn 
     *                                  has been marked (by Round).
     * @see     Round
     */
    public Set getDuplicateWords() {
        if (! hasBeenMarked()) {
            throw new IllegalStateException("Cannot get duplicate words "
                + "because this Turn has not been marked");
        }
        return this.duplicateWords;
    }
    
    /**
     * Gets the misspelt words in this Turn.
     *
     * @return                          Set of Strings of the misspelt words.
     * @throws  IllegalStateException   If this method is called before the Turn 
     *                                  has been marked (by Round).
     * @see     Round
     */
    public Set getMisspeltWords() {
        if (! hasBeenMarked()) {
            throw new IllegalStateException("Cannot get misspelt words "
                + "because this Turn has not been marked");
        }
        return this.misspeltWords;
    }
    
    /**
     * Gets the too short words in this Turn.
     *
     * @return                          Set of Strings of the too short words.
     * @throws  IllegalStateException   If this method is called before the Turn 
     *                                  has been marked (by Round).
     * @see     Round
     */
    public Set getTooShortWords() {
        if (! hasBeenMarked()) {
            throw new IllegalStateException("Cannot get too short words "
                + "because this Turn has not been marked");
        }
        return this.tooShortWords;
    }
    
    /**
     * Gets the not on board words in this Turn.
     *
     * @return                          Set of Strings of the not on board 
     *                                  words.
     * @throws  IllegalStateException   If this method is called before the Turn 
     *                                  has been marked (by Round).
     * @see     Round
     */
    public Set getNotOnBoardWords() {
        if (! hasBeenMarked()) {
            throw new IllegalStateException("Cannot get not on board words "
                + "because this Turn has not been marked");
        }
        return this.notOnBoardWords;
    }
    
    /**
     * Adds a unique word to the unique words held by this Turn.
     *
     * @param   word    Word to add.
     */
    public void addUniqueWord(String word) {
        this.uniqueWords.add(word);
    }
    
    /**
     * Adds a word to the duplicate words held by this Turn.
     *
     * @param   word    Word to add.
     */
    public void addDuplicateWord(String word) {
        this.duplicateWords.add(word);
    }
    
    /**
     * Adds a word to the misspelt words held by this Turn.
     *
     * @param    word    Word to add.
     */
    public void addMisspeltWord(String word) {
        this.misspeltWords.add(word);
    }
    
    /**
     * Add the specified word to the list of words that were not on the board.
     *
     * @param   word    <code>String</code> word to add.
     */
    public void addNotOnBoardWord(String word) {
        this.notOnBoardWords.add(word);
    }
    
    /**
     * Add the specified word to the list of words that were too short to add.
     *
     * @param   word    <code>String</code> word to add.
     */
    public void addTooShortWord(String word) {
        this.tooShortWords.add(word);
    }
    
    /**
     * Gets all the words that form part of this Turn.
     *
     * @return  Array of all words in this Turn.
     */
    public String[] getWords() {
        return (String[]) this.words.toArray(new String[this.words.size()]);
    }
    
    /**
     * Gets the NamedClient whose turn this is.
     *
     * @return  NamedClient whose turn this is.
     */
    public NamedClient namedClient() {
        return this.namedClient;
    }
}
