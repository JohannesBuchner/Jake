package com.jakeapp.gui.swing.renderer;

import com.jakeapp.gui.swing.controls.JAsynchronousProgressIndicator;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.FileObjectStatusProvider;
import com.jakeapp.gui.swing.helpers.FileObjectStatusCell;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.core.synchronization.FileStatus;
import com.jakeapp.core.domain.FileObject;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;


public class FileStatusTreeCellRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		table.getColumnModel().getColumn(column).setMaxWidth(25);
		table.getColumnModel().getColumn(column).setMinWidth(25);

		Component comp = (value == null) ? FileObjectStatusProvider.getEmptyComponent() : FileObjectStatusProvider.getStatusRendererComponent(((FileObjectStatusCell) value).getFileObject());

		if (isSelected) {
			comp.setBackground(table.getSelectionBackground());
		}

		return comp;
	}
}
