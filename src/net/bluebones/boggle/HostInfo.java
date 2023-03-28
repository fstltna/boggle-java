/*
 * HostInfo
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-12
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

/**
 * Represents host information for use in connection to a game of Network 
 * Boggle.
 *
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class HostInfo {
    
    private String address;
    private int port;

    /**
     * Initialises a HostInfo with the specified values.
     *   
     * @param   address Address of host.
     * @param   port    Port number of host.
     */
    public HostInfo(String address, int port) {
        //TODO validation?
        this.address = address;
        this.port = port;
    }
    
    /**
     * Gets the address of this HostInfo.
     *
     * @return  Address - a hostname, an IP address or an FQDN.
     */
    public String address() {
        return address;
    }
    
    /**
     * Gets the TCP port number of this HostInfo.
     *
     * @return  TCP port number.
     */
    public int port() {
        return port;
    }
    
    /**
     * Gets a simple representation of the host address in the following
     * format: <code>address:port</code>.
     *
     * @return  <code>String</code> simple representation of the host address.
     */
    public String getSimpleAddr() {
        return this.address + ":" + this.port;   
    }
    
    /**
     * Gets the RMI address of this HostInfo.
     *
     * @return  RMI address in 
     *          <code>//address:port/net.bluebones.Server</code> format.
     */
    public String getAddr() {
        return "//" + this.address() + ":" + this.port() 
            + "/net.bluebones.boggle.Server";
    }
}
