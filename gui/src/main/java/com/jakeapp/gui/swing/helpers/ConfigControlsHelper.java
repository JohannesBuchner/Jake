package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.renderer.EventCellRenderer;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;

/**
 * @author: studpete
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
		table.setSortable(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setColumnControlVisible(false);
		table.setEditable(false);
		table.setDoubleBuffered(true);
		table.setRolloverEnabled(false);
	}


	// do not instantiate
	private void ConfigControlsHelper() {

	}
}
