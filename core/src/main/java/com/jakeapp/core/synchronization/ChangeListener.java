package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;


public interface ChangeListener {
	public INegotiationSuccessListener beganRequest(JakeObject jo);

	public void pullNegotiationDone(JakeObject jo);

	public void pullDone(JakeObject jo);
	
	/**
	 * A pull-operation that has already begun failed.
	 */
	public void pullFailed(JakeObject jo, Exception reason);

	public void pullProgressUpdate(JakeObject jo, Status status, double progress);
}
