/*
 * Timer
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Timer visual component.
 *
 * @author  Thomas David Baker
 * @version 0.3 $Revision: 1.3 $
 */
public class Timer extends JPanel {
    
    private JLabel maxLabel = new JLabel();
    private JProgressBar progressBar;

    /** Initialises a new Timer. */
    public Timer() {


        progressBar = getProgressBar();

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(maxLabel, c);
        maxLabel.setPreferredSize(new Dimension(100, 20));
        this.add(maxLabel);
        
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(progressBar, c);
        progressBar.setPreferredSize(new Dimension(200, 20));
        this.add(progressBar);
    }

    /**
     * Sets the timer to the specified amount.
     *
     * @param   tenthsOfASecond Amount in tenths of a second.
     */
    public void setTimer(int tenthsOfASecond) {
        this.progressBar.setValue(tenthsOfASecond);
    }
    
    /** Sets the timer progress bar back to 0. */
    public void resetTimer() {
        this.progressBar.setValue(0);
    }
    
    /**
     * Sets the maximum time of this timer.
     *
     * @param   timeInSecs  Time in seconds to be set as this timer's maximum.
     */
    public void setTimerMaximum(int timeInSecs) {
        this.progressBar.setMaximum(timeInSecs * 10);
        int mins = timeInSecs / 60;
        int secs = timeInSecs % 60;
        String s = (mins >= 1 ? mins + " minute" : "");
        s += (mins > 1 ? "s" : "");
        s += (mins > 1 && secs > 1 ? " " : "");
        s += (secs > 0 ? secs + " second" : "");
        s += (secs > 1 ? "s" : "");
        this.maxLabel.setText(s);
    }
    
    private JProgressBar getProgressBar() {
        JProgressBar progressBar = new JProgressBar(0, 
            Boggle.timeInSecs * 10);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        return progressBar;
    }
}
