package com.jakeapp.jake.ics.filetransfer;

import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

public interface ITransferListener {

	/**
	 * An error occured and ended the transfer
	 * @param error
	 */
	public void onFailure(AdditionalFileTransferData transfer, String error);

	/**
	 * Transfer completed
	 */
	public void onSuccess(AdditionalFileTransferData transfer);

	/**
	 * The transfers status or progress changed
	 * @param status
	 * @param progress
	 */
	public void onUpdate(AdditionalFileTransferData transfer, Status status, double progress);

}
