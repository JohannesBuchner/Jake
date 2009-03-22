package com.jakeapp.availablelater;

/**
 * An AvailableLaterObject that immediately reports an error.
 * Use this if a method that should return an AvailableLaterObject
 * fails with an exception before it can create an AvailableLaterObject.
 * @author christopher
 */
public class AvailableErrorObject<T> extends AvailableNowObject<T> {

	private Exception exception;
	
	/**
	 * @param ex The error to report.
	 */
	public AvailableErrorObject(Exception ex) {
		super(null);
		this.setException(ex);
	}

	@Override
	public void setListener(AvailabilityListener<T> listener) {
		listener.error(this.exception);
	}

	@Override
	public T calculate() throws Exception {
		throw this.exception;
	}

	private void setException(Exception exception) {
		this.exception = exception;
	}

	private Exception getException() {
		return exception;
	}
}
