package com.jakeapp.gui.swing.renderer.files;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileIconLabelHelper;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * Renders file and folder nodes in the ProjectFilesTree
 */
public class ProjectFilesTreeCellRenderer implements TreeCellRenderer {
	private static final Logger log = Logger.getLogger(ProjectFilesTreeCellRenderer.class);

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (!(value instanceof ProjectFilesTreeNode)) return null;

		ProjectFilesTreeNode node = (ProjectFilesTreeNode) value;

		// TODO: This should be refactored (DRY, we have the same stuff in the TableCellRenderer)
		File file;
		if (node.isFile()) {
			try {
				file = JakeMainApp.getCore().getFile(node.getFileObject());
			} catch (FileOperationFailedException e) {
				ExceptionUtilities.showError(e);
			}
		} else {
			// TODO: make this not happen!
			if (node == null || node.getFolderObject() == null || node.getFolderObject().getRelPath() == null) {
				log.warn("Some data in note is null: " + node);
				file = null;
			} else {
				file = new File(JakeContext.getProject().getRootPath() + node.getFolderObject().getRelPath());
			}
		}

		return FileIconLabelHelper.getIconLabel(node.getFileObject());
	}
}
