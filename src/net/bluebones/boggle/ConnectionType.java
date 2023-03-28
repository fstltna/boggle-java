/*
 * ConnectionType
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

package net.bluebones.boggle;

import javax.swing.ImageIcon;

/**
 * Typesafe enum for connection types.
 *
 * @author  Thomas David Baker, bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class ConnectionType {

    /** Connection type representing no connection. */
    public static final ConnectionType NONE 
        = new ConnectionType(0, "conn-no.png");
    //TODO different icons for client and host?
    /** Connection type representing being the host of a game. */
    public static final ConnectionType CLIENT 
        = new ConnectionType(1, "conn-yes.png");
    /** Connection type representing being the client in a game. */
    public static final ConnectionType HOST 
        = new ConnectionType(2, "conn-yes.png");
    /** Connection type representing connecting to another machine. */
    public static final ConnectionType CONNECTING
        = new ConnectionType(3, "conn-connecting.png");

    private int id;
    private ImageIcon image;

    private ConnectionType(int id, String imageName) {
        this.id = id;
        try {
            this.image = new ImageIcon(ClassLoader.getSystemResource(
                "resources/images/" + imageName));
        } catch (NullPointerException e) {
            // Not the end of the world if we can't display a graphic.
            Boggle.debug("Cannot find " + imageName + " graphic.");
            this.image = new ImageIcon();
        }
    }
    
    /**
     * Gets the id of this ConnectionType.
     *
     * @return  int id of this ConnectionType.

     */
    public int id() {
        return this.id;
    }
    
    /**
     * Gets the image that represents this ConnectionType.
     *
     * @return  ImageIcon that represents this ConnectionType.
     */
    public ImageIcon image() {
        return this.image;
    }

    /**
     * Gets a <code>String</code> representation of this object.
     *
     * @return  <code>String</code> representation of this object.
     */
    public String toString() {
        //TODO this is an overcomplex representation and not useful?
        return this.getClass().toString() + " (ID: " + this.id()
            + ", Image: " + this.image()+ ")"; 
    }
}
