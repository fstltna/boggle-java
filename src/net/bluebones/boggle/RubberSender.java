/*
 * RubberSender
 * 
 * Thomas David Baker <bakert@gmail.com>, 2004-09-26
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

import java.rmi.RemoteException;

/**
 * Thread to send game details to clients to avoid having to wait for RMI to 
 * happen in the main thread.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class RubberSender implements Runnable {

    private Rubber rubber;
    private NamedClient namedClient;
    private Client host;

    /**
     * Initializes a new ResultsSender to send rubber details with the specified 
     * values.
     *
     * @param   rubber      Rubber details to send.
     * @param   namedClient NamedClient to send results to.
     * @param   host        Client that is hosting the game.
     */
    public RubberSender(Rubber rubber, NamedClient namedClient, Client host) {
        this.rubber = rubber;
        this.namedClient = namedClient;
        this.host   = host;
    }
    
    /**  Sends the <code>Rubber</code> details to the clients. */
    public void run() {
        //TODO combine this class with ResultsSender as ClientSender???
        try {
            this.namedClient.client().setRubber(this.rubber);
        } catch (RemoteException e) {
            String errMsg = "Could not pass game details to " 
                + namedClient.name() + ".  If the problem persists " 
                + namedClient.name() + " will be removed from the game.";
            Boggle.debug(errMsg + "  Exception was: " + e);
            this.host.problem(errMsg);
        }
    }
}
