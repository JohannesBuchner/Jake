/**
 * 
 */
package com.jakeapp.jake.fss;

import org.apache.log4j.Logger;

import com.googlecode.junit.ext.Checker;

public class AllowSlowChecker implements Checker {
	private static final Logger log = Logger.getLogger(AllowSlowChecker.class);

	private static final String ALLOWSLOWTESTS_PROPERTY = "com.jakeapp.jake.fss.tests.allowslowtests";

	public boolean satisfy() {
		String prop = System.getProperty(ALLOWSLOWTESTS_PROPERTY);
		if (prop == null || prop.isEmpty() || prop.equals("false")) {
			log.warn("Skipping slow tests. ");
			log.info("To allow them run with "
					+ "-Dcom.jakeapp.jake.fss.tests.allowslowtests=true");
			return false;
		}
		return true;
	}
}