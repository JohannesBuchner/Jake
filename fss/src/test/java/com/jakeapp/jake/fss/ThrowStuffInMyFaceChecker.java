/**
 * 
 */
package com.jakeapp.jake.fss;

import org.apache.log4j.Logger;

public class ThrowStuffInMyFaceChecker extends DesktopSupportedChecker {
	private final Logger log = Logger.getLogger(ThrowStuffInMyFaceChecker.class);

	private static final String ALLOWLAUNCHTESTS_PROPERTY = "com.jakeapp.jake.fss.tests.allowlauchtests";

	public boolean satisfy() {
		if(!super.satisfy())
			return false;
		String prop = System.getProperty(ALLOWLAUNCHTESTS_PROPERTY);
		if (prop == null || prop.isEmpty() || prop.equals("false")) {
			log.warn("Skipping launch tests. ");
			log.info("To allow them run with "
					+ "-Dcom.jakeapp.jake.fss.tests.allowlauchtests=true");
			return false;
		}
		return true;
	}
}