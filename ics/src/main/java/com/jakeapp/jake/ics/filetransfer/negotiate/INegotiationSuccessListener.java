package com.jakeapp.jake.ics.filetransfer.negotiate;

import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;

/**
 * Whether this TransferMethod was successful and a transfer was established.
 * The transfer itself can still fail, this only concerns the negotiation phase.
 * 
 * @author johannes
 */
public interface INegotiationSuccessListener {

	/**
	 * This TransferMethod didn't work, because the negotiation failed. Maybe
	 * try some other TransferMethod?
	 * 
	 */
	public void failed(Throwable reason);

	/**
	 * This TransferMethod worked. Follow the given {@link IFileTransfer} now.
	 */
	public void succeeded(IFileTransfer ft);
}
