/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.renderer;

import com.explodingpixels.macwidgets.MacFontUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author studpete
 */
@SuppressWarnings("serial")
public class IconifiedRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {

		super.getTableCellRendererComponent(table,
						value,
						isSelected,
						hasFocus,
						row,
						column);
		ImageIcon icon = (ImageIcon) value;
		setText(table.getValueAt(row, column + 2).toString());
		setIcon(icon);
		setFont(MacFontUtils.ITUNES_FONT);
		return this;
	}
}