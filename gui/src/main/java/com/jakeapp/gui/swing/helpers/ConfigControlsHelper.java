package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.renderer.EventCellRenderer;
import com.jakeapp.gui.swing.renderer.DefaultJakeTableCellRenderer;
import com.explodingpixels.macwidgets.MacFontUtils;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;

/**
 * @author studpete
 */
public class ConfigControlsHelper {

	/**
	 * Configurates an events table.
	 * Used in news, inspector code.
	 *
	 * @param table
	 */
	public static void configEventsTable(JXTable table) {
		table.getColumn(0).setCellRenderer(new EventCellRenderer());
	  table.getColumn(1).setCellRenderer(new DefaultJakeTableCellRenderer());
		table.setSortable(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setColumnControlVisible(true); // TODO false
		table.setEditable(false);
		table.setDoubleBuffered(true);
		table.setRolloverEnabled(false);
		table.setFont(MacFontUtils.ITUNES_FONT);
	}


	// do not instantiate
	private ConfigControlsHelper() {
	}
}
