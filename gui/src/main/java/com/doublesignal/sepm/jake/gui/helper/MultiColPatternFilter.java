package com.doublesignal.sepm.jake.gui.helper;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.decorator.PatternFilter;

public class MultiColPatternFilter extends PatternFilter {
	private static final Logger LOG = Logger.getLogger(MultiColPatternFilter.class);
	private final int[] cols;

	public MultiColPatternFilter(String regEx, int matchFlags, final int... cols) {
		super(regEx, matchFlags, 0);
		final int numCols = cols.length;
		this.cols = new int[numCols];
		System.arraycopy(cols, 0, this.cols, 0, numCols);
	}

	@Override
	public boolean test(final int row) {
		for (int colIdx : cols) {
			if (adapter.isTestable(colIdx)) {
				final String valueStr = (String) getInputValue(row, colIdx);
				final boolean ret = pattern.matcher(valueStr).find();
				if (ret) {
					return true;
				}
			} else {
				LOG.warn("column " + colIdx + " not testable");
				return false;
			}
		}
		return false;
	}
}