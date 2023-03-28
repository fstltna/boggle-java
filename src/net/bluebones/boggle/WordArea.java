/*
 * WordArea
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Panel that allows entry of new words and displays words already entered.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class WordArea extends JPanel {
    
    //TODO some kind of display when you enter a word you already have
    
    private JTextArea list;
    private Set<String> words = new HashSet<String>();
    private JTextField wordEntry;

    /** Initialises a new WordArea. */
    public WordArea() {
        super();
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel wordEntryLabel = new JLabel("Enter words:");
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 10, 0, 0);
        c.anchor = GridBagConstraints.SOUTHWEST;
        layout.setConstraints(wordEntryLabel, c);
        this.add(wordEntryLabel);
        
        wordEntry = new JTextField(Boggle.totalDice());
        wordEntry.setEditable(false);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 10;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 10, 10, 10);
        layout.setConstraints(wordEntry, c);
        wordEntry.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addWord(wordEntry.getText().trim());
            }
        });
            
        this.add(wordEntry);
        
        JLabel listLabel = new JLabel("Words So Far:");
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 10, 0, 0);
        c.anchor = GridBagConstraints.SOUTHWEST;
        layout.setConstraints(listLabel, c);
        this.add(listLabel);
        
        JScrollPane listScrollPane = new JScrollPane();
        listScrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        list = new JTextArea();
        final int DEFAULT_WORD_ROWS = 25;
        list.setRows(DEFAULT_WORD_ROWS);
        list.setEditable(false);
        listScrollPane.getViewport().add(list);

        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 10, 10, 10);
        c.gridheight = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        layout.setConstraints(listScrollPane, c);

        this.add(listScrollPane);
    }
    
    //XXX docs
    public void ready() {
        this.wordEntry.requestFocusInWindow();
    }
    
    /**
     * Gets the words currently displayed in this WordArea's list.
     *
     * @return  String[] of words.
     */
    public String[] getWords() {
        return (String[]) (words.toArray(new String[words.size()]));
    }
    
    private void addWord(String word) {
        if ((! words.contains(word)) && (! word.equals(""))) {
            this.words.add(word);
            this.list.setText(list.getText() + word 
                + System.getProperty("line.separator"));
        }
        this.wordEntry.setText("");
    }
    
    /** Empties the word entry box and word list. */
    public void clear() {
        this.wordEntry.setText("");
        this.list.setText("");
        this.words.clear();
    }
    
    /**
     * Sets whether the word entry box accepts text or not.
     *
     * @param   b   Wheter the word entry box is to accept text or not.
     */
    public void setEditable(boolean b) {
        this.wordEntry.setEditable(b);
    }
}
