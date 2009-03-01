package com.jakeapp.core.synchronization;

import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.domain.JakeObject;

import java.util.concurrent.Semaphore;

class FileRequestObject extends AvailableLaterObject<IFileTransfer> implements INegotiationSuccessListener {
	private IFileTransferService ts;
	private FileRequest request;
	private Semaphore sem;
	private ChangeListener cl;
	private JakeObject jo;
	private Throwable innerException;
	private ProjectApplicationContextFactory db;


	protected class FileProgressChangeListener extends ChangeListenerWrapper {
		public FileProgressChangeListener(ChangeListener cl) {
			super(cl);
		}

		@Override
		public void pullDone(JakeObject jo) {
			sem.release();
		}

		@Override
		public void pullFailed(JakeObject jo, Exception reason) {
			sem.release();
			innerException = reason;
		}
	}

	public FileRequestObject(JakeObject jo,
							 IFileTransferService ts,
							 FileRequest request,
							 ChangeListener cl,
							 ProjectApplicationContextFactory db) {
		super();
		this.ts = ts;
		this.request = request;
		this.cl = new FileProgressChangeListener(cl);
		this.jo = jo;
		sem = new Semaphore(0);
		this.db = db;
		innerException = null;
	}

	@Override
	public IFileTransfer calculate() throws Exception {
		INegotiationSuccessListener listener = new PullListener(this.jo, cl, db);
		ts.request(request, this);
		//wait for negotiation-success-listener
		sem.acquire();

		if (this.innerException != null && innerException instanceof Exception) {
			listener.failed(this.innerException);
			throw (Exception) innerException;
		}

		listener.succeeded(getInnercontent());

		//wait for FileProressChangeListener
		sem.acquire();
		if (this.innerException != null && innerException instanceof Exception) {
			throw (Exception) innerException;
			//this exception came from the listener!
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

