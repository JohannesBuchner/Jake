package com.jakeapp.jake.ics.filetransfer;

import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;

/**
 * A transfer is coming in.
 * 
 * This should handle whether it is accepted, the transfer and progress
 * 
 * @author johannes
 * 
 */
public interface IncomingTransferListener {

	/**
	 * Should we accept/allow this transfer?
	 * 
	 * @param req
	 * @return
	 */
	public boolean accept(FileRequest req);

	/**
	 * The Transfer was successfully negotiated and has started. (called after
	 * accept)
	 * 
	 * @param t
	 */
	public void started(IFileTransfer t);
}
