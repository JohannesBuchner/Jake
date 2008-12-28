package com.jakeapp.jake.ics.filetransfer;

/**
 * A transfer is coming in. 
 * 
 * This should handle wether it is accepted, the transfer and progress 
 * 
 * @author johannes
 *
 */
public interface IncomingTransferListener {

	public void fileTransferRequest(FileTransferRequest t);
}
