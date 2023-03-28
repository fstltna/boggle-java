/*
 * AIDialog
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-24
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

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Dialog box that allows the addition of AI players to the current game.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class AIDialog extends BoggleDialog implements ActionListener {

    private Player parentPlayer;
    private JComboBox aiComboBox;

    /**
     * Initializes an <code>AIDialog</code>.
     *
     * @param   parentPlayer    The <code>Player</code> that requested this 
     *                          dialog.
     * @param   owner           The <code>Frame</code> this dialog should be
     *                          centered in.
     */
    public AIDialog(Player parentPlayer, Frame owner) {
        this.setTitle("Add Computer Opponent");
        this.getContentPane().setLayout(new GridLayout(2, 1));
        this.parentPlayer = parentPlayer;
        this.getContentPane().add(getMainPanel());
        this.getContentPane().add(getButtonPanel(this));
        this.setResizable(false);
        this.pack();
        setLocationRelativeTo(owner);
        this.setVisible(true);
    }
    
    /**
     * Displays a new <code>AIDialog</code>.
     *
     * @param   parentPlayer    The <code>Player</code> that requested this 
     *                          dialog.
     * @param   owner           The <code>Frame</code> this dialog should be
     *                          centered in.
     */
    public static void showDialog(Player parentPlayer, Frame owner) {
        //TODO should return the AI or something instead of void?
        new AIDialog(parentPlayer, owner);
    }
    
    private JPanel getMainPanel() {
        JPanel panel = new JPanel();
        AIPlayer[] ais = AIPlayer.getAIPlayers(this.parentPlayer);
        aiComboBox = new JComboBox(ais);
        panel.add(aiComboBox);
        return panel;
    }
    
    /**
     * Called when any action is performed in this dialog (that is, when a 
     * button is clicked).
     *
     * @param   e   <code>ActionEvent</code> generated.
     */
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            AIPlayer ai = (AIPlayer) aiComboBox.getSelectedItem();
            //TODO AIs must have better names
            ai.joinGame(new GameInfo(ai.toString(), 
                this.parentPlayer.getHostInfo()));
        }

        this.setVisible(false);
    }
}
