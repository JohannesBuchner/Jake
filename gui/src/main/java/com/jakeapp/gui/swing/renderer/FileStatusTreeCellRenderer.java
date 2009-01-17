package com.jakeapp.gui.swing.renderer;

import com.jakeapp.gui.swing.controls.JAsynchronousProgressIndicator;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
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
		table.getColumnModel().getColumn(column).setMaxWidth(30);
		table.getColumnModel().getColumn(column).setMinWidth(30);

		if (value == null) {
			JLabel thing = new JLabel("");
			thing.setOpaque(true);
			thing.setBackground(new Color(0, 0, 0, 0));
			if (isSelected) {
				thing.setBackground(table.getSelectionBackground());
			}
			return thing;
		}

		FileStatus status = (FileStatus) value;
		FileObject fo = ((ProjectFilesTreeNode) table.getValueAt(row, FilePanel.FILETREETABLE_NODECOLUMN)).getFileObject();

		int transferStatus = FilePanel.getInstance().getFileProgress(fo);
		if (transferStatus != -1) {
			// TODO: Make this less ugly and implement it
			JLabel thing = new JLabel("50%");
			thing.setOpaque(true);
			thing.setBackground(new Color(0, 0, 0, 0));
			if (isSelected) {
				thing.setBackground(table.getSelectionBackground());
			}
			return thing;
		}

		JLabel progress = new JLabel("---");

		if (isSelected) {
			progress.setBackground(table.getSelectionBackground());
		}

		return progress;
	}
}
