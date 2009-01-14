package com.jakeapp.jake.ics.filetransfer;

import java.util.LinkedList;
import java.util.List;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.exceptions.CommunicationProblemException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class FailoverCapableFileTransferService implements IFileTransferService {

	private List<ITransferMethod> methods = new LinkedList<ITransferMethod>();

	@Override
	public synchronized void addTransferMethod(ITransferMethodFactory m,
			IMsgService negotiationService, UserId user) throws NotLoggedInException {
		this.methods.add(m.getTransferMethod(negotiationService, user));
	}

	/**
	 * returns null if index is out of range
	 * 
	 * @param index
	 * @return
	 */
	private synchronized ITransferMethod getTransferMethod(int index) {
		try {
			return this.methods.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public void request(FileRequest request, INegotiationSuccessListener nsl) {
		if (this.methods.size() == 0)
			throw new NullPointerException("register methods first");
		new FailoverRequest(request, nsl);
	}

	private class FailoverRequest implements INegotiationSuccessListener {

		private int counter = 0;

		private FileRequest request;

		private INegotiationSuccessListener parentListener;

		public FailoverRequest(FileRequest request, INegotiationSuccessListener nsl) {
			this.request = request;
			FailoverCapableFileTransferService.this.getTransferMethod(this.counter)
					.request(this.request, this);
		}

		@Override
		public void failed(Throwable reason) {
			this.counter++;
			if (reason.getClass().equals(CommunicationProblemException.class)) {
				ITransferMethod method = FailoverCapableFileTransferService.this
						.getTransferMethod(this.counter);
				if (method != null) {
					method.request(this.request, this);
					return;
				}
				this.parentListener.failed(reason);
			}
		}

		@Override
		public void succeeded(IFileTransfer ft) {
			this.parentListener.succeeded(ft);
		}

	};

	@Override
	public void startServing(IncomingTransferListener l, FileRequestFileMapper mapper)
			throws NotLoggedInException {
		for (ITransferMethod method : this.methods) {
			method.startServing(l, mapper);
		}
	}

	@Override
	public void stopServing() {
		for (ITransferMethod method : this.methods) {
			method.stopServing();
		}
	}

}
