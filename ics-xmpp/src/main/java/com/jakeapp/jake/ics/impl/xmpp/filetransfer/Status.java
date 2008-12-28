package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import org.jivesoftware.smackx.filetransfer.FileTransfer;

/**
 * A class to represent the current status of the file transfer.
 *
 * @author Alexander Wenckus (copied from smack)
 *
 */
public enum Status {

	/**
	 * An error occured during the transfer.
	 *
	 * @see FileTransfer#getError()
	 */
	error("Error"),

	/**
     * The initial status of the file transfer.
     */
    initial("Initial"),

    /**
	 * The file transfer is being negotiated with the peer. The party
	 * recieving the file has the option to accept or refuse a file transfer
	 * request. If they accept, then the process of stream negotiation will
	 * begin. If they refuse the file will not be transfered.
	 *
	 * @see #negotiating_stream
	 */
	negotiating_transfer("Negotiating Transfer"),

	/**
	 * The peer has refused the file transfer request halting the file
	 * transfer negotiation process.
	 */
	refused("Refused"),

	/**
	 * The stream to transfer the file is being negotiated over the chosen
	 * stream type. After the stream negotiating process is complete the
	 * status becomes negotiated.
	 *
	 * @see #negotiated
	 */
	negotiating_stream("Negotiating Stream"),

	/**
	 * After the stream negotitation has completed the intermediate state
	 * between the time when the negotiation is finished and the actual
	 * transfer begins.
	 */
	negotiated("Negotiated"),

	/**
	 * The transfer is in progress.
	 *
	 * @see FileTransfer#getProgress()
	 */
	in_progress("In Progress"),

	/**
	 * The transfer has completed successfully.
	 */
	complete("Complete"),

	/**
	 * The file transfer was canceled
	 */
	cancelled("Cancelled");

    private String status;

    private Status(String status) {
        this.status = status;
    }

    public String toString() {
        return status;
    }
}