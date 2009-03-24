package com.jakeapp.jake.ics.filetransfer.methods;

import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;

/**
 * constructed by the corresponding TransferMethodFactory
 * 
 * @author johannes
 * 
 */
public interface ITransferMethod {

	// TODO: refactor in extra class?
	public final String FILE_REQUEST = "<filerequest/>";
  public final String FILE_RESPONSE_DONT_HAVE = "<fileresponseno/>";

	/**
	 * Start the server so others may request files
	 * 
	 * @param l
	 * @param mapper
	 * @throws NotLoggedInException
	 */
	public void startServing(IncomingTransferListener l,
			FileRequestFileMapper mapper) throws NotLoggedInException;

	/**
	 * We want to request a file, i.e. start the negotiation
	 * @param request
	 * @param nsl
	 */
	public void request(FileRequest request, INegotiationSuccessListener nsl);

	/**
	 * Shutdown the server in a clean way
	 */
	public void stopServing();

}
