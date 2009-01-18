package com.jakeapp.gui.swing.helpers;

/**
 * Small class that has infos about running application
 *
 * @author: studpete
 */
public class AppUtilities {
	private static String appName = "Jake";
	private final static String appVersion = "0.9 Beta";

	/**
	 * Get application name
	 *
	 * @return Jake
	 */
	public static String getAppName() {
		return appName;
	}

	// DEBUG ONLY.
	public static void setAppName(String app) {
		appName = app;
	}


	/**
	 * Get application version
	 *
	 * @return some optimistic valie
	 */
	public static String getAppVersion() {
		return appVersion;
	}


	// prevent instantiation
	private AppUtilities() {

	}
}
