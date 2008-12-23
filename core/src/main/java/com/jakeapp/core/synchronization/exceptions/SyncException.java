package com.jakeapp.core.synchronization.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author johannes
 * 
 * A Synchronisation problem has occured
 */
@SuppressWarnings("serial")
public class SyncException extends Exception {
	Exception innerException;
	public SyncException(Exception innerException){
		this.innerException = innerException;
	}
	public Exception getInnerException(){
		return innerException;
	}
	@Override
	public Throwable getCause() {
		return innerException.getCause();
	}
	@Override
	public String getLocalizedMessage() {
		return innerException.getLocalizedMessage();
	}
	@Override
	public String getMessage() {
		return innerException.getMessage();
	}
	@Override
	public StackTraceElement[] getStackTrace() {
		return innerException.getStackTrace();
	}
	@Override
	public void printStackTrace() {
		innerException.printStackTrace();
	}
	@Override
	public void printStackTrace(PrintStream s) {
		innerException.printStackTrace(s);
	}
	@Override
	public void printStackTrace(PrintWriter s) {
		innerException.printStackTrace(s);
	}
	@Override
	public void setStackTrace(StackTraceElement[] stackTrace) {
		innerException.setStackTrace(stackTrace);
	}
}
