/*
 * Die
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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Boggle die (with letters on each face).
 *
 * @author  bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $, 2004-09-04
 */
public class Die {
    
    private char[] faces;
        
    /** The set of dice used to play the 4x4 game. */
    public static Die[] dice16 = new Die[] {
        new Die(new char[] { 'A', 'E', 'A', 'N', 'E', 'G' }),
        new Die(new char[] { 'A', 'H', 'S', 'P', 'C', 'O' }),
        new Die(new char[] { 'A', 'S', 'P', 'F', 'F', 'K' }),
        new Die(new char[] { 'O', 'B', 'J', 'O', 'A', 'B' }),
        new Die(new char[] { 'I', 'O', 'T', 'M', 'U', 'C' }),
        new Die(new char[] { 'R', 'Y', 'V', 'D', 'E', 'L' }),
        new Die(new char[] { 'L', 'R', 'E', 'I', 'X', 'D' }),
        new Die(new char[] { 'E', 'I', 'U', 'N', 'E', 'S' }),
        new Die(new char[] { 'W', 'N', 'G', 'E', 'E', 'H' }),
        new Die(new char[] { 'L', 'N', 'H', 'N', 'R', 'Z' }),
        new Die(new char[] { 'T', 'S', 'T', 'I', 'Y', 'D' }),
        new Die(new char[] { 'O', 'W', 'T', 'O', 'A', 'T' }),
        new Die(new char[] { 'E', 'R', 'T', 'T', 'Y', 'L' }),
        new Die(new char[] { 'T', 'O', 'E', 'S', 'S', 'I' }),
        new Die(new char[] { 'T', 'E', 'R', 'W', 'H', 'V' }),

        new Die(new char[] { 'N', 'U', 'I', 'H', 'M', 'Q' })
    };
    
    /** The set of dice used to play the 5x5 game. */
    public static Die[] dice25 = new Die[] {
        new Die(new char[] { 'E', 'T', 'I', 'L', 'C', 'I' }),
        new Die(new char[] { 'M', 'G', 'A', 'E', 'U', 'E' }),
        new Die(new char[] { 'E', 'A', 'E', 'A', 'E', 'E' }),
        new Die(new char[] { 'D', 'N', 'A', 'N', 'E', 'N' }),
        new Die(new char[] { 'T', 'M', 'T', 'E', 'T', 'O' }),
        new Die(new char[] { 'A', 'A', 'A', 'F', 'R', 'S' }),
        new Die(new char[] { 'T', 'C', 'S', 'N', 'W', 'C' }),
        new Die(new char[] { 'S', 'S', 'N', 'S', 'U', 'E' }),
        new Die(new char[] { 'E', 'M', 'E', 'A', 'E', 'E' }),
        new Die(new char[] { 'E', 'G', 'N', 'A', 'N', 'M' }),
        new Die(new char[] { 'T', 'E', 'T', 'I', 'I', 'I' }),
        new Die(new char[] { 'D', 'H', 'O', 'R', 'H', 'L' }),
        new Die(new char[] { 'S', 'P', 'T', 'E', 'I', 'C' }),
        new Die(new char[] { 'D', 'O', 'R', 'D', 'N', 'L' }),
        new Die(new char[] { 'H', 'O', 'T', 'H', 'N', 'D' }),
        new Die(new char[] { 'Y', 'I', 'R', 'P', 'R', 'H' }),
        new Die(new char[] { 'F', 'R', 'Y', 'S', 'I', 'A' }),
        new Die(new char[] { 'T', 'O', 'O', 'O', 'U', 'T' }),
        new Die(new char[] { 'N', 'O', 'W', 'O', 'T', 'U' }),
        new Die(new char[] { 'P', 'C', 'E', 'I', 'T', 'L' }),
        new Die(new char[] { 'A', 'S', 'A', 'R', 'I', 'F' }),
        new Die(new char[] { 'R', 'F', 'S', 'Y', 'P', 'I' }),
        new Die(new char[] { 'O', 'H', 'D', 'R', 'L', 'N' }),
        new Die(new char[] { 'K', 'Q', 'X', 'Z', 'J', 'B' }),
        new Die(new char[] { 'W', 'G', 'O', 'R', 'R', 'V' })
    };
    
    /* The set of dice used to play the game. */
    public static Die[] dice = dice16;
          
    /** 
     * Initialises this die with the specified faces.
     *
     * @param   faces   chars that appear on this die's faces.  Any number of 
     *                  faces is allowed and all dice in the game need not have
     *                  the same number.
     */
    public Die(char[] faces) {
        this.faces = faces;
    }
    
    /**
     * Gets the char that is on the specified face of this die.
     *
     * @param   face    The face to get the char from.
     * @return          char that appears on the specified face.
     */
    public char getFace(int face) {
        return faces[face];
    }
    
    /**
     * "Shakes" the supplied dice into a random order.
     *
     * @param   dice    Dice to shake.
     * @return          Array of dice in a random order.
     */
    public static Die[][] rollDice(Die[] dice) {
        // Setup list of dice.
        List<Die> left = new ArrayList<Die>();
        for (int i = 0; i < dice.length; i++) {
            left.add(dice[i]);
        }

        // Roll the dice.
        Die[][] rolledDice = new Die[Boggle.sideLength()][Boggle.sideLength()];
        while (left.size() > 0) {
            int dieNum = (int) (Math.random() * left.size() - 1);
            int x = (left.size() - 1) % Boggle.sideLength();
            int y = (left.size() - 1) / Boggle.sideLength();
            rolledDice[x][y] = (Die) left.get(dieNum);
            left.remove(dieNum);
        }
        return rolledDice;
    }
    
    /**
     * Gets a face from this die at random (simulates rolling it).
     *
     * @return  char value found on a random face of this die.
     */
    public char getRandomFace() {
        return this.getFace(((int) (Math.random() * this.faces.length)));
    }
}
