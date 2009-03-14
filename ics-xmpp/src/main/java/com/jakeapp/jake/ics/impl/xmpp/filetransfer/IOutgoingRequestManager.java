package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;

/**
 * Interface for classes that store outgoing FileRequests and need to be
 * notified when these FileRequests are invalidated.
 * @author christopher
 *
 */
public interface IOutgoingRequestManager {
	
	/**
	 * Invalidate the FileRequest r - stop keeping track of <code>r</code>.
	 * @param r
	 */
	void removeOutgoing(FileRequest r);
}
