/*
 * AppearanceDialog
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog box that takes appearance information.
 *
 * @author  Thomas David Baker <bakert@gmail.com>
 * @version 0.3 $Revision: 1.5 $
 */
public class AppearanceDialog extends JDialog {
    
    private UI ui;
    private Theme potentialTheme;
    private JTextField fontField;
    private JButton applyButton;
    private JLabel preview;
    private JComboBox themesComboBox;
    private JSpinner diceSizeSpinner;
    private JComboBox fontComboBox;

    /**
     * Initializes a new <code>AppearanceDialog</code>.
     *
     * @param   owner   <code>Frame</code> within which this dialog should 
     *                  appear.
     * @param   ui      <code>UI</code> which this dialog is to alter.
     */
    public AppearanceDialog(Frame owner, UI ui) {
        
        super(owner, "Appearance");

        this.ui = ui;
        
        GridBagLayout layout = new GridBagLayout();
        this.getContentPane().setLayout(layout);
        
        JPanel themePanel = getThemePanel();
        GridBagConstraints cTheme = new GridBagConstraints();
        cTheme.gridx = 0;
        cTheme.gridy = 0;
        cTheme.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(themePanel, cTheme);
        this.getContentPane().add(themePanel);
        
        JPanel previewPanel = getPreviewPanel();
        GridBagConstraints cPreview = new GridBagConstraints();
        cPreview.gridx = 0;
        cPreview.gridy = 2;
        cPreview.anchor = GridBagConstraints.NORTHWEST;
        cPreview.fill = GridBagConstraints.HORIZONTAL;
        cPreview.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(previewPanel, cPreview);
        this.getContentPane().add(previewPanel);

        JPanel buttonPanel = getButtonPanel();
        GridBagConstraints cButt = new GridBagConstraints();
        cButt.gridx = 0;
        cButt.gridy = 3;
        cButt.anchor = GridBagConstraints.SOUTHEAST;
        cButt.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(buttonPanel, cButt);
        this.getContentPane().add(buttonPanel);

        //TODO theme details should hug NORTHWEST (for when preview is big).
        
        JPanel detailPanel = getDetailPanel();
        GridBagConstraints cDetail = new GridBagConstraints();
        cDetail.gridx = 0;
        cDetail.gridy = 1;
        cDetail.anchor = GridBagConstraints.NORTHWEST;
        cDetail.fill = GridBagConstraints.HORIZONTAL;
        cDetail.insets = new Insets(10, 10, 10, 10);
        //TODO OK Cancel Apply align right.
        layout.setConstraints(detailPanel, cDetail);
        this.getContentPane().add(detailPanel);

        try {
            this.potentialTheme = (Theme) ui.getTheme().clone();
        } catch (CloneNotSupportedException e) {
            Boggle.problem("Could not copy theme.  This can probably be "
                + "ignored but changes you make to the board's appearance "
                + "may not work.  Exception was: " + e);
        }
        setDialogValues(this.potentialTheme, false);
        
        this.setResizable(false);
        this.pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
    
    private JPanel getThemePanel() {
        
        JPanel panel = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        
        //TODO a "Restore Defaults" button or is that covered by setting theme 
        // back to Default?
        
        JLabel themesLabel = new JLabel("Theme:");
        themesLabel.setDisplayedMnemonic(KeyEvent.VK_T);
        GridBagConstraints cThemesLab = new GridBagConstraints();
        cThemesLab.gridx = 0;
        cThemesLab.gridy = 1;
        cThemesLab.gridwidth = 2;
        cThemesLab.anchor = GridBagConstraints.NORTHWEST;
        //TODO or put label and thing together and give them 10 10 10 insets?
        cThemesLab.insets = new Insets(10, 10, 0, 10);
        layout.setConstraints(themesLabel, cThemesLab);
        panel.add(themesLabel);
                
        //TODO way to delete themes
        // TODO add some more default themes
        this.themesComboBox = new JComboBox();
        refreshThemesBox();
        themesLabel.setLabelFor(themesComboBox);
        themesComboBox.setPreferredSize(new Dimension(200, 20));
        themesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                themeChanged();
            }
        });
        GridBagConstraints cThemesLis = new GridBagConstraints();
        cThemesLis.gridx = 0;
        cThemesLis.gridy = 2;
        cThemesLis.gridwidth = 2;
        cThemesLis.fill = GridBagConstraints.HORIZONTAL;
        cThemesLis.insets = new Insets(0, 10, 10, 10);
        cThemesLis.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(themesComboBox, cThemesLis);
        panel.add(themesComboBox);
        
        JButton saveAsButton = new JButton("Save As  ...");
        saveAsButton.setMnemonic(KeyEvent.VK_A);
        saveAsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTheme();
            } 
        });
        GridBagConstraints cSaveAs = new GridBagConstraints();
        cSaveAs.gridx = 2;
        cSaveAs.gridy = 2;
        cSaveAs.insets = new Insets(0, 10, 10, 10);
        cSaveAs.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(saveAsButton, cSaveAs);
        panel.add(saveAsButton);
        
        return panel;
    }
    
    private JPanel getPreviewPanel() {
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Preview"));
        preview = new JLabel();
        preview.setBorder(BorderFactory.createRaisedBevelBorder());
        panel.add(preview);
        return panel;
    }

    //TODO font size?
    //TODO java look and feel can be chosen here too?
    
    private JPanel getDetailPanel() {
        
        JPanel panel = new JPanel();
        
        panel.setBorder(BorderFactory.createTitledBorder("Theme Details"));
        
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
     
        JLabel diceSizeLabel = new JLabel("Dice Size:");
        diceSizeLabel.setDisplayedMnemonic(KeyEvent.VK_S);
        GridBagConstraints cDiceLab = new GridBagConstraints();
        cDiceLab.gridx = 0;
        cDiceLab.gridy = 2;
        //TODO slider for dice size?
        cDiceLab.gridwidth = 2;
        cDiceLab.insets = new Insets(10, 10, 0, 10);
        cDiceLab.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(diceSizeLabel, cDiceLab);
        panel.add(diceSizeLabel);
        
        //TODO diceSizeSpinner and fontComboBox don't get their focus from the 
        // label mnemonics quite as nicely as they should (must release letter 
        // key before Alt key).  Is there anything we can do?
        
        final int MAX_DICE_SIZE = 999;
        this.diceSizeSpinner = new JSpinner(
            new SpinnerNumberModel(1, 1, MAX_DICE_SIZE, 1));
        diceSizeLabel.setLabelFor(diceSizeSpinner);
        diceSizeSpinner.setPreferredSize(new Dimension(50, 50));
        diceSizeSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setDiceSize();
            }
        });
        GridBagConstraints cDiceSpin = new GridBagConstraints();
        cDiceSpin.gridx = 0;
        cDiceSpin.gridy = 3;
        cDiceSpin.gridwidth = 2;
        cDiceSpin.anchor = GridBagConstraints.NORTHWEST;
        cDiceSpin.insets = new Insets(0, 10, 10, 10);
        cDiceSpin.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(diceSizeSpinner, cDiceSpin);
        panel.add(diceSizeSpinner);
        
        JLabel fontLabel = new JLabel("Font:");
        fontLabel.setDisplayedMnemonic(KeyEvent.VK_F);
        GridBagConstraints cFontLab = new GridBagConstraints();
        cFontLab.gridx = 2;
        cFontLab.gridy = 2;
        cFontLab.gridwidth = 2;
        cFontLab.anchor = GridBagConstraints.NORTHWEST;
        cFontLab.insets = new Insets(10, 10, 0, 10);
        cFontLab.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(fontLabel, cFontLab);
        panel.add(fontLabel);
        
        final Font[] fonts 
            = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        // TODO add arial to the list if it isn't there so we can use it as a 
        // default?
        final String[] fontNames = new String[fonts.length];
        for (int i = 0; i < fonts.length; i++) {
            fontNames[i] = fonts[i].getFontName();
        }
        this.fontComboBox = new JComboBox(fontNames);
        fontLabel.setLabelFor(fontComboBox);
        fontComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fontChanged(fonts);
            }
        });
        JScrollPane fontComboBoxScrollPane = new JScrollPane(fontComboBox);
        GridBagConstraints cFontLis = new GridBagConstraints();
        cFontLis.gridx = 2;
        cFontLis.gridy = 3;
        cFontLis.gridwidth = 2;
        cFontLis.insets = new Insets(0, 10, 10, 10);
        cFontLis.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(fontComboBoxScrollPane, cFontLis);
        panel.add(fontComboBoxScrollPane);
        
        JLabel textColorLabel = new JLabel("Text Color:");
        textColorLabel.setDisplayedMnemonic(KeyEvent.VK_E);
        GridBagConstraints cTxtColLab = new GridBagConstraints();
        cTxtColLab.gridx = 0;
        cTxtColLab.gridy = 4;
        cTxtColLab.insets = new Insets(10, 10, 0, 10);
        cTxtColLab.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(textColorLabel, cTxtColLab);
        panel.add(textColorLabel);
        
        //TODO customize color choosers

        //TODO should be like in desk.cpl color square with down arrow.
        JButton textColorButton = new JButton("...");
        textColorButton.setMnemonic(KeyEvent.VK_E);
        textColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(
                    AppearanceDialog.this, "Text Color", Color.BLACK);
                if (c != null) {
                    potentialTheme.setTextColor(c);
                    somethingChanged();
                }
            }
        });
        GridBagConstraints cTxtColBut = new GridBagConstraints();
        cTxtColBut.gridx = 0;
        cTxtColBut.gridy = 5;
        cTxtColBut.insets = new Insets(0, 10, 10, 10);
        cTxtColBut.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(textColorButton, cTxtColBut);
        panel.add(textColorButton);
        
        JLabel diceColorLabel = new JLabel("Dice Color:");
        diceColorLabel.setDisplayedMnemonic(KeyEvent.VK_D);
        GridBagConstraints cDicColLab = new GridBagConstraints();
        cDicColLab.gridx = 1;
        cDicColLab.gridy = 4;
        //TODO investigate why this needs a 20 but the item below does not.
        cDicColLab.insets = new Insets(10, 20, 0, 10);
        cDicColLab.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(diceColorLabel, cDicColLab);
        panel.add(diceColorLabel);
        
        JButton diceColorButton = new JButton("...");
        diceColorButton.setMnemonic(KeyEvent.VK_D);
        diceColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(
                    AppearanceDialog.this, "Dice Color", Color.WHITE);
                if (c != null) {
                    potentialTheme.setDiceColor(c);
                    somethingChanged();
                }
            }
        });
        GridBagConstraints cDicColBut = new GridBagConstraints();
        cDicColBut.gridx = 1;
        cDicColBut.gridy = 5;
        cDicColBut.insets = new Insets(0, 10, 10, 10);
        cDicColLab.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(diceColorButton, cDicColBut);
        panel.add(diceColorButton);
        
        return panel;
    }
    
    /**
     * Displays a new <code>AppearanceDialog</code>.
     *
     * @param   owner   <code>Frame</code> within which this dialog should 
     *                  appear.
     * @param   ui      <code>UI</code> which this dialog is to alter.
     */
    public static void showDialog(Frame owner, UI ui) {
        new AppearanceDialog(owner, ui);
    }
    
    private void themeChanged() {
        String newThemeName = (String) themesComboBox.getSelectedItem();
        try {
            Theme newTheme = Theme.getTheme(newThemeName);
            if (newTheme != null) {
                this.potentialTheme = newTheme;
                setDialogValues(this.potentialTheme, true);
            }
        } catch (IllegalArgumentException e) {
            Boggle.debug(e.toString());
            // Don't change the theme because that one does not exist.
        }
        
        //TODO some way to export themes

/*
        TODO
        
        If you choose a new theme, your previous scheme will be lost because you 
        did not save it.  Do you want to save the previous theme?
        
        Yes No Cancel
*/

    }
    
    private void applyTheme() {
        Boggle.debug("Doing applyTheme with diceSize of " 
            + this.potentialTheme.diceSize());
        ui.setTheme(this.potentialTheme);
        if (this.applyButton.isEnabled()) {
            this.applyButton.setEnabled(false);
        }
        Theme.saveCurrentTheme(this.potentialTheme);
    }

    private void saveTheme() {
        //TODO check for NewTheme and use a new name if there recursively.
        String name = (String) JOptionPane.showInputDialog(this, 
            "Name?", "Save Theme As", JOptionPane.QUESTION_MESSAGE, null, null, 
            "New Theme");
        //TODO look for commas in theme names when saving - not valid
        this.potentialTheme.setName(name);
        Theme.saveTheme(this.potentialTheme);
        applyTheme();
        refreshThemesBox();
        this.themesComboBox.setSelectedItem(this.potentialTheme.name());
    }
    
    private void refreshThemesBox() {
        this.themesComboBox.removeAllItems();
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        Boggle.debug("In getThemesComboBox with themes of " 
            + prefs.get("themes", "Default"));
        String[] themes = prefs.get("themes", "Default").split(",");
        Arrays.sort(themes);
        this.themesComboBox.addItem("Select Theme ...");
        for (int i = 0; i < themes.length; i++) {
            this.themesComboBox.addItem(themes[i]);
        }
    }
    
    private void somethingChanged() {
        if (! this.applyButton.isEnabled()) {
            this.applyButton.setEnabled(true);
        }
        //TODO in two places, make a routine?
        preview.setIcon(DieIcon.getDie("X", this.potentialTheme));
        this.pack();
    }
    
    private void chooseFont() {
        JFileChooser chooser = new JFileChooser();
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        /* ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("ttf");
        filter.setDescription("TrueType Fonts");
        chooser.setFileFilter(filter);*/
        //TODO filter for ttf
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getName().toLowerCase().endsWith(
                    ".ttf")) {
                fontField.setText(chooser.getSelectedFile().getName());
            } else {
                JOptionPane.showMessageDialog(this, "File must be a "
                    + "TrueType (.TTF) Font", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel getButtonPanel() {
        
        JPanel panel = new JPanel();
        
        JButton okButton = new JButton("OK");
        getRootPane().setDefaultButton(okButton);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //TODO some take theme as an arg and some don't - rationalize
                applyTheme();
                setVisible(false);
            }
        });
        panel.add(okButton);
    
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            } 
        });
        panel.add(cancelButton);
        
        //TODO use setLabelFor in other places in the app?
        //TODO mnemonics in other places in the app.

        this.applyButton = new JButton("Apply");
        this.applyButton.setEnabled(false);
        this.applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyTheme();
            }
        });
        panel.add(applyButton);
        
        return panel;
    }
    
    private void setDialogValues(Theme theme, boolean setThemeBox) {
        Boggle.debug("In setValues with font of " + theme.font().getFontName());
        // Don't set name here.
        this.diceSizeSpinner.setValue(new Integer(theme.diceSize()));
        this.fontComboBox.setSelectedItem(theme.font().getFontName());
        //TODO set default textcol?
        //TODO set default dicecol?
        //Need to make color choosers available to this method.
        preview.setIcon(DieIcon.getDie("X", this.potentialTheme));
        if (setThemeBox) {
            themesComboBox.setSelectedItem(theme.name());
        }
    }
    
    //TODO the way we pass fonts around can't be right?
    private void fontChanged(Font[] fonts) {
        if ((this.fontComboBox.getSelectedItem() != null) 
                && (! fontComboBox.getSelectedItem().equals(
                this.potentialTheme.font().getFontName()))) {
            changeFont(fonts, (String) fontComboBox.getSelectedItem());
            somethingChanged();
        }
    }
    
    private void changeFont(Font[] fonts, String sel) {
        //TODO better way to do this - with a Map or something?
        for (int i = 0; i < fonts.length; i++) {
            if (sel.equals(fonts[i].getFontName())) {
                this.potentialTheme.setFont(fonts[i]);
            }
        }
    }
    
    private void setDiceSize() {
        int val = ((Integer) this.diceSizeSpinner.getValue()).intValue();
        Boggle.debug("Doing setDiceSize with size of " + val);
        if (val != this.potentialTheme.diceSize()) {
            this.potentialTheme.setDiceSize(val);
            somethingChanged();
        }
    }
}
