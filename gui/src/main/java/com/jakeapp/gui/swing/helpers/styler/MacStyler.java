/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers.styler;

import com.jakeapp.gui.swing.helpers.GuiUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * @author studpete
 */
public class MacStyler extends AbstractStyler {

	public MacStyler() {
		// Tweak Apple's "Aqua" Mac OS LAF.
		// Apple's UI delegate has over-tight borders. (Apple 4417784.) Work-around by Werner Randelshofer.
		UIManager.put("OptionPane.border", GuiUtilities.makeEmptyBorderResource(15 - 3, 24 - 3, 20 - 3, 24 - 3));
		UIManager.put("OptionPane.messageAreaBorder", GuiUtilities.makeEmptyBorderResource(0, 0, 0, 0));
		UIManager.put("OptionPane.buttonAreaBorder", GuiUtilities.makeEmptyBorderResource(16 - 3, 0, 0, 0));
		// On Mac OS, standard tabbed panes use way too much space. This makes them slightly less greedy.
		UIManager.put("TabbedPane.useSmallLayout", Boolean.TRUE);
		// Apple's LAF uses the wrong background color for selected rows in lists and tables.
		Color MAC_OS_SELECTED_ROW_COLOR = new Color(0.24f, 0.50f, 0.87f);
		UIManager.put("List.selectionBackground", MAC_OS_SELECTED_ROW_COLOR);
		UIManager.put("List.selectionForeground", Color.WHITE);
		UIManager.put("Table.selectionBackground", MAC_OS_SELECTED_ROW_COLOR);
		UIManager.put("Table.selectionForeground", Color.WHITE);
	}

	@Override
	public void makeWhiteRecessedButton(JButton btn) {
		btn.setForeground(Color.WHITE);
		btn.putClientProperty("JButton.buttonType", "recessed");
	}


	@Override
	public void styleToolbarButton(JToggleButton btn) {
		if (btn.isSelected()) {
			btn.setForeground(Color.WHITE);
		} else {
			btn.setForeground(Color.BLACK);
		}
	}
}
