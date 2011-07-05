package com.jakeapp.violet.actions.global.serve;

import java.io.IOException;

import com.jakeapp.availablelater.AvailabilityListener;
import com.jakeapp.availablelater.AvailableLater;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcher;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

final class FileTransferWaiter implements
		AvailableLater<Void>, ITransferListener {

	private AvailabilityListener<Void> listener;

	private final IFileTransfer ft;


	public FileTransferWaiter(IFileTransfer ft) {
		this.ft = ft;
	}

	@Override
	public void onFailure(AdditionalFileTransferData transfer, String error) {
		listener.error(new IOException(error));
	}

	@Override
	public void onSuccess(AdditionalFileTransferData transfer) {
		listener.finished(null);
	}

	@Override
	public void onUpdate(AdditionalFileTransferData transfer,
			Status status, double progress) {
		// don't care
	}

	@Override
	public void setListener(AvailabilityListener<Void> listener) {
		this.listener = listener;
		new Thread(new TransferWatcher(ft, this)).start();
	}
}