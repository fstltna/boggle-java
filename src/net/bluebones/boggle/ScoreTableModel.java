/*
 * ScoreTableModel
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

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * TableModel for the table of scores in ScorePanel.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.3 $
 * @see     ScorePanel
 * @see     javax.swing.table.TableModel
 */
public class ScoreTableModel extends AbstractTableModel {

    private Object[][] data;
    private String[] cols = new String[] { "Name", "Score" };
    private NamedClient[] namedClients;
    
    /**
     * Initializes a new <code>ScoreTableModel</code> with the specified values.
     *
     * @param   r   <code>Rubber</code> containing values to initialize the 
     *              model with.
     */
    public ScoreTableModel(Rubber r) {
        //TODO scores should be sorted by scores then name
        // it's not as simple as it looks!
        Map scoresMap;
        if (r == null) {
            scoresMap = new HashMap();
        } else {
            scoresMap = r.getScores();
        }
        data = new Object[cols.length][scoresMap.size()];
        this.namedClients = new NamedClient[scoresMap.size()];
        int i = 0;
        for (Iterator iter = scoresMap.entrySet().iterator(); 
                iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            //TODO rationalise name and getName - there has to be a 
            // rule!
            NamedClient namedClient = (NamedClient) entry.getKey();
            data[0][i]   = namedClient.name();
            data[1][i]   = entry.getValue();
            namedClients[i]   = namedClient;
            i++;
        }
    }
    
    /** 
     * Gets the number of columns in the table data.
     *
     * @return  <code>int</code> number of columns in the table data.
     */
    public int getColumnCount() { return cols.length; }
    
    /** 
     * Gets the number of rows in the table data.
     *
     * @return  <code>int</code> number of rows in the table data.
     */
    public int getRowCount() { return data[0].length;}
    
    /**
     * Gets the name of the column at position <code>i</code>.
     *
     * @param   i   <code>int</code> column number to return name of.
     * @return      <code>String</code> name of column.
     */
    public String getColumnName(int i) { return cols[i]; }
    
    /**
     * Gets the cell contents at <code>row</code>, <code>col</code>.
     *
     * @param   row <code>int</code> row number.
     * @param   col <code>int</code> col number.
     * @return      <code>Object</code> value in specified cell.
     */
    public Object getValueAt(int row, int col) { 
        //TODO don't like the way they go row, col and I go col, row.
        return this.data[col][row];
    }
    
    /**
     * Gets the <code>NamedClient</code> represented in the specified row.
     *
     * @param   row                     <code>int</code> row number.
     * @return                          <code>NamedClient</code> for specified 
     *                                  row.
     * @throws  IllegalStateException   If no clients have been set in this 
     *                                  model.
     */
    public NamedClient getClientAt(int row) {
        if (namedClients == null) {
            throw new IllegalStateException("No clients have been set in "
                + "this model");
        }
        return this.namedClients[row];
    }
}
