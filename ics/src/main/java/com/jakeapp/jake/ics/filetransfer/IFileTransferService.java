package com.jakeapp.jake.ics.filetransfer;

import java.io.InputStream;

import com.jakeapp.jake.ics.UserId;

/**
 * For transferring huge files
 * 
 * @author johannes
 */
public interface IFileTransferService {
	/**
	 * send a file
	 * @param user
	 * @param content
	 */
	public void send(UserId user, InputStream content);
	
	/**
	 * wait and handle receiving
	 * @param l
	 */
	public void registerIncomingTransferListener(IncomingTransferListener l);
	
}
