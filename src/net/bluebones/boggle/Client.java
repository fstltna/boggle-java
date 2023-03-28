/*
 * Client
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

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;

//TODO total number of distinct words in the round in results.

/**
 * Controls the network side of the application from the client side.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Client extends UnicastRemoteObject implements IClient {
    
    private String addr;
    private Player player;
    private String name;
    //XXX this is an out and out bug - any server without a game in progress 
    // asking this client if it is a client of its will say yes.
    private GameId gameId = new GameId(null, null);
    
    /**
     * Initialises a Client.
     *
     * @throws  RemoteException         If anything goes wrong with the RMI.
     * @throws  UnknownHostException    If client is unable to determine its
     *                                  own address.
     */
    public Client(Player player, String name) 
            throws RemoteException, UnknownHostException {
        Boggle.debug("Creating new Client.");
         //TODO send letters in threads so they all at least set off at the same 
        // time.
        this.player = player;
        this.name   = name;
        //TODO don't do any of this with Strings do it with objects.
        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        this.addr = "//" + ipAddress + ":" + this.player.getPort() 
            + "/" + new UID();
        try {
            Boggle.debug("Trying to bind client at address " + this.addr);
            Naming.rebind(this.addr, this);
        } catch (MalformedURLException e) {
            String errMsg = "Cannot bind because of " + e;
            Boggle.debug(errMsg);
            throw new RemoteException(errMsg, e);
        }
    }

    /**
     * Notifies the client to start a game with the specified letters.
     *
     * @param   letters         Letters to initialise the board with.
     * @param   timeInSecs      Time of game in seconds.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public void startGame(char[][] letters, int timeInSecs, GameId gameId) 
            throws RemoteException {
        Boggle.debug("Told to start a new game (" + this.addr + ").");
        this.gameId = gameId;
        player.startLocalGame(letters, timeInSecs);
    }
    
    /**
     * Notifies the client to process the results of a previous round.
     *
     * @param   round           Results to process.
     * @param   rubber          Details of the rubber.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public void results(Round round, Rubber rubber) throws RemoteException {
        Boggle.debug("Received results (" + this.addr + ").");
        this.player.setRubber(rubber);
        this.player.results(round);
    }
    
    /**
     * Determines if this Client is actively connected to the specified game.
     *
     * @param   gameId          <code>GameID</code> of game to check for 
     *                          participation in.
     * @return                  Returns <code>true</code> if playing the game 
     *                          represented by <code>gameId</code>.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public boolean isActive(GameId gameId) throws RemoteException {
        //XXX surely this is dumb?
        if ((this.gameId.serverAddress() == null) 
                && (this.gameId.uid() == null)) {
            return true;
        } else {
            return (this.gameId.equals(gameId));
        }
    }
    
    //TODO if you remove an AI player during a game he returns for the 
    // results?!
    
    /**
     * Sets the rubber information for this client.
     *
     * @param   r   <code>Rubber</code> to set.
     */
    public void setRubber(Rubber r) throws RemoteException {
        this.player.setRubber(r);
    }
    
    /**
     * Gets the RMI address of this Client.
     *
     * @return  The RMI address of this Client.
     */
    public String getAddr() {
        return this.addr;
    }
    
    /**
     * Displays an error message on this Client.
     *
     * @param   msg Message to show.
     */
    public void problem(String msg) {
        this.player.problem(msg);
    }
    
    /**
     * Gets the name of this client.
     *
     * @return  String name of this client.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Simple commandline test of Client.
     *
     * @param   args                    Commandline arguments.
     * @throws  UnknownHostException    If local IP cannot be detected.
     * @throws  NotBoundException       If server cannot be found.
     * @throws  RemoteException         If anything goes wrong with the RMI.
     * @throws  MalformedURLException   If RMI address is malformed.
     */
    public static void main(String[] args) throws UnknownHostException, 
            NotBoundException, RemoteException, MalformedURLException {
        System.out.println("Hostname: " 
            + InetAddress.getLocalHost().getHostName());
        System.out.println("Canonical Hostname: " 
            + InetAddress.getLocalHost().getCanonicalHostName());

        System.out.println("Address:");
        byte[] address = InetAddress.getLocalHost().getAddress();
        for (int i = 0; i < address.length; i++) {
            System.out.print(address[i] + ".");
        }
        System.out.println();

        System.out.println("All By Name (hostname): " 
            + java.util.Arrays.asList(InetAddress.getAllByName(
            InetAddress.getLocalHost().getHostName())));
        System.out.println("All By Name (canonical): " 
            + java.util.Arrays.asList(InetAddress.getAllByName(
            InetAddress.getLocalHost().getCanonicalHostName())));
        System.out.println("All By Name (host address): " 
            + java.util.Arrays.asList(InetAddress.getAllByName(
            InetAddress.getLocalHost().getHostAddress())));

        Client c = new Client(new HumanPlayer(), "TestClient");
        IServer server = (IServer) Naming.lookup(
            "//127.0.0.1:1099/net.bluebones.boggle.Server");
        server.addClient(c.getAddr(), c.getName());
        server.writeMsg("hi");
    }
    
    /**
     * Gets a <code>String</code> representation of this <code>Client</code>.
     *
     * @return  <code>String</code> representation.
     */
    public String toString() {
        return this.getClass().toString() + " (Name: " + this.name 
            + ", Addr: " + this.addr + ", Player:" + this.player + ")";
    }
}
