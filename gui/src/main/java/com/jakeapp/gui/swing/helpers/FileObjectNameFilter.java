package com.jakeapp.gui.swing.helpers;

import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.Filter;
import org.apache.log4j.Logger;
import com.jakeapp.gui.swing.panels.FilePanel;

public class FileObjectNameFilter extends PatternFilter {
	private static final Logger log = Logger.getLogger(FileObjectNameFilter.class);

	public FileObjectNameFilter(String query) {
		super(query, 0, FilePanel.FILETREETABLE_NODECOLUMN);
		log.debug("Called with query " + query);
	}

	@Override
	protected String getInputString(int row, int column) {
		Filter filter = getMappingFilter();
		if (filter != null) {
			return filter.getStringAt(row, column);
		}
		if (adapter != null) {
			ProjectFilesTreeNode node = (ProjectFilesTreeNode) adapter.getValueAt(row, column);
			return node.getFileObject().getRelPath();
		}

		return null;
	}
}
