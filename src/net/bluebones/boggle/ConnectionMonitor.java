/*
 * ConnectionMonitor
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

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Thread that checks connection status every ten seconds and updates the 
 * ConnectionPanel to reflect current status.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 * @see     ConnectionPanel
 */
public class ConnectionMonitor implements Runnable {

    private HumanPlayer player;
    private int dots = 0;
    private ConnectionType connType;

    /**
     * Initialises a new ConnectionMonitor.
     *
     * @param   player      Player this connection checker belongs to.
     */
    public ConnectionMonitor(HumanPlayer player) {
        this.player = player;
    }
    
    //TODO change name to clientmonitor?
    
    /**
     * Main action of the ConnectionMonitor.  Checks connection every second, 
     * reporting changes to the ConnectionPanel.  Checks any clients this 
     * Player may have and removes them from the Player's list if they are no 
     * longer connected.  If this player is a client then checks connection
     * to server and reports errors if not connected.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // We don't care.
                Boggle.debug(e.toString());
                //TODO don't ignore exceptions.
            }
            monitorConnections();
        }
    }
    
    private void monitorConnections() {
        
        if ((this.player.getClient() == null) && (! this.player.connecting())) {
            setConnection(ConnectionType.NONE, null, 0);
        }
        
        IServer localServer = this.player.getLocalServer();
        IServer remoteServer = this.player.getRemoteServer();
        boolean isClient = false;
        try {
            isClient = (remoteServer == null 
                ? false 
                : remoteServer.isClient(this.player.getClient()));        
        } catch (RemoteException e) {
            //TODO should chain this this=>HumanPlayer=>UI=>ConnectionPanel
            // and remove ConnectionPanel from this (non-GUI) class?
            Boggle.debug("Remote exception finding out if this client is "
                + "still a client of its host.  Setting connection to none.");
            setConnection(ConnectionType.NONE, null, 0);
            this.player.problem("Connection to host lost.  Use File, Join Game "
                + "to retry.");
            this.player.hostConnectionLost();
        }
        HostInfo hostInfo = this.player.getHostInfo();
        if (localServer != null && hostInfo != null) {
            Set clients = monitorClients();
            //TODO could drop number of clients from display because you get
            // the score list now which effectively tells you.
            setConnection(ConnectionType.HOST, hostInfo.getSimpleAddr(), 
                clients.size());
        } else if (remoteServer != null && isClient) {
            setConnection(ConnectionType.CLIENT, hostInfo.getSimpleAddr(), 0);
        } else {
            if (this.connType == ConnectionType.CLIENT) {
                this.player.problem("Connection to host lost.  Use File, Join "
                    + "Game to retry.");
            }
            setConnection(ConnectionType.NONE, null, 0);
        }        
        this.player.checkResultsSend();
    }

    //TODO ant file that does the whole build and copies to rook1 and so on.
    
    private Set monitorClients() {
        
        Set<NamedClient> activeClients = new HashSet<NamedClient>();
        Set<NamedClient> inactiveClients = new HashSet<NamedClient>();
        
        NamedClient[] namedClients = this.player.getClients();
        for (int i = 0; i < namedClients.length; i++) {
            try {
                if (namedClients[i].client().isActive(
                        this.player.getServerGameId())) {
                    activeClients.add(namedClients[i]);
                } else {
                    Boggle.debug("Someone returned false from active!");
                    inactiveClients.add(namedClients[i]);
                }
            } catch (RemoteException e) {
                //TODO exceptions only for exceptional circumstances!
                Boggle.debug("Got a remote exception trying to contact "
                    + "a client so removing");
                Boggle.debug(e.toString());
                inactiveClients.add(namedClients[i]);
            }
        }
        if (inactiveClients.size() > 0) {
            this.player.removeClients(inactiveClients);
        }
        return activeClients;
    }
    
    private void setConnection(ConnectionType type, String addr, int clients) {
        //TODO this is a "side effect" and not desirable to do it here?
        if ((this.connType != type) && (type == ConnectionType.NONE)) {
            Boggle.debug("Resetting rubber.");
            // Have just dropped connection or just started, set rubber to null.
            this.player.setRubber(null);
        }
        this.connType = type;
        if (this.player.connecting()) {
            // Cycle from 0-3 dots while connecting
            this.dots = ++this.dots % 4;
            this.player.setConnection(ConnectionType.CONNECTING, 
                this.player.connectingTo(), 0, dots);
        } else {
            this.player.setConnection(type, addr, clients);
        }
    }
}
