package com.jakeapp.availablelater;


/**
 * An {@link AvailableLaterObject} that performs further calculations on the
 * results provided by another {@link AvailableLaterObject}. As with other
 * AvailableLaterObjects the calculation is performed in this class'
 * {@link #run()} method. The method can access the results of another
 * AvailableLaterObject: {@link #source} after finish has been called.
 * 
 * @author christopher
 * 
 * @param <T> result type
 * @param <S> result type of source
 */
public abstract class AvailableLaterWrapperObject<T, S> extends AvailableLaterObject<T> implements
		AvailabilityListener<S> {

	private AvailableLaterObject<S> source;
	
	public AvailableLaterWrapperObject(AvailableLaterObject<S> source) {
		this.setSource(source);
	}

	/**
	 * Sets the AvailableLaterObject that calculates the intermediate results
	 * needed by this {@link AvailableLaterObject}'s run method.
	 * 
	 * @param source
	 *            the source to set. Must been created with this Object as
	 *            AvailabilityListener.
	 */
	protected void setSource(AvailableLaterObject<S> source) {
		this.source = source;
		source.setListener(this);
	}

	protected AvailableLaterObject<S> getSource() {
		return this.source;
	}

	@Override
	public void error(Exception t) {
		getListener().error(t);
	}


	@Override
	public void finished(S o) {
		/*
		 * The run method of the source has returned by reporting finished. We
		 * can not perform our calculation based on the intermediate results the
		 * source provided.
		 */

		// perform this class' calculation
		new Thread(this).start();
		// report finished to the listener that is listening to
		// this AvailableLaterObject.

		/*
		 * Do not call this - the run method should set a value and report
		 * 'finished'.
		 */
		// this.listener.finished();
	}

	@Override
	public void statusUpdate(double progress, String status) {
		getListener().statusUpdate(progress, status);
	}

	@Override
	public AvailableLaterObject<T> start() {
		this.getSource().start();
		return this;
	}
}
