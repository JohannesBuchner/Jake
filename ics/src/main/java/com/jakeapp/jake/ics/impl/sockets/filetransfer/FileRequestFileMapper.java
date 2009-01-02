package com.jakeapp.jake.ics.impl.sockets.filetransfer;

import java.io.File;

import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;

/**
 * finds the file or ressource associated with the request.
 * @author johannes
 *
 */
public interface FileRequestFileMapper {

	/**
	 * finds the file or ressource associated with the request.
	 * @param r
	 * @return null if not available, the content otherwise
	 */
	public File getFileForRequest(FileRequest r);
}
