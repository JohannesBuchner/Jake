package com.jakeapp.core.synchronization.pull;

import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.change.ChangeListenerProxy;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.domain.JakeObject;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

public class FileRequestFuture extends AvailableLaterObject<IFileTransfer> implements
		INegotiationSuccessListener {

	private IFileTransferService transferService;

	private FileRequest request;

	private Semaphore sem;

	private ChangeListener changeListener;

	private JakeObject jo;

	private Throwable innerException;

	private ProjectApplicationContextFactory db;

	private static Logger log = Logger.getLogger(FileRequestFuture.class);


	protected class FileProgressChangeListener extends ChangeListenerProxy {

		public FileProgressChangeListener(ChangeListener cl) {
			super(cl);
		}

		@Override
		public void pullDone(JakeObject jo) {
			log.debug("pull done for " + jo);
			sem.release();
		}

		@Override
		public void pullFailed(JakeObject jo, Exception reason) {
			log.debug("pull failed for " + jo);
			sem.release();
			innerException = reason;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "]" + "transferService=" + transferService + ", request="
				+ request + ", jo=" + jo + "]";
	}

	public FileRequestFuture(JakeObject jo, IFileTransferService ts, FileRequest request,
			ChangeListener changeListener, ProjectApplicationContextFactory db) {
		super();
		this.transferService = ts;
		this.request = request;
		this.changeListener = new FileProgressChangeListener(changeListener);
		this.jo = jo;
		this.sem = new Semaphore(0);
		this.db = db;
		this.innerException = null;
		log.debug(this.toString());
	}

	@Override
	public IFileTransfer calculate() throws Exception {
		INegotiationSuccessListener listener = new PullListener(this.jo, changeListener,
				db);
		transferService.request(request, this);
		log.debug("waiting for negotiation-success-listener");

		sem.acquire();

		if (this.innerException != null && innerException instanceof Exception) {
			listener.failed(this.innerException);
			throw (Exception) innerException;
		}

		listener.succeeded(getInnercontent());

		log.debug("waiting for PullListener");
		sem.acquire();
		if (this.innerException != null && innerException instanceof Exception) {
			log.debug("listener threw Exception: " + innerException);
			throw (Exception) innerException;
		}

		return this.getInnercontent();
	}

	@Override
	public void failed(Throwable reason) {
		this.innerException = reason;
		sem.release();
	}

	@Override
	public void succeeded(IFileTransfer ft) {
		this.setInnercontent(ft);
		sem.release();
	}
}
