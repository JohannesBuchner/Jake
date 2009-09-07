package com.jakeapp.gui.swing.renderer.files;

import com.jakeapp.gui.swing.helpers.FileObjectLockedCell;
import com.jakeapp.gui.swing.helpers.FileObjectStatusProvider;
import com.jakeapp.gui.swing.renderer.DefaultJakeTableCellRenderer;

import javax.swing.*;
import java.awt.*;

public class FileLockedTreeCellRenderer extends DefaultJakeTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
		table.getColumnModel().getColumn(column).setMinWidth(25);
		table.getColumnModel().getColumn(column).setMaxWidth(25);

		Component c = (value == null) ? FileObjectStatusProvider.getEmptyComponent() :
						FileObjectStatusProvider
										.getLockedRendererComponent(((FileObjectLockedCell) value).getFileObject());

		return configComponent(table, isSelected, c);
	}
}
