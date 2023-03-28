/*
 * BoggleDialog
 * 
 * Thomas David Baker <bakert@gmail.com>, 2004-09-25
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * Default dialog for Boggle.  Designed for extension.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class BoggleDialog extends JDialog {
    
    BoggleDialog() {}

    //TODO unnecessary?
    BoggleDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
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
     * Gets a panel of OK and Cancel buttons in a <code>JPanel</code> and adds 
     * it to <code>component</code>.
     *
     * @param   component   <code>JDialog</code> to add the panel to.
     */
    protected JPanel getButtonPanel(JDialog component) {
        
        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionListener) component);
        final JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        //TODO document this cast.
        okButton.addActionListener((ActionListener) component);
        component.getRootPane().setDefaultButton(okButton);
        //Lay out the buttons from left to right.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }
}
