package com.jakeapp.gui.swing.filters;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.panels.FilePanel;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.decorator.PatternFilter;

/**
 * PatternFilter is normally used for, well, PatternFilters. But re-implementing all sorts of things
 * isn't what I feel like doing at the moment, so we use it for the wrong purpose :)
 */
public class FileObjectConflictStatusFilter extends PatternFilter {
	private static final Logger log = Logger
					.getLogger(FileObjectConflictStatusFilter.class);

	public FileObjectConflictStatusFilter() {
		super("", 0, 0);
	}

	@Override
	public boolean test(int row) {
		ProjectFilesTreeNode node = (ProjectFilesTreeNode) adapter
						.getValueAt(row, FilePanel.FILETREETABLE_NODECOLUMN);

		return node.isFile() && JakeMainApp.getCore()
						.getAttributed(node.getFileObject())
						.isInConflict();
	}
}