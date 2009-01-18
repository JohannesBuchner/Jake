package com.jakeapp.gui.swing.helpers;

import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.Filter;
import org.apache.log4j.Logger;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.JakeMainApp;

/**
 * PatternFilter is normally used for, well, PatternFilters. But re-implementing all sorts of things
 * isn't what I feel like doing at the moment, so we use it for the wrong purpose :)
 */
public class FileObjectConflictStatusFilter extends PatternFilter {
	private static final Logger log = Logger.getLogger(FileObjectConflictStatusFilter.class);

	public FileObjectConflictStatusFilter() {
		super("", 0, 0);
	}

	@Override
	public boolean test(int row) {
		ProjectFilesTreeNode node = (ProjectFilesTreeNode) adapter.getValueAt(row, FilePanel.FILETREETABLE_NODECOLUMN);

		return node.isFile() && JakeMainApp.getCore().getJakeObjectSyncStatus(JakeMainApp.getProject(), node.getFileObject()).isInConflict();
	}
}