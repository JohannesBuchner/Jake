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
		// WTF ?
		setFont(MacFontUtils.ITUNES_FONT);
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setFont(MacFontUtils.ITUNES_FONT);
		return c;
	}
}