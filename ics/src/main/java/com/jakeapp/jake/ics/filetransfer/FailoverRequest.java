package com.jakeapp.jake.ics.filetransfer;

import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import org.apache.log4j.Logger;

import java.util.List;


public class FailoverRequest implements INegotiationSuccessListener {
	private Logger log = Logger.getLogger(FailoverRequest.class);
	private int counter = 0;
	private List<ITransferMethod> methods;
	private FileRequest request;

	private INegotiationSuccessListener parentListener;

	public FailoverRequest(FileRequest request, INegotiationSuccessListener nsl, final List<ITransferMethod> methods) {
		this.request = request;
		this.parentListener = nsl;
		this.methods = methods;
		getTransferMethod(this.counter)
				.request(this.request, this);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void failed(Throwable reason) {
		this.counter++;
		// if
		// (reason.getClass().equals(CommunicationProblemException.class)) {
		ITransferMethod method = getTransferMethod(this.counter);
		if (method != null) {
			log.info("failing over to method#" + this.counter + " : " + method);
			method.request(this.request, this);
			return;
		}
		log.info("no methods left, failure");
		try {
			this.parentListener.failed(reason);
		} catch (Exception ignored) {
		}
		// }
	}

	@Override
	public void succeeded(IFileTransfer ft) {
		log.info("success with method#" + this.counter);
		try {
			this.parentListener.succeeded(ft);
		} catch (Exception ignored) {
		}
	}

}