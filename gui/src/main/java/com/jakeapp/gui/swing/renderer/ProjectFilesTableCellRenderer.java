package com.jakeapp.gui.swing.renderer;

import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.FileIconLabelHelper;
import com.jakeapp.gui.swing.JakeMainApp;

import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Renders file nodes in the ProjectFilesTable
 */
public class ProjectFilesTableCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (!(value instanceof ProjectFilesTreeNode)) return null;

		ProjectFilesTreeNode node = (ProjectFilesTreeNode) value;

		// TODO: This should be refactored (DRY, we have the same stuff in the TreeCellRenderer)
		File file;
		file = node.getFileObject().getAbsolutePath();

		return FileIconLabelHelper.getIconLabel(file, FileIconLabelHelper.State.NONE);
	}
}
