package com.jakeapp.jake.ics.filetransfer;

import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;

/**
 * Whether this TransferMethod was successful and a transfer was established. 
 * The transfer itself can still fail, this only concerns the negotiation phase.
 * 
 * @author johannes
 */
public interface INegotiationSuccessListener {

	/**
	 * This TransferMethod didn't work. Maybe try some other TransferMethod?
	 */
	public void failed();

	/**
	 * This TransferMethod worked. Follow the IFileTransfer now.
	 */
	public void succeeded(IFileTransfer ft);
}
