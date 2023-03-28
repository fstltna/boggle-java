/*
 * Game
 *
 * Thomas David Baker <bakert@gmail.com>, 2004-09-04
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

/**
 * Represents a local instance of a game of Boggle.  To be run in a separate 
 * thread.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Game implements Runnable {
    
    private int timeInSecs = 180;
    private boolean live = true;
    private Player player;
    
    /**
     * Initialises a game.
     *
     * @param   player      Player playing this game.
     * @param   letters     Letters to put on the board in this game.
     * @param   timeInSecs  Duration of the game in seconds.
     */
    public Game(Player player, char[][] letters, int timeInSecs) {
        this.player = player;
        this.player.init(letters, timeInSecs);
        this.timeInSecs = timeInSecs;
    }
    
    /** Initialises this game and then runs it. */
    public void run() {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis() + (this.timeInSecs * 1000);
        while (System.currentTimeMillis() < end) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Boggle.debug(e.toString());
                //TODO ignoring exceptions?
            }
            if (! live) {
                Boggle.debug("This game is not live, not going to finish it.");
                return;
            }
            this.player.setTimer(
                (int) ((System.currentTimeMillis() - start) / 100));
        }
        this.player.endGame();
    }
    
    /** Stops this game. */
    public void kill() {
        this.live = false;
    }
}
