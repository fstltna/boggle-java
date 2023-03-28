/*
 * IClient
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
 * Interface exposing remote methods available on a Boggle client.
 *
 * TODO notes about rmic and how this works.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public interface IClient extends Remote {

    /**
     * Notifies the client to initialise the board and start the timer.
     *
     * @param   letters         Letters to initialise the board with.
     * @param   timeInSecs      Time of game in seconds.
     * @param   gameId          Universally unique identifier for this game.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public void startGame(char[][] letters, int timeInSecs, GameId gameId) 
            throws RemoteException;
    
    /**
     * Notifies the client of the results of a game which are then 
     * processed by this method.
     *
     * @param   round           Results of a round of Boggle.
     * @param   rubber          Details of the rubber.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public void results(Round round, Rubber rubber) throws RemoteException;
    
    
    /**
     * Determines if this Client is actively connected to the specified game.
     *
     * @param   gameId          <code>GameID</code> of game to check for 
     *                          participation in.
     * @return                  Returns <code>true</code> if playing the game 
     *                          represented by <code>gameId</code>.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public boolean isActive(GameId gameId) throws RemoteException;
    
    //TODO results needn't send Rubber now could do it with this?
    /**
     * Sends the current rubber details to the client.  The implication of
     * this method being called is that there may have been a change and 
     * something on the client may need updating (score table, etc.)
     *
     * @param   rubber          <code>Rubber</code> details of the current 
     *                          rubber.
     * @throws  RemoteException If anything goes wrong with the RMI.
     */
    public void setRubber(Rubber rubber) throws RemoteException;
}
