package com.jakeapp.jake.ics.filetransfer;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.exceptions.CommunicationProblemException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class FailoverCapableFileTransferService implements IFileTransferService {

	private static final Logger log = Logger
			.getLogger(FailoverCapableFileTransferService.class);

	private List<ITransferMethod> methods = new LinkedList<ITransferMethod>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addTransferMethod(ITransferMethodFactory m,
											   IMsgService negotiationService, UserId user) throws NotLoggedInException {
		this.methods.add(m.getTransferMethod(negotiationService, user));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void request(FileRequest request, INegotiationSuccessListener negotiationSuccessListener) {
		if (this.methods.size() == 0)
			throw new NullPointerException("register methods first");
		new FailoverRequest(request, negotiationSuccessListener, methods);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startServing(IncomingTransferListener l, FileRequestFileMapper mapper)
			throws NotLoggedInException {
		for (ITransferMethod method : this.methods) {
			method.startServing(l, mapper);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopServing() {
		for (ITransferMethod method : this.methods) {
			method.stopServing();
		}
	}

}
