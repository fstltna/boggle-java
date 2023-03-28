/*
 * NamedClient
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-18
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

/**
 * Wraps an IClient with other non-remote details about that client.
 *
 * @author  Thomas David Baker, bakert@gmail.com
 * @version 0.3 $Revision: 1.3 $
 */
public class NamedClient implements Serializable {

    private String name;
    private IClient client;

    /**
     * Initializes a new <code>NamedClient</code>.
     *
     * @param   name    <code>String</code> name of the client (to use in 
     *                  display).
     * @param   client  Underlying <code>IClient</code> that does the work.
     */
    public NamedClient(String name, IClient client) {
        this.name = name;
        this.client = client;
    }
    
    /**
     * Name of this <code>NamedClient</code>.
     *
     * @return  <code>String</code> name.
     */
    public String name() {
        return this.name;
    }
    
    /**
     * <code>IClient</code> that is wrapped in this <code>NamedClient</code>.
     *
     * @return  <code>IClient</code>.
     */
    public IClient client() {
        return this.client;
    }

    /**
     * Gets a <code>String</code> representation of this object.
     *
     * @return  <code>String</code> representation of this object.
     */
    public String toString() {
        return this.getClass().toString() + " (Name: " + name()
            + ", IClient: " + client() + ")"; 
    }
    
    /**
     * Determines if <code>o</code> is the same as this <code>NamedClient</code> 
     * (has the same name and wraps the exact same client).
     *
     * @param   o   <code>Object</code> to compare.
     * @return      <code>boolean</code>.
     */
    public boolean equals(Object o) {
        if (! (o instanceof NamedClient)) {
            return false;
        }
        NamedClient namedClient = (NamedClient) o;
        return (namedClient.name().equals(this.name())
            && namedClient.client().equals(this.client()));
    }
    
    /**
     * Returns a hash code value for the object. This method is supported for 
     * the benefit of hashtables such as those provided by 
     * <code>java.util.Hashtable</code>. 
     *
     * @return  <code>int</code> hash code value for this object.
     */
    public int hashCode() {
        int result = 4;
        result = 37 * result + this.name.hashCode();
        result = 37 * result + this.client.hashCode();
        return result;
    }
}
