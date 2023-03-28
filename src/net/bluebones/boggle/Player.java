/*
 * Player
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-10
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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

/**
 * Represents the entire client side of Boggle.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public abstract class Player {

    private Game game = null;
    private IServer remoteServer;
    private Server localServer;
    private Client client;
    private HostInfo hostInfo;
    private int port;
    private String connectingTo;
    private Registry registry;

    /**
     * Relays the specified message to the user.
     *
     * @param   msg Message to relay.
     */
    public abstract void info(String msg);
    
    /**
     * Notifies the user of the specified problem.
     *
     * @param   msg Message to relay.
     */
    public abstract void problem(String msg);
    
    /**
     * Starts a game for this player.
     *
     * @param   letters     Letters to populate the board with.
     * @param   timeInSecs  Duration of game.
     */
    public void startLocalGame(char[][] letters, int timeInSecs) {
        
        Boggle.debug("startLocalGame called for " + this);
        
        if (getGame() != null) {
            Boggle.debug("Killing in " + this);
            getGame().kill();
        }
        setGame(new Game(this, letters, timeInSecs));
        new Thread(this.game).start();
    }
    
    //TODO put all debug messages into a "log" window which can be viewed in a 
    // scrolling window and optionally be saved to file automatically.
    
    /** 
     * Implemented by the subclassed <code>Player</code> to do anything
     * necessary at the end of a game (cleanup, marshalling of words and so 
     * on). 
     */
    public abstract void stopGame();

    /**
     * Get the words this <code>Player</code> has found this round.
     *
     * @return  <code>String[]</code> of words found.
     */
    public abstract String[] getWords();
    
    /** Ends the game and returns words to the host of the game. */
    public void endGame() {
        stopGame();
        String[] words = getWords();
        Boggle.debug("In endGame");
        try {
            Turn t = new Turn(new NamedClient(this.client.getName(), 
                this.client), words);
            if (getLocalServer() != null) {
                getLocalServer().returnResults(t);
            } else if (getRemoteServer() != null) {
                getRemoteServer().returnResults(t);
            } else {
                Boggle.debug("Both localserver and remoteserver are null.  "
                    + "Connection lost.");
                this.problem("Cannot get results as connection to host lost.");
            }
        } catch (RemoteException e) {
            Boggle.debug("Got an exception trying to return results to the "
                + "host: " + e);
            //TODO Try and send again?
            problem("Cannot get results as connection to host lost.");
        }
    }
    
    
    /**
     * Joins this player into an existing game.
     *
     * @param   gameInfo    Player-supplied host info to use to connect to the 
     *                      host.
     */
    public void joinGame(GameInfo gameInfo) {
        //TODO could have a Force End of Game on the host?
        //TODO need to allow the use of a proxy server.
        //TODO man this is confusing -- this is where client is created for an 
        // AI.  Simplify?
        new Thread(new Connector(this, gameInfo)).start();
    }
    
    /**
     * Gets the local server.  <code>null</code> if this Player is not 
     * currently hosting a game.
     *
     * @return  Local server or <code>null</code>.
     */
    public abstract IServer getLocalServer();
    
    /**
     * Gets the remote server.  <code>null</code> if this Player is not 
     * currently the client of another server.
     *
     * @return  Remote server or <code>null</code>.
     */
    public IServer getRemoteServer() {
        return this.remoteServer;
    }    
    
    /**
     * Gets the Client being used by this player.
     *
     * @return  Client for this player.
     */
    public Client getClient() {
        return this.client;
    }
    
    /**
     * Gets the HostInfo currently being used by this player.
     *
     * @return  HostInfo currently being used by this player.
     */
    public HostInfo getHostInfo() {
        //TODO better to read from server even though that might mean an RMI 
        // call than to add this "caching"?
        return this.hostInfo;
    }
    
    /**
     * Sets the local server.  If the local server is not null this 
     * <code>Player</code> is acting as a host.
     *
     * @param   server  <code>Server</code> to set as local server or null to 
     *                  unset.
     */
    public abstract void setLocalServer(Server server);
    
    /**
     * Sets the remote server.  If the remote server is not <code>null</code> 
     * this <code>Player</code> is a client of the remote server.
     *
     * @param   server  <code>IServer</code> to set as remote server or null to 
     *                  unset.
     */
    public void setRemoteServer(IServer server) {
        this.remoteServer = server;
    }
    
    /**
     * Removes the old client in use by this <code>Player</code> (if any) and 
     * sets a new one.
     *
     * @param   client  <code>Client</code> to use as client for this 
     *                  <code>Player</code>.
     */
    public void setClient(Client client) {
        if (this.client != null) {
            try {
                Boggle.debug("Unbinding " + this.client.getAddr());
                this.registry.unbind(this.client.getAddr());
            } catch (RemoteException e) {
                Boggle.debug("Got an error trying to ditch the "
                    + "previous client.  Exception was " + e);
                //TODO ignoring exceptions?
            } catch (NotBoundException e) {
                Boggle.debug("Got an error trying to ditch the "
                    + "previous client.  Exception was " + e);
                //TODO ignoring exceptions?
            }
        }
        this.client = client;
    }
    
    /**
     * Sets the current connection details.
     *
     * @param   type    <code>ConnectionType</code> type of connection.
     * @param   address <code>String</code> address connected to.
     * @param   clients <code>int</code> number of clients attached to this 
     *                  <code>Player</code>.
     */
    public abstract void setConnection(ConnectionType type, String address, 
            int clients);
    
    /**
     * Sets the current connection details with a number of dots to show 
     * progress.
     *
     * @param   type    <code>ConnectionType</code> type of connection.
     * @param   address <code>String</code> address connected to.
     * @param   clients <code>int</code> number of clients attached to this 
     *                  <code>Player</code>.
     * @param   dots    <code>int</code> number of dots to be displayed by the 
     *                  client at this time.  Maximum of three.
     */
    public abstract void setConnection(ConnectionType type, String address,
            int clients, int dots);
    
    /**
     * Sets the host details to be used by this <code>Player</code>.
     *
     * @param   hostInfo    <code>HostInfo</code> host details to set.
     */
    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }
    
    /** Takes the appropriate action after losing connection to a host. */
    public void hostConnectionLost() {
        this.remoteServer = null;
    }
    
    /**
     * Gets the port that the RMI Registry is running on.
     *
     * @return  Number of the TCP port that the RMI Registry is running on.
     */
    public int getPort() {
        Boggle.debug("Port requsted so returning " + this.port);
        return this.port;
    }
    
    private void setPort(int port) {
        Boggle.debug("Port on " + this + " being set to " + port);
        this.port = port;
    }
    
    /**
     * Gets the <code>NamedClient</code>s attached to this player.
     *
     * @return  <code>NamedClient[]</code> of the clients or an empty array if 
     *          this <code>Player</code> is not hosting a game currently.
     */
    public NamedClient[] getClients() {
        if (this.localServer == null) {
            return new NamedClient[0];
        } else {
            return this.localServer.getClients();
        }
    }
        
    //TODO prevent actions when connecting with modal dialog/splash with 
    // hourglass?
    
    /**
     * To be implemented by subclasses to deal with <code>Rubber</code> details
     * as they see fit (display, etc.)  Subclasses must deal with the possibilty 
     * of a null <code>Rubber</code> being passed in.
     *
     * @param   r   <code>Rubber</code> details to deal with.
     */
    public abstract void setRubber(Rubber r);
        
    /**
     * Sets the timer to the specified amount in tenths of a second
     *
     * @param   tenthsOfASecond Amount to set the display to in tenths of 
     *                          a second.
     */
    public abstract void setTimer(int tenthsOfASecond);
    
    /**
     * Initializes this <code>Player</code> with a board of letters and 
     * time for the round.
     *
     * @param   letters     <code>char[][]</code> of letters on the board.
     * @param   timeInSecs  <code>int</code> time of the round in seconds.
     */
    public abstract void init(char[][] letters, int timeInSecs);
    
    /**
     * Called when results are published.  To be implemented by subclass to 
     * display or otherwise deal with results.
     *
     * @param   round   <code>Round</code> result details.
     */
    public abstract void results(Round round);
    
    //XXX  should be able to use different ports for remote and local.
    /**
     * Creates an RMI Registry on the specified port for this 
     * <code>Player</code>.
     *
     * @param   port    <code>int</code> TCP port to create registry on.
     */
    public void createRegistry(int port) {
        Boggle.debug("Creating a registry on port " + port);
        // Set up rmiregistry.
        try {
            Boggle.debug("Port is " + port + " and this.port is " + this.port);
            if (this.port != port) {
                Boggle.debug("Setting port to " + port);
                this.setPort(port);
                this.registry = LocateRegistry.createRegistry(port);
            }
        } catch (ExportException e) {
            Boggle.debug("Got an export exception: " + e 
                + "  Perhaps there is already something running on that port?");
            //XXX if you set up a registry then try and change port you get an 
            // error.
            // XXX  There's already something on that port.  It could be an 
            // RMI registry.  Do we get the same exception if it is not?
            // If we sophisticate this look out for a problem with port being
            // set BEFORE this is thrown above to cope with two clients on one 
            // machine.
        } catch (RemoteException e) {
            System.out.println(e);
            Boggle.debug(e.toString());
            this.problem("Could not set up network connections for this game."
                + "  Sorry.");
        }
    }
    
    /**
     * Gets the game this <code>Player</code> is currently playing.
     *
     * @return  <code>Game</code> game being played.
     */
    public Game getGame() {
        return this.game;
    }
    
    /**
     * Sets the game this <code>Player</code> is currently playing.
     *
     * @param   game    <code>Game</code> this player is about to play.
     */
    public void setGame(Game game) {
        this.game = game;
    }
    
    /**
     * Gets whether this machine is in the process of connecting to another 
     * machine.
     *
     * @return  <code>boolean</code>.
     */
    public boolean connecting() {
        return (this.connectingTo != null);
    }
    
    /**
     * Gets the address of the machine this <code>Player</code> is currently 
     * trying to connect to.
     *
     * @return  <code>String</code> address of the machine being connected to.
     */
    public String connectingTo() {
        return this.connectingTo;
    }
    
    /**
     * Updates this <code>Player</code> so that it knows it is not connecting to 
     * any other machine (either because it has connected or because it has 
     * given up).
     */
    public void setNotConnecting() {
        this.connectingTo = null;
    }
    
    /**
     * Sets the address this <code>Player</code> is currently connecting to.
     *
     * @param   to  <code>String</code> address being connected to.
     */
    public void setConnecting(String to) {
        this.connectingTo = to;
    }
    
    /**
     * Gets the RMI Registry in use by this player.
     *
     * @return  <code>Registry</code> currently in use.
     */
    public Registry getRegistry() {
        return this.registry;
    }
    
    /**
     * Sets the RMI Registry to be used by this <code>Player</code>.
     *
     * @param   registry    <code>Registry</code> to use.
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
