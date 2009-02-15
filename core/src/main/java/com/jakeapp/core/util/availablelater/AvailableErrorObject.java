package com.jakeapp.core.util.availablelater;

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
	public T calculate() {
		getListener().error(this.exception);
		return null;
	}

	private void setException(Exception exception) {
		this.exception = exception;
		if (this.getException()==null)
			this.exception = new NullPointerException();
	}

	private Exception getException() {
		return exception;
	}
	
	@Override
	public void setListener(AvailabilityListener<T> listener) {
		listener.error(this.getException());
	}
}
