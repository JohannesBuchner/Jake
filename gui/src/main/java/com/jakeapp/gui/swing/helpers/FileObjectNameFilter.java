package com.jakeapp.gui.swing.helpers;

import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.Filter;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 13, 2009
 * Time: 12:43:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileObjectNameFilter extends PatternFilter {
	private static final Logger log = Logger.getLogger(FileObjectNameFilter.class);

	public FileObjectNameFilter(String query) {
		super(query, 0, 0);
		log.debug("Called with query " + query);
	}

	@Override
	protected String getInputString(int row, int column) {
		Filter filter = getMappingFilter();
		if (filter != null) {
			log.debug("Returning " + filter.getStringAt(row, column));
			return filter.getStringAt(row, column);
		}
		if (adapter != null) {
			ProjectFilesTreeNode node = (ProjectFilesTreeNode) adapter.getValueAt(row, column);
			log.debug("Returning " + node.getFileObject().getRelPath());
			return node.getFileObject().getRelPath();
		}

		log.warn("Returning NULL");
		return null;
	}
}
