package com.jakeapp.gui.swing.renderer;

import com.jakeapp.gui.swing.helpers.FileIconLabelHelper;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.JakeMainApp;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Renders file and folder nodes in the ProjectFilesTree
 */
public class ProjectFilesTreeCellRenderer implements TreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (!(value instanceof ProjectFilesTreeNode)) return null;

		ProjectFilesTreeNode node = (ProjectFilesTreeNode) value;

		// TODO: This should be refactored (DRY, we have the same stuff in the TableCellRenderer)
		File file;
		if (node.isFile()) {
			file = node.getFileObject().getAbsolutePath();
		} else {
			file = new File(JakeMainApp.getApp().getProject().getRootPath() + node.getFolderObject().getRelPath());
		}

		return FileIconLabelHelper.getIconLabel(file, FileIconLabelHelper.State.NONE);
	}
}
