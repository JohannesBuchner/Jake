package com.jakeapp.core.synchronization.change;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;


/**
 * Empty (default) implementation of a ChangeListener
 * @author djinn
 *
 */
public class ChangeAdapter implements ChangeListener {

	@Override
	public INegotiationSuccessListener beganRequest(JakeObject jo) {
		// empty implementation
		return new INegotiationSuccessListener() {

			@Override
			public void failed(Throwable reason) {
				// empty implementation
			}

			@Override
			public void succeeded(IFileTransfer ft) {
				// empty implementation
			}
		};
	}

	@Override
	public void pullDone(JakeObject jo) {
		// empty implementation
	}

	@Override
	public void pullNegotiationDone(JakeObject jo) {
		// empty implementation
	}

	@Override
	public void pullProgressUpdate(JakeObject jo, Status status, double progress) {
		// empty implementation
	}

	@Override public void syncStateChanged(Project p, SyncState state) {
	}

	@Override
	public void pullFailed(JakeObject jo, Exception reason) {
		// empty implementation
		
	}

}
