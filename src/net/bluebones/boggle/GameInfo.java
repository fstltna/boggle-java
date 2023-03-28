/*
 * GameInfo
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

/**
 * Data holder class that holds the information necessary to join or host a 
 * game.
 *
 * @author  Thomas David Baker, bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class GameInfo {
    
    private String name = "hi";
    private HostInfo hostInfo;

    /**
     * Initialises new GameInfo with the specified values.
     *
     * @param   name        Name of the player.
     * @param   hostInfo    HostInfo of the host of the game.
     */
    public GameInfo(String name, HostInfo hostInfo) {
        this.name = name;
        this.hostInfo = hostInfo;
    }
    
    /**
     * Name of the player.
     *
     * @return  Player's name.
     */
    public String name() {
        return this.name;
    }
    
    /**
     * HostInfo of this game.
     *
     * @return  HostInfo of this game.
     */
    public HostInfo hostInfo() {
        return this.hostInfo;
    }
    
    /**
     * Simple commandline test of GameInfo.
     *
     * @param   args    Commandline arguments.
     */
    public static void main(String[] args) {
        GameInfo gi = new GameInfo("Tom", new HostInfo("bluebones.net", 1099));
        System.out.println("Should be:");
        System.out.println("Tom@bluebones.net:1099");
        System.out.println("Is:");
        System.out.println(gi.name() + "@" + gi.hostInfo().address() + ":" 
            + gi.hostInfo().port());
    }
}
