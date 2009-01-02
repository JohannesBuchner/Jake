package com.jakeapp.jake.ics.filetransfer;

import java.io.InputStream;


/**
 * for javadoc, see Smacks OutgoingFileTransfer.
 * 
 * @author johannes
 */
public interface IOutgoingFileTransfer extends IFileTransfer {

	public void sendStream(InputStream in, String fileName, long fileSize,
			String description);

}
