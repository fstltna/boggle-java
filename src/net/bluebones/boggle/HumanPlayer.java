/*
 * HumanPlayer
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

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Represents the entire client side of Boggle.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class HumanPlayer extends Player {

    private UI ui;
    private IServer remoteServer;
    private Server localServer;
    private int port;

    /** Initialises a new HumanPlayer. */
    public HumanPlayer() {
        ui = new UI(this);
        new Thread(new ConnectionMonitor(this)).start();
    }
    
    /**
     * Relays the specified message to the user.
     *
     * @param   msg Message to relay.
     */
    public void info(String msg) {
        this.ui.info(msg);
    }
    
    /**
     * Notifies the user of the specified problem.
     *
     * @param   msg Message to relay.
     */
    public void problem(String msg) {
        this.ui.problem(msg);
    }
    
    /**
     * Starts a network game, setting up a Server on this host or using the 
     * existing one.
     */
    public void startNetworkGame() {
        
        if (Boggle.dictionary == null) {
            this.ui.problem("No dictionary found.  Cannot run game without "
                + "a dictionary at " + Boggle.DICT_PATH);
            return;
        }
        
        try {
            if (getRegistry() == null) {
                createRegistry(1099);
            }
            if (getClient() == null) {
                if (getHostInfo() == null) {
                    setHostInfo(new HostInfo("127.0.0.1", 1099));
                }
                //TODO are we happy with this name for a local game?
                setClient(new Client(this, "Player@127.0.0.1"));
            }
            if (this.localServer == null) {
                // If hasn't set self up as a host do this to allow solo play.
                HostInfo hostInfo = new HostInfo("127.0.0.1", 1099);
                this.localServer = new Server(this, getClient(), hostInfo);
                setHostInfo(hostInfo);
                Boggle.debug("Setting conn with remoteServer = " 
                    + this.remoteServer + " and localServer = " 
                    + this.localServer);
                setConnection(ConnectionType.HOST, hostInfo.getSimpleAddr(), 
                    1);
            }
            this.localServer.startGame();
        } catch (RemoteException e) {
            String errMsg = "Could not start network game because of "
                + "network problems.";
            Boggle.debug(errMsg + " Exception was: " + e);
            problem(errMsg);
        } catch (UnknownHostException e) {
            String errMsg = "Could not start network game because could "
                + " not detect local IP address.";
            Boggle.debug(errMsg + " Exception was " + e);
            problem(errMsg);
        }
    }
    
    //TODO put all debug messages into a "log" window which can be viewed in a 
    // scrolling window and optionally be saved to file automatically.
    
    /** Ends the game and returns words to the host of the game. */
    public void stopGame() {
        this.ui.endGame();
    }
    
    /**
     * Gets this player's words for this turn.
     *
     * @return  <code>String[]</code> of words the player has found this turn.
     */
    public String[] getWords() {
        return this.ui.getWords();
    }
    
    /**
     * Sets up this player as the host of a game.
     *
     * @param   gameInfo    Player-supplied host info to help set up the host.
     */
    public void hostGame(GameInfo gameInfo) {
        //TODO combine hostGame and joinGame - many similarities.
        HostInfo hostInfo = gameInfo.hostInfo();
        this.remoteServer = null;
        try {
            createRegistry(hostInfo.port());
            setHostInfo(hostInfo);
            setClient(new Client(this, gameInfo.name()));
            this.localServer = new Server(this, getClient(), hostInfo);
            setConnection(ConnectionType.HOST, hostInfo.getSimpleAddr(), 1);
        } catch (RemoteException e) {
            String errMsg = "Could not set up host.";
            Boggle.debug(errMsg + " Exception was: " + e);
            problem(errMsg);
        } catch (UnknownHostException e) {
            String errMsg = "Could not set up host because could not "
                + "detect local IP address.";
            Boggle.debug(errMsg + " Exception was " + e);
            problem(errMsg);
        }
    }
    
    /**
     * Gets the local server.  <code>null</code> if this HumanPlayer is not 
     * currently hosting a game.
     *
     * @return  Local server or <code>null</code>.
     */
    public IServer getLocalServer() {
        return this.localServer;
    }
    
    /**
     * Sets the local server.
     *
     * @param   server  <code>Server</code> to set as local server or null to 
     *                  unset.
     */
    public void setLocalServer(Server server) {
        this.localServer = server;
    }
    
    /**
     * Determines if this player is currently hosting a game.
     *
     * @return  <code>boolean</code>.
     */
    public boolean isHost() {
        return (this.localServer != null);
    }
    
    /**
     * Sets the displayed connection for this player.
     *
     * @param   type    <code>ConnectionType</code> of connection.
     * @param   address Address of host.
     * @param   clients Number of clients currently attached to this connection.
     */
    public void setConnection(ConnectionType type, String address, 
            int clients) {
        this.ui.setConnection(type, address, clients);
    }
    
    /**
     * Sets the displayed connection for this player.  Includes dots to show 
     * progress in connecting.
     *
     * @param   type    <code>ConnectionType</code> of connection.
     * @param   address Address of host.
     * @param   clients Number of clients currently attached to this connection.
     * @param   dots    Number of dots to display after the address.  For use in 
     *                  showing some animation while connecting.
     */
    public void setConnection(ConnectionType type, String address,
            int clients, int dots) {
        this.ui.setConnection(type, address, clients, dots);
    }
    
    /**
     * Gets the clients currently attached to this player.
     *
     * @return  <code>NamedClient[]</code> of the attached clients.
     */
    public NamedClient[] getClients() {
        if (this.localServer == null) {
            return new NamedClient[0];
        } else {
            return this.localServer.getClients();
        }
    }
    
    /**
     * Removes all clients in <code>clients</code> from this player's local 
     * (host) server.
     *
     * @param   clients <code>Collection</code> of clients to remove.
     */
    public void removeClients(Collection clients) {
        if (this.localServer != null) {
            this.localServer.removeClients(clients);
        }
    }
    
    //TODO prevent actions when connecting with modal dialog/splash with 
    // hourglass?
    
    /**
     * Set the displayed rubber information.
     *
     * @param   r   <code>Rubber</code> information to set.
     */
    public void setRubber(Rubber r) {
        this.ui.setRubber(r);
    }
    
    /**
     * Sets the timer to the specified time.  Measured in 10ths of a second.
     *
     * @param   tenthsOfASecond Amount to display in tenths of a second.
     */
    public void setTimer(int tenthsOfASecond) {
        this.ui.setTimer(tenthsOfASecond);
    }
    
    /**
     * Initializes the display of this player.
     *
     * @param   letters     Letters to initialize the board with.
     * @param   timeInSecs  Duration of game in seconds.
     */
    public void init(char[][] letters, int timeInSecs) {
        this.ui.resetTimer();
        this.ui.initWordArea();
        this.ui.setBoard(letters);
        this.ui.setTimerMaximum(timeInSecs);
    }
    
    /**
     * Displays the results of a round to the player.
     *
     * @param   round   <code>Round</code> to display.
     */
    public void results(Round round) {
        //TODO should pass this down to UI for display?  Or a separate class?
        Turn[] turns = round.getTurns();
        final String BR = System.getProperty("line.separator");
        StringBuffer summary = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < turns.length; i++) {
            //TODO Could somehow enforce no name conflicts.
            //TODO Could check which is you for prominent display?
            //TODO should have better display all around not (in another panel 
            // not a dialog)?
            //TODO Could collect in a data structure and display in a table or 
            // other.  Could use HTML and that text component that takes HTML.
            summary.append(turns[i].namedClient().name());
            summary.append(" scored ");
            summary.append(turns[i].score());
            summary.append(BR);
            sb.append(turns[i].namedClient().name());
            sb.append(" scored ");
            sb.append(turns[i].score());
            sb.append(BR);
            sb.append("Unique words: ");
            sb.append(turns[i].getUniqueWords());
            sb.append(BR);
            sb.append("Duplicate words: ");
            sb.append(turns[i].getDuplicateWords());
            sb.append(BR);
            sb.append("Misspelt words: ");
            sb.append(turns[i].getMisspeltWords());
            sb.append(BR);
            sb.append("Too short words: ");
            sb.append(turns[i].getTooShortWords());
            sb.append(BR);
            sb.append("Not on board words: ");
            sb.append(turns[i].getNotOnBoardWords());
            sb.append(BR);
            sb.append(BR);
        }
        this.ui.showResults(summary.toString() + BR + sb.toString());
    }
    
    /** 
     * Periodic check that sends results to clients if it is the appropriate 
     * time.
     */
    public void checkResultsSend() {
        if (localServer != null) {
            //TODO stoopid name in both places.
            this.localServer.checkResultsSend();
        }
    }
    
    /**
     * Gets the <code>GameId</code> of the game running on this player's local 
     * server.
     * 
     * @return  <code>GameId</code>.
     */
    public GameId getServerGameId() {
        return this.localServer.getGameId();
    }
}
