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
	private static String getStackTrace(){
		Throwable ex = new Throwable();
		return getStackTrace(ex.getStackTrace());
	}
	private static String getStackTrace(StackTraceElement[] elements){
		String o = "Cause: \n";
		for(StackTraceElement ste : elements){
			if(ste.getClass().equals(InvalidApplicationState.class))
				continue;
			o = o.concat("\t" + ste.getFileName() + ":" + ste.getLineNumber() + " " + 
				 ste.getClassName() + "." + ste.getMethodName() + "()\n");
		}
		return o;
	}
	public static void die(String reason, Exception parentException) {
		log.error(parentException.toString());
		log.debug(getStackTrace(parentException.getStackTrace()));
		die(reason);
	}
	
	public static void die(Exception parentException) {
		log.error(parentException.toString());
		log.debug(getStackTrace(parentException.getStackTrace()));
		log.fatal("Invalid Application State.");
		die();
	}
	
	public static void die(String reason) {
		log.debug(getStackTrace());
		log.fatal("Invalid Application State: " + reason);
		die();
	}
	
	private static void die() {
		System.exit(-1);
	}
	
}
