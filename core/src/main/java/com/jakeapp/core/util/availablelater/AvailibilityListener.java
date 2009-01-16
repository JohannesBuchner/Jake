package com.jakeapp.core.util.availablelater;

/**
 * The {@link AvailableLaterObject} reports its status updates using this
 *  
 * @author johannes
 */
public interface AvailibilityListener {

	public void statusUpdate(double progress, String status);

	public void finished();

	public void error(Exception t);

	public void error(String reason);

}
