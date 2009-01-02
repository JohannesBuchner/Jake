package com.jakeapp.jake.ics.filetransfer;

public interface ITransferListener {

	/**
	 * An error occured and ended the transfer
	 * @param error
	 */
	public void onFailure(TransferData transfer, Error error);

	/**
	 * Transfer completed
	 */
	public void onSuccess(TransferData transfer);

	/**
	 * The transfers status or progress changed
	 * @param status
	 * @param progress
	 */
	public void onUpdate(TransferData transfer, Status status, double progress);

}
