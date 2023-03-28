/*
 * Connector
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

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Thread for running of connection to a host stopping long waits in the GUI 
 * thread which makes the app unresponsive.
 *
 * @author  Thomas David Baker, bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Connector implements Runnable {
    
    private Player player;
    private GameInfo gameInfo;
    
    /**
     * Intializes a new <code>Connector</code>.
     *
     * @param   player      <code>Player</code> this <code>Connector</code> 
     *                      belongs to.
     * @param   gameInfo    <code>GameInfo</code> describing the game this 
     *                      <code>Connector</code> should connect to.
     */
    public Connector(Player player, GameInfo gameInfo) {
        this.player = player;
        this.gameInfo = gameInfo;
    }
    
    /**
     * Runs this <code>Connector</code>, trying to connected the owner to the 
     * game specified in the constructor.
     */
    public void run() {
        Boggle.debug("Trying to run Connector.");
        HostInfo hostInfo = this.gameInfo.hostInfo();
        this.player.setLocalServer(null);
        this.player.setConnecting(hostInfo.address());
        this.player.setConnection(ConnectionType.CONNECTING, hostInfo.address(), 
            0);
        try {
            //TODO doing too much here?
            Boggle.debug("Starting the difficult bits of Connector.");
            this.player.setRemoteServer(
                (IServer) Naming.lookup(hostInfo.getAddr()));
            this.player.createRegistry(hostInfo.port());
            //TODO is this too time consuming to do on the off chance?  Could 
            // instead check if server is the same as current server if 
            // implement equals?
            if ((this.player.getClient() != null) 
                    && (this.player.getRemoteServer().isClient(
                    this.player.getClient()))) {
                this.player.problem("You are already connected to that host.");
                this.player.setNotConnecting();
                this.player.setConnection(ConnectionType.CLIENT, 
                    this.player.getHostInfo().address(), 0);
                return;
            }
            this.player.setHostInfo(hostInfo);
            this.player.setClient(new Client(this.player, gameInfo.name()));
            //TODO clients get added in too many places?
            this.player.getRemoteServer().addClient(
                this.player.getClient().getAddr(), 
                this.player.getClient().getName());
            Boggle.debug("Setting conn with remoteServer = " 
                    + this.player.getRemoteServer() + " and localServer = " 
                    + this.player.getLocalServer());
          this.player.setNotConnecting();
        } catch (NotBoundException e) {
            this.player.setNotConnecting();
            String errMsg = "Could not find game at " + hostInfo.getAddr();
            Boggle.debug(errMsg + " because of " + e);
            this.player.problem(errMsg);
        } catch (MalformedURLException e) {
            this.player.setNotConnecting();
            String errMsg = "Could not find game because of incorrect address: " 
                + hostInfo.getAddr();
            this.player.problem(errMsg);
            Boggle.debug(errMsg + " because of " + e);
        } catch (UnknownHostException e) {
            this.player.setNotConnecting();
            String errMsg = "Could not join game because unable to detect "
                + "local IP address.";
            Boggle.debug(errMsg + " exception was: " + e);
            this.player.problem(errMsg);
        } catch (RemoteException e) {
            this.player.setNotConnecting();
            Boggle.debug("Could not join game because of " + e);
            this.player.problem("Could not join game.");
        }
        //TODO log window under help with all debug messages in it.
        ConnectionType type;
        if (this.player.getRemoteServer() != null) {
            type = ConnectionType.CLIENT;
        } else {
            type = ConnectionType.NONE;
        }
        this.player.setConnection(type, hostInfo.getSimpleAddr(), 0);
        Boggle.debug("Have run Connector.");
    }
    
    /**
     * Gets a <code>String</code> representation of this object.
     *
     * @return  <code>String</code> representation of this object.
     */
    public String toString() {
        return this.getClass().toString() + " (Player: " + this.player 
            + ", GameInfo: " + this.gameInfo + ")"; 
    }
}
