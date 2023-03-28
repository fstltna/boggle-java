/*
 * Round
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-10
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

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a round of Boggle as seen by the server.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Round implements Serializable {

    private Set<String> uniqueWords = new HashSet<String>();
    private Set<String> duplicateWords = new HashSet<String>();
    private Set<String> misspeltWords = new HashSet<String>();
    private Set<String> tooShortWords = new HashSet<String>();
    private Set<String> notOnBoardWords = new HashSet<String>();
    private Turn[] turns;
    private char[][] letters;
    
    /**
     * Initialises a round with the specified values.
     *
     * @param   turns                           Client turns that are part of 
     *                                          this round.
     * @param   letters                         Letters face up on the board 
     *                                          this round.
     * @throws  DictionaryUnavailableException If not dictionary is available 
     *                                          to validate words.
     */
    public Round(Turn[] turns, char[][] letters) 
            throws DictionaryUnavailableException {
        this.turns = turns;
        this.letters = letters;
        sortWords();
        markTurns();
    }
    
    private void sortWords() {
        
        BoardChecker boardChecker = new BoardChecker(this.letters);
        
        for (int i = 0; i < this.turns.length; i++) {
            String[] words = this.turns[i].getWords();
            for (int j = 0; j < words.length; j++) {
                if (! boardChecker.checkWord(words[j])) {
                    this.notOnBoardWords.add(words[j]);
                } else if ((words[j].length() < 3)) {
                    this.tooShortWords.add(words[j]);
                } else if (duplicateWords.contains(words[j])) {
                    // do nothing
                } else if (! uniqueWords.contains(words[j])) {
                    if (Boggle.dictionary.isValid(words[j])) {
                        this.uniqueWords.add(words[j]);
                    } else {
                        this.misspeltWords.add(words[j]);
                    }
                } else {
                    this.uniqueWords.remove(words[j]);
                    this.duplicateWords.add(words[j]);
                }
            }
        }
    }
    
    private void markTurns() {
        for (int i = 0; i < this.turns.length; i++) {
            String[] words = this.turns[i].getWords();
            for (int j = 0; j < words.length; j++) {
                if (notOnBoardWords.contains(words[j])) {
                    turns[i].addNotOnBoardWord(words[j]);
                } else if (tooShortWords.contains(words[j])) {
                    turns[i].addTooShortWord(words[j]);
                } else if (misspeltWords.contains(words[j])) {
                    turns[i].addMisspeltWord(words[j]);
                } else if (duplicateWords.contains(words[j])) {
                    turns[i].addDuplicateWord(words[j]);
                } else if (uniqueWords.contains(words[j])) {
                    turns[i].addUniqueWord(words[j]);
                } else {
                    //TODO catch this somewhere?
                    throw new IllegalStateException("Word " + words[j] 
                        + " does not fit into any of the categories.  This is "
                        + "a can't happen error.");
                }
            }
            turns[i].setMarked(true);
        }
    }
    
    /**
     * Gets all the client turns that make up this round.
     *
     * @return  Array of client turns.
     */
    public Turn[] getTurns() {
        return this.turns;
    }
    
    //TODO inappropriate too many dict unvail throwing - it's not so important 
    // now that we check it at the beginning?
    /**
     * Gets a <code>Round</code> to be used in tests.  <code>public</code> so 
     * that tests in other classes can use it.
     *
     * @return  <code>Round</code>
     * @throws  DictionaryUnavailableException  If the dictionary is 
     *                                          unavailable to higher level 
     *                                          calls.
     * @throws  FileNotFoundException           If the dictionary file is 
     *                                          missing initially.
     * @throws  IOException                     If there is a problem reading 
     *                                          the dictionary file initially.
     */
    public static Round getTestRound() throws DictionaryUnavailableException,
            java.io.FileNotFoundException, IOException {
        
        // TODO this test will no longer work.
        Boggle.dictionary = new Dictionary(
            new java.io.File("resources/dict.txt"));
        
        String[][] words = new String[3][5];
        words[0] = new String[] { "one", "fjdlksa", "funky", "cow", "six" };
        words[1] = new String[] { "one", "double", "funky", "bell", "duck", 
            "it", "penny", "lend", "dune", "wok", "con", "bend", "be" };
        words[2] = new String[] { "one", "bliadfakjl", "magic", "done",
            "buck", "nob", "funk", "cow" };
        Turn[] turns = new Turn[] { 
            new Turn(new NamedClient("Player A", null), words[0]), 
            new Turn(new NamedClient("Player B", null), words[1]), 
            new Turn(new NamedClient("Player C", null), words[2])
        };
        
        char[][] letters = new char[][] {
            new char[] { 'O', 'N', 'E', 'L', 'X' },
            new char[] { 'D', 'U', 'B', 'L', 'X' },
            new char[] { 'F', 'C', 'O', 'W', 'X' },
            new char[] { 'U', 'N', 'K', 'Y', 'X' },
            new char[] { 'X', 'X', 'X', 'X', 'X' }
        };
        
        //TODO This has side effects (alters turn).  Is there a better way?
        return new Round(turns, letters);
    }
        
    
    /**
     * A fairly simple test of <code>Round</code> that can be fired from the 
     * commandline.
     *
     * @param   args                            Commandline arguments.
     * @throws  DictionaryUnavailableException If there is a problem with the 
     *                                          dictionary.
     * @throws  RemoteException                 If there is any problem setting 
     *                                          up clients.
     * @throws  UnknownHostException            If clients cannot detect local 
     *                                          ip.
     */
    public static void main(String[] args) 
            throws DictionaryUnavailableException, RemoteException, 
            UnknownHostException, java.io.FileNotFoundException,
            java.io.IOException {

        Round r = Round.getTestRound();
        Turn[] turns = r.getTurns();
        
        for (int i = 0; i < turns.length; i++) {
            System.out.println(turns[i].namedClient().name() + " scores "
                + turns[i].score());
            System.out.println("Unique words are: " 
                + turns[i].getUniqueWords());
                System.out.println("Duplicate words are: " 
                + turns[i].getDuplicateWords());
            System.out.println("Misspelt words are: " 
                + turns[i].getMisspeltWords());
            System.out.println("Too short words are: " 
                + turns[i].getTooShortWords());
            System.out.println("Not on board words are: " 
                + turns[i].getNotOnBoardWords());
            System.out.println();
        }
    }
}
