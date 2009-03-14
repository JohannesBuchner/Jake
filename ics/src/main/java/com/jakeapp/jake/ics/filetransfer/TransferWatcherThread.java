package com.jakeapp.jake.ics.filetransfer;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

public class TransferWatcherThread implements Runnable {

	public static final int UPDATE_FREQUENCY = 300;

	private static final Logger log = Logger.getLogger(TransferWatcherThread.class);

	/**
	 * after this timeout (in seconds) when no progress is made, the transfer is
	 * cancelled
	 */
	private static final int TIMEOUT_LIMIT = 10000 + 3 * UPDATE_FREQUENCY;

	/**
	 * after this timeout (in seconds) when the transfer is >=100%, the transfer
	 * is cancelled and announced as successful. See
	 * {@link IFileTransferService}
	 */
	private static final int NOT_DONE_TIMEOUT_LIMIT = 1000 + 3 * UPDATE_FREQUENCY;

	private IFileTransfer transfer;

	private ITransferListener listener;

	private int update_frequency = UPDATE_FREQUENCY;

	public TransferWatcherThread(IFileTransfer transfer, ITransferListener listener) {
		this.transfer = transfer;
		this.listener = listener;
	}

	public TransferWatcherThread(IFileTransfer transfer, ITransferListener listener,
			int update_frequency) {
		this(transfer, listener);
		this.update_frequency = update_frequency;
	}

	public void run() {
		Thread.currentThread().setName(
				this.transfer.getPeer() + "/" + this.transfer.getFileName());
		log.debug(Thread.currentThread() + " starting ... ");
		if (watchTransfer()) {
			log.info("Transfer: " + this.transfer.getFileName() + " with peer "
					+ this.transfer.getPeer() + ": was successful");
			this.listener.onSuccess(this.transfer.getFileRequest().getData());
		} else {
			log.info("Transfer: " + this.transfer.getFileName() + " with peer "
					+ this.transfer.getPeer() + ": was NOT successful: "
					+ this.transfer.getError());
			this.listener.onFailure(this.transfer.getFileRequest().getData(),
				this.transfer.getError());
		}
		log.debug(Thread.currentThread() + " done");
	}

	/**
	 * @return whether the transfer completed successfully
	 */
	private boolean watchTransfer() {
		Status status = null;
		double progress = 0;
		int nochangeCounter = 0;

		while (!this.transfer.isDone()) {
			if (this.transfer.getStatus().equals(Status.error)) {
				return false;
			} else if (status != this.transfer.getStatus()
					|| progress != this.transfer.getProgress()) {
				try {
					this.listener.onUpdate(this.transfer.getFileRequest().getData(),
						this.transfer
						.getStatus(), this.transfer.getProgress());
				} catch (Exception ignored) {
				}
				nochangeCounter = 0;
			} else {
				nochangeCounter = nochangeCounter + UPDATE_FREQUENCY;
			}

			status = this.transfer.getStatus();
			progress = this.transfer.getProgress();

			if (nochangeCounter > NOT_DONE_TIMEOUT_LIMIT && progress >= 1.0
					&& status.equals(Status.in_progress)) {
				log.warn("transfer was stopped because it looked finished "
						+ "and didn't change anymore");
				this.transfer.cancel();
				return true; // or false
			} else if (nochangeCounter > TIMEOUT_LIMIT) {
				this.transfer.cancel();
				return false;
			}

			try {
				Thread.sleep(UPDATE_FREQUENCY);
			} catch (InterruptedException e) {
				// interrupts don't concern us
			}
		}
		return this.transfer.getStatus().equals(Status.complete);
	}
}