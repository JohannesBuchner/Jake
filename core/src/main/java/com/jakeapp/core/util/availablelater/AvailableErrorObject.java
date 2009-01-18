package com.jakeapp.core.util.availablelater;

/**
 * An AvailableLaterObject that never finishes but instead always
 * reports an error.
 * Use this if a method that should return an AvailableLaterObject
 * fails with an exception before it can create an AvailableLaterObject.
 * @author christopher
 */
public class AvailableErrorObject<T> extends AvailableLaterObject<T> {

	private Exception exception;
	
	/**
	 * @param ex The error to report.
	 */
	public AvailableErrorObject(AvailabilityListener listener, Exception ex) {
		super(listener);
		this.setException(ex);
	}

	@Override
	public void run() {
		this.listener.error(this.exception);
	}

	private void setException(Exception exception) {
		this.exception = exception;
		if (this.getException()==null)
			this.exception = new NullPointerException();
	}

	private Exception getException() {
		return exception;
	}
}
