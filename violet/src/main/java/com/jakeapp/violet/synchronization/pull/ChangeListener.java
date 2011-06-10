package com.jakeapp.violet.synchronization.pull;

import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.violet.model.JakeObject;

public interface ChangeListener {
	public INegotiationSuccessListener beganRequest(JakeObject jo);

	public void pullNegotiationDone(JakeObject jo);

	public void pullDone(JakeObject jo);

	/**
	 * A pull-operation that has already begun failed.
	 * 
	 * @param jo
	 * @param reason
	 */
	public void pullFailed(JakeObject jo, Throwable reason);

	public void pullProgressUpdate(JakeObject jo, Status status, double progress);

}
