package com.jakeapp.gui.swing.exceptions;

import java.util.ArrayList;
import java.util.List;


/**
 * Generic exception that may nest additional exceptions.
 *
 * @author Simon
 */
public class NestedException extends Exception {
	private static final long serialVersionUID = 5050234839403267541L;

	private List<Exception> nestedExceptions;

	public NestedException() {

	}

	public NestedException(Exception e) {
		append(e);
	}

	{
		this.nestedExceptions = new ArrayList<Exception>();
	}

	public void append(Exception e) {

		// FIXME: DANGER, HACK AHEAD: added direct printout for easier resolving of bugs
		// e.printStackTrace();

		this.nestedExceptions.add(e);
	}

	/**
	 * Get a list of nested <code>Exception</code>s.
	 *
	 * @return list of nested <code>Exception</code>s
	 */
	protected List<Exception> getNestedExceptions() {
		return this.nestedExceptions;
	}

	@Override
	public String getMessage() {
		String str = new String();
		str = super.getMessage() + ", " + nestedExceptions.size() + " nested exception(s); <br>";

		return str;
	}

	/**
	 * Get the array of <code>StackTraceElements</code>. It is a hacked bunch of all nested exceptions.
	 *
	 * @return the array
	 */
	@Override
	public StackTraceElement[] getStackTrace() {
		//FIXME: delete that hack!
		List<StackTraceElement> stack = new ArrayList<StackTraceElement>();

		for (StackTraceElement s : super.getStackTrace()) {
			stack.add(s);
		}

		for (Exception e : this.getNestedExceptions()) {
			stack.add(new StackTraceElement("nested Exception - " + e.getMessage(), "", "", 0));
			for (StackTraceElement s : super.getStackTrace()) {
				stack.add(s);
			}
		}
		return stack.toArray(new StackTraceElement[0]);
	}

}
