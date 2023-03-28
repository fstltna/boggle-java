/*
 * Dictionary
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a dictionary in which words can be checked for validity.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.4 $
 */
public class Dictionary {

    private Set<String> words = new HashSet<String>();
    
    /**
     * Initialises a Dictionary using the specified file.
     *
     * @param   f                       File to use for the dictionary.
     * @throws  FileNotFoundException   If the file cannot be found.
     * @throws  IOException             If there is any problem reading the 
     *                                  dictionary.
     */
    public Dictionary(File f) throws FileNotFoundException, IOException {
        if (! f.exists()) {
            throw new FileNotFoundException("Could not find " 
                + f.getAbsolutePath());
        }
        BufferedReader in = new BufferedReader(new FileReader(f));
        loadDictionary(in);
    }
    
    //TODO should all throw DictionaryUnavailableException?
    
    /**
     * Initialises a <code>Dictionary</code> using the specified 
     * <code>InputStream</code>.
     *
     * @param   is          InputStream to initialise the dictionary from.
     * @throws  IOException If there is any problem reading the dictionary.
     */
    public Dictionary(java.io.InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        loadDictionary(in);
    }
    
    private void loadDictionary(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            this.words.add(line.trim());
        }
    }
    
    /**
     * Determines if the specified word is valid (in the dictionary).
     *
     * @param   word    Word to check.
     * @return          <code>boolean</code>, <code>true</code> if the word is 
     *                  in the dictionary.
     */
    public boolean isValid(String word) {
        return this.words.contains(word.toUpperCase());
    }
    
    /**
     * Simple commandline test of Dictionary.
     *
     * @param   args                Commandline arguments.
     * @throws  FileNotFoundException   If the file cannot be found.
     * @throws  IOException             If there is any problem reading the 
     *                                  dictionary.
     */
    public static void main(String[] args) throws FileNotFoundException, 
            IOException {
        System.out.println("Should be true, false, true, false, true");
        System.out.println(Boggle.dictionary.isValid("hello"));
        System.out.println(Boggle.dictionary.isValid("jfkal"));
        System.out.println(Boggle.dictionary.isValid("Hello"));
        System.out.println(Boggle.dictionary.isValid("FAOI)"));
        System.out.println(Boggle.dictionary.isValid("HELLO"));
    }
}
