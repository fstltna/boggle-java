/*
 * DieIcon
 * 
 * Thomas David Baker <bakert@gmail.com>, 2004-09-25
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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 * Class that creates die images.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class DieIcon {

    //TODO bad name?

    private DieIcon() {}
    
    /**
     * Creates an image of a die.
     *
     * @param   text    <code>String</code> text to appear on the die.  This 
     *                  should be no more than two characters.
     * @param   theme   <code>Theme</code> to use to generate the die.
     */
    public static ImageIcon getDie(String text, Theme theme) {
        return DieIcon.getDie(text, theme, false);
    }
    
    /**
     * Creates an image of a die.
     *
     * @param   text    <code>String</code> text to appear on the die.  This 
     *                  should be no more than two characters.
     * @param   theme   <code>Theme</code> to use to generate the die.
     * @param   jumbled <code>boolean</code>, whether to randomly orient the die
     *                  (either straight, upside down, left, or right) like in 
     *                  the dice game.  If <code>false</code>, die will be 
     *                  straight.
     */
    public static ImageIcon getDie(String text, Theme theme, boolean jumbled) {
        //TODO could have a background image param
        //TODO should dice always be squares and only one param for size
        // with proportional font?
        if (theme == null) {
            return new ImageIcon();
        }
        int width = theme.diceSize();
        int height = theme.diceSize();
        Color background = theme.diceColor();
        Color foreground = theme.textColor();
        float baseFontSize = ((float) (theme.diceSize() * .8));
        Font font = theme.font();
    
        //TODO make Q bigger and u smaller rather than just rendering both at a 
        // smaller size.
        float fontSize = (text.length() > 1 
            ? (float) (baseFontSize * .7) : (float) baseFontSize);
        font = font.deriveFont(fontSize);
        
        BufferedImage buffer = new BufferedImage(1, 1, 
            BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = buffer.createGraphics();
        FontRenderContext fc = graphics.getFontRenderContext();
        
        //TODO Put the underline on the Z and make letters appear at different 
        // rotations?
        AttributedString attString = new AttributedString(text);
        attString.addAttribute(TextAttribute.FONT, font);
        attString.addAttribute(TextAttribute.FOREGROUND, foreground);
        attString.addAttribute(TextAttribute.BACKGROUND, background);
        Rectangle2D bounds = new java.awt.font.TextLayout(text, font, 
            fc).getBounds();
        
        float boundsWidth = (float) bounds.getWidth();
        float boundsHeight = (float) bounds.getHeight();
        if (text.length() > 1) {
            font = font.deriveFont(fontSize);
            attString.addAttribute(TextAttribute.FONT, font, 1, text.length());
        }
        
        // I don't know why it is better with that unmathematcial negative 
        // adjustment to xStart, it just is.
        float xStart = (float) ((width - boundsWidth) / 2) - (width / 20);
        float yStart = (float) ((height - boundsHeight) / 2) + boundsHeight;
        
        /*
        Boggle.debug("Height: " + height + "; Width: " + width);
        Boggle.debug("boundsHeight: " + boundsHeight + "; boundsWidth: " 
            + boundsWidth);
        Boggle.debug("xStart: " + xStart + ", yStart: " + yStart);
        */
        
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = buffer.createGraphics();
        //XXX need underlines on the right letters/all letters?
        //TODO make the dice look more like dice?
        if (jumbled) {
            double quarterOfACircleInRadians = (Math.PI / 2);
            double rotation 
                = quarterOfACircleInRadians * (double) new Random().nextInt(4);
            graphics.rotate(rotation, width / 2, height / 2);
        }
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(background);
        graphics.fillRect(0, 0, width, height);
        graphics.drawString(attString.getIterator(), xStart, yStart);
        
        return new ImageIcon(buffer);
    }
}
