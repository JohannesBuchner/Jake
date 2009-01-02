package com.jakeapp.jake.ics.filetransfer;

import java.io.InputStream;

/**
 * for javadoc, see Smacks IncomingFileTransfer.
 * 
 * @author johannes
 */
public interface IIncomingFileTransfer extends IFileTransfer {

	public InputStream recieveFile() throws TransferException;

}
