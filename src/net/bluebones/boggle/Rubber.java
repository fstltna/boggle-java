/*
 * Rubber
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class representing an ongoing series of games.  Keeps track of cumulative 
 * scores of the participants.
 *
 * @author  Thomas David Baker, bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Rubber implements Serializable {

    Map<NamedClient, Integer> scores = new HashMap<NamedClient, Integer>();

    /** Initializes an empty <code>Rubber</code>. */
    public Rubber() {}
    
    /**
     * Adds the specified client to this <code>Rubber</code>.
     *
     * @param   namedClient <code>NamedClient</code> to add.
     */
    public void addClient(NamedClient namedClient) {
        this.scores.put(namedClient, new Integer(0));
    }
    
    /**
     * Adds the scores from <code>round</code> to this <code>Rubber</code>'s 
     * totals.
     * 
     * @param   round   <code>Round</code> to add.
     */
    public void addScores(Round round) {
        Turn[] turns = round.getTurns();
        for (int i = 0; i < turns.length; i++) {
            addScore(turns[i]);
        }
    }

    //TODO longest word of the round.  Longest unique word of the round.  at top 
    // of results.
    
    private void addScore(Turn turn) {
        Boggle.debug("adding score for " + turn.namedClient().name());
        Object o = this.scores.get(turn.namedClient());
        int score = (o == null ? 0 : ((Integer) o).intValue());
        this.scores.put(turn.namedClient(), 
            new Integer(turn.score() + score));
    }
    
    /**
     * Gets the current scores.
     *
     * @return  <code>Map</code> of <code>NamedClient</code> = <code>int</code> 
     *          scores.
     */
    public Map getScores() {
        return this.scores;
    }

    /**
     * Remove <code>client</code> from the <code>Rubber</code>.
     *
     * @param   client  <code>NamedClient</code> to remove.
     */
    public void remove(NamedClient client) {
        this.scores.remove(client);
    }
    
    /**
     * Remove all <code>NamedClient</code>s in <code>clients</code> from
     * this <code>Rubber</code>.
     *
     * @param   clients <code>Collection</code> to remove.
     */
    public void removeAll(Collection clients) {
        for (Iterator iter = clients.iterator(); iter.hasNext(); ) {
            NamedClient client = (NamedClient) iter.next();
            remove(client);
        }
    }
}
