package com.jakeapp.jake.ics.filetransfer;

/**
 * Whether this TransferMethod was successful and a transfer was established. 
 * The transfer itself can still fail, this only concerns the negotiation phase.
 * 
 * @author johannes
 */
public interface INegotiationSuccessListener {

	/**
	 * This TransferMethod didn't work. Maybe try something else?
	 */
	public void failed();

	/**
	 * This TransferMethod worked.
	 */
	public void succeeded();
}
