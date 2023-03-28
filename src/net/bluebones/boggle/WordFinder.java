/*
 * WordFinder
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-22
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

/**
 * Finds words up <code>longestWord</code> in length for the specified player. 
 * Implements Runnable so searches can be done without impacting responsiveness 
 * of the game.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class WordFinder implements Runnable {

    private boolean finding = true;
    private AIPlayer player;
    private char[][] letters;
    private int longestWord;

    /**
     * Initializes a new <code>WordFinder</code>.
     *
     * @param   player                          <code>AIPlayer</code> that this 
     *                                          <code>WordFinder</code> belongs 
     *                                          to.
     * @param   letters                         <code>char[][]</code> of the 
     *                                          board to find words in.
     * @param   longestWord                     <code>int</code> length of 
     *                                          longest word to find.
     * @throws  DictionaryUnavailableException  If no dictionary is available to 
     *                                          validate found words.
     */
    WordFinder(AIPlayer player, char[][] letters, int longestWord) 
            throws DictionaryUnavailableException {
        //TODO overload with no longest word param and set max val as default?
        this.player = player;
        this.letters = letters;
        this.longestWord = longestWord;
        //TODO should use their own, more limited dictionaries?
        // that could have tremendous memory implications for running
        // many AIs on one machine.  better to limit them by arbitrarily
        // denying them words or slowing them down.
    }
    
    /**
     * Tells this <code>WordFinder</code> to stop finding words (probably 
     * because it is the end of the round).
     */
    public void stopFinding() {
        finding = false;
    }
    
    /**
     * Performs the action of the word finder, finding words up to a certain
     * maximum length on the previously specified board.
     */
    public void run() {
        Boggle.debug("Started at " + System.currentTimeMillis());
        //Boggle.debug("Running WordFinder");
        boolean[][] used 
            = new boolean[this.letters.length][this.letters[0].length];
        for (int x = 0; x < this.letters.length; x++) {
            for (int y = 0; y < this.letters[0].length; y++) {
                //Boggle.debug("Calling findWord from run");
                findWord(x, y, "", Boggle.copy(used)); 
            }
        }
        Boggle.debug("Finished at " + System.currentTimeMillis());
    }
    
    private void findWord(int posX, int posY, String oldWord, 
            boolean[][] used) {
        final int MINIMUM_WORD_LENGTH = 3;
        used[posX][posY] = true;
        String word = oldWord + this.letters[posX][posY];
        if (this.letters[posX][posY] == 'Q') {
            word += "U";
        }
        if (this.finding && word.length() >= MINIMUM_WORD_LENGTH 
                && Boggle.dictionary.isValid(word)) {
            this.player.addWord(word);
        }
        //TODO AI could have a dictionary of words it 
        // "knows" and check if it /could/ be working towards any of them and 
        // short circuit if not.
        
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int newX = x + posX;
                int newY = y + posY;
                boolean offBoard = ((newX < 0) || (newX >= this.letters.length) 
                    || (newY < 0) || (newY >= this.letters[0].length));
                if ((finding) && (! offBoard) && (! used[newX][newY]) 
                        && word.length() < this.longestWord) {
                    //Boggle.debug("Calling findWord from findWord.");
                    findWord(newX, newY, word, Boggle.copy(used));
                }
            }
        }
        
        //TODO AI players do unnecessary work because they use what is 
        // effectively the "HumanClient".  Do they need their own streamlined 
        // version that doesn't generate score info?  MORE likely this shows the 
        // Client does two jobs -- split into two classes one of which AI has 
        // and one it doesn't.
    }
    
    //TODO hard to do a test here because need a human player for AI which means 
    // the whole game starting up.  Does that show too tight coupling?  Perhaps 
    // AI player needs the UI and not the whole player?  Or even something 
    // smaller that allows access to problem.  And then pass that in here.
}
