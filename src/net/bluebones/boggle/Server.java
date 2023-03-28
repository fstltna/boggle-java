/*
 * Server
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-06
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
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Controls the network side of the application from the host/server side.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class Server extends UnicastRemoteObject implements IServer {

    private Client host;
    private Set<NamedClient> clients 
        = Collections.synchronizedSet(new HashSet<NamedClient>());
    private Set<Turn> turns = new HashSet<Turn>();
    private char[][] letters;
    private Rubber rubber;
    private Player player;
    private boolean sent = true;
    private String addr;
    private GameId gameId;
    private Set<NamedClient> waitingClients 
        = Collections.synchronizedSet(new HashSet<NamedClient>());
    private boolean gameInProgress = false;
    
    /**
     * Initialises a Server with the specified client as the host.
     *
     * @param   player                      Player this Server belongs to.
     * @param   host                        Client that is hosting this game.
     * @param   hostInfo                    IP and port information to set up 
     *                                      on.
     * @throws  RemoteException If anything goes wrong with the RMI.
     * @throws  IllegalArgumentException    If
     */
    public Server(Player player, Client host, HostInfo hostInfo) 
            throws RemoteException, IllegalArgumentException {
        Boggle.debug("Initializing a Server.");
        this.player = player;
        this.addr = hostInfo.getAddr();
        try {
            //TODO are we fucking stuff up by not doing bind and looking
            // for an alreadyboundexception?
            Naming.rebind(hostInfo.getAddr(), this);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Address of host is not a "
                + "well formed URL.  Exception was: " + e);
        }
        this.rubber = new Rubber();
        this.host = host;
        NamedClient namedClient = new NamedClient(host.getName(), host);
        clients.add(namedClient);
        this.rubber.addClient(namedClient);
        this.player.setRubber(rubber);
        //XXX this is an out and out bug - any client not attached to a game in 
        // progress will return true to isActive for this server now.
        this.gameId = new GameId(null, null);
    }
    
    /**
     * Writes the specified message to stdout.  For testing of RMI setup.
     *
     * @param   s   Message to write.
     */
    public void writeMsg(String s) {
        System.out.println(s);
    }
    
    /**
     * Adds a client to this server.  The client will henceforth be notified of 
     * game starts and will receive game results until removed.  Games will wait 
     * for this client's turn before ending.
     *
     * @param   clientAddr          RMI address on which the client can be 
     *                              invoked.
     * @param   clientName          Name of the player at 
     *                              <code>clientAddr</code>.
     * @throws  ConnectException    If the client cannot be added for some 
     *                              reason.
     * @throws RemoteException      If there is any problem with the RMI.
     */
    public void addClient(String clientAddr, String clientName) 
            throws ConnectException, RemoteException {
        Boggle.debug("Adding client at " + clientAddr);
        //TODO does client need to be a data class and a remote class not just 
        // one class?
        try {
            IClient client = (IClient) Naming.lookup(clientAddr);
            //TODO could add UID here.
            NamedClient namedClient = new NamedClient(clientName, client);
            if (this.gameInProgress) {
                waitingClients.add(namedClient);
            } else {
                clients.add(namedClient);
            }
            this.rubber.addClient(namedClient);
            setRubber();
        } catch (NotBoundException e) {
            // Don't add the client because it is not registered.
            throw new ConnectException("Cannot add client with addr "
                + clientAddr + " because of " + e, e);
        } catch (MalformedURLException e) {
            // Don't add the client because it has passed a bad URL.
            throw new ConnectException("Cannot add client with addr " 
                + clientAddr + " because of " + e, e);
        }
    }
    
    /** Initialises a game and notifies all registered clients to start. */
    public void startGame() {
        Boggle.debug("Starting a game on all clients.");
        
        clients.addAll(waitingClients);
        waitingClients.clear();
        
        this.gameId = new GameId(this.addr, new UID());
        this.gameInProgress = true;
        this.sent = false;
                
        turns = new HashSet<Turn>();
        
        // Generate the board.
        Die[][] dice = Die.rollDice(Die.dice);
        this.letters = new char[dice.length][dice[0].length];
        for (int x = 0; x < this.letters.length; x++) {
            for (int y = 0; y < this.letters[0].length; y++) {
                char c = dice[x][y].getRandomFace();
                letters[x][y] = c;
            }
        }
        Boggle.debugBoard(letters);
        
        synchronized(clients) {
            for (Iterator iter = clients.iterator(); iter.hasNext(); ) {
                NamedClient c = (NamedClient) iter.next();
                try {
                    c.client().startGame(letters, Boggle.timeInSecs, 
                        this.gameId);
                } catch (RemoteException e) {
                    Boggle.debug(e.toString());
                    clients.remove(c);
                    this.rubber.remove(c);
                    setRubber();
                }
            }
        }
    }
    
    /**
     * To be called by a client.  Passes turn information to the Server.
     *
     * @param   turn            Turn information.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public void returnResults(Turn turn) throws RemoteException {
        Boggle.debug("Getting results from a client (" 
            + turn.namedClient().name() + ").");
        this.turns.add(turn);
        checkResultsSend();
    }
    
    //TODO counts of unique, sync, etc. words (points or just number, just
    // number I think) as well as total score
    
    /**
     * Periodic check that sends results out to all clients if appropriate.
     *
     * Must be synchronized else it can be called again before it is complete 
     * and results go out more than once.
     */
    public synchronized void checkResultsSend() {

        if (this.sent) {
            return;
        }
        
        if (this.turns.size() == clients.size()) {
            Round round;
            try {
                round = new Round((Turn[]) this.turns.toArray(
                    new Turn[turns.size()]), this.letters);
                rubber.addScores(round);
                sendResults(round);
            } catch (DictionaryUnavailableException e) {
                String errMsg = "Cannot work out results because there is "
                    + "no dictionary to checkt the words with.";
                Boggle.debug(errMsg + "  Exception was: " + e);
                this.player.problem(errMsg);
            }
            clients.addAll(waitingClients);
            waitingClients.clear();
            this.sent = true;
            this.gameInProgress = false;
        }
    }
    
    /** 
     * Notifiy all registered clients of the results of a game. 
     *
     * @param   round   Results of the round.
     */
    public void sendResults(Round round) {
        Boggle.debug("Sending results to all clients.");
        synchronized(clients) {
            for (Iterator iter = clients.iterator(); iter.hasNext(); ) {
                NamedClient client = (NamedClient) iter.next();
                new Thread(new ResultsSender(this.rubber, round,
                    client, host)).start();
            }
        }
    }
    
    /** Sends up to date rubber information to all clients. */
    public synchronized void setRubber() {
        Boggle.debug("Sending rubber to all clients.");
        synchronized(clients) {
            for (Iterator iter = clients.iterator(); iter.hasNext(); ) {
                NamedClient client = (NamedClient) iter.next();
                new Thread(new RubberSender(this.rubber, client, 
                    host)).start();
            }
        }
    }
    
    /**
     * Gets the clients currently attached to this Server.
     *
     * @return  Array of clients currently attached to this Server.
     */
    public NamedClient[] getClients() {
        return (NamedClient[]) 
            (clients.toArray(new NamedClient[clients.size()]));
    }
    
    /**
     * Determines whether the specified client is a client of this Server.
     *
     * @param   client          IClient to check for.
     * @return                  boolean of whether the specified client is a 
     *                          client of this Server.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public boolean isClient(IClient client) throws RemoteException {
        synchronized(clients) {
            for (Iterator iter = clients.iterator(); iter.hasNext(); ) {
                NamedClient namedClient = (NamedClient) iter.next();
                if (namedClient.client().equals(client)) {
                    return true;
                }
            }
        }
        synchronized(waitingClients) {
            for (Iterator iter = waitingClients.iterator(); iter.hasNext(); ) {
                NamedClient namedClient = (NamedClient) iter.next();
                if (namedClient.client().equals(client)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Removes all the clients in <code>clients</code> from the list of clients 
     * of this server.  Clients in the collection will no longer receive updates
     * from this server without re-registering.  Update the rubber details to 
     * reflect the removal.
     *
     * @param   clients <code>Collection</code> of clients to remove.
     */
    public void removeClients(Collection clients) {
        Boggle.debug("Removing " + clients.size() + " clients");
        this.clients.removeAll(clients);
        this.rubber.removeAll(clients);
        setRubber();
    }
    
    /**
     * Gets the <code>GameId</code> of the game currently running on this 
     * server.
     *
     * @return  <code>GameId</code> of current game.
     */
    public GameId getGameId() {
        return this.gameId;
    }

    /**
     * A simple commandline test of Server.
     *
     * @param   args                    Commandline arguments.
     * @throws  RemoteException         If anything goes wrong with the RMI.
     * @throws  UnknownHostException    If can't detect local IP.
     */
    public static void main(String[] args) throws RemoteException, 
            UnknownHostException {
        Client host = new Client(new HumanPlayer(), "TestHost");
        HostInfo hostInfo = new HostInfo("127.0.0.1", 1099);
        Server c = new Server(new HumanPlayer(), host, hostInfo);
        c.writeMsg("message");
    }
}
