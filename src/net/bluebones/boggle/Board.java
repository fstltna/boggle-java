/*
 * Board
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

//TODO Could do some setLabelFor on our JLabels

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


//TODO DieFactory?

/**
 * Graphical board for a game of Boggle.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $, 2004-09-04
 */
public class Board extends JPanel {
    
    //TODO size of dice should vary according to window size.  Rerendering as 
    // they go or not?
    private Theme theme;
    private char[][] letters 
        = new char[Boggle.sideLength()][Boggle.sideLength()];
    
    private JLabel[][] dieFaces
        = new JLabel[Boggle.sideLength()][Boggle.sideLength()];

    /** Initialises an empty board. */
    public Board() {
        Boggle.debug("Initializing board.");
        for (int i = 0; i < this.letters.length; i++) {
            Arrays.fill(letters[i], ' ');
        }
        this.theme = Theme.getCurrentTheme();
        setTheme(this.theme);
    }

    /**
     * Sets up this board with the specified chars showing.
     *
     * @param   letters <code>char</code>s to show face up in the diagram.
     */
    public void setBoard(char[][] letters) {
        this.letters = letters;
        for (int x = 0; x < letters.length; x++) {
            for (int y = 0; y < letters[0].length; y++) {
                setDie(x, y, letters[x][y]);
            }
        }
    }
    
    private void setDie(int x, int y, char c) {
        dieFaces[x][y].setIcon(getIcon(c));
    }
    
    private Icon getIcon(char c) {
        //TODO blank dice should be forced grey?
        if (c == 'q' || c == 'Q') {
            return DieIcon.getDie("Qu", this.theme);
        } else {
            return DieIcon.getDie(String.valueOf(c).toUpperCase(), 
                this.theme);
        }
    }
    
    /**
     * Runnable test version of this class.
     *
     * @param   args    Commandline arguments.  Ignored.
     */
    public static void main(String[] args) {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        Board b = new Board();
        //TODO could start actual board like this?
        char[][] letters 
            = new char[Boggle.sideLength()][Boggle.sideLength()];
        for (int x = 0; x < letters.length; x++) {
            for (int y = 0; y < letters[0].length; y++) {
                letters[x][y] = (char) ((x * letters.length) + y + 65);
            }
        }
        b.setBoard(letters);
        frame.getContentPane().add(b);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Sets the board to display <code>newTheme</code>.
     *
     * param    newTheme    <code>Theme</code> to display.
     */
    public void setTheme(Theme newTheme) {

        initBoard();

        Boggle.debug("Doing setTheme with size of " + newTheme.diceSize());
    
        for (int x = 0; x < dieFaces.length; x++) {
            for (int y = 0; y < dieFaces[0].length; y++) {
                dieFaces[x][y].setPreferredSize(
                    new Dimension(newTheme.diceSize(), 
                        newTheme.diceSize()));
                dieFaces[x][y].repaint();
                setDie(x, y, ' ');
            }
        }

        this.theme = newTheme;
        setBoard(this.letters);
    }
    
    private void initBoard() {
        
        GridLayout layout
            = new GridLayout(Boggle.sideLength(), Boggle.sideLength());
        this.setLayout(layout);
        
        for (int x = 0; x < dieFaces.length; x++) {
            for (int y = 0; y < dieFaces[0].length; y++) {
                if (dieFaces[x][y] != null) {
                    this.remove(dieFaces[x][y]);
                }
                dieFaces[x][y] = new JLabel();
                dieFaces[x][y].setBorder(
                    BorderFactory.createRaisedBevelBorder());
                this.add(dieFaces[x][y]);
            }
        }
    }
    
    /**
     * Gets the theme currently displayed.
     *
     * @return  Current <code>Theme</code>.
     */
    public Theme getTheme() {
        return this.theme;
    }
    
    /**
     * Gets a <code>String</code> representation of this board.
     *
     * @return  <code>String</code> representation of this board.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(12);
        final String BR  = System.getProperty("line.separator");
        for (int x = 0; x < letters.length; x++) {
            for (int y = 0; y < letters[0].length; y++) {
                sb.append(letters[x][y] + BR);
            }
            sb.append(BR);
        }
        return sb.toString();
    }
    //TODO should there be a logical board and a graphical one?  logical is just 
    // the char[][] I suppose.
}
