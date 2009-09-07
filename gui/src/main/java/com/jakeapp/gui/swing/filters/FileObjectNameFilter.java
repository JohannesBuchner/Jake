package com.jakeapp.gui.swing.filters;

import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.Filter;
import org.apache.log4j.Logger;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FileObjectNameFilter extends PatternFilter {
	private static final Logger log = Logger.getLogger(FileObjectNameFilter.class);
	private static final Pattern BAD_STUFF = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");

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

	/**
	 * We need to override this so that entering an invalid regex doesn't **** up everything
	 *
	 * @param regularExpr
	 * @param matchFlags
	 */
	@Override
	public void setPattern(String regularExpr, int matchFlags) {
		Matcher match = BAD_STUFF.matcher(regularExpr);
      String searchString = match.replaceAll("\\\\$1");

		log.debug("PATTERN IS: \"" + searchString +"\"");

		if ((searchString == null) || (searchString.length() == 0)) {
			searchString = ".*";
		}
		setPattern(Pattern.compile(searchString, matchFlags | Pattern.CASE_INSENSITIVE));
	}
}
