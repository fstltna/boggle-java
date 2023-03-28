/*
 * GameId
 * 
 * Thomas David Baker <bakert@cw.com>, 2004-09-27
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

import java.io.Serializable;    
import java.rmi.server.UID;

/**
 * A universally unique identifier for a game of Boggle.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class GameId implements Serializable {

    private String serverAddress;
    private UID uid;

    /**
     * Initializes a new <code>GameId</code>.
     *
     * @param   serverAddress   <code>String</code> IP or other address of host.
     * @param   uid             <code>UID</code> game identifier.
     */
    public GameId(String serverAddress, UID uid) {
        //TODO docs for non public items.
        this.serverAddress = serverAddress;
        this.uid = uid;
    }

    /**
     * Gets the host IP or other address.
     *
     * @return  <code>String</code> of host IP or other address.
     */
    public String serverAddress() {
        return this.serverAddress;
    }
    
    /**
     * Gets the game identifier.
     *
     * @return  <code>UID</code> game identifier.
     */
    public UID uid() {
        return this.uid;
    }
    
    /**
     * Determines if this <code>GameId</code> is the same as the 
     * <code>Object</code> specified.
     *
     * @param   o   <code>Object</code> to compare with.
     */
    public boolean equals(Object o) {
        if (! (o instanceof GameId)) {
            return false;
        }
        GameId gameId = (GameId) o;
        
        //XXX this is an out and out bug - should not allow null serverAddress.
        if (this.serverAddress() == null) {
            Boggle.problem("this.serverAddress is null in GameId: " + this);
        }
        
        if ((this.serverAddress() == null) && (gameId.uid() == null) 
                && (this.uid() == null) && (gameId.uid() == null)) {
            return true;
        } else if ((this.serverAddress() == null) 
                || (gameId.serverAddress() == null)) {
            return false;
        } else if (! (this.serverAddress().equals(gameId.serverAddress()))) {
            return false;
        } else if ((this.uid() == null) && (gameId.uid() == null)) {
            return true;
        } else if ((this.uid() == null) || (gameId.uid() == null)) {
            return false;
        } else {
            return (this.uid().equals(gameId.uid()));
        }
    }
    
    /**
     * Returns a hash code value for the object. This method is supported for 
     * the benefit of hashtables such as those provided by 
     * <code>java.util.Hashtable</code>. 
     *
     * @return  <code>int</code> hash code value for this object.
     */
    public int hashCode() {
        int result = 9;
        result = 37 * result + this.serverAddress.hashCode();
        result = 37 * result + this.uid.hashCode();
        return result;
    }
    
    /**
     * Gets a <code>String</code> representation of this <code>GameId</code>.
     *
     * @return  <code>String</code> representation.
     */
    public String toString() {
        return this.getClass().getName() + " (Server Address: " 
            + serverAddress() + ", UID: " + this.uid() + ")";
    }
}
