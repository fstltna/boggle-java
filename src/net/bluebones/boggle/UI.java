/*
 * UI
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
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 * User Interface for Boggle.
 *
 * @author  Thomas David Baker, <bakert@gmail.com>
 * @version 0.3 $Revision: 1.6 $
 */
public class UI {

    //TODO esc doesn't exit appearance dialog?

    private JFrame frame;
    private Board board;
    private WordArea wordArea;
    private Timer timer;
    private ConnectionPanel connPanel;
    private ScorePanel scorePanel;
    private JPanel mainPanel;
    private GridBagLayout layout;
    
    //TODO or use Player as the interface?
    private HumanPlayer player;

    /**
     * Initialises this UI.
     *
     * @param   player  HumanPlayer to whom this UI belongs.
     */
    public UI(HumanPlayer player) {
        
        this.player = player;
        
        this.frame = new JFrame();
        mainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        this.frame.getContentPane().add(scrollPane);
        
        //TODO left hand side should be in a panel?
        
        layout = new GridBagLayout();
        mainPanel.setLayout(layout);

        newBoard();
        
        timer = new Timer();
        timer.setPreferredSize(new Dimension(400, 20));
        GridBagConstraints cTimer = new GridBagConstraints();
        cTimer.gridx = 0;
        cTimer.gridy = 1;
        cTimer.insets = new Insets(10, 10, 10, 10);
        cTimer.fill = GridBagConstraints.HORIZONTAL;
        cTimer.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(timer, cTimer);
        mainPanel.add(timer);
        
        //TODO every item should have its own GridBagConstraints everywhere.
        
        connPanel = new ConnectionPanel();
        GridBagConstraints cConn = new GridBagConstraints();
        cConn.gridx = 0;
        cConn.gridy = 2;
        cConn.insets = new Insets(10, 10, 10, 10);
        cConn.fill = GridBagConstraints.HORIZONTAL;
        cConn.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(connPanel, cConn);
        mainPanel.add(connPanel);
       
        //TODO it would be better for WordArea to get smaller as window shrinks 
        // rather than scrolling.
        wordArea = new WordArea();
        GridBagConstraints cWord = new GridBagConstraints();
        cWord.gridx = 1;
        cWord.gridy = 0;
        cWord.weightx = 1;
        cWord.weighty = 1;
        cWord.insets = new Insets(10, 10, 10, 10);
        cWord.fill = GridBagConstraints.BOTH;
        cWord.gridheight = 5;
        cWord.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(wordArea, cWord);
        mainPanel.add(wordArea);

        //TODO reset of scores to 0
        
        scorePanel = new ScorePanel(this.player);
        GridBagConstraints cScore = new GridBagConstraints();
        cScore.gridx = 0;
        cScore.gridy = 4;
        cScore.gridheight = 1;
        cScore.weightx = 1;
        cScore.weighty = 0;
        //TODO how come these are 20s.  Insets need looking at globally.
        cScore.insets = new Insets(10, 20, 10, 20);
        cScore.fill = GridBagConstraints.HORIZONTAL;
        cScore.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(scorePanel, cScore);
        mainPanel.add(scorePanel);

        this.frame.setJMenuBar(getMenuBar());
        this.frame.setTitle("Boggle");
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        this.frame.setIconImage(new ImageIcon(this.getClass().getResource(
            "/resources/images/icon.png")).getImage());

        this.frame.pack();
        this.frame.setVisible(true);
    }
    
    private JMenuBar getMenuBar() {
        
        //TODO application help.
        //TODO rules of boggle help.

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem helpAbout = new JMenuItem("About", KeyEvent.VK_A);
        helpAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // pop up a dialog about me!
                final String BR = System.getProperty("line.separator");
                JOptionPane.showMessageDialog(frame, 
                    "Boggle" + BR 
                    + "Thomas David Baker <bakert@gmail.com>" + BR
                    + "Licensed under the GPL" + BR
                    + "http://bluebones.net/boggle/" + BR
                    + "All feedback appreciated",
                    "Boggle",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        helpMenu.add(helpAbout);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getFileMenu());
        menuBar.add(getOptionsMenu());
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private JMenu getFileMenu() {
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        //TODO clean up the way games work.  New game spawns a server but 
        // doesn't imply that it does so you are a host but you don't know it.
        // Join game does not prevent you doing New Game which makes you back
        // into a host again.  But want to make it possible to do solo/pen and 
        // paper just looking at the screen without having to host game.
        // Perhaps the user should be prompted on startup to either be a host or
        // connect.  Yes, that's it I think.  They can always change later but
        // then they will know they have done it.  Also, there should be a 
        // display of what host they are connected to and if it is them or not.
        JMenuItem fileNewGame = new JMenuItem("New Game", KeyEvent.VK_N);
        fileNewGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
            Event.CTRL_MASK));
        fileNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        fileMenu.add(fileNewGame);
        
        //TODO why can't we have H as our shortcut key?
        JMenuItem fileHostNewGame = new JMenuItem("Host New Game",     
            KeyEvent.VK_G);
        fileHostNewGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, 
            Event.CTRL_MASK));
        fileHostNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHostGameDialog();
            }
        });
        fileMenu.add(fileHostNewGame);
            
        JMenuItem fileJoinNewGame = new JMenuItem("Join New Game",
            KeyEvent.VK_J);
        fileJoinNewGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,
            Event.CTRL_MASK));
        fileJoinNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showJoinGameDialog();
            }
        });
        fileMenu.add(fileJoinNewGame);
        
        //TODO put menu in it's own class.
        
        JMenuItem fileAddComputerPlayer 
            = new JMenuItem("Add Computer Player", KeyEvent.VK_A);
        //TODO should only be enabled when a host 
        // fileAddComputerPlayer.setEnabled(false);
        //TODO accelerator?
        //TODO want to check if they are a host or anything?  Grey out if they 
        // are not?
        /* XXX this menu must only be available when there is a game you can
           add players to. */
        fileAddComputerPlayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AIDialog.showDialog(UI.this.player, UI.this.frame);
            }
        });
        fileMenu.add(fileAddComputerPlayer);
        
        JMenuItem fileExit = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(fileExit);
        
        return fileMenu;
    }
    
    private JMenu getOptionsMenu() {
        
        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);
        
        //TODO this positioning implies that it is a local setting but it is a 
        // host setting?
        JMenuItem optionsTime = new JMenuItem("Time", KeyEvent.VK_T);
        optionsTime.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
            Event.CTRL_MASK));
        optionsTime.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String) JOptionPane.showInputDialog(frame, 
                    "How many seconds?", "Time", 
                    JOptionPane.QUESTION_MESSAGE, null, null, 
                        Integer.toString(Boggle.timeInSecs));
                // Ignore if they cancelled the dialog.
                if (s == null) {
                    return;
                }
                try {
                    Boggle.timeInSecs = Integer.parseInt(s);
                } catch (NumberFormatException ex) {
                    //TODO better to throw up a dialog and stay on this dialog
                    // like we do with port number.
                    problem("Please enter a whole number.");
                }
            }

        });
        optionsMenu.add(optionsTime);
        
        JMenuItem optionsAppearance = new JMenuItem("Appearance", 
            KeyEvent.VK_A);
        optionsAppearance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AppearanceDialog.showDialog(frame, UI.this);
            }
        });
        optionsMenu.add(optionsAppearance);
        
        JMenu optionsBoardSize = new JMenu("Board Size");
        optionsBoardSize.setMnemonic(KeyEvent.VK_B);
        
        JMenuItem optionsBoardSizeOriginal = new JMenuItem("Original (4 x 4)",
            KeyEvent.VK_O);
        optionsBoardSizeOriginal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Die.dice = Die.dice16;
                Boggle.debug("Setting totaldice to dice16 length: " 
                    + Die.dice.length);
                Boggle.setTotalDice(Die.dice.length);
                newBoard();
            }
        });
        optionsBoardSize.add(optionsBoardSizeOriginal);
    
        JMenuItem optionsBoardSizeLarge = new JMenuItem("Large (5 x 5)",
            KeyEvent.VK_L);
        optionsBoardSizeLarge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Die.dice = Die.dice25;
                Boggle.debug("Setting totaldice to dice25 length: " 
                    + Die.dice.length);
                Boggle.setTotalDice(Die.dice.length);
                newBoard();
            }
        });
        optionsBoardSize.add(optionsBoardSizeLarge);
    
        optionsMenu.add(optionsBoardSize);
         
        return optionsMenu;
    }
    
    private void newBoard() {
        Theme theme = null;
        if (this.board != null) {
            theme = this.board.getTheme();
            this.mainPanel.remove(this.board);
            
        }
        this.board = new Board();
        if (theme != null) {
            this.board.setTheme(theme);
        }
        //TODO could setBoard here but not size specific!
        GridBagConstraints cBoard = new GridBagConstraints();
        cBoard.gridx = 0;
        cBoard.gridy = 0;
        cBoard.fill = GridBagConstraints.NONE;
        cBoard.weightx = 0;
        cBoard.weighty = 0;
        cBoard.insets = new Insets(10, 10, 10, 10);
        cBoard.anchor = GridBagConstraints.NORTH;
        this.layout.setConstraints(board, cBoard);
        this.mainPanel.add(this.board);
        this.frame.pack();
    }
    
    
    /**
     * Initialises the board in this UI to hold the specified letters.
     *
     * @param   letters Letters to populate the board with.
     */
    public void setBoard(char[][] letters) {
        Boggle.debug("Going to set total dice to " + letters.length);
        Boggle.setTotalDice(letters.length * letters[0].length);
        newBoard();
        this.board.setBoard(letters);
        this.wordArea.ready();
    }
    
    /** Empties the word area and makes it possible to enter words. */
    public void initWordArea() {
        this.wordArea.clear();
        this.wordArea.setEditable(true);
    }
    
    /** Sets the timer progress bar back to 0. */
    public void resetTimer() {
        this.timer.resetTimer();
    }
    
    /**
     * Displays an information dialog with the specified message.
     *
     * @param   msg Message to display.
     */
    public void info(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays an error message dialog with the specified message.
     *
     * @param   errMsg  Error message to display.
     */
    public void problem(String errMsg) {
        JOptionPane.showMessageDialog(frame, errMsg, "Problem", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    //TODO should be done by registering an action listener with the Boggle 
    // instance?
    /** Ends the game by showing a dialog and beeping. */
    public void endGame() {
        // Beep

        System.out.print("\007"); 
        System.out.flush(); 
        this.wordArea.setEditable(false);
    }

    private void startGame() {
        this.player.startNetworkGame();
    }
    
    private void showHostGameDialog() {
        GameDialog.showHostGameDialog(this.frame);
        if (GameDialog.ok()) {
            this.player.hostGame(GameDialog.getValue());
        }
    }
    
    private void showJoinGameDialog() {
        GameDialog.showJoinGameDialog(this.frame);
        if (GameDialog.ok()) {
            this.player.joinGame(GameDialog.getValue());
        }
    }
    
    /**
     * Sets the timer to the specified amount.  Measured in tenths of a second.
     *
     * @param   tenthsOfASecond Amount to iset the timer to in in tenths of a 
     *                          second.
     */
    public void setTimer(int tenthsOfASecond) {
        timer.setTimer(tenthsOfASecond);
    }
    
    /**
     * Sets the maximum time of the timer in this UI.
     *
     * @param   timeInSecs  Maximum time to set the timer to.
     */
    public void setTimerMaximum(int timeInSecs) {
        timer.setTimerMaximum(timeInSecs);
    }
    
    /**
     * Sets the connection information visible in this UI.
     *
     * @param   type            <code>ConnectionType</code> of current 
     *                          connection.
     * @param   addr            Address of the host.
     * @param   clients         Number of clients attached to this player.
     */
    public void setConnection(ConnectionType type, String addr, int clients) {
        this.connPanel.setConnection(type, addr, clients);
    }
    
    /**
     * Sets the displayed connection in this <code>UI</code>.
     *
     * @param   type            <code>ConnectionType</code> of current 
     *                          connection.
     * @param   addr            Address of the host.
     * @param   clients         Number of clients attached to this player.
     * @param   dots            Number of dots to display (to animate while 
     *                          connecting).
     */
    public void setConnection(ConnectionType type, String addr, int clients, 
            int dots) {
        this.connPanel.setConnection(type, addr, clients, dots);
    }
    
    /**
     * Sets the displayed rubber details.
     *
     * @param   r   <code>Rubber</code> details to display.
     */
    public void setRubber(Rubber r) {
        this.scorePanel.setRubber(r);
        Boggle.debug("Setting rubber");
        this.frame.pack();
    }
    
    /**
     * Gets the words currenlty in the word area of this <code>UI</code>.
     *
     * @return  <code>String[]</code> of words.
     */
    public String[] getWords() {
        return this.wordArea.getWords();
    }
    
    /**
     * Displays the results to the user.
     *
     * @param   results <code>String</code> results to display.
     */
    public void showResults(String results) {
        Boggle.debug("Showing results.");
        new ResultsDialog(this.frame, results); 
    }
    
    /**
     * Sets the board's currently active <code>Theme</code>.
     *
     * @param   theme   <code>Theme</code> to set active.
     */
    public void setTheme(Theme theme) {
        this.board.setTheme(theme);
        this.frame.pack();
    }
    
    /**
     * Gets the board's currently active <code>Theme</code>.
     *
     * @return  <code>Theme</code>
     */
    public Theme getTheme() {
        return this.board.getTheme();
    }
    
    //TODO run that bug finder jakarta project
}
