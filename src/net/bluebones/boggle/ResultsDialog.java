/*
 * ResultsDialog
 *
 * Thomas David  Baker <bakert@gmail.com>, 2004-09-23
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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 * Dialog box that displays the results of a round.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class ResultsDialog extends JFrame implements ActionListener {

    private static ResultsDialog dialog;

    ResultsDialog(Frame owner, String results) {
        this.setTitle("Results");
        //TODO catch null pointers for these?
        this.setIconImage(new ImageIcon(ClassLoader.getSystemResource(
            "resources/images/icon.png")).getImage());

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.getContentPane().setLayout(layout);

        //TODO JEditorPane help section

        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        JTextArea resultsArea = new JTextArea(results);
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        resultsArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        this.getContentPane().add(scrollPane);
        this.getContentPane().add(okButton);
        scrollPane.setPreferredSize(new Dimension(480, 360));
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        layout.setConstraints(scrollPane, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        layout.setConstraints(okButton, c);
        
        this.pack();
        setLocationRelativeTo(owner);
        this.setVisible(true);
    }
    
    /**
     * Display a <code>ResultsDialog</code> over <code>owner</code> showing 
     * <code>results</code>.
     *
     * @param   owner   <code>Frame</code> to display this dialog over.
     * @param   results <code>String</code> of results to display.
     */
    public static void showDialog(Frame owner, String results) {
        dialog = new ResultsDialog(owner, results);
        dialog.setVisible(true);
    }
    
    /**
     * Handles clicks on the OK button.
     *
     * @param   e   Action event that has occurred.
     */
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }
    
    /**
     * Called by the constructor methods to create the default rootPane.  
     * Overridden here to allow Esc to cancel the dialog.
     *
     * @return  Default rootPane.
     */
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        rootPane.registerKeyboardAction(actionListener, stroke,
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
        
    /**
     * Simple test of <code>ResultsDialog</code>.
     *
     * @param   args    <code>String[]</code>.  Commandline arguments.  Ignored.
     */
    public static void main(String[] args) {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        ResultsDialog.showDialog(frame, "Test results");
    }
}
