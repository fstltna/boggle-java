/*
 * IServer
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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of remote methods available on a remote Server.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public interface IServer extends Remote {

    /**
     * Adds the client at the specified RMI address to the IServer's 
     * list of current participants.
     *
     * @param   clientAddr      RMI address of client to add.
     * @param   clientName      Name of the player at <code>clientAddr</code>.
     * @throws  RemoteException If there is any problem with the RMI.
     */
    public void addClient(String clientAddr, String clientName) 
        throws RemoteException;
    
    /**
     * Returns the specified Turn to the IServer.
     *
     * @param   turn            Turn to send to the IServer.
     * @throws  RemoteException If there is any problem with the RMI.
     */
    public void returnResults(Turn turn) throws RemoteException;
    
    /**
     * Writes the specified message to the server.
     *
     * @param   msg             The message to write.
     * @throws  RemoteException If there is any problem with the RMI.
     */
    public void writeMsg(String msg) throws RemoteException;
    
    /**
     * Determines if the specified client is a current client of this server.
     *
     * @param   client          Client to check.
     * @return                  boolean of whether the specified client is a 
     *                          current client of this server 
     *                          (<code>true</code>) or not (<code>false</code>).
     * @throws  RemoteException If there is any problem with the RMI.
     */
    public boolean isClient(IClient client) throws RemoteException;
}
