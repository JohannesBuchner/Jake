package com.jakeapp.jake.ics.filetransfer;

import java.io.InputStream;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;

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
	public ITransferListener send(UserId user, InputStream content);
	
	/**
	 * wait and handle receiving
	 * @param l
	 */
	public void registerIncomingTransferListener(IncomingTransferListener l);
	
	/**
	 * 
	 * @param m
	 */
	public void addTransferMethod(ITransferMethodFactory m);
}
