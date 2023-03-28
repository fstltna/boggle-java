/*
 * ConnectionPanel
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-14
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

/**
 * A graphical element for the display of current network connection status.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class ConnectionPanel extends JPanel {

    private JLabel connectionDetail = new JLabel("None");
    private JLabel connectionImage  = new JLabel(ConnectionType.NONE.image());
    private JLabel addressLabel     = new JLabel();
    private JLabel addressDetail    = new JLabel();
    
    /** Initialises a new ConnectionPanel. */
    public ConnectionPanel() {
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        this.setPreferredSize(new Dimension(400, 32));
        
        GridBagConstraints c = new GridBagConstraints();
        
        connectionImage.setPreferredSize(new Dimension(32, 32));
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.insets = new Insets(0, 10, 0, 10);
        layout.setConstraints(connectionImage, c);
        this.add(connectionImage);
        
        JLabel connectionLabel = new JLabel("Connection: ");
        connectionLabel.setPreferredSize(new Dimension(75, 16));
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;

        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 10, 0, 10);
        layout.setConstraints(connectionLabel, c);
        this.add(connectionLabel);
        
        //TODO need a kill connection option as well as host and join to throw 
        // off clients.
        //TODO block certain IPs from playing? 
        
        this.connectionDetail.setPreferredSize(new Dimension(325, 16));
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 10, 0, 10);
        layout.setConstraints(this.connectionDetail, c);
        this.add(this.connectionDetail);
        
        this.addressLabel.setPreferredSize(new Dimension(75, 16));
        c.anchor = GridBagConstraints.WEST;
        c.gridx  = 1;
        c.gridy  = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 10, 0, 10);
        layout.setConstraints(this.addressLabel, c);
        this.add(this.addressLabel);
        
        this.addressDetail.setPreferredSize(new Dimension(325, 16));
        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 2;
        c.gridy = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 10, 0, 10);
        layout.setConstraints(this.addressDetail, c);
        this.add(this.addressDetail);
        
        setAddress();
    }
    
    /**
     * Sets the visible connection details.
     *
     * @param   type    <code>ConnectionType</code> of connection.
     * @param   address Address of host.
     * @param   clients Number of clients currently attached to this connection.
     */
    public void setConnection(ConnectionType type, String address, 
            int clients) {
        setConnection(type, address, clients, 0);
    }
    
    /**
     * Sets the visible connection information on this Panel.
     *
     * @param   type    <code>ConnectionType</code> of connection.
     * @param   address Address of host.
     * @param   clients Number of clients currently attached to this connection.
     * @param   dots    Number of dots to display after the address.  For use in 
     *                  showing some animation while connecting.
     */
    public void setConnection(ConnectionType type, String address, 
            int clients, int dots) {
        this.connectionImage.setIcon(type.image());
        //TODO toString methods where they would be good.
        if (type == ConnectionType.NONE) {
            this.connectionDetail.setText("None");
        } else if (type == ConnectionType.CLIENT) {
            this.connectionDetail.setText("Client of " + address);
        } else if (type == ConnectionType.HOST) {
            this.connectionDetail.setText("Host at " + address + " with " 
                + clients + " Client" + (clients == 1 ? "" : "s"));
        } else if (type == ConnectionType.CONNECTING) {
            String dotString = "";
            for (int i = 0; i < dots; i++) {
                dotString += ".";
            }
            this.connectionDetail.setText("Connecting to " + address + " " 
                + dotString);
        } else {
            throw new IllegalArgumentException("Do not recognise connection "
                + "of type " + type);
        }
    }
    
    private void setAddress() {
        //TODO combine first part with getIpAddress() in GameDialog and put it 
        // somewhere more appropriate.
        InetAddress[] addresses;
        try {
            addresses = InetAddress.getAllByName(
                InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException e) {
            Boggle.debug(e.toString());
            //TODO err msg to user telling them they have no IP?
            addresses = new InetAddress[0];
        }

        String addressList = "";
        for (int i = 0; i < addresses.length; i++) {
            addressList += addresses[i].getHostAddress();
            addressList += (i < (addresses.length - 1) ? ", " : "");
        }
        String labelText = "Your address" 
            + (addresses.length > 1 ? "es" : "") + ":";
        this.addressLabel.setText(labelText);
        this.addressDetail.setText(addressList);
    }
    
    /**
     * Simple commandline test of ConnectionPanel.
     *
     * @param   args    Commandline arguments.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ConnectionPanel connPanel = new ConnectionPanel();
        frame.getContentPane().add(connPanel);
        frame.pack();
        frame.setVisible(true);
        JOptionPane.showMessageDialog(frame, "hi");
        connPanel.setConnection(ConnectionType.CLIENT, "127.0.0.1", 0);
    }
}
