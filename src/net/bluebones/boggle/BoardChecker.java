/*
 * BoardChecker
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-13
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

import java.util.Locale;

/**
 * Utility class that allows checking that words can be made from a given board
 * configuration.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class BoardChecker {

    private char[][] letters;
    // So much debug code in here that you don't want it even in debug mode 
    // usually so gave it its own switch.
    private static final boolean DEBUG = false;
    
    /**
     * Initialises a BoardChecker with the specified letters.
     *
     * @param   letters Letters to initialise the board with.
     */
    public BoardChecker(char[][] letters) {
        //TODO can use this code as the basis of an AI.
        this.letters = letters;
        Boggle.debugBoard(letters);
    }
    
    /**
     * Checks whether the specified word can be made from this board.
     *
     * @param   wordToCheck Word to look for on the board.
     * @return              Whether this word is on the board 
     *                      (<code>true</code>) or not (<code>false</code>).
     */
    public boolean checkWord(String wordToCheck) {
        // Q is a special case and actually represents the Qu face.
        char[] word = wordToCheck.toUpperCase(Locale.UK).replaceAll("QU", 
            "Q").toCharArray();
        //TODO create a coordinate class to use instead of int[] for boardPos?
        for (int x = 0; x < Boggle.sideLength(); x++) {
            for (int y = 0; y < Boggle.sideLength(); y++) {
                boolean[][] used
                    = new boolean[this.letters.length][this.letters[0].length];
                if (checkFrom(word, new int[] { x, y }, 0, 
                        Boggle.copy(used))) {
                    //Boggle.debug(wordToCheck + " is on the board.");
                    return true;
                } 
            }
        }
        Boggle.debug(wordToCheck + " is not on the board.");
        BoardChecker.debug("Fell through (orig)");
        return false;
    }

    //TODO this is confusing.  Refactor the whole thing.
    private boolean checkFrom(char[] word, int[] boardPos, int wordPos, 
            boolean[][] used) {
        String spacer = "";
        for (int i = 0; i < wordPos; i++) {
            spacer += "    ";
        }
        BoardChecker.debug(spacer + "Entering check from with wordPos = " 
            + wordPos + " and boardPos = " + boardPos[0] + ", " + boardPos[1]);
        if (used[boardPos[0]][boardPos[1]]) {
            BoardChecker.debug(spacer + "Have used this letter before (" 
                + this.letters[boardPos[0]][boardPos[1]] + ")");
            return false;
        }
        BoardChecker.debug(spacer + "wordPos is " + wordPos + " (" 
            + word[wordPos] + ") and word.length is " + word.length 
            + " and boardPos is " + boardPos[0] + ", " + boardPos[1] + " (" 
            + this.letters[boardPos[0]][boardPos[1]] + ")");
        if (this.letters[boardPos[0]][boardPos[1]] == word[wordPos]) {
            used[boardPos[0]][boardPos[1]] = true;
            BoardChecker.debug(spacer + "OK, " 
                + this.letters[boardPos[0]][boardPos[1]] + " == " 
                + word[wordPos]);
            if (wordPos >= word.length - 1) {
                return true;
            }
            for (int xAdd = -1; xAdd <= 1; xAdd++) {
                for (int yAdd = -1; yAdd <= 1; yAdd++) {
                    int x = boardPos[0] + xAdd;
                    int y = boardPos[1] + yAdd;
                    BoardChecker.debug(spacer + "Checking boardPos " 
                        + boardPos[0] + ", " + boardPos[1] + " (" 
                        + this.letters[boardPos[0]][boardPos[1]] + ") for " 
                        + word[wordPos + 1]);
                    if ((inBounds(x, y)) 
                            && (checkFrom(word, new int[] { x, y }, 
                            (wordPos + 1), Boggle.copy(used)))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean inBounds(int x, int y) {
        return (x >= 0 && x < Boggle.sideLength() 
                && y >= 0 && y < Boggle.sideLength());
    }
    
    /**
     * Simple commandline test of BoardChecker.
     *
     * @param   args    Commandline arguments.
     */
    public static void main(String[] args) {
        char[][] letters1 = new char[][] {
            new char[] { 'A', 'B', 'C', 'D' },
            new char[] { 'E', 'F', 'G', 'H' },
            new char[] { 'I', 'J', 'K', 'L' },
            new char[] { 'M', 'N', 'O', 'P' }
        };
        BoardChecker bc = new BoardChecker(letters1);
        System.out.println("Checking knife (true): " + bc.checkWord("knife"));
        System.out.println("Checking hello (false): " + bc.checkWord("hello"));
        System.out.println("Checking fab (true): " + bc.checkWord("fab"));
        System.out.println("Checking fin (true): " + bc.checkWord("fin"));
        System.out.println("Checking kop (true): " + bc.checkWord("kop"));
        System.out.println("Checking dgjmieabf (true): " 
            + bc.checkWord("dgjmieabf"));
        System.out.println("Checking jfdslka (false): " 
            + bc.checkWord("jfdslka"));
        System.out.println("Checking donkey (false): " 
            + bc.checkWord("donkey"));
        System.out.println("Checking pop (false): " + bc.checkWord("pop"));
        System.out.println("Checking afkplhdgjmiebc (true): " 
            + bc.checkWord("afkplhdgjmiebc"));
        System.out.println("Checking afkplhdgjmieabc (false): " 
            + bc.checkWord("afkplhdgjmieabc")); 
        char[][] letters2 = new char[][] {
            new char[] { 'L', 'I', 'V', 'P' },
            new char[] { 'O', 'E', 'E', 'A' },
            new char[] { 'B', 'B', 'N', 'A' },
            new char[] { 'R', 'H', 'G', 'S' }
        };
        bc = new BoardChecker(letters2);
        System.out.println("Checking bevel (true): " + bc.checkWord("bevel"));
    }
    
    private static void debug(String s) {
        if (BoardChecker.DEBUG) Boggle.debug(s);
    }
}
