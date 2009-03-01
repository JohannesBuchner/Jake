package com.jakeapp.gui.swing.renderer;

import com.jakeapp.gui.swing.helpers.FileIconLabelHelper;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renders file nodes in the ProjectFilesTable
 */
public class ProjectFilesTableCellRenderer implements TableCellRenderer {
	public ProjectFilesTableCellRenderer() {
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
		if (!(value instanceof ProjectFilesTreeNode))
			return null;

		ProjectFilesTreeNode node = (ProjectFilesTreeNode) value;

		Component c = FileIconLabelHelper.getIconLabel(node.getFileObject());

		if (isSelected) {
			c.setBackground(table.getSelectionBackground());
		}

		return c;
	}
}
