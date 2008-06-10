package com.doublesignal.sepm.jake.core;

import org.apache.log4j.Logger;

/**
 * This should replace all "This can not happen" log-entries or comments
 * 
 * @author johannes
 */
@SuppressWarnings("serial")
public class InvalidApplicationState {
	private static Logger log = Logger.getLogger(InvalidApplicationState.class);

	private InvalidApplicationState() {
	}
	
	public static void die(String reason, Exception parentException) {
		parentException.printStackTrace();
		die(reason);
	}
	
	public static void die(Exception parentException) {
		parentException.printStackTrace();
		die();
	}
	
	public static void die(String reason) {
		log.fatal("Invalid Application State: " + reason);
		die();
	}
	
	public static void die() {
		System.exit(-1);
	}
	
}
