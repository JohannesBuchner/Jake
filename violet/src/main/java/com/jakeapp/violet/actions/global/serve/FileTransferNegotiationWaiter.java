package com.jakeapp.violet.actions.global.serve;

import com.jakeapp.availablelater.AvailabilityListener;
import com.jakeapp.availablelater.AvailableLater;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;

final class FileTransferNegotiationWaiter implements
		AvailableLater<IFileTransfer>, INegotiationSuccessListener {

	private AvailabilityListener<IFileTransfer> listener;

	private final FileRequest sigfr;

	private final ITransferMethod method;

	public FileTransferNegotiationWaiter(ITransferMethod method,
			FileRequest sigfr) {
		this.method = method;
		this.sigfr = sigfr;
	}

	@Override
	public void failed(Exception reason) {
		listener.error(reason);
	}

	@Override
	public void setListener(AvailabilityListener<IFileTransfer> listener) {
		this.listener = listener;
		method.request(sigfr, this);
	}

	@Override
	public void succeeded(IFileTransfer ft) {
		listener.finished(ft);
	}
}