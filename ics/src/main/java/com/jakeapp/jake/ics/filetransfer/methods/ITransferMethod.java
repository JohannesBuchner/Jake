package com.jakeapp.jake.ics.filetransfer.methods;

import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.FileRequestFileMapper;

/**
 * constructed by the corresponding TransferMethodFactory
 * 
 * @author johannes
 * 
 */
public interface ITransferMethod {

	/**
	 * Start the server so others can request files
	 * 
	 * @param l
	 * @param mapper
	 * @throws NotLoggedInException
	 */
	public void startServing(IncomingTransferListener l,
			FileRequestFileMapper mapper) throws NotLoggedInException;

	/**
	 * Request a file
	 * @param request
	 * @param nsl
	 */
	public void request(FileRequest request, INegotiationSuccessListener nsl);

}
