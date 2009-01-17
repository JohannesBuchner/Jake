package com.jakeapp.core.util.availablelater;


/**
 * An {@link AvailableLaterObject} that performs further calculations on the results
 * provided by another {@link AvailableLaterObject}. As with other AvailableLaterObjects the
 * calculation is performed in this class' {@link #run()} method. The method can
 * access the results of another AvailableLaterObject: {@link #source} after finish has
 * been called. 
 * @author christopher
 *
 * @param <T>
 * @param <S>
 */
public abstract class AvailableLaterWrapperObject<T,S> extends AvailableLaterObject<T> implements
		AvailabilityListener {

	private AvailableLaterObject<S> source = null;

	/**
	 * Sets the AvailableLaterObject that calculates the intermediate results
	 * needed by this {@link AvailableLaterObject}'s run method.
	 * @param source the source to set. Must been created with this Object as AvailabilityListener.
	 */
	public void setSource(AvailableLaterObject<S> source) {
		if (this.getSource() == null && source.listener == this)
			this.source = source;
	}

	protected AvailableLaterObject<S> getSource() {
		return source;
	}

	public AvailableLaterWrapperObject(AvailabilityListener listener) {
		super(listener);
		this.setSource(source);
	}

	@Override
	public void error(Exception t) {
		this.listener.error(t);
	}

	@Override
	public void error(String reason) {
		this.listener.error(reason);

	}

	@Override
	public void finished() {
		/*
		 * The run method of the source has returned by reporting finished.
		 * We can not perform our calculation based on the intermediate results
		 * the source provided.
		 */
		
		//perform this class' calculation
		this.run();
		//report finished to the listener that is listening to
		//this AvailableLaterObject.
		
		/* Do not call this - the run method should set a value and report 'finished'.*/ 
		//this.listener.finished();
	}

	@Override
	public void statusUpdate(double progress, String status) {
		this.listener.statusUpdate(progress, status);
	}
	
	@Override
	public AvailableLaterObject<T> start() {
		this.getSource().start();
		return this;
	}
}
