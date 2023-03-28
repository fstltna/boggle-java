/*
 * ScorePanel
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-15
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

//XXX  right clicking should select the row under the cursor too.

package net.bluebones.boggle;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Graphical panel for display of cumulative scores in a rubber.
 *
 * @author  Thomas David Baker, bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class ScorePanel extends JPanel {

    //TODO this is now badly named - more a player panel

    private JTable scoreTable = new JTable();
    private ScoreTableModel tableModel;
    private NamedClient[] selectedClients = new NamedClient[0];
    final HumanPlayer player;
    private final JPopupMenu rightClickMenu = new JPopupMenu();
    
    /**
     * Initializes a new <code>ScorePanel</code> for the specified 
     * <code>HumanPlayer</code>.
     */
    public ScorePanel(final HumanPlayer player) {
        
        this.player = player;
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(layout);
        
        //TODO Del key with row highlighted removes player (with confirm 
        // dialog?)
        
        this.scoreTable.setColumnSelectionAllowed(false);
        this.scoreTable.getTableHeader().setReorderingAllowed(false);
        this.scoreTable.setDragEnabled(false);
        JScrollPane scrollPane = new JScrollPane(this.scoreTable);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        layout.setConstraints(scrollPane, c);

        //TODO could do a popup window with loads of detail here
        JMenuItem remove = new JMenuItem("Remove");
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeCurrentlySelectedClients();
            }
        });
        rightClickMenu.add(remove);
        this.add(scrollPane);

        this.scoreTable.addMouseListener(new ScorePanelMouseListener());

        setRubber(null);
    }
    
    /**
     * Sets the specified rubber details to be displayed in this 
     * <code>ScorePanel</code>.
     *
     * @param   r   <code>Rubber</code> to display.
     */
    public void setRubber(Rubber r) {
        tableModel = new ScoreTableModel(r);
        this.scoreTable.setModel(tableModel);
        this.scoreTable.setPreferredScrollableViewportSize(
            new Dimension(200, 
            (this.scoreTable.getRowCount()) 
            * this.scoreTable.getRowHeight()));
    }
    
    private void removeCurrentlySelectedClients() {
        for (int i = 0; i < selectedClients.length; i++) {
            Boggle.debug("Clicked remove.  Should remove " 
                +  selectedClients[i].name()
                + " possibly after a (single with all names) "
                + "confirmation dialog.");
        }
        //TODO pass them up as a set?
        this.player.removeClients(Arrays.asList(selectedClients));
    }
    
    /**
     * Simple commandline test of ScorePanel.
     *
     * @param   args    Commandline arguments.
     */
    public static void main(String[] args) 
            throws DictionaryUnavailableException, 

            java.io.FileNotFoundException, java.io.IOException {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        ScorePanel sp = new ScorePanel(new HumanPlayer());
        Rubber rubber = new Rubber();
        
        Round round = Round.getTestRound();

        rubber.addScores(round);
        rubber.addScores(round);
        sp.setRubber(rubber);
        frame.getContentPane().add(sp);
        frame.pack();
        frame.setVisible(true);
    }
    
    private class ScorePanelMouseListener extends MouseAdapter {
        
        /**
         * Called when a mouse button is released over the score table.
         *
         * @param   e   <code>MouseEvent</code> containing event details.
         */
        public void mouseReleased(MouseEvent e) {
            //TODO would be much better to show greyed out Remove 
            // instead of not having right-click menu.
            if (! player.isHost()) {
                return;
            }
            if (e.isPopupTrigger()) {
                int[] rows = scoreTable.getSelectedRows();
                selectedClients = new NamedClient[rows.length];
                for (int i = 0; i < rows.length; i++) {
                    Boggle.debug("Noting that we have right-clicked while " 
                        + i + ": " + tableModel.getValueAt(rows[i], 0) 
                        + " are selected.");
                    selectedClients[i] = tableModel.getClientAt(rows[i]);
                }
                rightClickMenu.show(e.getComponent(), e.getX(), 
                    e.getY());
            }
        }
    }

}
