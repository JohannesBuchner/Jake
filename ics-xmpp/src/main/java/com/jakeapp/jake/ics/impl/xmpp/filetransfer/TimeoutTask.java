/**
 * 
 */
package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import java.util.TimerTask;

import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;

/**
 * Scheduleable Task that invalidates a FileRequest by calling the appropriate method
 * of a IOutgoingRequestManager.
 * @author christopher
 */
public class TimeoutTask extends TimerTask {

	private INegotiationSuccessListener nsl;
	private IOutgoingRequestManager requestManager;
	private FileRequest request;
	
	private void setRequest(FileRequest request) {
		this.request = request;
	}

	private FileRequest getRequest() {
		return request;
	}

	private void setNsl(INegotiationSuccessListener nsl) {
		this.nsl = nsl;
	}

	private INegotiationSuccessListener getNsl() {
		return nsl;
	}

	private void setRequestManager(IOutgoingRequestManager requestManager) {
		this.requestManager = requestManager;
	}

	private IOutgoingRequestManager getRequestManager() {
		return requestManager;
	}
	
	public TimeoutTask(IOutgoingRequestManager requestManager,
			INegotiationSuccessListener nsl, FileRequest request) {
		super();
		this.setNsl(nsl);
		this.setRequestManager(requestManager);
		this.setRequest(request);
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		try {
			this.getNsl().failed(new TimeoutException());
		} finally {
			this.getRequestManager().removeOutgoing(this.getRequest());
		}

	}
}
