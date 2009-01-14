package com.jakeapp.jake.ics.filetransfer;

import java.io.File;

import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;

/**
 * finds the file or ressource associated with the request.
 * @author johannes
 *
 */
public interface FileRequestFileMapper {

	/**
	 * finds the file or ressource associated with the request. Note: the file has to be
	 * called the same as the {@link FileRequest#getFileName()}.
	 * @param r
	 * @return null if not available, the content otherwise
	 */
	public File getFileForRequest(FileRequest r);
}
