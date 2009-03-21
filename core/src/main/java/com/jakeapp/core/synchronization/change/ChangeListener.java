package com.jakeapp.core.synchronization.change;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;


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

	// fixme: add user that did change!
	void onlineStatusChanged(Project p);

	public enum SyncState {
		NOOP, SYNCING, DONE
	}

	public void syncStateChanged(Project p, SyncState state);
}
