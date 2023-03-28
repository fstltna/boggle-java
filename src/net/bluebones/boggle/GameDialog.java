/*
 * GameDialog
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-11
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

//TODO mnemonics for this and for ai dialog

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * Dialog box for setup of a game of Boggle.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class GameDialog extends BoggleDialog implements ActionListener {

    private static GameDialog dialog;
    private static GameInfo value;
    private static JTextField name;
    private static JTextField address;
    private static JTextField port;
    private static boolean ok = false;
    private boolean isHost;
    
    /*
     * TODO although copied from the java.sun.com tutorial this is all a bit 
     * static and non-OO with unnecessary frameComp, locationComp args isn't
     * it?
     */
    
    /**
     * Displays the Host Game dialog.
     *
     * @param   owner   Component that owns this dialog.
     * @return          GameInfo entered into this dialog.
     */
    public static GameInfo showHostGameDialog(Frame owner) {
        return showDialog(owner, true);
    }
    
    /**
     * Displays the Join Game dialog.
     *
     * @param   owner   Component that owns this dialog.
     * @return          GameInfo entered into this dialog.
     */
    public static GameInfo showJoinGameDialog(Frame owner) {
        return showDialog(owner, false);
    }
    
    private static GameInfo showDialog(Frame owner, boolean isHost) {
        dialog = new GameDialog(owner, isHost);
        dialog.setVisible(true);
        return GameDialog.value;
    }
    
    private void setValue(GameInfo newValue) {
        this.value = newValue;
    }
    
    private GameDialog(Frame owner, boolean isHost) {

        super(owner, (isHost ? "Host Game" : "Join Game"), true);

        this.isHost = isHost;

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        String myAddress = getIpAddress();
        String address = (isHost 
            ? myAddress : prefs.get("address", "127.0.0.1"));
        int port = (isHost 
            ? prefs.getInt("host.port", 1099) 
            : prefs.getInt("join.port", 1099));
        this.setValue(new GameInfo(prefs.get("name", "Player@" + myAddress), 
            new HostInfo(address, port)));
        
        GridLayout layout = new GridLayout(2, 1);
        this.getContentPane().setLayout(layout);
        this.getContentPane().add(getMainPanel(isHost));
        this.getContentPane().add(getButtonPanel(this));
        this.setResizable(false);
        this.pack();
        setLocationRelativeTo(owner);
    }
    
    private JPanel getMainPanel(boolean isHost) {
        
        JPanel mainPanel = new JPanel();
        
        //TODO store last used name somewhere and prompt with it
    
        GridBagLayout layout = new GridBagLayout();
        mainPanel.setLayout(layout);
        
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel nameLabel = new JLabel("Your Name: ");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.insets = new Insets(10, 10, 0, 10);
        layout.setConstraints(nameLabel, c);
        mainPanel.add(nameLabel);
        
        this.name = new JTextField(20);
        name.setText(GameDialog.value.name());
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.insets = new Insets(0, 10, 10, 10);
        layout.setConstraints(name, c);
        mainPanel.add(name);
        
        JLabel addressLabel = new JLabel("Address: ");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(10, 10, 0, 10);
        layout.setConstraints(addressLabel, c);
        mainPanel.add(addressLabel);
        
        this.address = new JTextField(Boggle.totalDice());

        this.address.setEditable(! isHost);
        this.address.setFocusable(! isHost);
        address.setText(GameDialog.value.hostInfo().address());
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 10, 10, 10);
        layout.setConstraints(address, c);
        mainPanel.add(address);
        
        JLabel portLabel = new JLabel("Port: ");
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(10, 10, 0, 10);
        layout.setConstraints(portLabel, c);
        mainPanel.add(portLabel);
        
        this.port = new JTextField(4);
        this.port.setText(Integer.toString(GameDialog.value.hostInfo().port()));
        //TODO Get support for other ports working and then remove this.
        this.port.setEditable(false);
        c.gridx = 1;
        c.gridy = 3;
        c.insets = new Insets(0, 10, 10, 10);
        layout.setConstraints(port, c);
        mainPanel.add(port);
        
        return mainPanel;
    }
    
    private String getIpAddress() {
        //TODO should display IP in the connection window too.
        try {
            /* TODO should display all IP addresses not just default one.
            InetAddress.getAllByName(
                InetAddress.getLocalHost().getCanonicalHostName()) */
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Cannot get local IP";
        }
    }
    
    /**
     * Gets the host information that has been input into the GameDialog.
     *
     * @return  The host information that has been input into the GameDialog.
     */
    public static GameInfo getValue() {
        return GameDialog.value;
    }
    
    /**
     * Handles clicks on the OK and Cancel buttons.
     *
     * @param   e   Action event that has occurred.
     */
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            GameDialog.ok   = true;
            String name     = this.name.getText().trim();
            String address  = this.address.getText().trim();
            int port;
            try {
                port = Integer.parseInt(this.port.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Port must be a whole number.", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            GameDialog.value 
                = new GameInfo(name, new HostInfo(address, port));
            Preferences prefs 
                = Preferences.userNodeForPackage(this.getClass());
            // IP is in this name and that may change so don't store in prefs.
            if (! name.startsWith("Player@")) {
                prefs.put("name", name);
            }
            prefs.putInt("port", port);
            if (this.isHost) {
                prefs.putInt("host.port", port);
            } else {
                prefs.put("address", address);
                prefs.putInt("join.port", port);
            }
        } else {
            GameDialog.ok = false;
        }
        GameDialog.dialog.setVisible(false);
    }
    
    /**
     * Whether the GameDialog was last OK'ed (true) or Cancelled (false).  If 
     * the dialog's buttons have never been clicked ok is false.
     *
     * @return  boolean of whether the last button clicked was OK.
     */
    public static boolean ok() {
        return GameDialog.ok;
    }
    
    /**
     * Simple commandline test.
     *
     * @param   args    Commandline arguments.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setVisible(true);
        GameDialog.showHostGameDialog(frame);
        GameInfo v = GameDialog.getValue();
        System.out.println("Values: " + v.name() + "@" 
            + v.hostInfo().address() + ":" + v.hostInfo().port());
    }
}
