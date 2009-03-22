package com.jakeapp.gui.swing.renderer;

import com.explodingpixels.macwidgets.MacFontUtils;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * @author studpete
 */
public class DefaultJakeTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
												boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		return configComponent(table, isSelected, c);
	}

	protected Component configComponent(JTable table, boolean isSelected, Component c) {
		c.setFont(MacFontUtils.ITUNES_FONT);

		// draw selection background
		if (isSelected) {
			c.setBackground(table.getSelectionBackground());
		}

		return c;
	}
}