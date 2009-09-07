package com.jakeapp.gui.swing.renderer.files;

import com.jakeapp.gui.swing.helpers.FileIconLabelHelper;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.renderer.DefaultJakeTableCellRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Renders file nodes in the ProjectFilesTable
 */
public class ProjectFilesTableCellRenderer extends DefaultJakeTableCellRenderer {
	public ProjectFilesTableCellRenderer() {
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
		if (!(value instanceof ProjectFilesTreeNode))
			return null;

		ProjectFilesTreeNode node = (ProjectFilesTreeNode) value;

		Component c = FileIconLabelHelper.getIconLabel(node.getFileObject());
		return configComponent(table, isSelected, c);
	}
}
