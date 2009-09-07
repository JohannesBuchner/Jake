package com.jakeapp.gui.swing.filters;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.panels.FilePanel;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.decorator.PatternFilter;

import java.util.Date;

/**
 * PatternFilter is normally used for, well, PatternFilters. But re-implementing all sorts of things
 * isn't what I feel like doing at the moment, so we use it for the wrong purpose :)
 */
public class FileObjectDateFilter extends PatternFilter {
	private static final Logger log = Logger.getLogger(FileObjectDateFilter.class);

	public FileObjectDateFilter() {
		super("", 0, 0);
	}

	@Override
	public boolean test(int row) {
		ProjectFilesTreeNode node = (ProjectFilesTreeNode) adapter
						.getValueAt(row, FilePanel.FILETREETABLE_NODECOLUMN);

		if (!node.isFile()) return false;

		long modified = JakeMainApp.getCore()
						.getAttributed(node.getFileObject())
						.getLastModificationDate();
		long now = new Date().getTime();

		long diff = now - modified;
		log.debug("Difference for " + node.getFileObject().getRelPath() + " is " + diff);

		return (diff < 60 * 60 * 1000);
	}
}