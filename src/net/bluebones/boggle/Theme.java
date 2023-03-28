/*
 * Theme
 *
 * Thomas David  Baker <bakert@gmail.com>, 2004-09-24
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

import java.awt.Color;
import java.awt.Font;
import java.util.prefs.Preferences;

/**
 * Represents a set of appearance settings for Boggle.  Also provides methods of 
 * storing and retrieving the current Theme.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 */
public class Theme implements Cloneable {
    //TODO discuss Cloneable
    
    private String name;
    private int diceSize;
    private Color textColor;
    private Color diceColor;
    private Font font;
    
    //TODO letters are often too high up -- need to baseline them somehow.

    /**
     * Initializes a new <code>Theme</code>.
     *
     * @param   name        Name of the theme.
     * @param   diceSize    Size of dice in pixels.
     * @param   textColor   Color of dice text.
     * @param   diceColor   Color of dice background.
     * @param   font        Font of dice text.
     */
    public Theme(String name, int diceSize, Color textColor, Color diceColor, 
            Font font) {
        //TODO skin each die not just all die
        this.name = name;
        this.diceSize = diceSize;
        this.textColor = textColor;
        this.diceColor = diceColor;
        this.font = font;
    }
    
    /**
     * Gets the <code>Theme</code> specified by <code>name</code>/
     *
     * @return                              <code>Theme</code> with that name.
     * @throws  IllegalArgumentException    If the specified theme is not in 
     *                                      preferences on this computer.
     */
    public static Theme getTheme(String name) {
        //throws IllegalArgumentException if theme not in prefs
        if (name == null) {
            return null;
        }
        Preferences prefs = Preferences.userNodeForPackage(Theme.class);
        String themes = prefs.get("themes", "");
        if (themes.indexOf(name) == -1) {
            throw new IllegalArgumentException("That theme " + name 
                + " is not found in user's themes (" + themes + ").");
        }
        final int DEFAULT_DICE_SIZE = 55;
        int diceSize = prefs.getInt(name + ".dicesize", DEFAULT_DICE_SIZE);
        Color textColor = new Color(prefs.getInt(name + ".textcolor", 0));
        Color diceColor 
            = new Color(prefs.getInt(name + ".dicecolor", Integer.MAX_VALUE));
        Font font = new Font(prefs.get(name + ".font", "Sans Serif"), 
            Font.PLAIN, diceSize);
        
        //TODO if only part of theme retrieved then send back total default 
        //rather than the gap-filling above
        
        return new Theme(name, diceSize, textColor, diceColor, font);
    }

    /**
     * Saves <code>theme</code> to this computer's preferences.
     *
     * @param   theme   <code>Theme</code> to save.
     */
    public static void saveTheme(Theme theme) {
        //TODO deal with commas, two called the same thing, etc.
        Preferences prefs = Preferences.userNodeForPackage(Theme.class);
        String themes = prefs.get("themes", "Default");
        prefs.put("themes", themes + "," + theme.name());
        prefs.putInt(theme.name() + ".dicesize", theme.diceSize());
        prefs.putInt(theme.name() + ".textcolor", theme.textColor().getRGB());
        prefs.putInt(theme.name() + ".dicecolor", theme.diceColor().getRGB());
        prefs.put(theme.name() + ".font", theme.font().getFontName());
    }
    
    /**
     * Gets the current saved default theme.
     *
     * @return  Current <code>Theme</code>.
     */
    public static Theme getCurrentTheme() {
        Preferences prefs = Preferences.userNodeForPackage(Theme.class);
        Theme theme;
        try {
            Boggle.debug("Trying to get current.theme.");
            theme = Theme.getTheme(prefs.get("current.theme", "Default"));
            Boggle.debug("Have got current.theme.");
        } catch (IllegalArgumentException e) {
            //TODO make the exception thrown here a must catch one?
            theme = new Theme("Default", 55, Color.BLACK, Color.WHITE, 
                new Font("Sans Serif", Font.PLAIN, 50));
        }
        return theme;
    }
    
    /**
     * Saves the specified theme in this computer's preferences as the current 
     * theme.
     *
     * @param   theme   <code>Theme</code> to save.
     */
    public static void saveCurrentTheme(Theme theme) {
        Preferences prefs = Preferences.userNodeForPackage(Theme.class);
        Boggle.debug("Saving current.theme.");
        prefs.put("current.theme", theme.name());
    }
    

    /**
     * Gets the name of this theme.
     *
     * @return  <code>String</code> theme name.
     */
    public String name() {
        return this.name;
    }
    
    /**
     * Gets the size of the dice (in pixels) in this theme.
     *
     * @return  Size of dice in pixels.
     */
    public int diceSize() {
        //TODO is it in pixels or is it in points?  Or are they the same?
        return this.diceSize;
    }
    
    /**
     * Gets the color of the text in this theme.
     *
     * @return  <code>Color</code> of text.
     */
    public Color textColor() {
        return this.textColor;
    }
    
    /**
     * Gets the color of the dice in this theme.
     *
     * @return  <code>Color</code> of dice.
     */
    public Color diceColor() {
        return this.diceColor;
    }
    
    /**
     * Gets the font of this <code>Theme</code>.
     *
     * @return  <code>Font</code> used in this <code>Theme</code>.
     */
    public Font font() {
        return this.font;
    }
    
    /**
     * Sets the name of this theme.
     *
     * @param   name    <code>String</code> name of theme.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the dice size.
     *
     * @param   diceSize    <code>int</code> size in pixels.
     */
    public void setDiceSize(int diceSize) {
        this.diceSize = diceSize;
    }
    
    /**
     * Sets the text color.
     *
     * @param   color   <code>Color</code> to set.
     */
    public void setTextColor(Color color) {
        this.textColor = color;
    }
    
    /**
     * Sets the dice color.
     *
     * @param   color   <code>Color</code> to set.
     */
    public void setDiceColor(Color color) {
        this.diceColor = color;
    }
    
    /**
     * Set the font of this <code>Theme</code>.
     *
     * @param   font    <code>Font</code> to set.
     */
    public void setFont(Font font) {
        //TODO are we better off passing font around as a string?
        this.font = font;
    }
    
    /**
     * Creates and returns a copy of this object.
     *
     * @return                              A clone of this instance. 
     * @throws  CloneNotSupportedException  If something goes really weird.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }        
}
//TODO sort words by length in each section of results, and put score for each 

// in brackets after unique words