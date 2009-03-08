package com.jakeapp.gui.swing.filters;

import org.jdesktop.swingx.decorator.PatternFilter;
import org.apache.log4j.Logger;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NoteObjectFilter extends PatternFilter {
	private static final Pattern BAD_STUFF = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");

	private static final Logger log = Logger.getLogger(NoteObjectFilter.class);

	public NoteObjectFilter(String filter) {
		super(filter, 0, 2);
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
