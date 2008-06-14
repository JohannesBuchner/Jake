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
	
	public enum StateType {
		NOT_IMPLEMENTED,
		SHOULD_NOT_HAPPEN
	}
	
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
	
	public static void die(StateType state) {
		die(state.toString());
	}
	public static void die(StateType state, Exception e) {
		die(state.toString(), e);
	}
	public static void shouldNotHappen(){
		die(StateType.SHOULD_NOT_HAPPEN);
	}
	public static void shouldNotHappen(Exception e){
		die(StateType.SHOULD_NOT_HAPPEN, e);
	}
	public static void notImplemented(){
		die(StateType.NOT_IMPLEMENTED);
	}
	public static void notImplemented(Exception e){
		die(StateType.NOT_IMPLEMENTED, e);
	}
}
