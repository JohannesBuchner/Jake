package com.jakeapp.availablelater;

/**
 * The {@link AvailableLaterObject} reports its status updates using this
 *  
 * @author johannes
 * @param <T>
 */
public interface AvailabilityListener<T> {

	public void statusUpdate(double progress, String status);

	public void finished(T o);

	public void error(Exception t);

}
